package net.frost_byte.worldguardian;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Injector;

import me.lucko.luckperms.api.LuckPermsApi;
import net.citizensnpcs.api.CitizensAPI;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

import net.frost_byte.worldguardian.command.*;
import net.frost_byte.worldguardian.integration.GuardianHealth;
import net.frost_byte.worldguardian.integration.GuardianPermissions;
import net.frost_byte.worldguardian.integration.GuardianSBTeams;
import net.frost_byte.worldguardian.integration.GuardianSquads;
import net.frost_byte.worldguardian.targeting.GuardianTarget;
import net.frost_byte.worldguardian.utility.ConfigUpdater;
import net.frost_byte.worldguardian.utility.GuardianUtilities;
import net.frost_byte.worldguardian.utility.ProjectUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.github.games647.tabchannels.TabChannelsManager;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

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

	/**
	 * A map of typeable target names to valid targets.
	 */
	public static HashMap<String, GuardianTarget> targetOptions = new HashMap<>();

	/**
	 * A map of entity types to target types.
	 */
	public static HashMap<EntityType, HashSet<GuardianTarget>> entityToTargets = new HashMap<>();

	/**
	 * A map of target prefixes to the integration object.
	 */
	public final static HashMap<String, GuardianIntegration> integrationPrefixMap = new HashMap<>();

	/**
	 * Configuration option: maximum health value any NPC can ever have.
	 */
	public double maxHealth;

	/**
	 * Configuration option: maximum duration (in ticks) an NPC can know where a hidden target is.
	 */
	public int cleverTicks;

	/**
	 * Configuration option: whether the skull weapon is allowed.
	 */
	public boolean canUseSkull;

	/**
	 * Configuration option: whether to block some events that may cause other plugins to have issues.
	 */
	public boolean blockEvents;

	/**
	 * Configuration option: whether to use an alternative (work-around) method of applying damage.
	 */
	public boolean alternateDamage;

	/**
	 * Configuration option: whether to work-around damage-giving issues.
	 */
	public boolean workaroundDamage;

	/**
	 * Configuration option: minimum arrow shooting speed.
	 */
	public double minShootSpeed;

	/**
	 * Configuration option: whether to work-around potential NPC item drop issues.
	 */
	public boolean workaroundDrops;

	/**
	 * Configuration option: whether to enable NPC death messages.
	 */
	public boolean deathMessages;

	/**
	 * Configuration option: the sound to play when using the Spectral attack.
	 */
	public Sound spectralSound;

	/**
	 * Configuration option: whether to ignore invisible targets.
	 */
	public boolean ignoreInvisible;

	/**
	 * Configuration option: guarding distance values.
	 */
	public int guardDistanceMinimum, guardDistanceSelectionRange, guardDistanceMargin;

	/**
	 * Configuration option: whether to work-around a pathfinder issue.
	 */
	public boolean workaroundEntityChasePathfinder;

	/**
	 * Configuration option: whether to protect the NPC from being harmed by ignored entities.
	 */
	public boolean protectFromIgnores;

	/**
	 * Configuration option: Standard tick-rate for NPC updates.
	 */
	public int tickRate = 10;

	/**
	 * Configuration option: time to keep running away for.
	 */
	public int runAwayTime;

	/**
	 * Configuration option: whether block players from damaging their own guards.
	 */
	public boolean noGuardDamage;

	/**
	 * Whether debugging is enabled.
	 */
	public static boolean debugMe = false;

	/**
	 * All current integrations available to WorldGuardian.
	 */
	public final static List<GuardianIntegration> integrations = new ArrayList<>();

	/**
	 * A list of all currently spawned Sentinel NPCs.
	 */
	public ArrayList<GuardianTrait> currentGuardianNPCs = new ArrayList<>();

	/**
	 * Permissions handler.
	 */
	public Permission vaultPerms;

	/**
	 * Expected configuration file version.
	 */
	public final static int CONFIG_VERSION = 9;

	/**
	 * Prefix string for an inventory title.
	 */
	public final static String InvPrefix = ChatColor.GREEN + "Guardian ";

	static {
		for (EntityType type : EntityType.values()) {
			entityToTargets.put(type, new HashSet<>());
		}
	}

	/**
	 * Registers a new integration to Sentinel.
	 */
	public void registerIntegration(GuardianIntegration integration) {
		integrations.add(integration);
		for (String prefix : integration.getTargetPrefixes()) {
			integrationPrefixMap.put(prefix, integration);
		}
	}

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
			msgList.forEach(m -> channelsManager.sendComponent("global", pid, fromLegacyText(m)));
		else
			player.sendMessage(messages);
	}

	public void broadcastChannelMessage(String... messages)
	{
		if (messages == null || messages.length == 0)
			return;

		List<String> msgList = Arrays.asList(messages);

		if (channelsManager != null)
			msgList.forEach(m -> channelsManager.broadcastComponent("global", fromLegacyText(m)));
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
		try {
			// Automatic config file update
			InputStream properConfig = WorldGuardianPlugin.class.getResourceAsStream("/config.yml");
			String properConfigString = GuardianUtilities.streamToString(properConfig);
			properConfig.close();
			FileInputStream currentConfig = new FileInputStream(getDataFolder() + "/config.yml");
			String currentConfigString = GuardianUtilities.streamToString(currentConfig);
			currentConfig.close();
			String updated = ConfigUpdater.updateConfig(currentConfigString, properConfigString);
			if (updated != null) {
				getLogger().info("Your config file is outdated. Automatically updating it...");
				FileOutputStream configOutput = new FileOutputStream(getDataFolder() + "/config.yml");
				OutputStreamWriter writer = new OutputStreamWriter(configOutput);
				writer.write(updated);
				writer.close();
				configOutput.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		loadConfigSettings();
		BukkitRunnable postLoad = new BukkitRunnable() {
			@Override
			public void run() {
				for (NPC npc : CitizensAPI.getNPCRegistry()) {
					if (!npc.isSpawned() && npc.hasTrait(GuardianTrait.class)) {
						GuardianTrait guardian = npc.getTrait(GuardianTrait.class);
						if (guardian.respawnTime > 0) {
							if (guardian.spawnPoint == null && npc.getStoredLocation() == null) {
								getLogger().warning("NPC " + npc.getId() + " has a null spawn point and can't be spawned. Perhaps the world was deleted?");
								continue;
							}
							npc.spawn(guardian.spawnPoint == null ? npc.getStoredLocation() : guardian.spawnPoint);
						}
					}
				}
			}
		};
		postLoad.runTaskLater(this, 40);
		getLogger().info("Guardian loaded!");
		getServer().getPluginManager().registerEvents(this, this);


		registerIntegration(new GuardianHealth());
		registerIntegration(new GuardianPermissions());
		registerIntegration(new GuardianSBTeams());
		registerIntegration(new GuardianSquads());

		// Configurations
		registerConfigs();

		// EventListeners
		registerListeners();

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
	 * Load the Configuration for the Plugin
	 */
	private void loadConfigSettings()
	{
		reloadConfig();
		cleverTicks = getConfig().getInt("random.clever ticks", 10);
		canUseSkull = getConfig().getBoolean("random.skull allowed", true);
		blockEvents = getConfig().getBoolean("random.workaround bukkit events", false);
		alternateDamage = getConfig().getBoolean("random.enforce damage", false);
		workaroundDamage = getConfig().getBoolean("random.workaround damage", false);
		minShootSpeed = getConfig().getDouble("random.shoot speed minimum", 20);
		workaroundDrops = getConfig().getBoolean("random.workaround drops", false) || blockEvents;
		deathMessages = getConfig().getBoolean("random.death messages", true);
		try {
			spectralSound = Sound.valueOf(getConfig().getString("random.spectral sound", "ENTITY_VILLAGER_YES"));
		}
		catch (Throwable e) {
			getLogger().warning("Sentinel Configuration value 'random.spectral sound' is set to an invalid sound name. This is usually an ignorable issue.");
		}
		ignoreInvisible = getConfig().getBoolean("random.ignore invisible targets");
		guardDistanceMinimum = getConfig().getInt("random.guard follow distance.minimum", 7);
		guardDistanceMargin = getConfig().getInt("random.guard follow distance.selction range", 4);
		guardDistanceSelectionRange = getConfig().getInt("random.guard follow distance.margin", 2);
		workaroundEntityChasePathfinder = getConfig().getBoolean("random.workaround entity chase pathfinder", false);
		protectFromIgnores = getConfig().getBoolean("random.protected", false);
		runAwayTime = getConfig().getInt("random.run away time");
		maxHealth = getConfig().getDouble("random.max health", 2000);
		noGuardDamage = getConfig().getBoolean("random.no guard damage", true);
		tickRate = getConfig().getInt("update rate", 10);
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
