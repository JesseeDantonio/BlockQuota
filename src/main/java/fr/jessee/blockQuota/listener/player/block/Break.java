package fr.jessee.blockQuota.listener.player.block;

import fr.jessee.blockQuota.BlockQuota;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public class Break implements Listener {
    private BlockQuota blockQuota;

    public Break(BlockQuota blockQuota) {
        this.blockQuota = blockQuota;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Material bloc = e.getBlock().getType();
        NamespacedKey key = bloc.getKey();
        String keyString = key.toString(); // "minecraft:diamond_ore"

        if (!BlockQuota.getInstance().getMainConfig().containsInSection("block_limits", bloc.name())) {
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("not-whitelisted"));
            return;
        }

        if (player.hasPermission("blockquota.bypass")) return;

        int limit = BlockQuota.getInstance().getConfig().getInt("block_limits." + bloc.name());
        Map<Material, Integer> stats = BlockQuota.getInstance().getStats()
                .computeIfAbsent(uuid, k -> BlockQuota.getInstance().getSqLiteStorage().getAllQuotasAsMaterial(uuid));
        int alreadyBreak = stats.getOrDefault(bloc, 0);

        if (alreadyBreak >= limit) {
            e.setCancelled(true);
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit-reached")
                    .replace("%block%", bloc.name())
            );
        } else {
            stats.put(bloc, alreadyBreak + 1);
            BlockQuota.getInstance().getSqLiteStorage().addQuota(uuid, keyString, alreadyBreak + 1);
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("block-break-added")
                    .replace("%block%", bloc.name())
                    .replace("%current%", String.valueOf(alreadyBreak + 1))
                    .replace("%limit%", String.valueOf(limit))
            );
        }
    }
}
