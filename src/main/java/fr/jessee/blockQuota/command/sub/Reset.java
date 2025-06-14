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
                player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("no-permission"));
                return;
            }
            if (args.length == 0) {
                BlockQuota.getInstance().getSqLiteStorage().resetQuota(player.getUniqueId());
            } else if (args[0].equalsIgnoreCase("all")) {
                BlockQuota.getInstance().getSqLiteStorage().resetAllQuotas();
                player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit-reset-all"));
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.getName().equals(args[0])) {
                        BlockQuota.getInstance().getSqLiteStorage().resetQuota(p.getUniqueId());
                        player.sendMessage(BlockQuota.getInstance().getLangConfig()
                                .getString("limit-reset-another")
                                .replace("%player%", p.getName())
                        );
                    } else {
                        player.sendMessage(BlockQuota.getInstance().getLangConfig()
                                .getString("not-connected")
                                .replace("%player%", p.getName())
                        );
                    }
                });
            }
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit-reset"));
        } else {
            if (args[0].equalsIgnoreCase("all")) {
                BlockQuota.getInstance().getSqLiteStorage().resetAllQuotas();
                sender.sendMessage(BlockQuota.getInstance().getLangConfig().getString("limit-reset-all"));
                return;
            }

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.getName().equals(args[0])) {
                    BlockQuota.getInstance().getSqLiteStorage().resetQuota(p.getUniqueId());
                    sender.sendMessage(BlockQuota.getInstance().getLangConfig()
                            .getString("limit-reset-another")
                            .replace("%player%", p.getName())
                    );
                } else {
                    sender.sendMessage(BlockQuota.getInstance().getLangConfig()
                            .getString("not-connected")
                            .replace("%player%", p.getName()));
                }
            });
        }
    }
}
