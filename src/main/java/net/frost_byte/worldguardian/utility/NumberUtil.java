package net.frost_byte.worldguardian.utility;

import java.util.concurrent.ThreadLocalRandom;

public final class NumberUtil
{
	private NumberUtil()
	{
	}

	public static int randomInt(int min, int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
