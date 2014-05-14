package DFLDA;

import java.util.ArrayList;
import java.util.List;

import TM.Config.DFLDAConfig;


import util.File_util;

public class SeedWords {

	public static List<List<String>> seedWordsList;
	public static List<List<String>> mustlinkList;
	
	static{	
		List<String> tagsSens = File_util.readFile(DFLDAConfig.seedPath);
		seedWordsList = new ArrayList<List<String>>();
		for(String s: tagsSens){
			List<String> temp = new ArrayList<String>();
			String[] tags = s.split(" ");
			for(String tag: tags){
				temp.add(tag.toLowerCase());
			}
			seedWordsList.add(temp);
		}
		
	}

}
