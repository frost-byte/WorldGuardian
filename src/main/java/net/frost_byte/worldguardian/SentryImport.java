package net.frost_byte.worldguardian;

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frost_byte.worldguardian.utility.GuardianTargetType;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import net.aufdemrand.sentry.SentryInstance;
import net.aufdemrand.sentry.SentryTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import static net.frost_byte.worldguardian.GuardianTrait.*;
import static net.frost_byte.worldguardian.utility.GuardianTargetUtil.*;

@SuppressWarnings("unused")
@Singleton
public class SentryImport implements Listener {

	private final WorldGuardianPlugin plugin;

	@Inject
	public SentryImport(
		@Named("WorldGuardian")
		WorldGuardianPlugin plugin
	){
		this.plugin = plugin;
	}
	/**
	 * Converts Sentry NPCs to Guardian NPCs. Returns the number of NPCs converted.
	 */
	public int PerformImport() {
		int convertedCount = 0;
		for (NPC npc : CitizensAPI.getNPCRegistry()) {
			if (!npc.hasTrait(SentryTrait.class)) {
				continue;
			}
			SentryInstance sentry = npc.getTrait(SentryTrait.class).getInstance();
			if (sentry == null) {
				continue;
			}
			convertedCount++;
			if (!npc.hasTrait(GuardianTrait.class)) {
				npc.addTrait(GuardianTrait.class);
			}
			GuardianTrait guardian = npc.getTrait(GuardianTrait.class);
			guardian.armor = Math.min(sentry.Armor * 0.1, 1.0);
			guardian.attackRate = (int) (sentry.AttackRateSeconds * 20);
			if (guardian.attackRate < plugin.tickRate) {
				guardian.attackRate = plugin.tickRate;
			}
			else if (guardian.attackRate > attackRateMax) {
				guardian.attackRate = attackRateMax;
			}
			guardian.chaseRange = sentry.sentryRange;
			guardian.closeChase = true;
			guardian.rangedChase = false;
			guardian.damage = sentry.Strength;
			guardian.enemyDrops = sentry.KillsDropInventory;
			guardian.fightback = sentry.Retaliate;
			double hpHealedPerPeriod = 1;
			if (sentry.HealRate < .5) {
				hpHealedPerPeriod = .5 / sentry.HealRate;
			}
			double secondsPerHpPoint = sentry.HealRate / hpHealedPerPeriod;
			guardian.healRate = (int) (20 * secondsPerHpPoint);
			if (guardian.healRate < plugin.tickRate) {
				guardian.healRate = plugin.tickRate;
			}
			else if (guardian.healRate > healRateMax) {
				guardian.healRate = healRateMax;
			}

			double health = sentry.sentryHealth;
			if (health < healthMin) {
				health = healthMin;
			}
			else if (health > plugin.maxHealth) {
				health = plugin.maxHealth;
			}
			guardian.setHealth(health);
			guardian.setInvincible(sentry.Invincible);
			guardian.needsAmmo = false;
			guardian.range = sentry.sentryRange;
			guardian.respawnTime = sentry.RespawnDelaySeconds * 20;
			guardian.safeShot = false;
			guardian.spawnPoint = sentry.Spawn;
			if (sentry.guardTarget != null && sentry.guardTarget.length() > 0) {

				OfflinePlayer op = Bukkit.getOfflinePlayer(sentry.getGuardTarget().getUniqueId());
				if (op != null) {
					UUID playerId = op.getUniqueId();
					if (playerId != null) {
						guardian.setGuarding(playerId);
					}
				}
			}
			guardian.targets.clear();
			guardian.eventTargets.clear();
			guardian.playerNameTargets.clear();
			guardian.npcNameTargets.clear();
			guardian.groupTargets.clear();
			guardian.ignores.clear();
			guardian.playerNameIgnores.clear();
			guardian.npcNameIgnores.clear();
			guardian.groupIgnores.clear();
			for (String t : sentry.validTargets) {
				if (t.contains("ENTITY:ALL")) {
					guardian.targets.add(forName("MOBS").getName());
					guardian.targets.add(GuardianTargetType.OWNER.name());
					guardian.targets.add(GuardianTargetType.NPCS.name());
				}
				else if (t.contains("ENTITY:MONSTER")) {
					guardian.targets.add(forName("MONSTERS").getName());
				}
				else if (t.contains("ENTITY:PLAYER")) {
					guardian.targets.add(GuardianTargetType.PLAYERS.name());
				}
				else if (t.contains("ENTITY:NPC")) {
					guardian.targets.add(GuardianTargetType.NPCS.name());
				}
				else if (t.contains("EVENT:PVP")) {
					guardian.eventTargets.add("pvp");
				}
				else if (t.contains("EVENT:PVE")) {
					guardian.eventTargets.add("pve");
				}
				else if (t.contains("EVENT:PVNPC")) {
					guardian.eventTargets.add("pvnpc");
				}
				else {
					String[] sections = t.split(":");
					if (sections.length != 2) {
						continue;
					}
					sections[0] = sections[0].trim();
					sections[1] = sections[1].trim();
					switch (sections[0])
					{
						case "NPC":
							guardian.npcNameTargets.add(sections[1]);
							break;
						case "GROUP":
							guardian.groupTargets.add(sections[1]);
							break;
						case "PLAYER":
							guardian.playerNameTargets.add(sections[1]);
							break;
						case "ENTITY":
							GuardianTarget target = forName(sections[1]);
							if (target != null)
							{
								guardian.targets.add(target.getName());
							}
							break;
					}
				}
			}
			for (String t : sentry.ignoreTargets) {
				if (t.contains("ENTITY:ALL")) {
					guardian.ignores.add(forName("MOBS").getName());
					guardian.ignores.add(GuardianTargetType.PLAYERS.name());
					guardian.ignores.add(GuardianTargetType.NPCS.name());
				}
				else if (t.contains("ENTITY:MONSTER")) {
					guardian.ignores.add(forName("MONSTERS").getName());
				}
				else if (t.contains("ENTITY:PLAYER")) {
					guardian.ignores.add(GuardianTargetType.PLAYERS.name());
				}
				else if (t.contains("ENTITY:NPC")) {
					guardian.ignores.add(GuardianTargetType.NPCS.name());
				}
				else if (t.contains("ENTITY:OWNER")) {
					guardian.ignores.add(GuardianTargetType.OWNER.name());
				}
				else {
					String[] sections = t.split(":");
					if (sections.length != 2) {
						// Invalid target identifier?
						continue;
					}
					// Sentry was spacing tolerant, so we should be too.
					sections[0] = sections[0].trim();
					sections[1] = sections[1].trim();
					switch (sections[0])
					{
						case "NPC":
							guardian.npcNameIgnores.add(sections[1]);
							break;
						case "GROUP":
							guardian.groupIgnores.add(sections[1]);
							break;
						case "PLAYER":
							guardian.playerNameIgnores.add(sections[1]);
							break;
						case "ENTITY":
							GuardianTarget target = forName(sections[1]);
							if (target != null)
							{
								guardian.ignores.add(target.getName());
							}
							break;
					}
				}
			}
			npc.removeTrait(SentryTrait.class);
		}
		return convertedCount;
	}
}
