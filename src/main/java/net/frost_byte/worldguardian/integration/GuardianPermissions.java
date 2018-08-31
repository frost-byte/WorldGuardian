package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GuardianPermissions extends GuardianIntegration
{
	@Override
	public String getTargetHelp() {
		return "permission PERM.KEY";
	}

	@Override
	public boolean isTarget(LivingEntity ent, String... options)
	{
		return options.length == 2 &&
			ent instanceof Player &&
			options[0].equalsIgnoreCase("permission") &&
			ent.hasPermission(options[1]);
	}
}
