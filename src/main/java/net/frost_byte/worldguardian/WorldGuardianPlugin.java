package net.frost_byte.worldguardian;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.Lists;
import com.google.inject.Injector;

import me.lucko.luckperms.api.LuckPermsApi;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

import net.frost_byte.worldguardian.command.*;
import net.frost_byte.worldguardian.integration.GuardianHealth;
import net.frost_byte.worldguardian.integration.GuardianPermissions;
import net.frost_byte.worldguardian.integration.GuardianSBTeams;
import net.frost_byte.worldguardian.integration.GuardianSquads;
import net.frost_byte.worldguardian.utility.ProjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.github.games647.tabchannels.TabChannelsManager;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({ "FieldCanBeLocal", "WeakerAccess", "unused" })
public class WorldGuardianPlugin extends JavaPlugin implements Listener
{
	/**
	 * Guice Injection
	 */
	private Injector injector;

	/**
	 * ACF CommandManager for Bukkit used for
	 * registering in game and console commands, contexts,
	 * completions, and replacements
	 *
	 * @see co.aikar.commands.BukkitCommandManager
	 * @see co.aikar.commands.CommandManager
	 */
	private BukkitCommandManager commandManager;

	private TabChannelsManager channelsManager;

	public static final String ColorBasic = ChatColor.YELLOW.toString();

	public static final String prefixGood = ChatColor.DARK_GREEN + "[Guardian] " + ColorBasic;

	public static final String prefixBad = ChatColor.DARK_GREEN + "[Guardian] " + ChatColor.RED;

//	private GuardianTargetFactory targetFactory;


	public double maxHealth;
	public int cleverTicks;
	public boolean canUseSkull;
	public static boolean debugMe = false;
	public int tickRate = 10;


	public final static List<GuardianIntegration> integrations = new ArrayList<>();

	public final static int CONFIG_VERSION = 9;

	public final static String InvPrefix = ChatColor.GREEN + "Guardian ";
	/**
	 * Perform Plugin initialization before it is
	 * enabled.
	 */
	@Override
	public void onLoad()
	{

	}

	public void sendChannelMessage(Player player, String... messages)
	{
		if (player == null || !player.isOnline() || messages == null || messages.length == 0)
			return;

		List<String> msgList = Arrays.asList(messages);
		UUID pid = player.getUniqueId();

		if (channelsManager != null)
			msgList.forEach(m -> channelsManager.sendMessage(m, "global", pid));
		else
			player.sendMessage(messages);
	}

	public void broadcastChannelMessage(String... messages)
	{
		if (messages == null || messages.length == 0)
			return;

		List<String> msgList = Arrays.asList(messages);

		if (channelsManager != null)
			msgList.forEach(m -> channelsManager.broadcastMessage(m, "global"));
		else
			msgList.forEach(Bukkit::broadcastMessage);
	}

	/**
	 * The plugin has been activated and enabled by Bukkit.
	 * This is the entry point for the plugin.
	 */
	@SuppressWarnings("unused")
	public void onEnable()
	{
		getLogger().info("Using " + ProjectUtil.getBranchAndID());

		// Initialize the ACF Command Manger and assign it's error handler
		commandManager = new BukkitCommandManager(this);

		//noinspection deprecation
		commandManager.enableUnstableAPI("help");

		// Gain access to the LuckPerms API
		// Bukkit Service Provider for the LuckPerms API
		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);

		/*
		  The LuckPermsAPI instance for interacting with
		  the LuckPerms Plugin for managing access via permissions
		  for commands and plugin features
		*/
		LuckPermsApi permsApi = provider.getProvider();

		/*
			The TabChannels instance for sending chat
			to different tab channels.
		 */
		RegisteredServiceProvider<TabChannelsManager> channelsProvider = Bukkit
			.getServicesManager()
			.getRegistration(TabChannelsManager.class);

		channelsManager = channelsProvider.getProvider();

		// Create the Guice Injection module and configure its bindings
		PluginBinderModule module = new PluginBinderModule(
			this,
			channelsManager,
			commandManager,
			permsApi,
			getDataFolder(),
			getLogger()
		);

		injector = module.createInjector();
//		targetFactory = injector.getInstance(GuardianTargetFactory.class);

