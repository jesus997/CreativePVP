package es.hol.iberians;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args){
            if (!(sender instanceof Player)) {
              return false;
            }
            Player p = (Player)sender;
            if(args.length < 1){
                if(p.hasPermission("cp.help")){
                    ArenaManager.getManager().sendMessageHelp(p);
                }else{
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
                }
            }else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
                if(p.hasPermission("cp.help")){
                    ArenaManager.getManager().sendMessageHelp(p);
                }else{
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
                }
            }else if(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("j") || args[0].equalsIgnoreCase("+")){
                if(p.hasPermission("cp.join")){
                    if(args.length != 2){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("help-join-command"), p);
                    }else{
                        int i = Integer.parseInt(args[1]);
                        ArenaManager.getManager().addPlayer(p, i);
                    }
                }else{
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
                }
            }else if(args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("-")){
                if(p.hasPermission("cp.join")){
                    if(args.length != 1){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("help-leave-command"), p);
                    }else{
                        ArenaManager.getManager().removePlayer(p);
                    }
                }else{
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
                }
            }else if(args[0].equalsIgnoreCase("list")){
                if(p.hasPermission("cp.list")){
                    if(args.length != 1){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("help-list-command"), p);
                    }else{
                        ArenaManager.getManager().showIconMenu(p);
                    }
                }else{
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
                }
            }

        return true;
      }
}
