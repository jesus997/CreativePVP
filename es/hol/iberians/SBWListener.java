package es.hol.iberians;


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class SBWListener implements Listener {
        final ArrayList<Player> cooldown = new ArrayList<>();
	private CPVP pl = null;
	public SBWListener(CPVP pl) {
		this.pl = pl;
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setCancelled(ArenaManager.getManager().isInGame(e.getPlayer()));
	}
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		e.setCancelled(ArenaManager.getManager().isInGame(e.getPlayer()));
	}
        
        @EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
            Player p = e.getEntity().getPlayer();
            if(ArenaManager.getManager().isInGame(p)){
                if(CPVP.cfg.getBoolean("usedb")){
                    new DatabaseMan().update("UPDATE `scores` SET `deaths`=`deaths`+1 WHERE `username`='" + p.getName() +"'");
                    if(e.getEntity().getKiller() instanceof Player){
                        new DatabaseMan().update("UPDATE `scores` SET `kills`=`kills`+1 WHERE `username`='" + e.getEntity().getKiller().getName() +"'");
                    }
                    int x = CPVP.cfg.getInt("coins-per-kills-min");
                    Random r = new Random();
                    if(e.getEntity().getKiller() instanceof Player){
                        new DatabaseMan().update("UPDATE `scores` SET `coins`=`coins`+" + r.nextInt(x) + " WHERE `username`='" + e.getEntity().getKiller().getName() +"'");
                    }
                    if(e.getEntity().getKiller() instanceof Player){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("get-coins").replaceAll("%n%", String.valueOf(r)), e.getEntity().getKiller());
                    }
                }else{
                    int kills = ArenaManager.getManager().killstemp.get(p.getName());
                    if(e.getEntity().getKiller() instanceof Player){
                        ArenaManager.getManager().killstemp.put(e.getEntity().getKiller().getName(), kills + 1);
                    }
                    int deaths = ArenaManager.getManager().deathstemp.get(p.getName());
                    ArenaManager.getManager().deathstemp.put(p.getName(), deaths + 1);
                    int coins = ArenaManager.getManager().coinstemp.get(p.getName());
                    Random r = new Random();
                    int x = CPVP.cfg.getInt("coins-per-kills-min");
                    ArenaManager.getManager().coinstemp.put(p.getName(), coins + r.nextInt(x));
                    if(e.getEntity().getKiller() instanceof Player){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("get-coins").replaceAll("%n%", String.valueOf(r)), e.getEntity().getKiller());
                    }
                }
                if(ArenaManager.getManager().isInGame(p)){
                    e.getDrops().clear();
                }
            }
        }
                
        @EventHandler        
        public void respawn(PlayerRespawnEvent e){
            if(ArenaManager.getManager().isInGame(e.getPlayer())){
                final Player p = e.getPlayer();
                final Arena a = ArenaManager.getManager().getArena(p);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
                    @Override
                    public void run(){
                        p.teleport(a.getRespawn().get(0)); 
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
                    }
                }, 2L);
            }
        }

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
             if(e.getEntity() instanceof Player){
             Player p = (Player) e.getEntity();
             if(ArenaManager.getManager().isInGame(p)){
                 if(!(p.getGameMode() == GameMode.CREATIVE)){
                     p.getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 1);
                 }
             }
         }
	}
	@EventHandler
	public void onFoodDecrease(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player)e.getEntity();
		if(ArenaManager.getManager().getArena(p) != null) {
			e.setCancelled(true);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLeavePaper(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getItem() != null) {
				if(e.getItem().getType() == Material.PAPER) {
                                    Player p = Bukkit.getPlayer(e.getPlayer().getName());
                                    ArenaManager.getManager().removePlayer(p);
                                    p.updateInventory();
				}
			}
		}
	}
	public ArrayList<String> getArenas() {
		File folder = new File(this.pl.getDataFolder() + "/arenas");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> s = new ArrayList<>();
		for(File f: listOfFiles) {
			s.add(f.getName());
		}
		return s;
	}
	@EventHandler
	public void onSignEdit(SignChangeEvent e) {
		if(!e.getPlayer().hasPermission("cp.admin")) 
			return;
		if(e.getLine(0).equalsIgnoreCase("[CPVP]") && e.getLine(1).equalsIgnoreCase("menu")) {
			e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&2&lC&4&lPVP"));
                        e.setLine(1, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-line-1"));
                        e.setLine(2, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-line-2"));
                        e.setLine(3, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-line-3"));
		}
	}
        
        @EventHandler
        public void onSignEdit2(SignChangeEvent e) {
            if(!e.getPlayer().hasPermission("cp.admin")){
                return;
            }
            if(e.getLine(0).equalsIgnoreCase("[CPVP]") && e.getLine(1).equalsIgnoreCase("life-user")) {
                e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&2&lLIFE"));
                e.setLine(1, ChatColor.GOLD + CPVP.cfg.getString("sing-life-line-1"));
                e.setLine(2, ChatColor.GOLD + CPVP.cfg.getString("sing-life-line-2"));
                e.setLine(3, ChatColor.GOLD + CPVP.cfg.getString("sing-life-line-3"));
            }
        }
        
        @EventHandler
        public void onSignEdit3(SignChangeEvent e) {
            if(!e.getPlayer().hasPermission("cp.admin")){
                return;
            }
            if(e.getLine(0).equalsIgnoreCase("[CPVP]") && e.getLine(1).equalsIgnoreCase("life-vip")) {
                e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&c&lLIFE-VIP"));
                e.setLine(1, ChatColor.GOLD + CPVP.cfg.getString("sing-life-vip-line-1"));
                e.setLine(2, ChatColor.GOLD + CPVP.cfg.getString("sing-life-vip-line-2"));
                e.setLine(3, ChatColor.GOLD + CPVP.cfg.getString("sing-life-vip-line-3"));
            }
        }
        
        @EventHandler
        public void onSignEdit4(SignChangeEvent e) {
            if(!e.getPlayer().hasPermission("cp.admin")){
                return;
            }
            if(e.getLine(0).equalsIgnoreCase("[CPVP]") && e.getLine(1).equalsIgnoreCase("exit")) {
                e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&2&lSALIDA"));
                e.setLine(1, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-exit-line-1"));
                e.setLine(2, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-exit-line-2"));
                e.setLine(3, ChatColor.DARK_GRAY + CPVP.cfg.getString("sing-exit-line-3"));
            }
        }     
        
	@EventHandler
        public void onPlayerCommandBlock(PlayerCommandPreprocessEvent e){
            Player p = e.getPlayer();
            if(ArenaManager.getManager().isInGame(p)){
                if(p.isOp()){
                    e.setCancelled(false);
                }else{
                    if(!e.getMessage().contains("cp leave") || !e.getMessage().contains("cp l") || !e.getMessage().contains("cp -")){
                        ArenaManager.getManager().sendMessage(CPVP.cfg.getString("no-msg-in-game"), p);
                        e.setCancelled(true);
                     }
                }
            }
        }
        
	@EventHandler
	public void onSignRightClick(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lC&4&lPVP"))) {
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
                                        menu.setOption(i2, new ItemStack(Material.NETHER_STAR, 1), "" + ArenaManager.getManager().getArenas().get(i2).getId(), "Click to join");
                                    }
                                    menu.open(e.getPlayer()); 
				}
			}
		}
	}
        
        @EventHandler
	public void onSignRightClick2(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lLIFE"))) {
                                    final Player p = e.getPlayer(); 
                                    if(p.hasPermission("cp.reglife")){
                                        if(ArenaManager.getManager().isInGame(p)){
                                            if(p.getHealth() == p.getMaxHealth()){
                                                ArenaManager.getManager().sendMessage("&4&lYa tienes el maximo de vida!", p);
                                                return;
                                            }
                                            double h = p.getHealth();
                                            double d = h+5;
                                            if(cooldown.contains(p)){
                                                ArenaManager.getManager().sendMessage("&4&lUsa este cartel en unos segundos mas.", p);
                                                return;
                                            }
                                            if(d>20){
                                                p.setHealth(20); 
                                            }else{
                                                p.setHealth(d);
                                            }
                                            cooldown.add(p);
                                            ArenaManager.getManager().sendMessage("&4&lVida Regenerada!, &cespera 1 minuto para volver a usar el cartel.", p);
                                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
                                                @Override 
                                                public void run(){
                                                    cooldown.remove(p);
                                                    ArenaManager.getManager().sendMessage("&2Cartel de vida activado!", p);
                                                }
                                            }, 1200L);                                            
                                        }
                                    }
                                }
                        }
                }
        }
             
        @EventHandler
	public void onSignRightClick3(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&c&lLIFE-VIP"))) {
                                    final Player p = e.getPlayer(); 
                                    if(p.hasPermission("cp.reglifevip")){
                                        if(ArenaManager.getManager().isInGame(p)){
                                            if(p.getHealth() == p.getMaxHealth()){
                                                ArenaManager.getManager().sendMessage("&4&lYa tienes el maximo de vida!", p);
                                                return;
                                            }
                                            double h = p.getHealth();
                                            double d = h + 10;
                                            if(cooldown.contains(p)){
                                                ArenaManager.getManager().sendMessage("&4&lUsa este cartel en unos segundos mas.", p);
                                                return;
                                            }
                                            if(d>20){
                                                p.setHealth(20); 
                                            }else{
                                                p.setHealth(d);
                                            }
                                            cooldown.add(p);
                                            ArenaManager.getManager().sendMessage("&4&lVida Regenerada!, &cespera 1 minuto para volver a usar el cartel.", p);
                                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
                                                @Override 
                                                public void run(){
                                                    cooldown.remove(p);
                                                    ArenaManager.getManager().sendMessage("&2Cartel de vida activado!", p);
                                                }
                                            }, 1200L);  
                                        }
                                    }
                                }
                        }
                }
        }    
        
        @EventHandler
	public void onSignRightClick4(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lSALIDA"))) {
                                    Player p = e.getPlayer();
                                    ArenaManager.getManager().removePlayer(p);
                                }
                        }
                }
        }
        
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		ArenaManager.getManager().removePlayer(e.getPlayer());
	}
	@EventHandler
	public void onLeave2(PlayerKickEvent e) {
		ArenaManager.getManager().removePlayer(e.getPlayer());
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(ArenaManager.getManager().getArena(e.getPlayer()) != null) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
            if(CPVP.cfg.getBoolean("usedb")) {
                    new DatabaseMan().update("INSERT INTO `scores` (`id`, `username`, `kills`, `deaths`, `coins`, `level`, `exp`) VALUES (NULL, '" + e.getPlayer().getName() + "', '0', '0', '0', '0', '0');");
            }else{
                ArenaManager.getManager().coinstemp.put(e.getPlayer().getName(), 0);
                ArenaManager.getManager().deathstemp.put(e.getPlayer().getName(), 0);
                ArenaManager.getManager().killstemp.put(e.getPlayer().getName(), 0);
            }
	}
        
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
	}.runTaskLater(pl , 2);
    } 
        
	@EventHandler
	public void onEggLand(ProjectileHitEvent e) {
            if(CPVP.cfg.getBoolean("surprise-enabled-in-projectile") == true){
                if(e.getEntity().getShooter() instanceof Player) {
			Player s = (Player)e.getEntity().getShooter();
                        if(ArenaManager.getManager().getArena(s) != null) {
                            FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
                            try {
                                    fplayer.playFirework(e.getEntity().getWorld(), e.getEntity().getLocation(), getRandom());
                            } catch (Exception e1) {
                                    e1.printStackTrace();
                            }
                    }
		}
            }
	}
        
        @EventHandler
        public void onEggLaunch(PlayerEggThrowEvent e){
            Player p = e.getPlayer();
            if(ArenaManager.getManager().isInGame(p)){
                e.setHatching(false);
            }
        }
        
        /*@EventHandler //Fix bug in a future
        public void onProjectileHit(ProjectileHitEvent e){
            Projectile proj = (Projectile) e.getEntity();
            Entity possibleTarget = proj.getNearbyEntities(1, 1, 1).get(0);
            if(possibleTarget instanceof Player){
                Player target = (Player) possibleTarget;
                if(ArenaManager.getManager().isInGame(target)){
                    target.damage(1); 
                }
            }
        }*/

	public FireworkEffect getRandom() {
		Random r = new Random();
		Type t = null;
		switch(r.nextInt(4)) {
		case 0:
			t = Type.BALL;
			break;
		case 1:
			t = Type.BALL_LARGE;
			break;
		case 2:
			t = Type.BURST;
			break;
		case 3:
			t = Type.CREEPER;
			break;
		case 4:
			t = Type.STAR;
			break;
		}
		// Blue, Orange
		// Yellow, Green
		// Yellow, Purple
		// Fuchisa, Silver
		// Black, White
		Color w1 = null;
		Color w2 = null;
		switch(r.nextInt(4)) {
		case 0:
			w1 = Color.BLUE;
			w2 = Color.ORANGE;
			break;
		case 1:
			w1 = Color.YELLOW;
			w2 = Color.GREEN;
			break;
		case 2:
			w1 = Color.YELLOW;
			w2 = Color.PURPLE;
			break;
		case 3:
			w1 = Color.FUCHSIA;
			w2 = Color.SILVER;
			break;
		case 4:
			w1 = Color.BLACK;
			w2 = Color.WHITE;
			break;
		}
		Color w3 = null;
		Color w4 = null;
		switch(r.nextInt(4)) {
		case 0:
			w3 = Color.BLUE;
			w4 = Color.ORANGE;
			break;
		case 1:
			w3 = Color.YELLOW;
			w4 = Color.GREEN;
			break;
		case 2:
			w3 = Color.YELLOW;
			w4 = Color.PURPLE;
			break;
		case 3:
			w3 = Color.FUCHSIA;
			w4 = Color.SILVER;
			break;
		case 4:
			w3 = Color.BLACK;
			w4 = Color.WHITE;
			break;
		}
 		return FireworkEffect.builder().with(t).withColor(w1).withColor(w2).withFade(w3).withFade(w4).build();
	}


}
