World Guardian: Modified Sentinel NPCs for Spigot
-------------------------------------------------
**Version 1.0.0**: Compatible with Spigot 1.13

### Info

First of all, the vast majority of the code and functionality was taken
straight from Guardians, created by mcmonkey4eva.

Here is what has been added:
- Set a teleportation destination for when a player interacts while sneaking
with an npc.
- Add a list of dialogue options that are randomly chosen from when a player
approaches the npc.
- Add a list of farewell messages that are randomly selected from when
a player teleports to the guardian's destination location.
- Command Completions and Command functionality using the [Annotated Command
Framework][5]
- Setting a Guardian's targets view the category:REGEX|String have been
refactored into a new command.

Note: I may refactor all of this so it just adds the above features and
not just a total rewrite of Guardians, because as it is now it'll be much
harder to maintain or keep up to date.  The intention isn't just to make
a knock off of Guardians but to add some functionality or another trait.

Instead of mirroring all of the Documentation for Guardians, I will only
describe the changes here.

Please read the [Sentinel Documentation][1]

### Usage

- First, get acquainted with Citizens in general for best luck using Guardian.
- Second, download the plugin and put it into your server's `plugins` folder.
- Third, start the server to generate a config, then close the server and edit the config to your liking and finally restart the server.
- Now, to create your first Guardian:
	- Select or create an NPC (`/npc sel` or `/npc create Bob`)
	- Run command: `/trait Guardian`
	- Run command: `/npc equip`
	- Give the NPC items as needed, by right click the NPC with the wanted item.
	- Run command: `/guardian add MONSTERS`
	- Spawn a zombie via creative inventory eggs and watch it die!
	- Run command: `/guardian help`
		- This will list all your options to edit the NPC's Guardian settings.
			- Play with them freely, just be careful if you have other players around!
			- Do note, they won't attack you unless you run command: `/guardian iremove owner`.
- Examples:
	- To make your NPC attack sword wielders, use `/guardian add_category helditem .*sword`

### Commands

NOTES:
- \+ indicates a change from the format/name of the command in Sentinels
- & indicates a new command
- \+ /guardian [help] - Shows help info.
- \+ /guardian add TYPE - Adds a target.
- \+ /guardian add_category - Adds a target by Category using a search string
- \+ /guardian remove TYPE - Removes a target.
- \+ /guardian iadd TYPE - Ignores a target.
- \+ /guardian iadd_category CATEGORY REGEX - Ignores by Category using a seach string
- \+ /guardian iremove TYPE - Allows targeting a target.
- \+ /guardian irem_category CATEGORY REGEX - Removes targets from Ignores by Category using a seach string
- & /guardian destination - Specify a location (world_name:x,y,z,[yaw],[pitch]) to teleport players to when they
sneak and right click the guardian.
- & /guardian dialogue MESSAGE - Adds a message to the list of dialogue options that the
guardian will randomly greet players with when they approach
- & /guardian farewell MESSAGE - Adds a farewell message to the list of things the
guardian will say when the player uses them to teleport
- /guardian range RANGE - Sets the NPC's maximum attack range.
- /guardian damage DAMAGE - Sets the NPC's attack damage.
- /guardian armor ARMOR - Sets the NPC's armor level.
- /guardian health HEALTH - Sets the NPC's health level.
- /guardian attackrate RATE ['ranged'] - Changes the rate at which the NPC attacks, in seconds. Either ranged or close modes.
- /guardian healrate RATE - Changes the rate at which the NPC heals, in seconds.
- /guardian respawntime TIME - Changes the time it takes for the NPC to respawn, in seconds.
- /guardian chaserange RANGE - Changes the maximum distance an NPC will run before returning to base.
- /guardian guard (PLAYERNAME) - Makes the NPC guard a specific player. Don't specify a player to stop guarding.
- /guardian invincible - Toggles whether the NPC is invincible.
- /guardian fightback - Toggles whether the NPC will fight back.
- /guardian needammo - Toggles whether the NPC will need ammo.
- /guardian safeshot - Toggles whether the NPC will avoid damaging non-targets.
- /guardian chaseclose - Toggles whether the NPC will chase while in 'close quarters' fights.
- /guardian chaseranged - Toggles whether the NPC will chase while in ranged fights.
- /guardian drops - Changes the drops of the current NPC.
- /guardian spawnpoint - Changes the NPC's spawn point to its current location, or removes it if it's already there.
- /guardian forgive - Forgives all current targets.
- /guardian enemydrops - Toggles whether enemy mobs of this NPC drop items.
- /guardian info - Shows info on the current NPC.
- /guardian stats - Shows statistics about the current NPC.
- /guardian targets - Shows the targets of the current NPC.
- /guardian kill - Kills the NPC.
- /guardian respawn - Respawns the NPC.
- /guardian targettime TIME - Sets the NPC's enemy target time limit in seconds.
- /guardian speed - Sets the NPC's movement speed modifier.
- /guardian autoswitch - Toggles whether the NPC automatically switches items.
- /guardian greeting GREETING - Sets a greeting message for the NPC to say.
- /guardian warning WARNING - Sets a warning message for the NPC to say.
- /guardian greetrange RANGE - Sets how far a player can be from an NPC before they are greeted.
- /guardian accuracy OFFSET - Sets the accuracy of an NPC.
- /guardian squad SQUAD - Sets the NPC's squad name (null for none).
- /guardian realistic - Toggles whether the NPC should use "realistic" targeting logic (don't attack things you can't see.)
- /guardian reach REACH - Sets the NPC's reach (how far it can punch.)

