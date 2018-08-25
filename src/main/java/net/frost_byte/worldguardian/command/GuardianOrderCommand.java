package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
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
	@Named("WorldGuardian")
	private WorldGuardianPlugin plugin;

	@Description("Respawn the guardian.")
	@Subcommand("respawn")
	@CommandPermission("guardian.respawn")
	public void respawn(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}
		Location loc = guardian.spawnPoint == null ? guardian.getNPC().getStoredLocation() : guardian.spawnPoint;

		if (!guardian.getNPC().spawn(loc)) {
			guardian.getNPC().teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
		}

		sender.sendMessage(prefixGood + "Respawned!");
	}

	@Description("Kill the guardian.")
	@Subcommand("kill")
	@CommandPermission("guardian.kill")
	public void kill(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			sender.sendMessage(prefixBad + "NPC is already dead!");
		}
		else {
			guardian.getLivingEntity().damage(guardian.health * 2);
			sender.sendMessage(prefixGood + "Killed!");
		}
		guardian.currentTargets.clear();
		guardian.chasing = null;
		sender.sendMessage(prefixGood + "Targets forgiven.");
	}

	@Description("Forgive the guardian's targets to reset its agro.")
	@Subcommand("forgive")
	@CommandPermission("guardian.forgive")
	public void forgive(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.currentTargets.clear();
		guardian.chasing = null;
		sender.sendMessage(prefixGood + "Targets forgiven.");
	}
}
