package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixBad;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixGood;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianOrderCommand extends BaseCommand
{
	@Inject
		private WorldGuardianPlugin plugin;

	@Description("Respawn the guardian.")
	@Subcommand("respawn")
	@CommandPermission("guardian.respawn")
	public void respawn(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, prefixBad + "Could not find guardian!");
			return;
		}
		Location loc = guardian.spawnPoint == null ? guardian.getNPC().getStoredLocation() : guardian.spawnPoint;

		if (!guardian.getNPC().spawn(loc)) {
			guardian.getNPC().teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
		}

		plugin.sendChannelMessage(sender, prefixGood + "Respawned!");
	}

	@Description("Kill the guardian.")
	@Subcommand("kill")
	@CommandPermission("guardian.kill")
	public void kill(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, prefixBad + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			plugin.sendChannelMessage(sender, prefixBad + "NPC is already dead!");
		}
		else {
			guardian.getLivingEntity().damage(guardian.health * 2);
			plugin.sendChannelMessage(sender, prefixGood + "Killed!");
		}
		guardian.currentTargets.clear();
		guardian.chasing = null;
		plugin.sendChannelMessage(sender, prefixGood + "Targets forgiven.");
	}

	@Description("Forgive the guardian's targets to reset its agro.")
	@Subcommand("forgive")
	@CommandPermission("guardian.forgive")
	public void forgive(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, prefixBad + "Could not find guardian!");
			return;
		}

		guardian.currentTargets.clear();
		guardian.chasing = null;
		plugin.sendChannelMessage(sender, prefixGood + "Targets forgiven.");
	}
}
