package es.hol.iberians.CPVP;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author jesus
 */

public class Arena {
    public static ArrayList<Arena> arenaObjects = new ArrayList<Arena>();
    private Location joinLocation, startLocation, endLocation;
    private String name;
    private ArrayList<String> players = new ArrayList<String>();
    private int maxPlayers;
    private boolean inGame = false;
    
    public Arena(String arenaName, Location joinLocation, Location startLocation, Location endLocation, int maxPlayers){
        this.name = arenaName;
        this.joinLocation = joinLocation;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.maxPlayers = maxPlayers;
        
        arenaObjects.add(this);
    }
    
    public Location getJoinLocation(){
        return this.joinLocation;
    }
    
    public void setJoinLocation(Location joinLocation){
        this.joinLocation = joinLocation;
    }
    
    public Location getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return this.endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public ArrayList<String> getPlayers() {
        return this.players;
    }
    
    public boolean isFull() { 
        if (players.size() >= maxPlayers) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void sendMessage(String message) {
        for (String s: players) {
            Bukkit.getPlayer(s).sendMessage(message);
        }
    }

}
