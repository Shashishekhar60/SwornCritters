/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;

/**
 * @author t7seven7t
 */
public class LegendarySpawnTask extends BukkitRunnable {
	private final static int MAX_DISTANCE_FROM_PLAYER = 50;
	private final SwornCritters plugin;
	private final Random random;
	private final boolean legendariesNotify;
	private final boolean factionsEnabled;
	
	public LegendarySpawnTask(final SwornCritters plugin) {
		this.plugin = plugin;
		this.random = new Random();
		this.legendariesNotify = plugin.getConfig().getBoolean("legendariesNotify");
		
		this.factionsEnabled = 	plugin.getServer().getPluginManager().isPluginEnabled("Factions") 
				|| plugin.getServer().getPluginManager().isPluginEnabled("SwornNations");
	}
	
	@Override
	public void run() {
		if (	plugin.getLegendaryEntityId() == null 
				&& System.currentTimeMillis() - plugin.getLastLegendaryDeathTime() > 30000L
				&& plugin.getServer().getOnlinePlayers().length > 0) {
			Player player = plugin.getServer().getOnlinePlayers()[random.nextInt(plugin.getServer().getOnlinePlayers().length)];
			
			if (!factionsEnabled || Board.getFactionAt(new FLocation(player.getLocation())).isNone()) {
				EntityType type = LegendaryMonsterType.getRandom();
				final int xOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
				final int zOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
				final Location spawnLocation = player.getLocation().add(xOffset, 0, zOffset);
				
				// Make sure entity doesn't spawn inside of blocks or floating in the air
				while (	player.getWorld().getBlockAt(spawnLocation.add(0, 1, 0)).getType().isSolid() 
						&& player.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid());
				while (player.getWorld().getBlockAt(spawnLocation.subtract(0, 1, 0)).getType() == Material.AIR);
				
				LivingEntity legendary = (LivingEntity) player.getWorld().spawn(spawnLocation.add(0, 1, 0), type.getEntityClass());
				if (type.equals(EntityType.GIANT)) {
					legendary.setMaxHealth(1000);
				} else if (type.equals(EntityType.SLIME)) {
					legendary.setMaxHealth(1200);
					((Slime) legendary).setSize(25);
				} else if (type.equals(EntityType.WITHER)) {
					legendary.setMaxHealth(300);
				}
				
				legendary.setHealth(legendary.getMaxHealth());
				plugin.setLegendaryEntityId(legendary.getUniqueId());
				plugin.setLegendary(legendary);
				
				if (legendariesNotify)
					for (Player p : plugin.getServer().getOnlinePlayers())
						p.sendMessage(ChatColor.GREEN + "[SwornCreatures] A legendary " + type.toString().toLowerCase() + " has spawned!");
			}
		} else {
			if (plugin.getLegendary() != null) {
				if (!plugin.getLegendary().isValid()) {
					plugin.getLegendary().remove();
					plugin.setLegendaryEntityId(null);
					plugin.setLastLegendaryDeathTime(System.currentTimeMillis());
					plugin.setLegendary(null);
				}
			}
		}
	}

}
