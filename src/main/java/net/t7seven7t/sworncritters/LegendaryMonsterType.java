/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.Random;

import org.bukkit.entity.EntityType;

/**
 * @author t7seven7t
 */
public enum LegendaryMonsterType {
	SLIME,
	GIANT,
	WITHER;
	
	private final EntityType type;
	LegendaryMonsterType() {
		type = EntityType.valueOf(name());
	}
	
	public EntityType getType() {
		return type;
	}
	
	public static EntityType getRandom() {		
		return LegendaryMonsterType.values()[new Random().nextInt(LegendaryMonsterType.values().length)].getType();
	}
}
