package fr.jessee.blockQuota.listener.player.block;

import fr.jessee.blockQuota.BlockQuota;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;
import java.util.UUID;

public class Place implements Listener {
    private final BlockQuota blockQuota;

    public Place(BlockQuota blockQuota) {
        this.blockQuota = blockQuota;
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        Material bloc = e.getBlock().getType();
        NamespacedKey key = bloc.getKey();
        String keyString = key.toString();

        Map<Material, Integer> quotasPlace = BlockQuota.getInstance().getQuotasBreakCache().computeIfAbsent(
                p.getUniqueId(), k -> new java.util.concurrent.ConcurrentHashMap<>()
        );

        if (!blockQuota.getMainConfig().containsInSection("block_placement_limit", bloc.name())) {
            return;
        }

        if (p.hasPermission("blockquota.bypass")) return;

        int limit = blockQuota.getConfig().getInt("block_placement_limit." + bloc.name());
        int alreadyPlace = quotasPlace.getOrDefault(bloc, 0) + 1;

        if (alreadyPlace > limit) {
            e.setCancelled(true);
            p.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit_reached")
                    .replace("%block%", bloc.name()));
            return;
        }

        quotasPlace.put(bloc, alreadyPlace);
        blockQuota.getStoragePlace().addQuotaAsync(uuid, keyString, alreadyPlace);
    }
}
