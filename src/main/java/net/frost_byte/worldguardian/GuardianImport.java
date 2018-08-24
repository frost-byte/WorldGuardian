package net.frost_byte.worldguardian;

import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class GuardianImport
{
	public String getTargetHelp() {
		return "{{Error:UnimplementedGetTargetHelp}}";
	}

	public boolean isTarget(LivingEntity livingEntityentity, String text) {
		return false;
	}

	public boolean tryAttack(GuardianTrait trait, LivingEntity livingEntity) {
		return false;
	}
}
