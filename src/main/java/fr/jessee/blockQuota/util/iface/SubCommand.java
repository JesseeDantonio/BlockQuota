package fr.jessee.blockQuota.util.iface;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName(); // ex: "invite"
    List<String> getAliases(); // ex: Arrays.asList("inv")
    String getDescription();
    String getUsage();
    void execute(CommandSender sender, String[] args);
}
