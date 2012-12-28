/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author t7seven7t
 */
public class SwornCritters extends JavaPlugin {
	private int creatureSpawnTaskId;
	private int legendarySpawnTaskId;
	private UUID legendaryEntityId;
	private long lastLegendaryDeathTime;
	private Entity legendary;
	
	public void onEnable() {
		clearEntities();
		
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		saveDefaultConfig();
		reloadConfig();
		
		creatureSpawnTaskId = getServer().getScheduler()
				.runTaskTimer(	this, 
								new CreatureSpawnTask(this), 
								20L, 
								getConfig().getLong("spawnTicks")).getTaskId();
		
		if (getConfig().getBoolean("legendariesEnabled")) {
			legendarySpawnTaskId = getServer().getScheduler()
					.runTaskTimer(	this,
									new LegendarySpawnTask(this),
									20L,
									600L).getTaskId();				
		}
		
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		getServer().getPluginManager().registerEvents(new WorldListener(this), this);
	}
	
	public void onDisable() {
		getServer().getScheduler().cancelTask(creatureSpawnTaskId);
		getServer().getScheduler().cancelTask(legendarySpawnTaskId);
	}
	
	public UUID getLegendaryEntityId() {
		return legendaryEntityId;
	}
	
	public void setLegendaryEntityId(UUID id) {
		legendaryEntityId = id;
	}
	
	public long getLastLegendaryDeathTime() {
		return lastLegendaryDeathTime;
	}
	
	public void setLastLegendaryDeathTime(long time) {
		lastLegendaryDeathTime = time;
	}
	
	public Entity getLegendary() {
		return legendary;
	}
	
	public void setLegendary(Entity e) {
		legendary = e;
	}
	
	/**
	 * Used on startup to remove all legendary monsters in the world
	 */
	private void clearEntities() {
		for (World world : getServer().getWorlds()) {
			for (Entity entity : world.getEntities()) {
				for (LegendaryMonsterType type : LegendaryMonsterType.values()) {
					if (type.getType().equals(entity.getType()))
						entity.remove();
				}
			}
		}
	}
	
}
