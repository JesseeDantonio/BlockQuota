package fr.jessee.blockQuota.command.sub;

import fr.jessee.blockQuota.BlockQuota;
import fr.jessee.blockQuota.feature.AbstractSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Reset extends AbstractSubCommand {
    @Override
    public String getName() {
        return "reset";
    }
    @Override
    public List<String> getAliases() {
        return List.of("");
    }
    @Override
    public String getDescription() {
        return "Reset the quota of a player";
    }
    @Override
    public String getUsage() {
        return "/bq reset <player> | <all>";
    }
    @Override
    public void execute(org.bukkit.command.CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("blockQuota.reset")) {
                player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("no_permission"));
                return;
            }
            if (args.length == 0) {
                BlockQuota.getInstance().getStorageBreak().resetQuotaAsync(player.getUniqueId());
                BlockQuota.getInstance().getStoragePlace().resetQuotaAsync(player.getUniqueId());
                BlockQuota.getInstance().getQuotasBreakCache().remove(player.getUniqueId());
            } else if (args[0].equalsIgnoreCase("all")) {
                BlockQuota.getInstance().getStorageBreak().resetAllQuotasAsync();
                BlockQuota.getInstance().getStoragePlace().resetAllQuotasAsync();
                BlockQuota.getInstance().getQuotasBreakCache().clear();
                player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit_reset_all"));
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.getName().equals(args[0])) {
                        BlockQuota.getInstance().getStorageBreak().resetQuotaAsync(p.getUniqueId());
                        BlockQuota.getInstance().getStoragePlace().resetQuotaAsync(p.getUniqueId());
                        BlockQuota.getInstance().getQuotasBreakCache().remove(p.getUniqueId());
                        player.sendMessage(BlockQuota.getInstance().getLangConfig()
                                .getString("limit_reset_another")
                                .replace("%player%", p.getName())
                        );
                    } else {
                        player.sendMessage(BlockQuota.getInstance().getLangConfig()
                                .getString("not_connected")
                                .replace("%player%", p.getName())
                        );
                    }
                });
            }
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit_reset"));
        } else {
            if (args[0].equalsIgnoreCase("all")) {
                BlockQuota.getInstance().getStorageBreak().resetAllQuotasAsync();
                BlockQuota.getInstance().getStoragePlace().resetAllQuotasAsync();
                BlockQuota.getInstance().getQuotasBreakCache().clear();
                sender.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit_reset_all"));
                return;
            }

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.getName().equals(args[0])) {
                    BlockQuota.getInstance().getStorageBreak().resetQuotaAsync(p.getUniqueId());
                    BlockQuota.getInstance().getQuotasBreakCache().remove(p.getUniqueId());
                    BlockQuota.getInstance().getStoragePlace().resetQuotaAsync(p.getUniqueId());
                    sender.sendMessage(BlockQuota.getInstance().getLangConfig()
                            .getString("limit_reset_another")
                            .replace("%player%", p.getName())
                    );
                } else {
                    sender.sendMessage(BlockQuota.getInstance().getLangConfig()
                            .getString("not_connected")
                            .replace("%player%", p.getName()));
                }
            });
        }
    }
}
