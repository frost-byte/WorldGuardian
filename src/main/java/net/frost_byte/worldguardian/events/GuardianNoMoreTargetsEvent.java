package net.frost_byte.worldguardian.events;

import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a Guardian NPC has no targets remaining.
 */
public class GuardianNoMoreTargetsEvent extends NPCEvent
{

	/**
	 * Handler objects, for Bukkit internal usage.
	 */
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Constructs the attack event.
	 */
	public GuardianNoMoreTargetsEvent(NPC npc) {
		super(npc);
	}

	/**
	 * Returns the handler list for use with Bukkit.
	 */
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Returns the handler list for use with Bukkit.
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
