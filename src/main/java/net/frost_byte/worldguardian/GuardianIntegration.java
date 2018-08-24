package net.frost_byte.worldguardian;

import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class GuardianIntegration
{
	public String getTargetHelp()
	{
		return "{{Error:UnimplementedGetTargetHelp}}";
	}

	public boolean isTarget(LivingEntity livingEntity, String text)
	{
		return false;
	}

	public boolean tryAttack(GuardianTrait guardianTrait, LivingEntity livingEntity)
	{
		return false;
	}
}
