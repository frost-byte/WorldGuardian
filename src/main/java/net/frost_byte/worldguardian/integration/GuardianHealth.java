package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class GuardianHealth extends GuardianIntegration
{
	@Override
	public String getTargetHelp()
	{
		return "healthabove:PERCENTAGE, healthbelow:PERCENTAGE";
	}

	@Override
	public String[] getTargetPrefixes()
	{
		return new String[] { "healthabove", "healthbelow" };
	}

	@Override public boolean isTarget(LivingEntity livingEntity, String prefix, String value)
	{
		try {
			if (prefix.equals("healthabove")) {
				double haVal = Double.parseDouble(value);
				if (livingEntity.getHealth() / livingEntity.getMaxHealth() > haVal * 0.01) {
					return true;
				}
			}
			else if (prefix.equals("healthbelow")) {
				double haVal = Double.parseDouble(value);
				if (livingEntity.getHealth() / livingEntity.getMaxHealth() < haVal * 0.01) {
					return true;
				}
			}
		}
		catch (NumberFormatException ex) {
			// Do nothing.
			// TODO: Maybe show a one-time warning?
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
