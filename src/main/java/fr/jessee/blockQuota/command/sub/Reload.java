package fr.jessee.blockQuota.command.sub;

import fr.jessee.blockQuota.BlockQuota;
import fr.jessee.blockQuota.feature.AbstractSubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends AbstractSubCommand {
    @Override
    public String getName() {
        return "reload";
    }
    @Override
    public List<String> getAliases() {
        return List.of("");
    }
    @Override
    public String getDescription() {
        return "Reload the plugin configuration";
    }
    @Override
    public String getUsage() {
        return "/bq reload";
    }
    @Override
    public void execute(org.bukkit.command.CommandSender sender, String[] args) {
        BlockQuota.getInstance().reloadConfig();
        BlockQuota.getInstance().getLangConfig().reload();
        BlockQuota.getInstance().getMainConfig().reload();
        if (sender instanceof Player player) {
            player.sendMessage(BlockQuota.getInstance().getLangConfig().getString("reload-success"));
        } else {
            sender.sendMessage(BlockQuota.getInstance().getLangConfig().getString("reload-success"));
        }
    }
}
