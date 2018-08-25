package net.frost_byte.worldguardian;

import net.frost_byte.worldguardian.utility.GuardianTargetType;
import org.bukkit.entity.EntityType;


@SuppressWarnings("unused")
public interface Target
{
	String getName();
	EntityType[] getEntityTypes();
	GuardianTargetType getTargetType();
}
