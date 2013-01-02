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
	private final int minutesBetweenLegendaries;
	private final int slimeSpawnChance;
	private final int giantSpawnChance;
	private final int witherSpawnChance;
	private final int giantMaxHealth;
	private final int slimeMaxHealth;
	private final int witherMaxHealth;
	
	public LegendarySpawnTask(final SwornCritters plugin) {
		this.plugin = plugin;
		this.random = new Random();
		this.legendariesNotify = plugin.getConfig().getBoolean("legendariesNotify");
		
		this.factionsEnabled = 	plugin.getServer().getPluginManager().isPluginEnabled("Factions") 
				|| plugin.getServer().getPluginManager().isPluginEnabled("SwornNations");
		
		this.minutesBetweenLegendaries = plugin.getConfig().getInt("minutesBetweenLegendaries");
		this.slimeSpawnChance = plugin.getConfig().getInt("slimeSpawnChance");
		this.giantSpawnChance = plugin.getConfig().getInt("giantSpawnChance");
		this.witherSpawnChance = plugin.getConfig().getInt("witherSpawnChance");
		this.slimeMaxHealth = plugin.getConfig().getInt("slimeMaxHealth");
		this.giantMaxHealth = plugin.getConfig().getInt("giantMaxHealth");
		this.witherMaxHealth = plugin.getConfig().getInt("witherMaxHealth");
	}
	
	@Override
	public void run() {
		if (	plugin.getLegendaryEntityId() == null 
				&& System.currentTimeMillis() - plugin.getLastLegendaryDeathTime() > minutesBetweenLegendaries * 60 * 1000L
				&& plugin.getServer().getOnlinePlayers().length > 0) {
			Player player = plugin.getServer().getOnlinePlayers()[random.nextInt(plugin.getServer().getOnlinePlayers().length)];
			
			if (!factionsEnabled || Board.getFactionAt(new FLocation(player.getLocation())).isNone()) {
				EntityType type = LegendaryMonsterType.getRandom();
				LivingEntity legendary;
				
				double dRand = random.nextDouble();
				
				if (type.equals(EntityType.GIANT) && (1.0D / giantSpawnChance) > dRand) {
					legendary = spawn(type, player.getLocation());
					legendary.setMaxHealth(giantMaxHealth);
				} else if (type.equals(EntityType.SLIME) && (1.0D / slimeSpawnChance) > dRand) {
					legendary = spawn(type, player.getLocation());
					legendary.setMaxHealth(slimeMaxHealth);
					((Slime) legendary).setSize(25);
				} else if (type.equals(EntityType.WITHER) && (1.0D / witherSpawnChance) > dRand) {
					legendary = spawn(type, player.getLocation());
					legendary.setMaxHealth(witherMaxHealth);
				} else {
					return;
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
	
	private LivingEntity spawn(EntityType type, Location location) {
		final int xOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
		final int zOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
		final Location spawnLocation = location.add(xOffset, 0, zOffset);
		
		// Make sure entity doesn't spawn inside of blocks or floating in the air
		while (	spawnLocation.add(0, 1, 0).getBlockY() < 250 
				&& location.getWorld().getBlockAt(spawnLocation).getType().isSolid() 
				&& location.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid());
		while ( spawnLocation.subtract(0, 1, 0).getBlockY() > 2
				&& location.getWorld().getBlockAt(spawnLocation).getType() == Material.AIR);
		
		return (LivingEntity) location.getWorld().spawn(spawnLocation.add(0, 1, 0), type.getEntityClass());
	}

}
