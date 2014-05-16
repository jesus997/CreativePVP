package es.hol.iberians;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class ArenaManager {
    private static CPVP pl = null;
    private static ArenaManager a = new ArenaManager(pl);
    private List<Arena> arenas = new ArrayList<>();
    private int arenaSize = 0;
    private HashMap<String, ItemStack[]> inventory = new HashMap<>();
    private HashMap<String, ItemStack[]> armor = new HashMap<>();
    private HashMap<String, Location> locs = new HashMap<>();
    private String prefix = CPVP.cfg.getString("prefix");
    public HashMap<String, Integer> killstemp = new HashMap<>();
    public HashMap<String, Integer> deathstemp = new HashMap<>();
    public HashMap<String, Integer> coinstemp = new HashMap<>();
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, Integer> kills = new HashMap<>();
    public HashMap<String, Integer> deaths = new HashMap<>();

    public ArenaManager(CPVP pl) {
        ArenaManager.pl = pl;
    }

    public static ArenaManager getManager() {
        return a;
    }

    public Arena getArena(int i) {
        for(Arena a: arenas) {
                if(a.getId() == i) {
                        return a;
                }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void addPlayer(Player p, int i) {
        Arena a = getArena(i);
        if(a == null) {
            this.sendMessage(CPVP.cfg.getString("arena-not-found"), p);
            return; 
        }
        if(a.getPlayers().contains(p.getName())) {
                return; 
        }
        this.sendMessage(CPVP.cfg.getString("your-has-joined-the-arena"), p);
        for(String str: a.getPlayers()) {
                this.sendMessage(CPVP.cfg.getString("player-has-joined-the-arena"), Bukkit.getPlayerExact(str));
        }
        inventory.put(p.getName(), p.getInventory().getContents());
        armor.put(p.getName(), p.getInventory().getArmorContents());
        locs.put(p.getName(), p.getLocation());
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.updateInventory();
        a.getPlayers().add(p.getName());
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.teleport(a.getNextSpawn());
        a.getUsedSpawns().put(p.getName(), a.getNextSpawn());
        p.setGameMode(GameMode.CREATIVE);
        if(CPVP.cfg.getBoolean("enabled-item-exit") == true){
            ItemStack item = new ItemStack(Material.PAPER, 1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName(ChatColor.translateAlternateColorCodes('&', CPVP.cfg.getString("leave-Arena-item-title")));
            List<String> lore = new ArrayList<>();
            lore.add(CPVP.cfg.getString("clic-to-left-arena"));
            item.setItemMeta(m);
            p.getInventory().setItem(8, item);
        }
        p.setScoreboard(a.getSBApi().getBoard("arena" + a.getId()));
    }

    @SuppressWarnings("deprecation")
    public void removePlayer(Player p) {
        if(getArena(p) == null){
            return;
        }
        Arena a = getArena(p);
        a.getUsedSpawns().remove(p.getName());
        a.getPlayers().remove(p.getName());
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.updateInventory();
        p.getInventory().setContents(inventory.get(p.getName()));
        inventory.remove(p.getName());
        p.getInventory().setArmorContents(armor.get(p.getName()));
        armor.remove(p.getName());
        p.updateInventory();
        p.setExp(0);
        p.setLevel(0);
        p.teleport(locs.get(p.getName()));
        p.setGameMode(GameMode.SURVIVAL);
        locs.remove(p.getName());
        this.sendMessage(CPVP.cfg.getString("your-has-left-the-arena"), p);
        for(String str: a.getPlayers()) {
                this.sendMessage(CPVP.cfg.getString("player-has-left-the-arena"), Bukkit.getPlayerExact(str));
        }
    }

    public List<Arena> getArenas() {
        return this.arenas;
    } 

    public Arena createArena(List<Location> list, List<Location> respawn){
        int num = arenaSize + 1;
        arenaSize++;
        Arena a = new Arena(list, num, respawn);
        arenas.add(a);

        return a;
    }

    public Arena getArena(Player p) {
        for(Arena a: arenas) {
                if(a.getPlayers().contains(p.getName()))
                        return a;
        }
        return null;
    }

    public boolean isInGame(Player p){
        for(Arena a : arenas){
            if(a.getPlayers().contains(p.getName()))
                return true;
        }
        return false;
    }
 
    public String serializeLoc(Location l){
        return l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
    }
    public Location deserializeLoc(String s){
        String[] st = s.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
    }
    
    public void sendMessage(String msg, Player p){
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', msg).replace("%player%", p.getName()));
    }
    
    public void sendMessageHelp(Player p){
        ArenaManager.getManager().sendMessage("&f[]&c ---------- &f[ &6CREATIVE PVP &f] &c---------- &f[]", p);
        ArenaManager.getManager().sendMessage("&8/cp join [id] &f-> " + CPVP.cfg.getString("command-join-arena"), p);
        ArenaManager.getManager().sendMessage("&8/cp leave &f-> " + CPVP.cfg.getString("command-leave-arena"), p);
        //ArenaManager.getManager().sendMessage("&8/cp list &f-> " + CPVP.cfg.get("command-list-arenas"), p);
        if(p.isOp()){
            ArenaManager.getManager().sendMessage("&8/cpa create&f-> " + CPVP.cfg.getString("command-create-arena"), p); 
            ArenaManager.getManager().sendMessage("&8/cpa addspawn&f-> " + CPVP.cfg.getString("command-addspawn-arena"), p);
            ArenaManager.getManager().sendMessage("&8/cpa addrespawn&f-> " + CPVP.cfg.getString("command-addrespawn-arena"), p);
            ArenaManager.getManager().sendMessage("&8/cpa cancel&f-> " + CPVP.cfg.getString("command-cancel-srena"), p);
            ArenaManager.getManager().sendMessage("&8/cpa save&f-> " + CPVP.cfg.getString("command-save-arena"), p);
            ArenaManager.getManager().sendMessage("&8/cpa remove [id] &f-> " + CPVP.cfg.getString("command-remove-arena"), p);
            ArenaManager.getManager().sendMessage("&8/cpa reload &f-> " + CPVP.cfg.getString("command-reload-plugin"), p);
        }
        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("foot-command-help"), p);
    }
    
    public void showIconMenu(Player p){
        Double d = (double) this.getArenas().size();
        d = d / 9;
        d = Math.ceil(d);
        if(d == 0)
                d++;
        d = d * 9;
         IconMenu menu = new IconMenu(ChatColor.GOLD + "" + ChatColor.BOLD + "Arenas", d.intValue(), new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                if(event.getName().equalsIgnoreCase("No arenas!")) {
                        event.setWillClose(true);
                        return;
                }
                ArenaManager.getManager().addPlayer(event.getPlayer(), Integer.valueOf(event.getName()));
                event.setWillClose(true);
            }
        }, this.pl);
        if(ArenaManager.getManager().getArenas().isEmpty()) {
            menu.setOption(0, new ItemStack(Material.BEDROCK, 1), "No arenas!", "This server hasn't setup any arenas yet!");
        }
        for(int i2 = 0; i2 < ArenaManager.getManager().getArenas().size(); i2++) {
            menu.setOption(i2, new ItemStack(Material.BEDROCK, 1), "" + ArenaManager.getManager().getArenas().get(i2).getId(), "Click to join");
        }
        menu.open(p);
    }
    
    public void setCoins(Arena a){
        for(String p : a.getPlayers()){
            this.coins.put(p, new DatabaseMan().update("SELECT `coins` FROM `scores` WHERE `username`='" + p +"'"));
        }
    }
    
    public HashMap getCoins(){
        return this.coins;
    }
    
    public void setKills(Arena a){
        for(String p : a.getPlayers()){
            this.kills.put(p, new DatabaseMan().update("SELECT `kills` FROM `scores` WHERE `username`='" + p +"'"));
        }
    }
    
    public HashMap getKills(){
        return this.kills;
    }
    
    public void setDeaths(Arena a){
        for(String p : a.getPlayers()){
            this.deaths.put(p, new DatabaseMan().update("SELECT `deaths` FROM `scores` WHERE `username`='" + p +"'"));
        }
    }
    
    public HashMap getDeaths(){
        return this.deaths;
    }
}
