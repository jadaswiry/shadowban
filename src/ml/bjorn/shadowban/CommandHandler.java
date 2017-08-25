package ml.bjorn.shadowban;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private Main plugin = Main.plugin;

    private final Map<String, SubCommand> subcommands = new LinkedHashMap<>();
    {
        subcommands.put("help", new HelpSubCommand());
        subcommands.put("show", new ShowSubCommand());
        subcommands.put("mute", new Punishment("mute"));
        subcommands.put("unmute", new Apology("mute"));
        subcommands.put("ban", new Punishment("ban"));
        subcommands.put("unban", new Apology("ban"));
        subcommands.put("silent", new SilentSubCommand());
        subcommands.put("reload", new ReloadSubCommand());
        subcommands.put("version", new VersionSubCommand());
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        SubCommand subcommand = arguments.length == 0 ? subcommands.get("help") : subcommands.get(arguments[0]);
        if(subcommand == null){
            sender.sendMessage("§cNieprawidłowa komenda! Zobacz /sb help");
            return true;
        }
        String[] subcommandargs = arguments.length != 0 ? Arrays.copyOfRange(arguments, 1, arguments.length) : new String[]{};
        if(subcommandargs.length < subcommand.getMinArgs()) {
            sender.sendMessage("§cZa mało argumentów! Zobacz /sb help");
            return true;
        }
        return subcommand.handle(sender, subcommandargs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        if (strings.length == 1 ) {
            return subcommands.keySet().stream()
                    .filter(subcommand -> subcommand.startsWith(strings[0]))
                    .collect(Collectors.toList());
        } else {
            List<String> players = new ArrayList<>();
            plugin.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
            return players;
        }
    }
}