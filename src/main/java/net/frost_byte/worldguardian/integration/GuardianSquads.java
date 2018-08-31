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
		return "squad GUARDIAN_SQUAD_NAME";
	}

	@Override
	public boolean isTarget(LivingEntity ent, String... options) {
		if(
			options.length != 2 ||
			!(ent instanceof Player) ||
			!options[0].equalsIgnoreCase("squad")
		){
			return false;
		}

		try {
			if (
				CitizensAPI.getNPCRegistry().isNPC(ent) &&
				CitizensAPI.getNPCRegistry()
					.getNPC(ent)
					.hasTrait(GuardianTrait.class)
			){
				GuardianTrait guardian = CitizensAPI.getNPCRegistry()
					.getNPC(ent)
					.getTrait(GuardianTrait.class);

				if (guardian.squad != null)
				{
					String squadName = options[1].toLowerCase(Locale.ENGLISH);

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
