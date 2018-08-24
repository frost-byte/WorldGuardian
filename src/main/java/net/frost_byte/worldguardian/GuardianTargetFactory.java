package net.frost_byte.worldguardian;

import net.frost_byte.worldguardian.utility.GuardianTargetType;

public interface GuardianTargetFactory<T extends GuardianTarget>
{
	T createTarget(GuardianTargetType targetType);
}
