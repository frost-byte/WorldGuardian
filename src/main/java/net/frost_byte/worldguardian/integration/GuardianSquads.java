package net.frost_byte.worldguardian.integration;

import net.citizensnpcs.api.CitizensAPI;
import net.frost_byte.worldguardian.GuardianIntegration;
import net.frost_byte.worldguardian.GuardianTrait;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Locale;

public class GuardianSquads extends GuardianIntegration
{
	@Override
	public String getTargetHelp() {
		return "squad:GUARDIAN_SQUAD_NAME";
	}

	@Override public String[] getTargetPrefixes()
	{
		return new String[] { "squad" };
	}

	@Override
	public boolean isTarget(LivingEntity livingEntity, String prefix, String value)
	{
		try {
			if (
				prefix.equals("squad") && CitizensAPI.getNPCRegistry().isNPC(livingEntity) &&
				CitizensAPI.getNPCRegistry().getNPC(livingEntity).hasTrait(GuardianTrait.class)
			) {
				GuardianTrait guardian = CitizensAPI.getNPCRegistry().getNPC(livingEntity).getTrait(GuardianTrait.class);

				if (guardian.squad != null) {
					String squadName = value.toLowerCase(Locale.ENGLISH);
					if (squadName.equals(guardian.squad)) {
						return true;
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