		// Inject all class members annotated with Inject
		injector.injectMembers(this);


		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GuardianTrait.class).withName("guardian"));
		saveDefaultConfig();
		if (getConfig().getInt("config version", 0) != CONFIG_VERSION) {
			getLogger().warning("Outdated Guardian config - please delete it to regenerate it!");
		}
		cleverTicks = getConfig().getInt("random.clever ticks", 10);
		canUseSkull = getConfig().getBoolean("random.skull allowed", true);
		BukkitRunnable postLoad = new BukkitRunnable() {
			@Override
			public void run() {
//				for (NPC npc : CitizensAPI.getNPCRegistry()) {
//					if (!npc.isSpawned() && npc.hasTrait(GuardianTrait.class)) {
//						GuardianTrait guardian = npc.getTrait(GuardianTrait.class);
//						for (String target : new HashSet<String>(guardian.targets)) {
//							guardian.targets.add(GuardianTarget.forName(target).name());
//						}
//						for (String target : new HashSet<String>(guardian.ignores)) {
//							guardian.ignores.add(GuardianTarget.forName(target).name());
//						}
//						if (guardian.respawnTime > 0) {
//							if (guardian.spawnPoint == null && npc.getStoredLocation() == null) {
//								getLogger().warning("NPC " + npc.getId() + " has a null spawn point and can't be spawned. Perhaps the world was deleted?");
//								continue;
//							}
//							npc.spawn(guardian.spawnPoint == null ? npc.getStoredLocation() : guardian.spawnPoint);
//						}
//					}
//				}
			}
		};
		maxHealth = getConfig().getDouble("random.max health", 2000);
		postLoad.runTaskLater(this, 40);
		tickRate = getConfig().getInt("update rate", 10);
		getLogger().info("Guardian loaded!");
		getServer().getPluginManager().registerEvents(this, this);

//		try {
//			MetricsLite metrics = new MetricsLite(this);
//			metrics.start();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				if (!getConfig().getBoolean("stats_opt_out", false)) {
//					new StatsRecord().start();
//				}
//			}
//		}.runTaskTimer(this, 100, 20 * 60 * 60);
//
		integrations.add(new GuardianHealth());
		integrations.add(new GuardianPermissions());
		integrations.add(new GuardianSBTeams());
		integrations.add(new GuardianSquads());

//		if (Bukkit.getPluginManager().getPlugin("Towny") != null) {
//			try {
//				integrations.add(new GuardianTowny());
//				getLogger().info("Guardian found Towny! Adding support for it!");
//			}
//			catch (Throwable ex) {
//				ex.printStackTrace();
//			}
//		}

		// Configurations
		registerConfigs();

		// EventListeners
		registerListeners();

		// Menus
		//registerMenus();

		// Initialize Managers
		initManagers();

		// Commands

		ACFSetup acfSetup = injector.getInstance(ACFSetup.class);
		acfSetup.registerCommandContexts();
		acfSetup.registerCommandReplacements();
		acfSetup.registerCommandCompletions();

		registerCommands();
	}

	/**
	 * Register all Managers and classes that will process Events
	 * triggered by Bukkit or by the plugin
	 */
	private void registerListeners()
	{
		PluginManager pm = getServer().getPluginManager();

		getLogger().info("Main: Registering listeners");

		/*
		 * World and Server
		 */

//		pm.registerEvents(injector.getInstance(WorldManager.class), this);
	}

	/**
	 * Initialize Managers
	 * <p>
	 * Note: Most are instantiated by Guice when the members of this plugin class
	 * are injected in onEnable
	 */
	private void initManagers()
	{
		getLogger().info("Initialzing Managers");
//		challengeManager.setupChallenges();
	}

	/**
	 * Register Console and Player Commands with ACF
	 */
	private void registerCommands()
	{
		/*
		  Commands
		 */
		getLogger().info("Registering Commands");
		commandManager.registerCommand(injector.getInstance(GuardianCommand.class));
		commandManager.registerCommand(injector.getInstance(GuardianTargetCommand.class));
		commandManager.registerCommand(injector.getInstance(GuardianConfigCommand.class));
		commandManager.registerCommand(injector.getInstance(GuardianWorldCommand.class));
		commandManager.registerCommand(injector.getInstance(GuardianOrderCommand.class));
	}

	/**
	 * Register classes with Bukkit that implement its system for
	 * serializing and deserializing their data
	 */
	private void registerConfigs()
	{
		/*
		  Challenges and Objectives Configuration
		 */
		getLogger().info("Main: Registering Configurations");

		//ConfigurationSerialization.registerClass(Objective.class, "Objective");
	}
}
