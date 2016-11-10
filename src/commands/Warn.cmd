command warn {
	[string:name] {
		run warn name;
		type player;
		help Warns other players about definite lag;
		perm utils.warn;
	}
}

command warnp {
	[string:name] {
		run warnp name;
		type player;
		help Warns other players about possible lag;
		perm utils.warn;
	}
}