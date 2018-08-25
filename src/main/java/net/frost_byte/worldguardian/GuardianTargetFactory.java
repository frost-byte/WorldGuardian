package net.frost_byte.worldguardian;

import net.frost_byte.worldguardian.utility.GuardianTargetType;

public interface GuardianTargetFactory
{
	Target createTarget(GuardianTargetType targetType);
}
