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
	public static double randomDecimal(double min, double max)
	{
		return (ThreadLocalRandom.current().nextDouble() * (max - min)) + min;
	}
}
