package me.HybridPlague.Freeze;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
	}
	
	@Override
	public void onDisable() {
		this.getConfig().set("Frozen-Players", null);
		this.saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("freeze")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command is only executable by a player.");
				return true;
			}
			Player p = (Player) sender;
			if (!p.hasPermission("freeze.use")) {
				p.sendMessage(ChatColor.RED + "Insufficient permission.");
				return true;
			}
			if (args.length == 0) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "/freeze <player>"));
				return true;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]); 
			if (!target.isOnline()) {
				p.sendMessage("Player not found.");
				return true;
			}
			if (target.getPlayer().hasPermission("freeze.bypass")) {
				p.sendMessage("You cannot freeze that person!");
				return true;
			}
			if (target.equals(p)) {
				p.sendMessage("You cannot freeze yourself!");
				return true;
			}
			if (this.getConfig().getStringList("Frozen-Players").contains(target.getName())) {
				List<String> f = this.getConfig().getStringList("Frozen-Players");
				f.remove(target.getName());
				this.getConfig().set("Frozen-Players", f);
				this.saveConfig();
				target.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou are no longer frozen."));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + target.getName() + " is no longer frozen!"));
				return true;
			}
			List<String> f = this.getConfig().getStringList("Frozen-Players");
			f.add(target.getName());
			this.getConfig().set("Frozen-Players", f);
			this.saveConfig();
			target.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou have been frozen by " + p.getName() + "."));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + target.getName() + " is now frozen!"));
			return true;
			
		}
		return false;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = (Player) e.getPlayer();
		if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
			if (e.getTo().getX() == e.getFrom().getX() && e.getTo().getY() == e.getFrom().getY() && e.getTo().getZ() == e.getFrom().getZ()) return;
			e.setCancelled(true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
			return;
		}
	}
	
	@EventHandler
	public void onCMD(PlayerCommandPreprocessEvent e) {
		Player p = (Player) e.getPlayer();
		if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
			return;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		try {
			Player p = (Player) e.getPlayer();
			if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
				return;
			}
		} catch (Exception ex) {
			
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		try {
			Player p = (Player) e.getPlayer();
			if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
				return;
			}
			
			
		} catch (Exception ex) {
			
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		try {
			Entity eAttack = e.getDamager();
			Entity eDamaged = e.getEntity();
			
			if (eAttack instanceof Player) {
				if (this.getConfig().getStringList("Frozen-Players").contains(eDamaged.getName())) {
					e.setCancelled(true);
					eAttack.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cPlayer cannot be harmed!"));
					return;
				}
			} else if (eAttack instanceof Projectile) {
				if (this.getConfig().getStringList("Frozen-Players").contains(eDamaged.getName())) {
					Projectile projectile = (Projectile) e.getDamager();
					Object shooter = projectile.getShooter();
					if (shooter instanceof Player) {
						((Player) shooter).sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cPlayer cannot be harmed!"));
					}
					e.setCancelled(true);
					return;
				}
			}
			
			/*if (e.getEntity() instanceof Player) {
				if (this.getConfig().getStringList("Frozen-Players").contains(eDamager.getName())) {
					e.setCancelled(true);
					e.setDamage(0);
					eAttack.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cPlayer cannot be harmed!"));
					return;
				}
			}*/
		} catch (Exception ex) {
			
		}
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		try {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
					return;
				}
			}
		} catch (Exception ex) {
			
		}
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = (Player) e.getPlayer();
		if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lFrozen &cYou are not allowed to do!"));
			return;
		}
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (this.getConfig().getStringList("Frozen-Players").contains(p.getName())) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
}
