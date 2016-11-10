command an {
	perm utils.adminnotes;
	
	add [string:note...] {
		type player;
		help Creates a new admin note;
		run an_create note;
	}
	
	del [int:id] {
		help Deletes an admin note;
		run an_del id;
	}
	
	list {
		help Lists all notes;
		run an_list;
	}
}