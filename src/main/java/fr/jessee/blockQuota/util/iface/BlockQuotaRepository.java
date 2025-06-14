package fr.jessee.blockQuota.util.iface;

import java.util.Map;
import java.util.UUID;

public interface BlockQuotaRepository {
    int getQuota(UUID playerId, String blockType);
    void addQuota(UUID playerId, String blockType, int amount);
    void resetQuota(UUID playerId);
    void resetAllQuotas();
    Map<String, Integer> getAllQuotas(UUID playerId);
}
