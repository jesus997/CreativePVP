package es.hol.iberians.CPVP;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author jesus
 */

public class CPVP extends JavaPlugin implements Listener{
    
    @Override
    public void onEnable(){
        ArenaManager arenaManager = new ArenaManager(this);
        getServer().getPluginManager().registerEvents(new GameLsitener(this), this);
        File config1 = new File("plugins/CreativoPVP/config.yml");
        File players = new File("plugins/CreativoPVP/players.yml");
        if(config1.exists()){
                //ArenaManager.getManager().loadArenas();
        }
        if(players.exists()){
            try {
                ArenaManager.getManager().loadInventary(); 
            } catch (IOException ex) {
                Logger.getLogger(CPVP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        getLogger().info("Se ha cargado CreativoPVP :D");
    }
    
    @Override
    public void onDisable(){
        this.saveConfig();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("cp") || cmd.getName().equalsIgnoreCase("cpvp")){
            if(args.length < 1){
                if(sender.hasPermission("cpvp.help")){
                    this.sendMessageHelp(sender);
                }else{
                    this.sendMessageError("&4&lNo tienes permio de usar este comando!", sender);
                }
            }else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
                if(sender.hasPermission("cpvp.help")){
                    this.sendMessageHelp(sender);
                }else{
                    this.sendMessageError("No tienes permiso de usar este comando!", sender);
                }
            }else if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")){
                if(args.length != 3){
                    this.sendMessageInfo("Crea una arena: /cp create [arenaName] [maxPlayers]", sender);
                }else{
                    if(sender.hasPermission("cpvp.create-arena")){
                        Player player = (Player) sender;
                        String arenaName = args[1];
                        int maxPlayers = Integer.parseInt(args[2]);
                        /*if(ArenaManager.getManager().getArena(arenaName) == null){*/
                                World world = player.getWorld();
                                double x = player.getLocation().getX();
                                double y = player.getLocation().getY();
                                double z = player.getLocation().getZ();
                                Location startLocation = new Location (world, x, y, z);
                                ArenaManager.getManager().createArena(arenaName, null, startLocation, null, maxPlayers);
                                this.sendMessageOk("Arena creada con exito!", sender);
                                this.sendMessageInfo("Por favor añade el lobby y salida a tu arena!", sender);
                                this.sendMessageInfo("Usa: /cp add [arenaName] lobby o /cp add [arenaName] final", sender);
                        /*}else{
                            this.sendMessageError("Esta arena ya existe!", sender);
                        }*/
                    }else {
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("add")){
                if(args.length != 3){
                    this.sendMessageInfo("Añade un lobby o un final: /cp add [arenaName] [lobby/final]", sender);
                }else{
                    if(sender.hasPermission("cpvp.create-arena")){
                        Player player = (Player) sender;
                        String arenaName = args[1];
                        if(args[2].equalsIgnoreCase("lobby")){
                            FileConfiguration config = getConfig();
                            String path = "Arenas." + arenaName + ".";

                            if(ArenaManager.getManager().getArena(arenaName) != null){
                                config.set(path + "joinX", player.getLocation().getX());
                                config.set(path + "joinY", player.getLocation().getY());
                                config.set(path + "joinZ", player.getLocation().getZ());
                                saveConfig();
                                reloadConfig();
                            }else {
                                this.sendMessageError("Esa arena no existe!", sender);
                            }
                        }else if(args[2].equalsIgnoreCase("final")){
                            FileConfiguration config = getConfig();
                            String path = "Arenas." + arenaName + ".";

                            if(ArenaManager.getManager().getArena(arenaName) != null){
                                config.set(path + "endX", player.getLocation().getX());
                                config.set(path + "endY", player.getLocation().getY());
                                config.set(path + "endZ", player.getLocation().getZ());

                                saveConfig();
                                reloadConfig();
                            }else {
                                this.sendMessageError("Esa arena no existe!", sender);
                            }
                        }else{
                            this.sendMessageError("Añade un lobby o un final: /cp add [arenaName] [lobby/final]", sender);
                        }
                    }else{
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")){
                if(args.length != 2){
                    this.sendMessageInfo("Elimina una arena: /cp remove [arenaName]", sender);
                }else{
                    if(sender.hasPermission("cpvp.create-arena")){
                        String arenaName = args[1];
                        ArenaManager.getManager().removeArena(arenaName);
                        saveConfig();
                        reloadConfig();
                        this.sendMessageOk("Arena borrada!", sender);
                    }else{
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("fsa") || args[0].equalsIgnoreCase("forcestartarena")){
                if(args.length != 2){
                    this.sendMessageInfo("Forzar inicio de una arena: /cp fsa [arenaName]", sender);
                }else{
                    if(sender.hasPermission("cpvp.fsa")){
                        String nameArena = args[1];
                        if(ArenaManager.getManager().getArena(nameArena) != null){
                            ArenaManager.getManager().startArena(nameArena);
                            this.sendMessageOk("Arena forzada con exito! :D", sender);
                        }else{
                            this.sendMessageError("Esa arena no existe!", sender);
                        }
                    }else {
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("fea") || args[0].equalsIgnoreCase("forceendarena")){
                if(args.length != 2){
                    this.sendMessageInfo("Forzar fin de una arena: /cp fea [arenaName]", sender);
                }else{
                    if(sender.hasPermission("cpvp.fea")){
                        String nameArena = args[1];
                        if(ArenaManager.getManager().getArena(nameArena) != null){
                            ArenaManager.getManager().endArena(nameArena);
                            this.sendMessageOk("Arena forzada con exito! :D", sender);
                        }else{
                            this.sendMessageError("Esa arena no existe!", sender);
                        }
                    }else {
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("join")){
                if(args.length != 2){
                    this.sendMessageInfo("Entrar a una arena: /cp join [arenaName]", sender);
                }else{
                    if(sender.hasPermission("cpvp.join")){
                        Player player = (Player) sender;
                        String arenaName = args[1];
                        if(ArenaManager.getManager().getArena(arenaName) != null){
                            ArenaManager.getManager().addPlayers(player, label);
                        }else{
                            this.sendMessageError("Esa arena no existe!", sender);
                        }
                    }else{
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("leave")){
                if(args.length != 1){
                    this.sendMessageInfo("Salir de una arena: /cp leave", sender);
                }else{
                    if(sender.hasPermission("cpvp.join")){
                        Player player = (Player) sender;
                        ArenaManager.getManager().removePlayer(player);
                    }
                }
            }else if(args[0].equalsIgnoreCase("list")){
                if(args.length != 1){
                    this.sendMessageInfo("Lista de arenas: /cp list", sender);
                }else {
                    if(sender.hasPermission("cpvp.list")){
                        List<Arena> list = ArenaManager.getManager().arenas;
                        this.sendMessageInfo("Arenas:", sender);
                        this.sendMessageOk("- " + list, sender);
                    }
                }
            }else if(args[0].equalsIgnoreCase("reload")){
                if(args.length != 1){
                    this.sendMessageInfo("Reload: /cp leave", sender);
                }else{
                    if(sender.isOp()){
                        this.reloadConfig();
                        this.sendMessageOk("Configuracion recargada!", sender);
                    }else {
                        this.sendMessageError("No tienes permiso de usar este comando!", sender);
                    }
                }
            }
        }
        return false;
    }
    
    public void sendMessageOk(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "CPVP" + ChatColor.WHITE + "]" + ChatColor.GREEN + message);
    }
    
    public void sendMessageError(String message, CommandSender sender){
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "CPVP" + ChatColor.WHITE + "]" + ChatColor.RED + message);
    }
    
    public void sendMessageInfo(String message, CommandSender sender){
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "CPVP" + ChatColor.WHITE + "]" + ChatColor.DARK_BLUE + message);
    }
    
    public void sendMessageCommand(String message, CommandSender sender){
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "CPVP" + ChatColor.WHITE + "]" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public void sendMessageHelp(CommandSender sender){
        this.sendMessageCommand("&f&l[]&c ---------- &f[ &6CREATIVE PVP &f] &c---------- &f[]", sender);
        this.sendMessageCommand("&8/cp join [arenaName] &f-> &8Ingresa a la arena.", sender);
        this.sendMessageCommand("&8/cp leave &f-> &8Sal de la arena.", sender);
        this.sendMessageCommand("&8/cp list &f-> &8Lista de las arenas disponibles.", sender);
        if(sender.isOp()){
            this.sendMessageCommand("&8/cp create [nameArena] [maxPlayers] &f-> &8Creas una arena.", sender);
            this.sendMessageCommand("&8/cp add [nameArena] [lobby/final] &f-> &8Añades el lobby y el final a la arena.", sender);
            this.sendMessageCommand("&8/cp remove [nameArena] &f-> &8Eliminas una arena.", sender);
            this.sendMessageCommand("&8/cp fsa [nameArena] &f-> &8Fuerzas el inicio de una arena.", sender);
            this.sendMessageCommand("&8/cp fea [nameArena] &f-> &8Fuerzas a finalizar una arena.", sender);
        }
        this.sendMessageCommand("&f&l[]&c ------ &f[ &6GRACIAS POR JUGAR :D &f] &c------ &f[]", sender);
    }
}
