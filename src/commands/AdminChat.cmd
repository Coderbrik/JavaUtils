command ac {
	[string:message...] {
		help Sends a message in Admin Chat;
		perm utils.ac;
		run ac_msg message;
	}
}
		
command ackey {
	[string:key] {
		help Sets your Admin Chat key;
		perm utils.ac;
		type player;
		run ackey key;
	}
}