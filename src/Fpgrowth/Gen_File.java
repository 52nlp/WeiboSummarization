package Fpgrowth;


import java.io.*;
import java.util.*;
class hashNode				//构建哈希表节点
{
	String word;
	int number;
	hashNode next;
}

public class Gen_File
{

	final static int CHARSIZE = 30;
	final static int HASHSIZE = 1000019;
	final static int LINESIZE = 100000;
	final static String OUTPUT_FILE =  "data1.txt";
	final static String OUTPUT_FILE_GO = "data.txt";
	final static String INPUT_FILE = "input.txt";
	final static String  DICT_FILE = "dict.txt";
	
	int countTransNum = 0;		//总的行数
	int countWordsNumPerLine[];//每行单词数

	String wordLine;	//用于存储每行字符
	String word;		//用于存储一个单词
	//int DiggingNum;
	//float SupportThreshold;



	public int elf_hash(String str, int number){   
	    int hash = 0;   
	    long x =0l;   
	    char[] array = str.toCharArray();   
	    for(int i=0; i<array.length; i++){   
	        hash = (hash << 4) + array[i];   
	        if((x = (hash & 0xF0000000L)) != 0){   
	            hash ^= (x >> 24);   
	            hash &= ~x;   
	        }   
	    }   
	    int result = (hash & 0x7FFFFFFF) % number;   
	    return result;   
	}  

	
	public static void GenerateFile(int diggingNum,double supportThreshold)
	{
		FileReader fr = null;
		Gen_File tl = new Gen_File();
		int i;
		try
		{
			fr = new FileReader(INPUT_FILE);
		}
		catch(Exception e)
		{
			System.out.println("错误:不能打开input.txt文件\n"+ e);
				
		}
		BufferedReader  br = new BufferedReader(fr);
		
		Scanner fin= new Scanner(br);
		//Scanner fin2 = new Scanner(System.in);
		//System.out.print("请输入预挖掘频繁项个数：");
		//tl.DiggingNum = fin2.nextInt();
		//System.out.print("请输入支持度大小（百分比）");
		//tl.SupportThreshold = fin2.nextFloat();
		//tl.SupportThreshold /= 100;

		hashNode p;
		hashNode q;
		hashNode [] link = new hashNode[HASHSIZE];
	

		while(fin.hasNext()){
			
			tl.wordLine = fin.nextLine();
	//		System.out.println("**"+tl.wordLine );
			String [] words;
			words = tl.wordLine.split(" ");
			tl.countWordsNumPerLine[tl.countTransNum] = words.length;
			for( i = 0;i<words.length;i++){
				words[i].trim();
				int e = tl.elf_hash(words[i],HASHSIZE);		//		j = 0;
				q = link[e];
				while(q!=null)				//查找单词表里是否存在
				{
					if(q.word.equals(words[i]))
						break;
					q = q.next;
				}
				if(q == null)				//如不不存在单词，则插入哈希表中
				{
					p = new hashNode();
					p.word = words[i];
					p.next = link[e];
					link[e] = p;
				}
			}
			tl.countTransNum++;
		}
		try{
			fr.close();
			br.close();
			fin.close();
		}
		catch(Exception exception)
		{
			System.out.println("错误：文件关闭时发生错误\n"+exception);
		}
		
		FileWriter wr = null;
		try{
			wr = new FileWriter(DICT_FILE);
			}
			catch(Exception e)
			{
				System.out.println("错误:不能打开dict.txt文件\n"+ e);
				
			}
			
			BufferedWriter br2 = new BufferedWriter (wr);
			PrintWriter pw = new PrintWriter(br2);
			
			int wordNum=0;
			for(i = 0; i < HASHSIZE; i++)
			{
				q = link[i];
				while(q != null)
				{
					q.number = wordNum;
//					System.out.println(q.word);
					pw.println(q.word);
					wordNum++;
					q = q.next;
				}
			}
			try{
				pw.flush();
				//wr.close();
				br2.close();
				pw.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
	//		System.out.println("不同单词共有" + wordNum + "个");
			FileWriter wr2 = null;
		
			
			int lineNum = 0;

			try
			{
				fr = new FileReader(INPUT_FILE);
			}
			catch(Exception e)
			{
				System.out.println("错误:不能打开input.txt文件\n"+ e);
					
			}
			br = new BufferedReader(fr);
			
			fin= new Scanner(br);
			try
			{
			 wr2 = new FileWriter(OUTPUT_FILE);
			}
			catch(Exception e)
			{
				System.out.println("config.txt 文件读取错误!\n"+e);
			}
			br2 = new BufferedWriter(wr2);
			pw = new PrintWriter(br2);
			while(fin.hasNext())
			{
				tl.wordLine = fin.nextLine();
				String [] words;
				words = tl.wordLine.split(" ");
				pw.print(tl.countWordsNumPerLine[lineNum]+" ");
				for( i = 0;i<words.length;i++)
				{
					words[i].trim();
					int e = tl.elf_hash(words[i],HASHSIZE);
		//			j = 0;
					q = link[e];

					while(q!=null)				//查找单词表里是否存在
					{
						if(q.word.equals(words[i]))
						{
							break;
						}
						q = q.next;
					}
					if(q != null)				//如不不存在单词，则插入哈希表中
					{
						pw.print(q.number+" ");
					}
				//	tl.countTransNum++;
				}
				pw.println();
				lineNum++;
			}
			try
			{
				fr.close();
				br.close();
				fin.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
			try{
				wr2.flush();
				//wr2.close();
				br2.close();
				pw.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
			
			try
			{
				wr2 = new FileWriter("config.txt");
			}
			catch(Exception e)
			{
				System.out.println("config.txt 文件读取错误!\n"+e);
			}
			br2 = new BufferedWriter(wr2);
			pw = new PrintWriter(br2);
			pw.println(diggingNum);
			pw.println(supportThreshold);
			pw.println(wordNum);
			pw.println(lineNum);
			pw.flush();
			
			try
			{
				wr2.close();
				br2.close();
				pw.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
			int exist[] = new int[wordNum];
			
			try
			{
				fr = new FileReader(OUTPUT_FILE);
			}
			catch(Exception e)
			{
				System.out.println("错误:不能打开input.txt文件\n"+ e);
					
			}
			br = new BufferedReader(fr);
			
			fin= new Scanner(br);
			
			try
			{
				wr2 = new FileWriter(OUTPUT_FILE_GO);
			}
			catch(Exception e)
			{
				System.out.println("config.txt 文件读取错误!\n"+e);
			}
			br2 = new BufferedWriter(wr2);
			pw = new PrintWriter(br2);
			
			int countnum;
			while(fin.hasNext())
			{
				countnum = fin.nextInt();
				//memset(exist,0,sizeof(int)*wordNum);
				for(i =0;i<wordNum;i++)
				{
					exist[i] = 0;
				}
				for(i = 0;i<countnum;i++)
				{
					int temp = fin.nextInt();
					exist[temp] = 1;
				}
				int countSize = 0;
				int t;
				for(t=0;t<wordNum;t++)
				{
					if(exist[t] == 1)
						countSize++;
				}
				pw.print(countSize+" ");
				for(t = 0;t<wordNum;t++)
					if(exist[t] == 1)
					{
						pw.print(t+" ");
					}
					pw.println();
					pw.flush();
			}
			try
			{
				wr2.close();
				br2.close();
				pw.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
			try{
				fr.close();
				br.close();
				fin.close();
			}
			catch(Exception exception)
			{
				System.out.println("错误：文件关闭时发生错误\n"+exception);
			}
	}
	

	Gen_File()
	{
		countWordsNumPerLine = new int [HASHSIZE];
		countTransNum =0;
	}

}