package com.redstoner.javautils.loginSecurity;

import org.bukkit.command.CommandSender;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class LoginSecurity extends Module {
	@Override
	public String getName() {
		return "LoginSecurity";
	}
	
	@Override
	public String getDescription() {
		return "Allows you to protect your account with a password";
	}
	
	@Command(hook = "register")
	public void register(CommandSender sender, String password) {
		
	}
	
	@Command(hook = "login")
	public void login(CommandSender sender, String password) {
		
	}
	
	@Command(hook = "cgpass")
	public void cgpass(CommandSender sender, String oldPassword, String newPassword) {
		
	}
	
	@Command(hook = "rmpass")
	public void rmpass(CommandSender sender) {
		
	}
	
	//@formatter:off - turns off formatter (has to be a comment)
	@Override
	public String getCmdManagerString() {
		return
			"command register [string:password] {"
				+ "run register password;"
				+ "help Protects your account with a password;"
				+ "type player;"
			+ "}"
			
			+ "command login [string:password] {"
				+ "run login password;"
				+ "help Logs you in, allowing you to access your account;"
				+ "type player;"
			+ "}"
			
			+ "command cgpass [string:oldPassword] [string:newPassword] {"
				+ "run cgpass oldPassword newPassword;"
				+ "help Changes your password to the specified one;"
				+ "type player;"
			+ "}"
			
			+ "command rmpass {"
				+ "run rmpass;"
				+ "help Removes your account's password;"
				+ "type player;"
			+ "}";
	}
	//@formatter:on - turns on formatter (has to be a comment)
	
}
