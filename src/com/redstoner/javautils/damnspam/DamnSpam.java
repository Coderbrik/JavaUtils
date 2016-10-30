package com.redstoner.javautils.damnspam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;

public class DamnSpam extends Module implements Listener {

	@Override
	public String getDescription() {
		return "Allows a cooldown on buttons and levers to stop spam.";
	}

	@Override
	public String getName() {
		return "DamnSpam";
	}

	public String getCmdManagerString() {
		return "command damnspam {"
				+ "  [double:seconds] {"
				+ "    run damnspamSingle seconds;"
				+ "    help Set single input cooldown for button or lever.;"
				+ "    type player;"
				+ "  }"
				+ "  [double:secondsOff] [double:secondsOn] {"
				+ "    run damnspamDouble secondsOff secondsOn;"
				+ "    help Set input cooldown after it's been turned off and turned on (for lever only).;"
				+ "    type player;"
				+ "  }"
				+ "}";
	};

	ModuleLoader loader;

	Map<String, String> inputs;

	boolean removingInput = false;
	
	List<Material> acceptedInputs;
	HashMap<Material, int[][]> attachedBlocks;

	HashMap<Player, SpamInput> players;

	int maxTimeout = 240;

	String timeoutErrorString = "&cThe timeout must be -1 or within 0 and " + maxTimeout;

	@Override
	public void onEnable() {
		loader = ModuleLoader.getLoader();

		inputs = loader.getConfiguration("DamnSpam.json");

		acceptedInputs = new ArrayList<Material>();
		Collections.addAll(acceptedInputs, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.LEVER);

		attachedBlocks = new HashMap<Material, int[][]>();
		attachedBlocks.put(Material.LEVER,
				new int[][] { { 0, 7, 8, 15 }, { 5, 6, 13, 14 }, { 4, 12 }, { 3, 11 }, { 2, 10 }, { 1, 9 } });
		attachedBlocks.put(Material.STONE_BUTTON,
				new int[][] { { 0, 8 }, { 5, 6, 7, 13, 14, 15 }, { 4, 12 }, { 3, 11 }, { 2, 10 }, { 1, 9 } });
		attachedBlocks.put(Material.WOOD_BUTTON,
				new int[][] { { 0, 8 }, { 5, 6, 7, 13, 14, 15 }, { 4, 12 }, { 3, 11 }, { 2, 10 }, { 1, 9 } });

		players = new HashMap<Player, SpamInput>();
	}

	public void saveInputs() {
		loader.saveConfiguration(inputs, "DamnSpam.json");
	}

	public String locationString(Location loc) {
		return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}

	public boolean isAcceptableTimeout(double timeout) {
		return (timeout > 0 && timeout <= maxTimeout) || timeout == -1;
	}

	public boolean canBuild(Player player, Block block) {
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		Bukkit.getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	@Command(hook = "damnspamSingle")
	public void damnspam(CommandSender sender, double seconds) {
		boolean destroyingInput = false;
		seconds = (double) Math.round(seconds * 100) / 100;
		if (seconds == 0)
			destroyingInput = true;
		else if (!isAcceptableTimeout(seconds)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cThe timeout must be -1 or within 0 and " + maxTimeout));
			return;
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPlease click the input you would like to set."));
		setPlayer((Player) sender, destroyingInput, seconds, seconds);
	}

	@Command(hook = "damnspamDouble")
	public void damnspam(CommandSender sender, double secondsOff, double secondsOn) {
		boolean destroyingInput = false;
		secondsOn = (double) Math.round(secondsOn * 100) / 100;
		secondsOff = (double) Math.round(secondsOff * 100) / 100;
		if (secondsOn == 0 && secondsOff == 0) {
			destroyingInput = true;
		} else if (!(isAcceptableTimeout(secondsOn) && isAcceptableTimeout(secondsOff))) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cThe timeout must be -1 or within 0 and " + maxTimeout));
			return;
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPlease click the input you would like to set."));
		setPlayer((Player) sender, destroyingInput, secondsOff, secondsOn);
	}

	public void setPlayer(Player player, boolean destroying, double timeoutOff, double timeoutOn) {
		SpamInput input = null;
		if (!destroying) {
			input = new SpamInput(player, timeoutOff, timeoutOn, 0);
		}
		players.put(player, input);
	}

	public boolean attemptInputRegister(Player player, Block block, Cancellable event) {
		if (players.containsKey(player)) {
			if (!acceptedInputs.contains(block.getType())) {
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cThat block is not an acceptable input!"));
				return true;
			}

			String typeStr = block.getType().toString().toLowerCase().replace("_", " ");
			String locationStr = locationString(block.getLocation());

			removingInput = true;
			boolean buildCheck = canBuild(player, block);
			removingInput = false;
			
			if (!buildCheck) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cThere is no timeout to remove on this " + typeStr + "(by setting the timeout to 0)"));
				return true;
			}

			SpamInput input = players.get(player);
			if (input == null) {
				if (!inputs.containsKey(locationStr)) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&cThere is no timeout to remove on this " + typeStr + "(by setting the timeout to 0)"));
					return true;
				}
				inputs.remove(locationStr);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aSuccessfully removed the timeout for this " + typeStr));
			} else {
				inputs.put(locationStr, players.get(player).toString());
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aSuccessfully set a timeout for this " + typeStr));
			}
			players.remove(player);
			saveInputs();
			return true;
		}
		return false;
	}