### Sentry user?

Type "/guardian sentryimport" on a server running both Sentry and Guardian to instantly transfer all data to Guardian!

### Permissions
- & guardian.dialogue for adding dialogue messages for the Guardian
- & guardian.farewell for adding farewell messages for the Guardian
- & guardian.destination for adding the teleport destination for players
- guardian.basic for the /guardian command
- guardian.admin to edit other player's Guardian NPCs.
- guardian.greet for commands: greeting, warning, greetrange
- guardian.info for commands: info, stats, targets
- Everything else is "guardian.X" where "X" is the command name, EG "guardian.damage".

### Targets

These are all valid targets and ignores:

- Primary set: NPCS, OWNER, PASSIVE_MOB, MOBS, MONSTERS, PLAYERS, PIGS, OCELOTS, COWS, RABBITS, SHEEP, CHICKENS, HORSES, MUSHROOM_COW, IRON_GOLEMS, SQUIDS, VILLAGER, WOLF, SNOWMEN, WITCH, GUARDIANS, SHULKERS, CREERERS, SKELETONS, ZOMBIES, MAGMA_CUBES, ZOMBIE_PIGMEN, SILVERFISH, BATS, BLAZES, GHASTS, GIANTS, SLIME, SPIDER, CAVE_SPIDERS, ENDERMEN, ENDERMITES, WITHER, ENDERDRAGON
- + Also allowed: Category REGEX|String
  - Valid categories:
    - player, npc, entityname: A Regex|String that matches a Name
    - helditem Regex|String matches or is a Material Name
    - group: The exact name of a group
    - event: A string matching pvp, pvnpc, pve, or pvguardian
    - sbteam: The name of a scoreboard team
    - healthabove, healthbelow: A percentage
    - permission: A string matching a permission key, PERM.KEY
    - squad: A string exactly matching a Guardian Squad Name

### Some random supported things

- Weapons: see the [Sentinel Documentation][1]

### Dependencies

- **[Spigot (Plugin-ready server mod)][3]**
- **[Citizens2 (NPC engine)][4]**

#### Also check out:

- **[Sentinel (Combat NPCs for Spigot!)][1]**
- **[Denizen (Powerful script engine)][2]**

[1]:https://github.com/mcmonkey4eva/Sentinel
[2]:https://github.com/DenizenScript/Denizen-For-Bukkit
[3]:https://www.spigotmc.org/
[4]:https://github.com/CitizensDev/Citizens2/
[5]:https://github.com/aikar/commands/wiki