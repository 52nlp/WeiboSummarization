package classBasedSummary;

import java.util.Comparator;

public class Lib {
	public static int speech(String elem){
		int index=elem.indexOf("/");
		char s=elem.charAt(index+1);
		switch (s){
		case 'n':return 0;
		case 'v':return 1;
		case 'a':return 2;
		case 'd':return 3;
		case 'q':return 4;
		case 'm':return 5;
		case 'u':return 6;
		default:return 7;
		}
	}
	
	public static boolean canMerge(String w1,String w2,boolean order){
		int sp1=Lib.speech(w1);
		int sp2=Lib.speech(w2);
		if(order==true){
			if(Config.speech2merge[sp1][sp2]==0)
				return false;
			else
				return true;
		}else{
			if(Config.speech2merge[sp2][sp1]==0)
				return false;
			else
				return true;
		}
	}
	
	static class wordpair{
		int word1;
		int word2;
		int frequency;
		double similarity;
		int type;
		public wordpair(int word1,int word2,int frequency,double similarity){
			this.word1=word1;
			this.word2=word2;
			this.frequency=frequency;
			this.similarity=similarity;
			this.type=0;
		}
		
		public void add(int frequency){
			this.frequency+=frequency;
		}
		
		public void setType(int type){this.type=type;}
		
		public boolean equals(wordpair tocompare){
			if(word1==tocompare.word1&&word2==tocompare.word2)
				return true;
			if(word1==tocompare.word2&&word2==tocompare.word1)
				return true;
			return false;
		}
	}
	
	static class wordpairCompare implements Comparator<Object>{
		@Override
		public int compare(Object arg0, Object arg1) {
			wordpair wp1=(wordpair)arg0;
			wordpair wp2=(wordpair)arg1;
//			Double index1=wp1.similarity;
//			Double index2=wp2.similarity;
			Integer frequency1=wp1.frequency;
			Integer frequency2=wp2.frequency;
			return -frequency1.compareTo(frequency2);
		}
	}
}
