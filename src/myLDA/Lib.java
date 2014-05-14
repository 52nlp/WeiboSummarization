package myLDA;

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
	
	static class halfSummary{
		String sentence;
		double score;
		
		public halfSummary(String sentence,double score){
			this.sentence=sentence;
			this.score=score;
		}
	}
	
	static class halfSummaryComparator implements Comparator<Object>{
		@Override
		public int compare(Object arg0, Object arg1) {
			halfSummary s1=(halfSummary)arg0;
			halfSummary s2=(halfSummary)arg1;
			Double score1=s1.score;
			Double score2=s2.score;
			return -score2.compareTo(score1);
		}
		
	}
}
