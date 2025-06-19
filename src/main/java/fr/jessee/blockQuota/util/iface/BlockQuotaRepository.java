package fr.jessee.blockQuota.util.iface;

import fr.jessee.blockQuota.util.dto.QuotaDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BlockQuotaRepository {
    CompletableFuture<Integer> getQuotaCountAsync(UUID playerId, String blockType);
    CompletableFuture<List<QuotaDTO>> getQuotaAsync(UUID playerId);
    CompletableFuture<Void> addQuotaAsync(UUID playerId, String blockType, int amount);
    CompletableFuture<Void> resetQuotaAsync(UUID playerId);
    CompletableFuture<Void> resetAllQuotasAsync();
    CompletableFuture<Map<String, Integer>> getAllQuotasAsync(UUID playerId);
}
