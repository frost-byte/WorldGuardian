package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class GuardianUUID extends GuardianIntegration
{
	@Override
	public String getTargetHelp() {
		return "uuid:UUID";
	}

	@Override public String[] getTargetPrefixes()
	{
		return new String[] { "uuid" };
	}

	@Override public boolean isTarget(LivingEntity livingEntity, String prefix, String value)
	{
		try {
			if (prefix.equals("uuid")) {
				return livingEntity.getUniqueId().equals(UUID.fromString(value));
			}
		}
		catch (IllegalArgumentException ex) {
			// Do nothing.
			// TODO: Maybe show a one-time warning?
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
