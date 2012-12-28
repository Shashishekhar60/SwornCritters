/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.Random;

import org.bukkit.entity.EntityType;

/**
 * @author t7seven7t
 */
public enum AnimalType {
	BAT, COW, CHICKEN, IRON_GOLEM, MUSHROOM_COW, OCELOT, PIG, SHEEP, VILLAGER, WOLF;
	
	private final EntityType type;
	AnimalType() {
		type = EntityType.valueOf(name());
	}
	
	public EntityType getType() {
		return type;
	}
	
	public static EntityType getRandom() {		
		return AnimalType.values()[new Random().nextInt(AnimalType.values().length)].getType();
	}
}
