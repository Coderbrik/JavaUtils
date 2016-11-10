command damnspam {
	perm utils.damnspam;
	
	[double:seconds] {
		run damnspamSingle seconds;
		help Set single input cooldown for button or lever.;
		type player;
	}
	
	[double:secondsOff] [double:secondsOn] {
		run damnspamDouble secondsOff secondsOn;
		help Set input cooldown after it's been turned off and turned on (for lever only).;
		type player;
	}
}