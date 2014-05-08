package es.hol.iberians.CPVP;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author jesus
 */

public class GameLsitener implements Listener{
    static List<String> players = new ArrayList<String>();
    static CPVP plugin;
    
    public GameLsitener(CPVP plugin){
        GameLsitener.plugin = plugin;
    }
    
    public void respawn(PlayerRespawnEvent e){
        if(ArenaManager.getManager().isInGame(e.getPlayer())){
            final Player p = e.getPlayer();
            final Arena arenaName = ArenaManager.getManager().arenaInPlayer(p);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                @Override
                public void run(){
                    ArenaManager.getManager().respawnPlayer(p, arenaName);
                }
            }, 20L);
        }
    }
    
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity().getPlayer();
        if(ArenaManager.getManager().isInGame(p)){
            e.getDrops().clear();
        }
    }
    
    public void BlockBreakEvent(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(ArenaManager.getManager().isInGame(p)){
            e.setCancelled(true);
        }
    }
    
    public void BlockPlaceEvent(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(ArenaManager.getManager().isInGame(p)){
            e.setCancelled(true);
        }
    }
    
     public void onLeavePlayerServer(PlayerQuitEvent e){
         Player p = e.getPlayer();
         if(ArenaManager.getManager().isInGame(p)){
             ArenaManager.getManager().removePlayer(p);
         }
     }
     
     public void onPlayerCommandBlock(PlayerCommandPreprocessEvent e){
         Player p = e.getPlayer();
        if(ArenaManager.getManager().isInGame(p)){
          if(p.isOp()){
              e.setCancelled(false);
          }else{
              if(!e.getMessage().contains("cp leave")){
                    p.sendMessage("No puedes usar comandos dentro de la arena, solo /cp leave");
                    e.setCancelled(true);
              }
          }
        }
     }
     
     public void damagePlayer(EntityDamageEvent e){
         if(e.getEntity() instanceof Player){
             Player p = (Player) e.getEntity();
             if(ArenaManager.getManager().isInGame(p)){
                 p.getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 1);
             }
         }
     }
     
        // AutoRespawn by Sothatsit
     @EventHandler
     public void onPlayerDeath2(final PlayerDeathEvent e){
            new BukkitRunnable(){
                @Override
		public void run(){
                    try{
			Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle").invoke(e.getEntity());
			Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

			Class< ? > EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

			Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
			minecraftServer.setAccessible(true);
			Object mcserver = minecraftServer.get(con);

			Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
			Method moveToWorld = playerlist.getClass().getMethod("moveToWorld" , EntityPlayer , int.class , boolean.class);
			moveToWorld.invoke(playerlist , nmsPlayer , 0 , false);
                    }catch (Exception ex){
			ex.printStackTrace();
                    }
                }
	}.runTaskLater(plugin , 2);
    } 
}
