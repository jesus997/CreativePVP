package es.hol.iberians;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;

public class Arena {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int id = 0;
    private List<Location> spawns = new ArrayList<>();
    private List<Location> respawn = new ArrayList<>();
    private int maxPlayers = 10;
    private List<String> players = new ArrayList<>();
    private HashMap<String, Location> usedSpawns = new HashMap<>();
    private int state = 0;
    private HashMap<String, Integer> lives = new HashMap<>();
    private ScoreboardAPI sbapi = new ScoreboardAPI();
    protected boolean gameOver;

    public Arena(List<Location> list, int id, List<Location> respawn) {
        this.spawns = list;
        this.id = id;
        this.respawn = respawn;
    }

    public int getState() {
        return this.state;
    }
    public void nextState() {
        this.state++;
    }
    public void resetState() {
        this.state = 0;
    }

    public Integer getLives(String p) {
        if(this.lives.get(p) != null){
            return this.lives.get(p);
        }
        return null;
    }
    public void setLives(String p, int l) {
        this.lives.remove(p);
        this.lives.put(p, l);
    }
    public void addLives(String p, int l) {
        this.lives.put(p, l);
    }
    public int getId() {
        return this.id;
    }
    public HashMap<String, Location> getUsedSpawns() {
        return this.usedSpawns;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public void setId(int i) {
        this.id = i;
    }
    public List<Location> getSpawn() {
        return spawns;
    }

    public Location getNextSpawn() {
        for(Location l: this.spawns) {
            if(!usedSpawns.containsValue(l)) {
                    return l;
            } else {
                    continue;
            }
        }
        System.out.print("NO SPAWN?? :(");
        return spawns.get(0);
    }
    
    public List<Location> getRespawn(){
        return respawn;
    }
    
    public int getMaxPlayers(){
        return this.maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    public ScoreboardAPI getSBApi() {
        return this.sbapi;
    }

    public void removeLives(String name) {
        this.lives.remove(name);
    }
}
