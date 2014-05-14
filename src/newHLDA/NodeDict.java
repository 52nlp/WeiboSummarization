package newHLDA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

//强行将第二层的Node分为若干类，每类的主题给定
public class NodeDict {
	//每个主题词需限定的主题
	public static HashMap<String,ArrayList<Integer>> topicDict;
//	//每个真实主题下的词
	public static ArrayList<ArrayList<String>> id2topicWord;
	//实际主题和主题ID的转换
	public static HashMap<Integer,Integer> id2topic;
	public static HashMap<Integer,ArrayList<Integer>> topic2id;
	//聚类的主题数目
	public static int topicNum;
	//实际的主题数目
	public static int realTopicNum;
	
	//包含的单词
	public static HashSet<String> wordSet;
	
	//测试
	public static void LoadTopicDict(){
		String path = "HLDAModelDir\\nodeDict.txt";
		topicNum = 0;
		realTopicNum = 0;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(path), "utf-8");
			BufferedReader bf = new BufferedReader(isr);
			topicDict = new HashMap<String, ArrayList<Integer>>();
			id2topic = new HashMap<Integer,Integer>();
			topic2id = new HashMap<Integer,ArrayList<Integer>>();
			String str;
			int curTopic = -1;
			ArrayList<Integer> topicList = null;
			while((str = bf.readLine())!= null){
				if(str.indexOf("Node")!=-1){
					//新的主题
					curTopic++;
					String[] temp = str.split(" ");
					topicList = new ArrayList<Integer>();
					for(int i = 1; i < temp.length ; i++){
						topicList.add(new Integer(temp[i]));
						id2topic.put(new Integer(temp[i]), curTopic);
						if(new Integer(temp[i])>topicNum){
							topicNum = new Integer(temp[i]);
						}
					}
					topic2id.put(curTopic, topicList);
				}
				else{
					topicDict.put(str, topicList);
				}
			}
			bf.close();
			isr.close();
			topicNum++;
			curTopic++;
			realTopicNum = curTopic;
			id2topicWord = new ArrayList<ArrayList<String>>();
			wordSet = new HashSet<String>();
			for(int i = 0 ; i < realTopicNum; i++){
				id2topicWord.add(new ArrayList<String>());
			}
			Iterator it = topicDict.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
				int realValue = id2topic.get(value.get(0));
				id2topicWord.get(realValue).add(key);
				wordSet.add(key);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	static{
		LoadTopicDict();
	}
	
	public static void main(String[] args){
		System.out.println(topicDict);
		System.out.println(topic2id);
		System.out.println(id2topic);
		System.out.println(topicNum);
		System.out.println(realTopicNum);
		System.out.println(id2topicWord);
	}

}
