package thu.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.mallet.MalletInputFileGenerator;



public class fileConvert {

	public static void toMalletFile(String srcPath,String desPath,boolean segFlag){
		try{
			ArrayList<String> resList = fileToList(srcPath);
			if(segFlag){
				//需要分词
				MalletInputFileGenerator.generateInputFile(resList, desPath);
			}
			else{
				File des = new File(desPath);
				PrintWriter pw = new PrintWriter(des);
				int count = 1;
				for(String s : resList){
					pw.println(count + " X " + s);
					count++;
				}
				pw.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void toStandfordTMFile(String srcPath,String desPath,boolean segFlag){
		try{
			ArrayList<String> resList = fileToList(srcPath);
			if(segFlag){
				//需要分词
				MalletInputFileGenerator.generateInputFile(resList, desPath);
			}
			else{
				File des = new File(desPath);
				PrintWriter pw = new PrintWriter(des);
				int count = 1;
				for(String s : resList){
					pw.println(count + ",X," + s);
					count++;
				}
				pw.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void malletFileToStandfordTMFile(String srcPath,String desPath){
		try{
			ArrayList<String> resList = malletFileToList(srcPath);
//			System.out.println(resList);
			File des = new File(desPath);
			PrintWriter pw = new PrintWriter(des);
			int count = 1;
			for(String s : resList){
				pw.println(count + ",X," + s);
				count++;
			}
			pw.close();	
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static ArrayList<String> fileToList(String srcPath){
		try{
			ArrayList<String> resList = new ArrayList<String>();
			File src = new File(srcPath);
			String enc = "GBK";
			FileInputStream fis = new FileInputStream(src);
			UnicodeInputStream uin = new UnicodeInputStream(fis, enc); 
			enc = uin.getEncoding(); // check and skip
			InputStreamReader in;
			if (enc == null) 
				in = new InputStreamReader(uin);
			else 
				in = new InputStreamReader(uin, enc);	
			BufferedReader bf = new BufferedReader(in);
			String str;
			while((str=bf.readLine())!=null){
				resList.add(str);
			}
			bf.close();
			in.close();
			return resList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static ArrayList<String> malletFileToList(String srcPath){
		try{
			ArrayList<String> resList = new ArrayList<String>();
			File src = new File(srcPath);
			String enc = "GBK";
			FileInputStream fis = new FileInputStream(src);
			UnicodeInputStream uin = new UnicodeInputStream(fis, enc); 
			enc = uin.getEncoding(); // check and skip
			InputStreamReader in;
			if (enc == null) 
				in = new InputStreamReader(uin);
			else 
				in = new InputStreamReader(uin, enc);	
			BufferedReader bf = new BufferedReader(in);
			String str;
			while((str=bf.readLine())!=null){
				String[] temp = str.split(" ");
				if(temp.length > 2){
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < temp.length; i++){
						sb.append(temp[i] + " ");
					}
					resList.add(sb.toString());
				}
			}
			bf.close();
			in.close();
			return resList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String srcPath = "newLDA/trandocs_mallet.dat";
		String desPath = "newLDA/trandocs_standfordTM.dat";
		malletFileToStandfordTMFile(srcPath, desPath);
	}

}
