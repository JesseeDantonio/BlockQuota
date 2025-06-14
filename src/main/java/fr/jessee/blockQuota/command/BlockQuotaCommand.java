package fr.jessee.blockQuota.command;

import fr.jessee.blockQuota.BlockQuota;
import fr.jessee.blockQuota.util.iface.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Modifier;
import java.util.*;

public class BlockQuotaCommand implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public BlockQuotaCommand() {
        // Scan le package des sous-commandes
        Reflections reflections = new Reflections(
                "fr.jessee.blockQuota.command.sub",
                Scanners.SubTypes
        );
        Set<Class<? extends SubCommand>> commandClasses = reflections.getSubTypesOf(SubCommand.class);

        for (Class<? extends SubCommand> cmdClass : commandClasses) {
            try {
                // ⚠️ Ignore les classes abstraites ou interfaces
                if (Modifier.isAbstract(cmdClass.getModifiers()) || Modifier.isInterface(cmdClass.getModifiers())) {
                    continue;
                }

                SubCommand sub = cmdClass.getDeclaredConstructor().newInstance();
                subCommands.put(sub.getName().toLowerCase(), sub);
                for (String alias : sub.getAliases()) {
                    subCommands.put(alias.toLowerCase(), sub);
                }
            } catch (Exception e) {
                BlockQuota.getInstance().getLogger().warning("Impossible d'instancier la sous-commande: " + cmdClass.getName());
                BlockQuota.getInstance().getLogger().severe(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            subCommands.get("help").execute(sender, args);
            return true;
        }
        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(ChatColor.RED + "Unknown sub-command. Use /bq help.");
            return true;
        }
        sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}

