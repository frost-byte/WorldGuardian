package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixBad;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.prefixGood;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianWorldCommand extends BaseCommand
{
	@Inject
		private WorldGuardianPlugin plugin;

	@Description("Set the guardian's spawn point.")
	@Subcommand("spawn_point|spawn")
	@CommandPermission("guardian.spawnpoint")
	public void setSpawnPoint(Player sender, @Optional Location location)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, prefixBad + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			plugin.sendChannelMessage(sender, prefixBad + "NPC must be spawned for this command!");
		}
		else if (location != null)
		{
			guardian.destinationLocation = location;
			guardian.destinationWorld = location.getWorld().getName();
			plugin.sendChannelMessage(sender, prefixGood + "Spawn point updated using location!");
		}
		else {
			// The guardian owner's position? or the guardian's position?
			Location pos = guardian.getLivingEntity().getLocation().getBlock().getLocation();
			if (guardian.spawnPoint != null
					&& pos.getBlockX() == guardian.spawnPoint.getBlockX()
					&& pos.getBlockY() == guardian.spawnPoint.getBlockY()
					&& pos.getBlockZ() == guardian.spawnPoint.getBlockZ()
					&& pos.getWorld().getName().equals(guardian.spawnPoint.getWorld().getName())) {
				guardian.spawnPoint = null;

				plugin.sendChannelMessage(sender, prefixGood + "Spawn point removed!");
			}
			else {
				guardian.spawnPoint = pos.add(0.5, 0.0, 0.5);
				guardian.spawnPoint.setYaw(guardian.getLivingEntity().getLocation().getYaw());
				plugin.sendChannelMessage(sender, prefixGood + "Spawn point updated!");
			}
		}
	}

	@Description("Set the location for the guardian's teleport destination.")
	@Subcommand("destination|dest")
	@CommandPermission("guardian.destination")
	public void setDestination(Player sender, @Optional Location destination)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			plugin.sendChannelMessage(sender, prefixBad + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			plugin.sendChannelMessage(sender, prefixBad + "NPC must be spawned for this command!");
		}
		else if (destination != null)
		{
			guardian.destinationLocation = destination;
			guardian.destinationWorld = destination.getWorld().getName();
			plugin.sendChannelMessage(sender, prefixGood + "Teleport Destination updated using location!");
		}
		else {
			// The guardian owner's position? or the guardian's position?
			Location pos = guardian.getLivingEntity().getLocation().getBlock().getLocation();
			if (guardian.destinationLocation != null
					&& pos.getBlockX() == guardian.destinationLocation.getBlockX()
					&& pos.getBlockY() == guardian.destinationLocation.getBlockY()
					&& pos.getBlockZ() == guardian.destinationLocation.getBlockZ()
					&& pos.getWorld().getName().equals(guardian.destinationLocation.getWorld().getName())) {
				guardian.destinationLocation = null;

				plugin.sendChannelMessage(sender, prefixGood + "Teleport Destination removed!");
			}
			else {
				guardian.destinationLocation = pos.add(0.5, 0.0, 0.5);
				guardian.destinationLocation.setYaw(guardian.getLivingEntity().getLocation().getYaw());
				plugin.sendChannelMessage(sender, prefixGood + "Teleport Destination updated!");
			}
		}
	}
}
