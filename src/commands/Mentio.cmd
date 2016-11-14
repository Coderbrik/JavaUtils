command mentio {
	perm utils.mentio;
	
	add [string:word] {
		run addWord word;
		help Adds a word to your mentions.;
		type player;
	}
	
	del [string:word] {
		run delWord word;
		help Removes a word from your mentions.;
		type player;
	}
	
	list {
		run listWords;
		help Lists words that mention you.;
		type player;
	}
}