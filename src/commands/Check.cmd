command check {
	perm utils.check;
	
	[string:player] {
		run checkCommand player;
		help Get info on a player;
	}
}