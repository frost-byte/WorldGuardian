package net.frost_byte.worldguardian;

import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GuardianAttackEvent extends NPCEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;

	public GuardianAttackEvent(NPC npc)
	{
		super(npc);
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
