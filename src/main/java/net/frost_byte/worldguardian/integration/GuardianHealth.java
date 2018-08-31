package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class GuardianHealth extends GuardianIntegration
{
	@Override
	public String getTargetHelp()
	{
		return "healthabove PERCENTAGE, healthbelow PERCENTAGE";
	}

	@Override
	public boolean isTarget(LivingEntity ent, String... options) {
		if (options.length != 2)
			return false;

		try {
			String haText = options[1];
			double haVal = Double.parseDouble(haText);
			double maxHealth = ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

			switch(options[0])
			{
				case "healthabove":
					if (ent.getHealth() / maxHealth > haVal * 0.01) {
						return true;
					}
					break;
				case "healthbelow":
					if (ent.getHealth() / maxHealth < haVal * 0.01) {
						return true;
					}
					break;
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
