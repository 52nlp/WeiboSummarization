package util.mallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

public class MalletInput {

	//所有的instance必须passing同一个pipe对象
	public static ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
	
	static{
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_.]+")) );
		pipeList.add( new TokenSequence2FeatureSequence());
	}
	
	
	public static InstanceList getInstanceList(String malletFileName){
		InstanceList instances = null;
		try{
			instances = new InstanceList (new SerialPipes(pipeList));
			Reader insfileReader = new InputStreamReader(new FileInputStream(new File(malletFileName)), "utf-8");
			instances.addThruPipe(new CsvIterator (insfileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
					   3, 2, 1)); 
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return instances;
	}
	
	
	public static InstanceList getInstanceList(File srcFile){
		InstanceList instances = null;
		try{
			instances = new InstanceList (new SerialPipes(pipeList));
			Reader insfileReader = new InputStreamReader(new FileInputStream(srcFile), "utf-8");
			instances.addThruPipe(new CsvIterator (insfileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
					   3, 2, 1)); 
//			instances.addThruPipe(new CsvIterator (insfileReader, Pattern.compile(""),
//					   3, 2, 1)); 
			insfileReader.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return instances;
	}
	
	
	public static InstanceList getInstanceList(List<String> segSensList){
		File srcFile = generateMalletTempInputFile(segSensList);
		InstanceList instances  = getInstanceList(srcFile);
		return instances;
		
	}
	
	//预处理后的文本
	public static File generateMalletTempInputFile(List<String> segSensList){
		File des = null;
		try {
			des = File.createTempFile("mallet_", ".txt");
			PrintWriter pw = new PrintWriter(des,"utf-8");
			for(String s: segSensList){
				pw.println(System.currentTimeMillis() + " X " + s);
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally { 
			des.deleteOnExit(); 
		} 
		return des;
	}
	
	
	public static void main(String[] args){
		List<String> sens1 = new ArrayList<String>();
		sens1.add("中国");
		List<String> sens2 = new ArrayList<String>();
		sens2.add("人民");
		sens2.add("hahahah");
		
//		System.out.println(generateMalletTempInputFile(sens1));
//		System.out.println(getInstanceList(sens1).size());
		
		InstanceList res = getInstanceList(sens1);
		InstanceList res1 = getInstanceList(sens2);
	
//		res.getDataAlphabet().startGrowth();
		res.addAll(res1);
		System.out.println(res.size());
		
		System.out.println(res.getDataAlphabet());
	}
}