	public void checkBlockBreak(BlockBreakEvent event, Block block) {
		if (!acceptedInputs.contains(block.getType()))
			return;

		String posStr = locationString(block.getLocation());
		if (!inputs.containsKey(posStr))
			return;

		Player sender = event.getPlayer();

		String typeStr = block.getType().toString().toLowerCase().replace("_", " ");
		String inputStr = (block.getLocation().equals(event.getBlock()) ? "this " + typeStr
				: "the " + typeStr + " attached to that block");

		if (!sender.isSneaking()) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot destroy " + inputStr));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&c&nSneak&c and break or set the timeout to 0 if you want to remove it."));
			event.setCancelled(true);
			return;
		}

		removingInput = true;
		boolean success = canBuild(sender, block);
		removingInput = false;
		
		if (success) {
			inputs.remove(posStr);
			saveInputs();
			sender.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&aSuccesfully removed " + inputStr));
		} else {
			sender.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&cYou are not allowed to remove " + inputStr));
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	public List<Block> getAttachedBlocks(Block block) {
		List<Block> blocks = new ArrayList<Block>();

		BlockFace[] directions = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
				BlockFace.EAST };

		for (int i = 0; i < directions.length; i++) {
			Block side = block.getRelative(directions[i]);
			int[][] dvalues = attachedBlocks.get(side.getType());
			if (dvalues != null) {
				boolean onSide = false;
				for (int val : dvalues[i]) {
					if (side.getData() == (byte) val) {
						onSide = true;
						break;
					}
				}

				if (onSide)
					blocks.add(side);
			}
		}

		return blocks;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBreak(BlockBreakEvent event) {
		if (removingInput || event.isCancelled())
			return;

		boolean register = attemptInputRegister(event.getPlayer(), event.getBlock(), event);
		
		if(!register) {
			Block block = event.getBlock();
			checkBlockBreak(event, block);
			for (Block affected : getAttachedBlocks(block)) {
				checkBlockBreak(event, affected);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		boolean register = attemptInputRegister(event.getPlayer(), event.getClickedBlock(), event);

		if (!register && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.isCancelled()) {
			Player sender = event.getPlayer();
			Block block = event.getClickedBlock();
			String posStr = locationString(block.getLocation());
			String inputStr = inputs.get(posStr);
			if (inputStr != null) {
				SpamInput data = SpamInput.fromString(inputStr);
				String btype = block.getType().toString().toLowerCase().replace("_", " ");
				double checktime = 0;
				if (btype.equals("lever") && block.getData() < 8)
					checktime = data.getTimeoutOff();
				else
					checktime = data.getTimeoutOn();

				double timeLeft = (data.getLastTime() + checktime) - ((double) Math.round((double) System.currentTimeMillis() / 10) / 100);
				
				timeLeft = (double) Math.round(timeLeft * 100) / 100;
				
				if(checktime == -1){
					event.setCancelled(true);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis " + btype + " is locked permanently by /damnspam."));
				} else if(timeLeft > 0){
					event.setCancelled(true);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis " + btype + " has a damnspam timeout of " + checktime + ", with " + timeLeft + " left."));
				} else {
					data.setLastTime(((double) Math.round((double) System.currentTimeMillis() / 10) / 100));
				}
				
				inputs.put(posStr, data.toString());
			}
		}
	}

}
