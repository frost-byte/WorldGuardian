package net.frost_byte.worldguardian;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import co.aikar.commands.BukkitCommandManager;
import me.lucko.luckperms.api.LuckPermsApi;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Implementation of a Guice AbstractModule for handling
 * dependency injection
 *
 * @author frost-byte
 * @see com.google.inject.AbstractModule
 */
@SuppressWarnings("WeakerAccess") public class PluginBinderModule extends AbstractModule
{
	private WorldGuardianPlugin plugin;

	/**
	 * The ACF Bukkit Command Manager
	 */
	private final BukkitCommandManager commandManager;

	/**
	 * The LuckPerms API
	 */
	private final LuckPermsApi permsApi;

	/**
	 * The plugin's data folder
	 */
	private final File dataFolder;

	/**
	 * The plugin's Logger
	 */
	private final Logger logger;

	/**
	 * Binds Instances of Objects that cannot be modified to use
	 * Guice's Dependency Injection
	 *
	 * @param plugin         The WorldGuardian plugin
	 * @param commandManager The ACF Bukkit Command Manager
	 * @param permsApi       The LuckPerms API instance
	 * @param dataFolder     The Plugin's Data Folder
	 * @param logger         The Plugin's Logger
	 */
	public PluginBinderModule(
			WorldGuardianPlugin plugin,
			BukkitCommandManager commandManager,
			LuckPermsApi permsApi,
			File dataFolder,
			Logger logger
	)
	{
		this.plugin = Preconditions.checkNotNull(plugin,
				"The plugin instance cannot be null");

		this.commandManager = Preconditions.checkNotNull(commandManager,
				"The ACF API cannot be null");

		this.permsApi = Preconditions.checkNotNull(permsApi,
				"The luck perms API cannot be null");

		this.dataFolder = Preconditions.checkNotNull(dataFolder,
				"The plugin data folder cannot be null");

		this.logger = Preconditions.checkNotNull(logger,
				"The logger instance cannot be null");
	}

	/**
	 * Generate the Guice Injector for this Module
	 *
	 * @return The guice injector used to retrieve bound instances and create new instances
	 * based upon the implementations bound to their specified contract class
	 */
	public Injector createInjector()
	{
		return Guice.createInjector(this);
	}

	/**
	 * Configure the Injections and Bindings for our Guice Module
	 * Binds classes to specific instances.
	 * Creates the Configuration Factory and associated implementations
	 */
	@Override
	protected void configure()
	{
		bind(Plugin.class).toInstance(plugin);
		bind(WorldGuardianPlugin.class).toInstance(plugin);
		bind(BukkitCommandManager.class).toInstance(commandManager);
		bind(LuckPermsApi.class).toInstance(permsApi);

		install(new FactoryModuleBuilder()
				.build(GuardianTargetFactory.class));

		bind(File.class)
				.annotatedWith(Names.named("DataFolder"))
				.toInstance(dataFolder);

		bind(Logger.class)
				.annotatedWith(Names.named("Logger"))
				.toInstance(logger);

		bind(String.class)
				.annotatedWith(Names.named("ServerFileName"))
				.toInstance("server");

	}
}
