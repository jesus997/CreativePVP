package es.hol.iberians.CPVP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * @author jesus
 */

public class ArenaManager {
    Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
    Map<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();
    private Map<String, Float> exp = new HashMap<String, Float>();
    private Map<String, Integer> level = new HashMap<String, Integer>();
    private Map<String, Scoreboard> score = new HashMap<String, Scoreboard>();
    List<Arena> arenas = new ArrayList<Arena>();
    private static ArenaManager am = new ArenaManager();
    
    static CPVP plugin;
    public ArenaManager(CPVP creativoPVP){plugin = creativoPVP;}
    
    public static ArenaManager getManager(){
        return am;
    }

    private ArenaManager() {
    }
    
    public Arena getArena(String name){
        for(Arena a : Arena.arenaObjects){
            if(a.getName().equals(name)){
                return a;
            }
        }
        return null;
    }
    
    public void addPlayers(Player player, String arenaName){
        if(getArena(arenaName) != null){
            Arena arena = getArena(arenaName);
            if(!arena.isFull()){
                inv.put(player.getName(), player.getInventory().getContents());
                armor.put(player.getName(), player.getInventory().getArmorContents());
                
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                
                String namePlayer = player.getName();
                
                this.level.put(namePlayer, player.getLevel());
                this.exp.put(namePlayer, player.getExp());
                
                this.saveInventoryInConfig(player, inv, armor, exp, level);
                
                player.setExp(0);
                player.setLevel(0);
                
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setFireTicks(0);
                player.teleport(arena.getJoinLocation());
                player.setGameMode(GameMode.CREATIVE);
                arena.getPlayers().add(player.getName());

                arena.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + ChatColor.GREEN + " ha entrado a la arena!");

                if(arena.getPlayers().size() == 2){
                    startArena(arenaName);
                }
            }else {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Esta arena ya esta llena, intenta con otra.");
            }
        }else{
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "No existe esa arena, intenta con otra.");
        }
    }
    
    public void saveInventoryInConfig(Player p, Map<String, ItemStack[]> inv, Map<String, ItemStack[]> armor, Map<String, Float> exp, Map<String, Integer> level){
        FileConfiguration players = plugin.getConfig();
        File CreativoPVP = new File("plugins" + File.separator + "CreativoPVP" + File.separator + "players.yml");
        CreativoPVP.mkdir();
        
        String path = "Players." + p + ".";
        
        if(!players.contains("Players." + p)){
            players.set(path + "inventario", inv);
            players.set(path + "armadura", armor);
            players.set(path + "exp", exp);
            players.set(path + "level", level);
        }
    }
    
    public void deleteInventoryInConfig(Player p){
        FileConfiguration players = plugin.getConfig();
        File CreativoPVP = new File("plugins" + File.separator + "CreativoPVP" + File.separator + "players.yml");
        CreativoPVP.mkdir();
        
        if(players.contains("Players." + p)){
            players.set("Players." + p, null);
        }
    }
    
    public void loadInventary() throws IOException{
        YamlConfiguration c = YamlConfiguration.loadConfiguration(new File("plugins" + File.separator + "CreativoPVP" + File.separator + "players.yml"));
        for(String keys : c.getConfigurationSection("Players").getKeys(false)){
            ItemStack[] in = ((List<ItemStack>) c.get("Players." + keys + "inventario")).toArray(new ItemStack[0]);
            Player p = Bukkit.getPlayer(keys);
            Map<String, ItemStack[]> inve = new HashMap<>();
            inve.put(p.getName(), in);
            ItemStack[] arm = ((List<ItemStack>) c.get("Players." + p + "armadura")).toArray(new ItemStack[0]);
            Map<String, ItemStack[]> armo = new HashMap<>();
            armo.put(p.getName(), arm);
            float expe = (float) c.getDouble("Players." + p + "exp");
            Map<String, Float> ex = new HashMap<>();
            ex.put(p.getName(), expe); 
            int leve = c.getInt("Players." + p + "level");
            Map<String, Integer> le = new HashMap<>();
            le.put(p.getName(), leve);

            this.inv = inve;
            this.armor = armo;
            this.exp = ex;
            this.level = le;
        }
    }
    
    public void removePlayer(Player player){
        Arena a = null;
        for(Arena arena : arenas){
            String p = player.getName();
            if(arena.getPlayers().contains(p)){
                a = arena;
            }
        }
        if(a != null){
            if(a.getPlayers().contains(player.getName())){
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                
                player.setExp(0);
                player.setLevel(0);
                
                player.getInventory().setArmorContents(armor.get(player.getName()));
                player.getInventory().setContents(inv.get(player.getName()));
                
                player.setExp(exp.get(player.getName()));
                player.setLevel(level.get(player.getName()));
                
                this.deleteInventoryInConfig(player);
                
                player.setHealth(player.getMaxHealth());
                player.setFireTicks(0);
                player.setGameMode(GameMode.SURVIVAL);
                ScoreboardManager manager = Bukkit.getScoreboardManager(); 
                player.setScoreboard(manager.getNewScoreboard()); 
                player.teleport(a.getEndLocation());
                a.getPlayers().remove(player.getName());
                
                a.sendMessage(ChatColor.BOLD + "" + ChatColor.BLUE + player.getName() + ChatColor.GREEN + " ha salido de la arena! Quedan " + ChatColor.BLUE + a.getPlayers().size() + ChatColor.GREEN + "!");
                a.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Lucha hasta la muerte!...");
                a.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Suerte! :D");
            }else {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Que raro, no estas dentro de ninguna arena :/");
            }
        }else {
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "No existe esa arena, intenta con otra.");
        }
    }
    
    public void respawnPlayer(Player p, Arena arenaName){
        if(arenaName == null){
            p.sendMessage("Arena invalida!");
            return;
        }
        p.teleport(arenaName.getStartLocation());
        p.setGameMode(GameMode.CREATIVE);
        p.sendMessage("Has respawneado!, para salir pon /cp leave");
    }
    
    public boolean isInGame(Player p){
        for(Arena a : arenas){
            if(a.getPlayers().contains(p.getName())){
                return true;
            }
        }
        return false;
    }
    
    public Arena arenaInPlayer(Player p){
        for(Arena a : arenas){
            if(a.getPlayers().contains(p.getName())){
                return a;
            }
        }
        return null;
    }
    
    public void startArena(String arenaName){
        if(getArena(arenaName) != null){
            Arena arena = getArena(arenaName);
            arena.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Lucha hasta la muerte!...");
            arena.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Suerte! :D");
            
            arena.setInGame(true);
            
            for(String s : arena.getPlayers()){
                Bukkit.getPlayer(s).teleport(arena.getStartLocation());
                this.scoreboardGame(arenaName, Bukkit.getPlayer(s));
            }
        }
    }
    
    public void endArena(String arenaName){
        if(getArena(arenaName) != null){
            Arena arena = getArena(arenaName);
            arena.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "La arena ha finalizado!");
            arena.setInGame(false);
            for(String s : arena.getPlayers()){ 
                Player p = Bukkit.getPlayer(s);
                this.removePlayer(p); 
            }
        }
    }
    
    public void loadArenas(){
        FileConfiguration config = plugin.getConfig();
        for(String keys : config.getConfigurationSection("Arenas").getKeys(false)){
            World world = Bukkit.getWorld("Arenas." + keys + ".world");
            
            double joinX = config.getDouble("Arena." + keys + ".joinX");
            double joinY = config.getDouble("Arena." + keys + ".joinY");
            double joinZ = config.getDouble("Arena." + keys + ".joinZ");
            Location joinLocation = new Location(world, joinX, joinY, joinZ);
            
            double startX = config.getDouble("arenas." + keys + ".startX");
            double startY = config.getDouble("arenas." + keys + ".startY");
            double startZ = config.getDouble("arenas." + keys + ".startZ");
            Location startLocation = new Location(world, startX, startY, startZ);

            double endX = config.getDouble("arenas." + keys + ".endX");
            double endY = config.getDouble("arenas." + keys + ".endX");
            double endZ = config.getDouble("arenas." + keys + ".endX");
            Location endLocation = new Location(world, endX, endY, endZ);

            int maxPlayers = config.getInt("arenas." + keys + ".maxPlayers");
            
            Arena arena = new Arena(keys, joinLocation, startLocation, endLocation, maxPlayers);
            arenas.add(arena);
        }
    }
    
    public void createArena(String arenaName, Location joinLocation, Location startLocation, Location endLocation, int maxPlayers){
        Arena arena = new Arena(arenaName, joinLocation, startLocation, endLocation, maxPlayers);
        arenas.add(arena);
        
        FileConfiguration config;
        config = plugin.getConfig();
        File CreativoPVP = new File("plugins" + File.separator + "CreativoPVP" + File.separator + "config.yml");
        CreativoPVP.mkdir();
        
        if(!(CreativoPVP.exists())){
            plugin.saveResource("config.yml", true);
        }
        
        String path = "Arenas." + arenaName + ".";
        
        if(!config.contains("Arenas." + arenaName)){
            config.set(path + "joinX", joinLocation.getX());
            config.set(path + "joinY", joinLocation.getY());
            config.set(path + "joinZ", joinLocation.getZ());
            config.set(path + "world", joinLocation.getWorld().getName());
            
            config.set(path + "startX", startLocation.getX());
            config.set(path + "startY", startLocation.getY());
            config.set(path + "startZ", startLocation.getZ());
            config.set(path + "world", startLocation.getWorld().getName());
            
            config.set(path + "endX", endLocation.getX());
            config.set(path + "endY", endLocation.getY());
            config.set(path + "endZ", endLocation.getZ());
            config.set(path + "world", endLocation.getWorld().getName());
            
            config.set(path + "maxPlayers", maxPlayers);
        }
    }
    
    public void removeArena(String arenaName){
        if(this.getArena(arenaName) != null){
            FileConfiguration config = plugin.getConfig();
            Arena a = this.getArena(arenaName);
            arenas.remove(a);
            config.set("Arenas." + arenaName, null);
            List<Object> list = (List) config.get("Arenas");
            list.remove(arenaName);
            config.set("Arenas", list);
        }
    }
    
    public void scoreboardGame(String arenaName, Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = board.registerNewObjective("kills", "playerKillCount");
        Objective ob = board.registerNewObjective("healt", "health");
        Team team = board.registerNewTeam("CreativePVP");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.GREEN + "[*] --- CREATIVEPVP --- [*]");
        ob.setDisplaySlot(DisplaySlot.BELOW_NAME);
        ob.setDisplayName("/ 20");
        int size = this.getArena(arenaName).getPlayers().size();
        Score players = o.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Jugadores:" + ChatColor.BLUE + size));
        players.setScore(1);
        Score nameArena = o.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Arena:" + ChatColor.BLUE + arenaName));
        nameArena.setScore(0);

        team.addPlayer(p);
        
        p.setScoreboard(board);
    }
    
    public Map<String, ItemStack[]> getInvPlayer(){
        return this.inv;
    }
    
   public void setInvPlayer(Map<String, ItemStack[]> inv){
       this.inv = inv;
   }
}
