/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * @author t7seven7t
 */
public class WorldListener implements Listener {
	private final SwornCritters plugin;
	
	public WorldListener(final SwornCritters plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkUnload(final ChunkUnloadEvent event) {
		for (final Entity entity : event.getChunk().getEntities()) {
			if (entity.getUniqueId().equals(plugin.getLegendaryEntityId())) {
				entity.remove();
				plugin.setLegendaryEntityId(null);
				plugin.setLastLegendaryDeathTime(System.currentTimeMillis());
				plugin.setLegendary(null);
			}
		}
	}
	
}
