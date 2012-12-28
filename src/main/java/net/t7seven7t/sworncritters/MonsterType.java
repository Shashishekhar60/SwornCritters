/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.sworncritters;

import java.util.Random;

import org.bukkit.entity.EntityType;

/**
 * @author t7seven7t
 */
public enum MonsterType {
	BLAZE, CREEPER, ENDERMAN, GHAST, MAGMA_CUBE, PIG_ZOMBIE, SKELETON, SLIME, SPIDER, WITCH, ZOMBIE;
	
	private final EntityType type;
	MonsterType() {
		type = EntityType.valueOf(name());
	}
	
	public EntityType getType() {
		return type;
	}
	
	public static EntityType getRandom() {		
		return MonsterType.values()[new Random().nextInt(MonsterType.values().length)].getType();
	}
}
