package fr.jessee.blockQuota.util.dto;

import java.util.UUID;

public class QuotaDTO {
    private final UUID playerId;
    private final String blockType; // ou Material, selon ton besoin
    private final int count;

    public QuotaDTO(UUID playerId, String blockType, int count) {
        this.playerId = playerId;
        this.blockType = blockType;
        this.count = count;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getBlockType() {
        return blockType;
    }

    public int getCount() {
        return count;
    }

    // Validation simple pour la conformité
    public boolean isValid() {
        if (playerId == null) return false;
        if (blockType == null || blockType.isEmpty()) return false;
        if (count < 0) return false; // ou autre règle métier
        return true;
    }

    @Override
    public String toString() {
        return "BlockQuotaDTO{" +
                "playerId=" + playerId +
                ", blockType='" + blockType + '\'' +
                ", count=" + count +
                '}';
    }
}
