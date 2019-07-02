package net.frost_byte.worldguardian.integration;

import net.frost_byte.worldguardian.GuardianIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class GuardianSBTeams extends GuardianIntegration
{
	@Override
	public String getTargetHelp() {
		return "sbteam:SCOREBOARD_TEAM_NAME";
	}

	@Override
	public String[] getTargetPrefixes()
	{
		return new String[] { "sbteam" };
	}

	@Override
	public boolean isTarget(LivingEntity ent, String prefix, String value) {
		try {
			if (prefix.equals("sbteam") && ent instanceof Player) {
				Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(value);
				if (t != null) {
					if (t.hasEntry(((Player) ent).getName())) {
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
