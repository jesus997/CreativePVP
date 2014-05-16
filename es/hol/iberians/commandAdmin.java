package es.hol.iberians;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdmin implements CommandExecutor{
    
    private CPVP pl = null;
    private HashMap<String, List<Location>> spawns = new HashMap<>();
    private List<Location> respawn = new ArrayList<>();
    public CommandAdmin(CPVP pl) {
            this.pl = pl;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player p = (Player) sender;
        if(args.length < 1){
            if(p.hasPermission("cp.admin")){
                ArenaManager.getManager().sendMessageHelp(p);
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
            if(p.hasPermission("cp.admin")){
                ArenaManager.getManager().sendMessageHelp(p);
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("+")){
            if(p.hasPermission("cp.admin")){
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("help-create-command"), p);
            }else{
                 ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("addspawn") || args[0].equalsIgnoreCase("+s")){
            if(p.hasPermission("cp.admin")){
                if(spawns.containsKey(p.getName())){
                    List<Location> spw = spawns.get(p.getName());
                    spw.add(p.getLocation());
                    spawns.remove(p.getName());
                    spawns.put(p.getName(), spw);
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-addspawn"), p);
                }else{
                    List<Location> spw = new ArrayList<>();
                    spw.add(p.getLocation());
                    spawns.put(p.getName(), spw);
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-addspawn"), p);
                }
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("addrespawn") || args[0].equalsIgnoreCase("+r")){
            if(p.hasPermission("cp.admin")){
                this.respawn.add(p.getLocation());
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-addrespawn"), p);
            }else{
                 ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("+g")){
            if(p.hasPermission("cp.admin")){
                this.pl.saveToFile(ArenaManager.getManager().createArena(spawns.get(p.getName()), this.respawn), this.respawn);
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-save-arena"), p);
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("-c")){
            if(p.hasPermission("cp.admin")){
                spawns.remove(p.getName());
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-cancel-arena"), p);
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-")){
            if(p.hasPermission("cp.admin")){
                if(args.length != 2){
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("help-remove-command"), p);
                }else{
                    File f = new File(this.pl.getDataFolder() + "/arenas/arena" + args[1] + ".yml");
                    f.delete();
                    ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-remove-arena"), p);
                }
            }else{
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-permissions"), p);
            }
        }else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r+")){
            if(p.hasPermission("cp.admin")){
                this.pl.reloadConfig();
                ArenaManager.getManager().sendMessage(CPVP.cfg.getString("msg-reload-arena"), p);
            }
        }
        return true;
    }
}
