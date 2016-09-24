import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import redempt.moduleloader.Module;
import redempt.moduleloader.ModuleLoader;

public class SkullClick extends Module implements Listener {
	
	@Override
	public void onEnable() {
		ModuleLoader.registerEvent(this);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public String getName() {
		return "SkullClick";
	}

	@Override
	public String getDescription() {
		return "Shows a skull's owner when it is right clicked";
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.isCancelled()) {
			BlockState block = e.getClickedBlock().getState();
			if (block instanceof Skull) {
				Skull skull = (Skull) block;
				String owner = skull.getOwner();
				if (owner == null || owner.equals("")) {
					e.getPlayer().sendMessage(ChatColor.YELLOW + "That skull has no owner.");
				} else {
					e.getPlayer().sendMessage(ChatColor.YELLOW + "That's " + owner + ".");
				}
			}
		}
	}
	
}
