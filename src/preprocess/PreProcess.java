package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ICTCLAS.I3S.AC.SegTagICTCLAS50;

public class PreProcess {
	protected int wordNumber=0;
	protected int lineNumber=0;
	protected Vector<String> wordList;
	protected Vector<Integer> wordFrequency;
	protected Vector<String> stopWords;
	protected String speechChosen;
	
	//Construction function
	public PreProcess(){
		wordList=new Vector<String>();
		wordFrequency=new Vector<Integer>();
		stopWords=new Vector<String>();
		speechChosen="";
		if(Config.def==true){
			speechChosen+="^";
			if(Config.noun==false)
				speechChosen+="n";
			if(Config.verb==false)
				speechChosen+="v";
			if(Config.adj==false)
				speechChosen+="a";
			if(Config.adv==false)
				speechChosen+="d";
			if(Config.qua==false)
				speechChosen+="q";
			if(Config.num==false)
				speechChosen+="m";
			if(Config.unk==false)
				speechChosen+="x";
		}else{
			if(Config.noun==true)
				speechChosen+="n";
			if(Config.verb==true)
				speechChosen+="v";
			if(Config.adj==true)
				speechChosen+="a";
			if(Config.adv==true)
				speechChosen+="d";
			if(Config.qua==true)
				speechChosen+="q";
			if(Config.num==true)
				speechChosen+="m";
			if(Config.unk==true)
				speechChosen+="x";
		}
	}

	//Load stopwords
	public boolean loadStopWords(){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(Config.path+Config.stopWordFile),"UTF-8"));
			String buffer="";
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				for(int i=0;i<words.length;i++)
					stopWords.add(words[i]);
			}
			reader.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+Config.path+" "+Config.stopWordFile);
			e.printStackTrace();
			return false;
		}
	}

	

	//Split words and build the word list
	public boolean splitWord(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			String buffer;
			//int number=0;
			while((buffer=reader.readLine())!=null){
				//number++;
				if(buffer.equals(""))
				//System.out.println(number);
					continue;
				String line=SegTagICTCLAS50.segTag(buffer);	//split
				String[] words=line.split(" ");
				for(int i=0;i<words.length;i++){
					if(words[i].equals("")||words[i]==null)
						continue;
					writer.write(words[i]+" ");
					if(words[i].indexOf("/w")>=0)			//Filter the word with of suffix /w
						continue;
					int pos=words[i].indexOf("/");
					if(pos<0)
						continue;
					int exist=wordList.indexOf(words[i]);	//Build the vocabulary list
					if(exist>=0){
						wordFrequency.set(exist,wordFrequency.get(exist)+1);
					}else{
						wordList.add(words[i]);
						wordFrequency.add(1);
					}
					wordNumber++;
				}
				if(words.length>0)
					writer.write("\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	//take off stop words
	public boolean takeOffStopWords(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				for(int i=0;i<words.length;i++){
					int pos=words[i].indexOf("/");
					if(pos<0)
						continue;
					String rawword=words[i].substring(0,pos);
					if(stopWords.contains(rawword)){
						int index=wordList.indexOf(words[i]);
						if(index>=0){
							wordNumber-=wordFrequency.get(index);
							wordList.remove(index);
							wordFrequency.remove(index);
						}
					}else{
						writer.write(words[i]+" ");
					}
				}
				writer.write("\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	//select words whose occurrence is above the threshold
	public boolean selectWords(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			int threshold=(int)((double)wordNumber*Config.frequencyThresholdRate);
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				boolean empty=true;
				for(int i=0;i<words.length;i++){
					int pos=wordList.indexOf(words[i]);
					if(pos>=0&&wordFrequency.get(pos)>threshold){
						writer.write(words[i]+" ");
						empty=false;
					}
				}
				if(empty==false)
					writer.write("\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	//take off speech tags
	public boolean takeOffSpeechTags(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				for(int i=0;i<words.length;i++){
					int pos=words[i].indexOf("/");
					if(pos>=0)
						writer.write(words[i].substring(0,pos)+" ");
				}
				writer.write("\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	//choose the proper speech
	public boolean chosenSpeech(String infile,String outfile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					new FileInputStream(infile),"UTF-8"));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfile),"UTF-8"));
			String buffer;
			while((buffer=reader.readLine())!=null){
				String[] words=buffer.split(" ");
				String regex="/["+speechChosen+"]";
				Pattern pat=Pattern.compile(regex);
				for(int i=0;i<words.length;i++){
					Matcher mat=pat.matcher(words[i]);
					if(mat.find())
						writer.write(words[i]+" ");
				}
				writer.write("\n");
			}
			reader.close();
			writer.close();
			return true;
		}catch(IOException e){
			System.out.println("Error While Loading the File "+infile);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean execuate(){
		GenDoc gen=new GenDoc();
		if(!gen.Execuate())
			return false;
		System.out.println("split words");
		if(splitWord(Config.path+Config.inputFile,Config.path+Config.wordSplitedFile)!=true)
			return false;
		if(Config.useStopWords==true){
			System.out.println("loading stopwords");
			loadStopWords();
			System.out.println("take off stopwords");
			if(takeOffStopWords(Config.path+Config.wordSplitedFile,Config.path+Config.offStopWordFile)!=true)
				return false;
			System.out.println("select words");
			if(selectWords(Config.path+Config.offStopWordFile,Config.path+Config.wordSelectedFile)!=true)
				return false;
		}else{
			System.out.println("select words");
			if(selectWords(Config.path+Config.wordSplitedFile,Config.path+Config.wordSelectedFile)!=true)
				return false;
		}
		System.out.println("select words with certain speech tags");
		if(chosenSpeech(Config.path+Config.wordSelectedFile,Config.path+Config.speechChosenFile)!=true)
			return false;
		System.out.println("take off speech tags");
		if(takeOffSpeechTags(Config.path+Config.wordSelectedFile,Config.path+Config.offSpeechTagFile)!=true)
			return false;
		if(takeOffSpeechTags(Config.path+Config.speechChosenFile,Config.path+Config.speechChosenOffSpeechTagFile)!=true)
			return false;
		System.out.println("all finished");
		return true;
	}
	
	//for test
	public static void main(String[] args){
		PreProcess process=new PreProcess();
		boolean result=process.execuate();
		if(result==false){
			System.out.println("There is something error\n");
		}
	}
}
