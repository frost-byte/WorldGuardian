package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GuardianPermissions extends GuardianIntegration
{
	@Override
	public String getTargetHelp() {
		return "permission:PERM.KEY";
	}

	@Override
	public String[] getTargetPrefixes()
	{
		return new String[] { "permission" };
	}

	@Override
	public boolean isTarget(LivingEntity livingEntity, String prefix, String value)
	{
		if (!(livingEntity instanceof Player)) {
			return false;
		}
		if (prefix.equals("permission")) {
			if (livingEntity.hasPermission(value)) {
				return true;
			}
		}
		return false;
	}
}
