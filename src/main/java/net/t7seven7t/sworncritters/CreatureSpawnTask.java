/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;

/**
 * @author t7seven7t
 */
public class CreatureSpawnTask extends BukkitRunnable {
	private final static int MAX_DISTANCE_FROM_PLAYER = 50;
	private final SwornCritters plugin;
	private final Random random;
	private final boolean factionsEnabled;
	private final int spawnChance;
	private final int monsterCapPerPlayer;
	private final int animalCapPerPlayer;
	
	private final Map<Environment, List<EntityType>> passiveEntities;
	private final Map<Environment, List<EntityType>> hostileEntities;
	
	public CreatureSpawnTask(final SwornCritters plugin) {
		this.plugin = plugin;
		this.random = new Random();
		
		this.factionsEnabled = 	plugin.getServer().getPluginManager().isPluginEnabled("Factions") 
								|| plugin.getServer().getPluginManager().isPluginEnabled("SwornNations");
		
		this.spawnChance = plugin.getConfig().getInt("spawnChance");
		this.monsterCapPerPlayer = plugin.getConfig().getInt("monsterCapPerPlayer");
		this.animalCapPerPlayer = plugin.getConfig().getInt("animalCapPerPlayer");
		
		passiveEntities = new HashMap<Environment, List<EntityType>>();
		hostileEntities = new HashMap<Environment, List<EntityType>>();
		
		// Get entity types from config.
		passiveEntities.put(Environment.NORMAL, getEntityListFromConfig("passiveNormal"));
		passiveEntities.put(Environment.THE_END, getEntityListFromConfig("passiveEnd"));
		passiveEntities.put(Environment.NETHER, getEntityListFromConfig("passiveNether"));
		hostileEntities.put(Environment.NORMAL, getEntityListFromConfig("hostileNormal"));
		hostileEntities.put(Environment.THE_END, getEntityListFromConfig("hostileEnd"));
		hostileEntities.put(Environment.NETHER, getEntityListFromConfig("hostileNether"));
	}

	public void run() {
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (!factionsEnabled || Board.getFactionAt(new FLocation(player.getLocation())).isNone()) {
				if (random.nextInt(100) < spawnChance) {
					final int nearbyEntityCount = getNearbyEntityCount(player, 32);

					// If day time only spawn passive mobs.
					if (player.getWorld().getTime() <= 13500) {
						if (nearbyEntityCount <= animalCapPerPlayer) {
							final EntityType type = getRandomEntityType(passiveEntities.get(player.getWorld().getEnvironment()));
							final int xOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
							final int zOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
							final Location spawnLocation = player.getLocation().add(xOffset, 0, zOffset);
							
							// Make sure entity doesn't spawn inside of blocks or floating in the air
							while (	spawnLocation.add(0, 1, 0).getBlockY() < 250 
									&& player.getWorld().getBlockAt(spawnLocation).getType().isSolid() 
									&& player.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid());
							while ( spawnLocation.subtract(0, 1, 0).getBlockY() > 2
									&& player.getWorld().getBlockAt(spawnLocation).getType() == Material.AIR);
							
							player.getWorld().spawn(spawnLocation.add(0, 1, 0), type.getEntityClass());
						}
					} else {
						if (nearbyEntityCount <= monsterCapPerPlayer && random.nextInt(3) == 0) {
							final EntityType type = getRandomEntityType(hostileEntities.get(player.getWorld().getEnvironment()));
							final int xOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
							final int zOffset = random.nextInt(MAX_DISTANCE_FROM_PLAYER * 2) - MAX_DISTANCE_FROM_PLAYER;
							final Location spawnLocation = player.getLocation().add(xOffset, 0, zOffset);
							
							// Make sure entity doesn't spawn inside of blocks or floating in the air
							while (	spawnLocation.add(0, 1, 0).getBlockY() < 250 
									&& player.getWorld().getBlockAt(spawnLocation).getType().isSolid() 
									&& player.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid());
							while ( spawnLocation.subtract(0, 1, 0).getBlockY() > 2
									&& player.getWorld().getBlockAt(spawnLocation).getType() == Material.AIR);
							
							final LivingEntity monster = (LivingEntity) player.getWorld().spawn(spawnLocation.add(0, 1, 0), type.getEntityClass());
							
							// Attempt to randomly make some skeletons wither skeletons
							if (monster instanceof Skeleton) {
								((Skeleton) monster).setSkeletonType(SkeletonType.WITHER);
							}
						}
					}
				}
			}
		}
	}
	
	private int getNearbyEntityCount(final Entity entity, final int radius) {
		return entity.getNearbyEntities(radius, radius, radius).size();
	}
	
	private EntityType getRandomEntityType(final List<EntityType> list) {
		return list.get(new Random().nextInt(list.size()));
	}
	
	private List<EntityType> getEntityListFromConfig(final String path) {
		final List<EntityType> ret = new ArrayList<EntityType>();
		for (String string : plugin.getConfig().getStringList(path))
			ret.add(EntityType.valueOf(string));
		return ret;
	}
	
}
