package fr.jessee.blockQuota.listener.player;

import fr.jessee.blockQuota.BlockQuota;
import fr.jessee.blockQuota.util.dto.QuotaDTO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(BlockQuota.getInstance(), () -> {
           List<QuotaDTO> quotas = BlockQuota.getInstance().getSqLiteStorage().getQuotaAsync(p.getUniqueId()).join();
            Map<Material, Integer> map = quotas.stream()
                    .filter(dto -> Material.matchMaterial(dto.getBlockType()) != null)
                    .collect(Collectors.toMap(
                            dto -> Material.matchMaterial(dto.getBlockType()),
                            QuotaDTO::getCount
                    ));
           BlockQuota.getInstance().getQuotasCache().putIfAbsent(p.getUniqueId(), map);
        });
    }
}
