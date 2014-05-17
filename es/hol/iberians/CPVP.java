package es.hol.iberians;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CPVP extends JavaPlugin{
    public ArrayList<String> isRun = new ArrayList<>();
    public static FileConfiguration cfg;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
   public void onEnable(){
        this.saveDefaultConfig();
        cfg = this.getConfig();
        if(this.getConfig().getBoolean("usedb") == true) {
		DatabaseMan.host = this.getConfig().getString("host");
		DatabaseMan.port = this.getConfig().getInt("port");
		DatabaseMan.user = this.getConfig().getString("user");
		DatabaseMan.pass = this.getConfig().getString("pass");
		DatabaseMan.db = this.getConfig().getString("name");
		DatabaseMan.connect();
                new DatabaseMan().update("CREATE TABLE IF NOT EXISTS `scores` (" + "`id` int(11) NOT NULL AUTO_INCREMENT," + "`username` varchar(32) NOT NULL," + "`kills` int(11) NOT NULL," + "`deaths` int(11) NOT NULL," + "`coins` int(11) NOT NULL, " + "`level` int(11) NOT NULL, " + "`exp` int(11) NOT NULL, " + "PRIMARY KEY (`id`)" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
	}
        this.getServer().getPluginManager().registerEvents(new SBWListener(this), this);
        
        //start lobbytimer
        new LobbyTimer(this).start();
        new GameTick(this).start();
        
        this.getCommand("cpa").setExecutor(new CommandAdmin(this));
        this.getCommand("cp").setExecutor(new Command());
        
        File folder = new File(this.getDataFolder() + "/arenas");
        if(!folder.exists()){
            folder.mkdir();
        }
        File[] listOfFiles = folder.listFiles();
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[*]--------------[ CPVP ]--------------[*]");
        for(File f : listOfFiles){
            loadFromFiles(f.getName());
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "Cargando datos de la arena: " + ChatColor.YELLOW + f.getName());
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[*]------------------------------------[*]");
    }
    @Override
    public void onDisable(){
        this.saveDefaultConfig();
    }
    
    public void loadFromFiles(String name){
        File f = new File(this.getDataFolder() + "/arenas/" + name);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        ArrayList<Location> locs = new ArrayList<>();
        ArrayList<Location> locs2 = new ArrayList<>();
        for(String s : c.getStringList("spawns")){
            locs.add(ArenaManager.getManager().deserializeLoc(s));
        }
        for(String s : c.getStringList("respawn")){
            locs2.add(ArenaManager.getManager().deserializeLoc(s));
        }
        ArenaManager.getManager().createArena(locs, locs2);
    }
    
    public void saveToFile(Arena a, List<Location> respawn){
        File f = new File(this.getDataFolder() + "/arenas/arena" + a.getId() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        List<String> wee = new ArrayList<>();
        List<String> weee  = new ArrayList<>();
        for(Location l: a.getSpawn()) {
                wee.add(ArenaManager.getManager().serializeLoc(l));
        }
        for(Location l : a.getRespawn()){
            weee.add(ArenaManager.getManager().serializeLoc(l));
        }
        c.set("spawns", wee);
        c.set("respawn", weee);
        try {
                c.save(f);
        } catch (IOException e) {
                // do you even catch bro?
        }
    }
    
    public void reloadConfigYML(){
        this.reloadConfig();
    }
}
