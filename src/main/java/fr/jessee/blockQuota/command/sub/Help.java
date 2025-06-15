package fr.jessee.blockQuota.command.sub;

import fr.jessee.blockQuota.BlockQuota;
import fr.jessee.blockQuota.feature.AbstractSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Help extends AbstractSubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getUsage() {
        return "/bq help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        List<List<String>> help = new ArrayList<>();
        for (String key : BlockQuota.getInstance().getLangConfig().getConfiguration()
                .getConfigurationSection("FACTION_HELP").getKeys(false)) {
            help.add(BlockQuota.getInstance().getLangConfig().getStringList("FACTION_HELP." + key));
        }

        if (args.length == 0) {
            for (String msg : help.getFirst()) {
                if (sender instanceof Player p) {
                    p.sendMessage(msg);
                } else {
                    sender.sendMessage(msg);
                }
            }
            return;
        }

        if (isInteger(args[0])) {
            int page = Integer.parseInt(args[0]);
            if (page > 0 && page <= help.size()) {
                for (String msg : help.get(page - 1)) {
                    if (sender instanceof Player p) {
                        p.sendMessage(msg);
                    } else {
                        sender.sendMessage(msg);
                    }
                }
            }
        }
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
