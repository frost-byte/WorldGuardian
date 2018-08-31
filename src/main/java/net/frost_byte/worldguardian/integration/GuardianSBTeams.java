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
		return "sbteam SCOREBOARD_TEAM_NAME";
	}

	@Override
	public boolean isTarget(LivingEntity ent, String... options) {
		if(
			options.length != 2 ||
			!(ent instanceof  Player) ||
			!options[0].equalsIgnoreCase("sbteam")
		){
			return false;
		}

		try
		{
			String sbteamName = options[1];
			Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(sbteamName);

			if (t != null) {
				if (t.hasEntry(ent.getName())) {
					return true;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
