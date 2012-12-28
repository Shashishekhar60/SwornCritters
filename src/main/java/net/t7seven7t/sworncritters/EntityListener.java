/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 */
public class EntityListener implements Listener {
	private final SwornCritters plugin;
	
	public EntityListener(final SwornCritters plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		// Allow vanilla squid spawning, block all other natural spawns
		if (event.getEntityType() != EntityType.SQUID 
				&& event.getEntityType() != EntityType.COW 
				&& event.getEntityType() != EntityType.CHICKEN 
				&& event.getEntityType() != EntityType.PIG
				&& event.getEntityType() != EntityType.SHEEP) {
			if (event.getSpawnReason() == SpawnReason.NATURAL || event.getSpawnReason() == SpawnReason.DEFAULT) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityCombust(final EntityCombustEvent event) {
		// Stop skeletons and zombies from burning in sun
		if (event.getEntityType().equals(EntityType.SKELETON) || event.getEntityType().equals(EntityType.ZOMBIE))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onSlimeSplit(final SlimeSplitEvent event) {
		final Entity slime = event.getEntity();
		
		if (plugin.getLegendaryEntityId() != null && plugin.getLegendaryEntityId().equals(slime.getUniqueId())) {
			event.setCount(0);
			if (event.getEntity().getKiller() != null) {
				event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(266, 10));
				event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "You have killed a legendary monster!");
			}
			slime.remove();
			plugin.setLegendaryEntityId(null);
			plugin.setLastLegendaryDeathTime(System.currentTimeMillis());
			plugin.setLegendary(null);
		}
	}
	
	@EventHandler
	public void onCreatureDeath(final EntityDeathEvent event) {
		final Entity creature = event.getEntity();
		if (plugin.getLegendaryEntityId() != null && plugin.getLegendaryEntityId().equals(creature.getUniqueId())) {
			if (event.getEntityType().equals(EntityType.GIANT) || event.getEntityType().equals(EntityType.WITHER)) {
				event.setDroppedExp(100);
			} else if (event.getEntityType().equals(EntityType.SLIME)) {
				event.setDroppedExp(300);
			} 
			
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(266, 10));
			creature.remove();
			plugin.setLegendaryEntityId(null);
			plugin.setLastLegendaryDeathTime(System.currentTimeMillis());
			plugin.setLegendary(null);
		}
	}
	
}
