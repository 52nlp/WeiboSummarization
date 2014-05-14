package NodeClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class ConceptNode {
	private String concept; //概念本身
	private String doc; //概念的离散上下文
	private boolean isPhrase; //概念是否是词组或者二维频繁项的合并
	private int  support, txtWordSum, vocSize, tf;
//	private double tf_p, pmi;
//	private ArrayList<Integer> word_pos, word_num;
	private ArrayList<ContextWordsNode> txtWordTF;

	public void setTxtWordSum(int sum){
		txtWordSum = sum;
	}
	public void setVocSize(int size){
		vocSize = size;
	}
	public ArrayList<ContextWordsNode> getTxtWordTF(){
		return txtWordTF;
	}
	public int getVocSize(){
		return vocSize;
	}
	public ConceptNode(String con, String doc, boolean ip, int sup){
		concept = con;
		this.doc = doc;
		isPhrase = ip;
		support = sup;
	}
	public void setWordList(Map<Integer, Integer> map){
		txtWordTF = new ArrayList<ContextWordsNode>();
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Integer> en = (Entry<Integer, Integer>) it.next();
			int pos = en.getKey();
			double tf = (double)en.getValue()/txtWordSum;
			txtWordTF.add(new ContextWordsNode(pos, tf));
		}
//		for(int i = 0; i < word_pos.size(); i++){
//			double tf = word_num.get(i)/txtWordSum;
//			txtWordTF.add(new ContextWordsNode(word_pos.get(i), tf));
//		}
	}
	public void setTF(int tf){
		this.tf = tf;
	}
	public int getTF(){
		return tf;
	}
	public String getConcept(){
		return concept;
	}
	public String getDoc(){
		return doc;
	}
	public boolean isPhrase(){
		return isPhrase;
	}
}
