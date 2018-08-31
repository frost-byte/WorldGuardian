package net.frost_byte.worldguardian.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.GuardianTrait;
import net.frost_byte.worldguardian.WorldGuardianPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static net.frost_byte.worldguardian.GuardianTrait.*;
import static net.frost_byte.worldguardian.WorldGuardianPlugin.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("guardian|guard|wgd")
public class GuardianConfigCommand extends BaseCommand
{
	@Inject
	@Named("WorldGuardian")
	private WorldGuardianPlugin plugin;

	@Description("Adjust the range for a guardian")
	@Syntax("<range> The tracking range for the guardian (1-200)")
	@Subcommand("range")
	@CommandPermission("guardian.range")
	@CommandCompletion("@range:1-200")
	public void setRange(Player player, double range)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (range > 0 && range < 200) {
				guardian.range = range;
				player.sendMessage(
					prefixGood +
					"Range set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid range number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the damage for a guardian")
	@Syntax("<damage> The damage for the guardian (< 1000)")
	@Subcommand("damage")
	@CommandPermission("guardian.damage")
	@CommandCompletion("@range:-1-1000")
	public void setDamage(Player player, double damage)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (damage >= -1 && damage < 1000) {
				guardian.damage = damage;
				player.sendMessage(
					prefixGood +
					"Damage set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid damage number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the speed for a guardian")
	@Syntax("<speed> The speed for the guardian (< 1000)")
	@Subcommand("speed")
	@CommandPermission("guardian.speed")
	@CommandCompletion("@range:1-1000")
	public void setSpeed(Player player, double speed)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (speed >= 0 && speed < 1000) {
				guardian.speed = speed;
				player.sendMessage(
					prefixGood +
					"Speed set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid speed number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the greet range for a guardian")
	@Syntax("<range> The greet range for the guardian (<100)")
	@Subcommand("greet_range|greetrange|grange")
	@CommandPermission("guardian.range")
	@CommandCompletion("@range:1-100")
	public void setGreetRange(Player player, double range)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (range > 0 && range < 100) {
				guardian.greetRange = range;
				player.sendMessage(
					prefixGood +
					"Range set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid range number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the armor for a guardian")
	@Syntax("<armor> The armor for the guardian (<=1)")
	@Subcommand("armor")
	@CommandPermission("guardian.armor")
	@CommandCompletion("@range:-1-1")
	public void setArmor(Player player, double armor)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (armor >= -1 && armor <= 1) {
				guardian.armor = armor;
				player.sendMessage(
					prefixGood +
					"Armor set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid armor number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the health for a guardian")
	@Syntax("<health> The health for the guardian (0.01-2000)")
	@Subcommand("health")
	@CommandPermission("guardian.health")
	@CommandCompletion("@range:0.01-2000")
	public void setHealth(Player player, double health)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (health >= healthMin && health <= plugin.maxHealth) {
				guardian.setHealth(health);
				player.sendMessage(
					prefixGood +
					"Health set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid health number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the heal rate, in ticks, for a guardian")
	@Syntax("<health> The heal rate for the guardian (0-100)")
	@Subcommand("heal_rate|healrate|hrate")
	@CommandPermission("guardian.healrate")
	@CommandCompletion("@range:0-100")
	public void setHealRate(Player player, double rate)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			int d = (int)(rate * 20);
			if ((d >= plugin.tickRate && d <= healRateMax) || d == 0)
			{
				guardian.healRate = d;
				player.sendMessage(
					prefixGood +
					"Heal rate set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid heal rate number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the how often the guardian will respond to being targeted, in ticks")
	@Syntax("<rate> How often the guardian will respond to being targeted (1-100)")
	@Subcommand("targeted_rate|tarrate|trate")
	@CommandPermission("guardian.targetedrate")
	@CommandCompletion("@range:1-100")
	public void setTargetedRate(Player player, double rate)
	{
		GuardianTrait guardian = getGuardianFor(player);
		int d = (int)(rate * 20);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (d >= plugin.tickRate && d <= targetedRateMax)
			{
				guardian.targetedRate = d;
				player.sendMessage(
					prefixGood +
						"Targeted rate set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
					"Invalid targeted rate number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the attack rate, in ticks, for a guardian")
	@Syntax("<rate> ['ranged'] The attack rate for the guardian (1-100)")
	@Subcommand("attack_rate|atrate|at_rate")
	@CommandPermission("guardian.attackrate")
	@CommandCompletion("@range:1-100 @nothing")
	public void setAttackRate(Player player, double rate, @Optional String ranged)
	{
		GuardianTrait guardian = getGuardianFor(player);
		int d = (int)(rate * 20);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (d >= plugin.tickRate && d <= attackRateMax)
			{
				if (ranged != null && ranged.equalsIgnoreCase("ranged"))
				{
					guardian.attackRateRanged = d;
					player.sendMessage(
						prefixGood +
						"Ranged attack rate set!"
					);
				}
				else
				{
					guardian.attackRate = d;
					player.sendMessage(
						prefixGood +
						"Attack rate set!"
					);
				}
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid attack rate number: " + ex.getMessage()
			);
		}
	}

	@Description("The length of time, in ticks, that a guardian will target an enemy")
	@Syntax("<ticks> The number of ticks that a guardian will spend tracking an enemy, 0 means always (0-100)")
	@Subcommand("target_time|tartime|ttime")
	@CommandPermission("guardian.targettime")
	@CommandCompletion("@range:1-100")
	public void setTargetTime(Player player, double ticks)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{

			if (ticks >= 0)
			{
				guardian.enemyTargetTime = (int)(ticks * 20);
				player.sendMessage(
					prefixGood +
					"Target time set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid time number: " + ex.getMessage()
			);
		}
	}

	@Description("The amount of ticks required, in ticks, before a guardian will respawn")
	@Syntax("<ticks> The ticks required before a guardian will respawn")
	@Subcommand("respawn_time|restime|rtime")
	@CommandPermission("guardian.targettime")
	@CommandCompletion("@range:1-100")
	public void setRespawnTime(Player player, double ticks)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (ticks >= 0)
			{
				guardian.respawnTime = (int)(ticks * 20);
				player.sendMessage(
					prefixGood +
					"Respawn time set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
					prefixBad +
							"Invalid time number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the chase range for a guardian")
	@Syntax("<range> The greet range for the guardian (<100)")
	@Subcommand("chase_range")
	@CommandPermission("guardian.chaserange")
	@CommandCompletion("@range:1-100")
	public void setChaseRange(Player player, double range)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (range > 0 && range < 100) {
				guardian.chaseRange = range;
				player.sendMessage(
					prefixGood +
					"Chase range set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid range number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust the accuracy for a guardian, higher means less accurate")
	@Syntax("<accuracy> The accuracy for the guardian (0-5)")
	@Subcommand("accuracy")
	@CommandPermission("guardian.accuracy")
	@CommandCompletion("@range:1-1000")
	public void setAccuracy(Player player, double accuracy)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (accuracy >= 0 && accuracy < 1000) {
				guardian.accuracy = accuracy;
				player.sendMessage(
					prefixGood +
					"Accuracy offset set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid accuracy offset number: " + ex.getMessage()
			);
		}
	}

	@Description("Adjust how far the guardian can reach.")
	@Syntax("<reach> The reach for the guardian (0-50)")
	@Subcommand("reach")
	@CommandPermission("guardian.reach")
	@CommandCompletion("@range:0-50")
	public void setReach(Player player, double reach)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		try
		{
			if (reach >= 0 && reach <= 50) {
				guardian.reach = reach;
				player.sendMessage(
					prefixGood +
					"Reach set!"
				);
			}
			else
			{
				throw new NumberFormatException("Number out of range.");
			}
		}
		catch (NumberFormatException ex)
		{
			player.sendMessage(
				prefixBad +
				"Invalid reach number: " + ex.getMessage()
			);
		}
	}

	@Description("Set the guardian's invincibility.")
	@Syntax("<true/false> should the guardian be invincible")
	@Subcommand("invincible")
	@CommandPermission("guardian.invincible")
	public void setInvincibility(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.invincible = value;

		if (guardian.invincible)
		{
			player.sendMessage(
				prefixGood +
				"NPC now invincible!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer invincible!"
			);
		}
	}

	@Description("Does the guardian automatically switch items.")
	@Syntax("<true/false> should the guardian automatically switch items")
	@Subcommand("auto_switch|autoswitch")
	@CommandPermission("guardian.autoswitch")
	public void setAutoSwitch(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.autoswitch = value;

		if (guardian.autoswitch)
		{
			player.sendMessage(
				prefixGood +
				"NPC now automatically switches items!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer automatically switches items!"
			);
		}
	}

	@Description("The guardian targets enemies realistically.")
	@Syntax("<true/false> Should the guardian target enemies realistically")
	@Subcommand("realistic")
	@CommandPermission("guardian.realistic")
	public void setRealisticTargeting(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.realistic = value;

		if (guardian.realistic)
		{
			player.sendMessage(
				prefixGood +
				"NPC now realistically targets enemies!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer realistically targets enemies!"
			);
		}
	}

	@Description("The guardian fights back when attacked.")
	@Syntax("<true/false> Should the guardian fight back when attacked?")
	@Subcommand("fightback|retaliate")
	@CommandPermission("guardian.fightback")
	public void setFightback(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.fightback = value;

		if (guardian.fightback)
		{
			player.sendMessage(
				prefixGood +
				"NPC now fights back!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer fights back!"
			);
		}
	}

	@Description("The guardian needs ammunition to attack.")
	@Syntax("<true/false> Does the guardian need ammunition to attck?")
	@Subcommand("needsammo|needs_ammo")
	@CommandPermission("guardian.needammo")
	public void setNeedsAmmo(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.needsAmmo = value;

		if (guardian.needsAmmo)
		{
			player.sendMessage(
				prefixGood +
				"NPC now needs ammo!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer needs ammo!"
			);
		}
	}

	@Description("The guardian is a safe shooter.")
	@Syntax("<true/false> Should the guardian shoot when a friendly would be hit?")
	@Subcommand("safeshot|safe_shot")
	@CommandPermission("guardian.safeshot")
	public void setSafeShot(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.safeShot = value;

		if (guardian.safeShot)
		{
			player.sendMessage(
				prefixGood +
				"NPC now is a safe shot!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC is no longer a safe shot!"
			);
		}
	}

	@Description("The guardian will chase close enemies.")
	@Syntax("<true/false> Should the guardian chase nearby enemies?")
	@Subcommand("chase_close|chaseclose")
	@CommandPermission("guardian.chase")
	public void setChaseClose(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.closeChase = value;

		if (guardian.closeChase)
		{
			player.sendMessage(
				prefixGood +
				"NPC now chases nearby enemies!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer chases nearby enemies!"
			);
		}
	}

	@Description("Should the guardian chase ranged enemies.")
	@Syntax("<true/false> Should the guardian chase ranged enemies?")
	@Subcommand("chaseranged|chase_ranged")
	@CommandPermission("guardian.chase")
	public void setChaseRanged(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.rangedChase = value;

		if (guardian.rangedChase)
		{
			player.sendMessage(
				prefixGood +
				"NPC now chases ranged enemies!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC no longer chases ranged enemies!"
			);
		}
	}

	@Description("The guardian will guard a player or an area.")
	@Syntax("[target] If a target player is specified they will be guarded, otherwise an area is guarded")
	@Subcommand("guard")
	@CommandPermission("guardian.guard")
	public void setGuard(Player player, @Flags("other") @Optional OnlinePlayer onlinePlayer)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		if (onlinePlayer != null)
			guardian.setGuarding(onlinePlayer.getPlayer().getUniqueId());
		else
			guardian.setGuarding(null);

		if (guardian.getGuarding() == null)
		{
			player.sendMessage(
				prefixGood +
				"NPC now guarding its area!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC now guarding that player!"
			);
		}
	}

	@Description("Set inventory for the guardian that they will drop.")
	@Subcommand("drops")
	@CommandPermission("guardian.drops")
	public void setDrops(Player player)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		Inventory inv = Bukkit.createInventory(
			null,
			9 * 4,
			InvPrefix + guardian.getNPC().getId()
		);

		ItemStack[] items = new ItemStack[guardian.drops.size()];
		inv.addItem(guardian.drops.toArray(items));
		player.openInventory(inv);
	}

	@Description("Should the guardian's enemies drop items and xp.")
	@Syntax("<true/false> Should the guardian's enemies drop loot and xp?")
	@Subcommand("enemydrops|enemy_drops")
	@CommandPermission("guardian.enemydrops")
	public void setEnemyDrops(Player player, boolean value)
	{
		GuardianTrait guardian = getGuardianFor(player);

		if (guardian == null)
		{
			player.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.enemyDrops = value;

		if (guardian.enemyDrops)
		{
			player.sendMessage(
				prefixGood +
				"NPC enemy mobs now drop items and XP!"
			);
		}
		else
		{
			player.sendMessage(
				prefixGood +
				"NPC enemy mobs no longer drop items and XP!"
			);
		}
	}

	@Description("Set the guardian's greeting.")
	@Syntax("<greeting> The guardian's greeting")
	@Subcommand("greeting")
	@CommandPermission("guardian.greet")
	public void setGreeting(Player sender, String greeting)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.greetingText = greeting;

		sender.sendMessage(prefixGood + "Set!");
	}

	@Description("Add a message to the list of the guardian's farewells.")
	@Syntax("<message> The message to add to the guardian's farewells")
	@Subcommand("farewell")
	@CommandPermission("guardian.farewell")
	public void addFarewell(Player sender, String message)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.farewell.add(message);

		sender.sendMessage(prefixGood + "Farewell added!");
	}

	@Description("Add a message to the guardian's dialogue.")
	@Syntax("<message> The message to add to the guardian's dialogue")
	@Subcommand("dialogue")
	@CommandPermission("guardian.dialogue")
	public void addDialogue(Player sender, String message)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.dialogue.add(message);

		sender.sendMessage(prefixGood + "Dialogue added!");
	}

	@Description("Set the guardian's warning.")
	@Syntax("<warning> The guardian's warning")
	@Subcommand("warning")
	@CommandPermission("guardian.greet")
	public void setWarning(Player sender, String warning)
	{
		GuardianTrait guardian = getGuardianFor(sender);

		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.warningText = warning;

		sender.sendMessage(prefixGood + "Set!");
	}

	@Description("Set the guardian's squad.")
	@Syntax("<squadName> The guardian's squad name")
	@Subcommand("squad")
	@CommandPermission("guardian.squad")
	public void setSquad(Player sender, String squadName)
	{
		GuardianTrait guardian = getGuardianFor(sender);
	
		if (guardian == null)
		{
			sender.sendMessage(ChatColor.RED + "Could not find guardian!");
			return;
		}

		guardian.squad = squadName;
		if (guardian.squad.equals("null")) {
			guardian.squad = null;
		}

		sender.sendMessage(prefixGood + "Set!");
	}
}
