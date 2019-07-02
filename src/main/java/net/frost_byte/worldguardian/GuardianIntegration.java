package net.frost_byte.worldguardian;

import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class GuardianIntegration
{

	/**
	 * Gets the 'target help' data for this integration (empty string if not relevant).
	 * Example format is: "myintegration:MY_TARGET_IDENTIFIER" like "squad:SQUAD_NAME" or "healthabove:PERCENTAGE"
	 */
	public String getTargetHelp() {
		return "{{Error:UnimplementedGetTargetHelp}}";
	}

	/**
	 * Gets the list of target prefixes that this integration handles.
	 * For a "squad:SQUAD_NAME" target, this should return: new String[] { "squad" }
	 */
	public String[] getTargetPrefixes() {
		return new String[0];
	}

	/**
	 * Returns whether an entity is a target of the integration label.
	 */
	public boolean isTarget(LivingEntity livingEntity, String prefix, String value) {
		return isTarget(livingEntity, prefix + ":" + value);
	}

	/**
	 * Returns whether an entity is a target of the integration label.
	 */
	@Deprecated
	public boolean isTarget(LivingEntity livingEntity, String text) {
		return false;
	}

	/**
	 * Runs when an NPC intends to attack a target - return 'true' to indicate the integration ran its own attack methodology
	 * (and no default attack handling is needed).
	 */
	public boolean tryAttack(GuardianTrait guardianTrait, LivingEntity livingEntity) {
		return false;
	}
}
