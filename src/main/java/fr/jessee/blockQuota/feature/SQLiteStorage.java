package fr.jessee.blockQuota.feature;

import fr.jessee.blockQuota.util.dto.QuotaDTO;
import fr.jessee.blockQuota.util.iface.BlockQuotaRepository;
import fr.jessee.blockQuota.util.iface.Storage;
import org.bukkit.Material;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Bukkit.getLogger;

public class SQLiteStorage implements BlockQuotaRepository {
    private final Storage storage;

    public SQLiteStorage(Storage storage) throws SQLException {
        this.storage = storage;
        // Création de la table si elle n'existe pas
        try (Statement stmt = storage.getConnection().createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS storage (
                    uuid TEXT NOT NULL,
                    bloc TEXT NOT NULL,
                    count INTEGER NOT NULL,
                    PRIMARY KEY (uuid, bloc)
                );
            """);
        }
    }

    private List<QuotaDTO> getQuotas(UUID playerId) {
        List<QuotaDTO> quotas = new ArrayList<>();
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "SELECT * FROM storage WHERE uuid=?")) {
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    QuotaDTO dto = hydrateBlockQuotaDTO(rs);
                    if (dto.isValid()) {
                        quotas.add(dto);
                    }
                } catch (Exception e) {
                    getLogger().severe(e.getMessage());
                }
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
        return quotas;
    }


    private int getQuotaCount(UUID playerId, String blockType) {
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "SELECT count FROM storage WHERE uuid=? AND bloc=?")) {
            ps.setString(1, playerId.toString());
            ps.setString(2, blockType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
        return 0;
    }


    private void addQuota(UUID playerId, String blockType, int amount) {
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "INSERT INTO storage (uuid, bloc, count) VALUES (?, ?, ?)" +
                        "ON CONFLICT(uuid, bloc) DO UPDATE SET count=excluded.count")) {
            ps.setString(1, playerId.toString());
            ps.setString(2, blockType);
            ps.setInt(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }


    private void resetQuota(UUID playerId) {
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "DELETE FROM storage WHERE uuid=?")) {
            ps.setString(1, playerId.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }


    private void resetAllQuotas() {
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "DELETE FROM storage")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }


    private Map<String, Integer> getAllQuotas(UUID playerId) {
        Map<String, Integer> quotas = new HashMap<>();
        try (PreparedStatement ps = storage.getConnection().prepareStatement(
                "SELECT bloc, count FROM storage WHERE uuid=?")) {
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quotas.put(rs.getString("bloc"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
        return quotas;
    }

    private QuotaDTO hydrateBlockQuotaDTO(ResultSet rs) throws SQLException {
        UUID playerId = UUID.fromString(rs.getString("uuid"));
        String blockType = rs.getString("bloc");
        int count = rs.getInt("count");
        return new QuotaDTO(playerId, blockType, count);
    }


    public Map<Material, Integer> getAllQuotasAsMaterial(UUID playerId) {
        Map<String, Integer> raw = getAllQuotas(playerId);
        Map<Material, Integer> converted = new HashMap<>();
        for (Map.Entry<String, Integer> entry : raw.entrySet()) {
            try {
                Material mat = Material.matchMaterial(entry.getKey());
                converted.put(mat, entry.getValue());
            } catch (IllegalArgumentException err) {
                // Si jamais un bloc a été supprimé de Spigot
                getLogger().warning(err.getMessage());
            }
        }
        return converted;
    }

    @Override
    public CompletableFuture<Integer> getQuotaCountAsync(UUID playerId, String blockType) {
        return CompletableFuture.supplyAsync(() -> getQuotaCount(playerId, blockType));
    }

    @Override
    public CompletableFuture<List<QuotaDTO>> getQuotaAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> getQuotas(playerId));
    }


    @Override
    public CompletableFuture<Void> addQuotaAsync(UUID playerId, String blockType, int amount) {
        return CompletableFuture.runAsync(() -> addQuota(playerId, blockType, amount));
    }

    @Override
    public CompletableFuture<Void> resetQuotaAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> resetQuota(playerId));
    }

    @Override
    public CompletableFuture<Void> resetAllQuotasAsync() {
        return CompletableFuture.runAsync(this::resetAllQuotas);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getAllQuotasAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> getAllQuotas(playerId));
    }
}
