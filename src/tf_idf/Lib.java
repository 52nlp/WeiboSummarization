package tf_idf;

import java.util.Comparator;
import java.util.Vector;

public class Lib {
	static class wordlist{
		public int docNum;
		public Vector<String> words;
		public Vector<Integer> tf;
		public Vector<Integer> df;
		public Vector<Double> tf_idf;
		
		public wordlist(){
			docNum=0;
			words=new Vector<String>();
			tf=new Vector<Integer>();
			df=new Vector<Integer>();
			tf_idf=new Vector<Double>();
		}
		
		public void addSentence(String[] sentence){
			docNum++;
			Vector<Integer> index=new Vector<Integer>();
			for(int i=0;i<sentence.length;i++){
				int pos=words.indexOf(sentence[i]);
				if(pos<0){
					pos=words.size();
					words.add(sentence[i]);
					tf.add(1);
					df.add(1);
					tf_idf.add(0.0);
					index.add(pos);
				}else{
					if(!index.contains(pos)){
						df.set(pos, df.get(pos)+1);
						index.add(pos);
					}
					tf.set(pos, tf.get(pos)+1);
				}
			}
		}
		
		public void calc(){
			for(int i=0;i<words.size();i++){
				double value=tf.get(i)*Math.log10((double)docNum/(double)df.get(i));
				tf_idf.set(i,value);
			}
		}
		
		public double getValue(String content){
			int pos=words.indexOf(content);
			if(pos<0)
				return 0;
			else
				return tf_idf.get(pos);
		}
	}
	
	static class sentence{
		public Double value;
		public String[] contents;
		
		public sentence(String[] contents){
			this.contents=contents;
		}
		
		public void calc(wordlist list){
			double ret=0;
			for(int i=0;i<contents.length;i++){
				ret+=list.getValue(contents[i]);
			}
			int length=contents.length>Config.shortLength?contents.length:Config.shortLength;
			value=ret/(double)length;
		}
	}
	
	static class sentenceComparator implements Comparator<Object>{

		@Override
		public int compare(Object arg0, Object arg1) {
			sentence s1=(sentence)arg0;
			sentence s2=(sentence)arg1;
			return -s1.value.compareTo(s2.value);
		}
		
	}
}
