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
		// Do nothing :P
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
				event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(plugin.getConfig().getInt("slimeDropsType"), plugin.getConfig().getInt("slimeDropsAmount")));
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
			event.getDrops().clear();
			
			if (event.getEntityType().equals(EntityType.GIANT)) {
				event.setDroppedExp(plugin.getConfig().getInt("giantDropsXP"));
				event.getDrops().add(new ItemStack(plugin.getConfig().getInt("giantDropsType"), plugin.getConfig().getInt("giantDropsAmount")));
			} else if (event.getEntityType().equals(EntityType.WITHER)) {
				event.setDroppedExp(plugin.getConfig().getInt("witherDropsXP"));
				event.getDrops().add(new ItemStack(plugin.getConfig().getInt("witherDropsType"), plugin.getConfig().getInt("witherDropsAmount")));
			} else if (event.getEntityType().equals(EntityType.SLIME)) {
				event.setDroppedExp(plugin.getConfig().getInt("slimeDropsXP"));
				event.getDrops().add(new ItemStack(plugin.getConfig().getInt("slimeDropsType"), plugin.getConfig().getInt("slimeDropsAmount")));
			} 
			
			creature.remove();
			plugin.setLegendaryEntityId(null);
			plugin.setLastLegendaryDeathTime(System.currentTimeMillis());
			plugin.setLegendary(null);
		}
	}
	
}
