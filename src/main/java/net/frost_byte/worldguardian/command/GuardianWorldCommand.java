package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;
import org.bukkit.ChatColor;
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
	@Named("WorldGuardian")
	private WorldGuardianPlugin plugin;

	@Description("Set the guardian's spawn point.")
	@Subcommand("spawn_point|spawn")
	@CommandPermission("guardian.spawnpoint")
	public void setSpawnPoint(Player sender)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			sender.sendMessage(prefixBad + "NPC must be spawned for this command!");
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

				sender.sendMessage(prefixGood + "Spawn point removed!");
			}
			else {
				guardian.spawnPoint = pos.add(0.5, 0.0, 0.5);
				guardian.spawnPoint.setYaw(guardian.getLivingEntity().getLocation().getYaw());
				sender.sendMessage(prefixGood + "Spawn point updated!");
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
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (!guardian.getNPC().isSpawned()) {
			sender.sendMessage(prefixBad + "NPC must be spawned for this command!");
		}
		else if (destination != null)
		{
			guardian.destinationLocation = destination;
			guardian.destinationWorld = destination.getWorld().getName();
			sender.sendMessage(prefixGood + "Teleport Destination updated using location!");
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

				sender.sendMessage(prefixGood + "Teleport Destination removed!");
			}
			else {
				guardian.destinationLocation = pos.add(0.5, 0.0, 0.5);
				guardian.destinationLocation.setYaw(guardian.getLivingEntity().getLocation().getYaw());
				sender.sendMessage(prefixGood + "Teleport Destination updated!");
			}
		}
	}
}
