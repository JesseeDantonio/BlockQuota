package fr.jessee.blockQuota.listener.player.block;

import fr.jessee.blockQuota.BlockQuota;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

public class Break implements Listener {
    private final BlockQuota blockQuota;

    public Break(BlockQuota blockQuota) {
        this.blockQuota = blockQuota;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        Material bloc = e.getBlock().getType();
        NamespacedKey key = bloc.getKey();
        String keyString = key.toString();

        Map<Material, Integer> quotas = BlockQuota.getInstance().getQuotasCache().computeIfAbsent(
                p.getUniqueId(), k -> new java.util.concurrent.ConcurrentHashMap<>()
        );

        if (!blockQuota.getMainConfig().containsInSection("block_break_limit", bloc.name())) {
            return;
        }

        if (p.hasPermission("blockquota.bypass")) return;

        int limit = blockQuota.getConfig().getInt("block_break_limit." + bloc.name());
        int alreadyBreak = quotas.getOrDefault(bloc, 0) + 1;

        if (alreadyBreak > limit) {
            e.setCancelled(true);
            p.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit_reached")
                    .replace("%block%", bloc.name()));
            return;
        }

        quotas.put(bloc, alreadyBreak);
        blockQuota.getSqLiteStorage().addQuotaAsync(uuid, keyString, alreadyBreak);
    }
}