command chatgroup {
	perm utils.chatgroups;
	
	join [string:name] {
		run cgjoin name;
		help Joins the specified chat group.;
		type player;
	}
	
	leave {
		run cgleave;
		help Leaves the group you are currently in.;
		type player;
	}
	
	info {
		run cginfo;
		help Shows information about the group you are currently in.;
		type player;
	}
	
	key {
		run cgshowkey;
		help Shows your current chatgroup key.;
		type player;
	}
}

command cgt {
	perm utils.chatgroups;
	
	[string:nil] {
		run cgtoggle nil;
		help Toggles sending all messages to chatgroups.;
		type player;
	}
}

command cgkey {
	perm utils.chatgroups;
	
	[string:key] {
		run cgkey key;
		help Sets your chatgroup key to the specified one.;
		type player;
	}
}