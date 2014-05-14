package sentiment.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

public class StopDict {

	public static HashSet<String> stopword;
	
	
	public static void loadStopDict(){
		String path = "TopicModelData\\IAR_Dict\\stopwords.dat";
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(path)), "GBK");
			BufferedReader bf = new BufferedReader(isr);
			stopword = new HashSet<String>();
			String str;
			while((str = bf.readLine())!=null){
				stopword.add(str.trim());
			}
			bf.close();
			isr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static{
		loadStopDict();
	}
	

}
