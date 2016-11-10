command register {
	perm utils.loginsecurity;
	
	[string:password] {
		run register password;
		help Protects your account with a password;
		type player;
	}
}

command login {
	perm utils.loginsecurity;
	
	[string:password] {
		run login password;
		help Logs you in;
		type player;
	}
}

command cgpass {
	perm utils.loginsecurity;
	
	[string:oldPassword] [string:newPassword] {
		run cgpass oldPassword newPassword;
		help Changes your password to the specified one;
		type player;
	}
}

command rmpass {
	perm utils.loginsecurity;
	
	[string:oldPassword] {
		run rmpass oldPassword;
		help Removes the password of your account;
		type player;
	}
}

command rmotherpass {
	perm utils.loginsecurity.admin;
	
	[string:playerName] {
		run rmotherpass playerName;
		help removes the password of another player;
		perm utils.loginsecurity.admin;
	}
}