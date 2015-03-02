package naiveSummary;

import java.util.Comparator;
import java.util.Vector;

public class Lib {
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
			return score2.compareTo(score1);
		}		
	}
	
	static class sortObject{
		Object obj;
		double score;
		
		public sortObject(Object obj,double score){
			this.obj=obj;
			this.score=score;
		}
		
		public sortObject(Object obj){
			this.obj=obj;
			this.score=0;
		}
		
		public sortObject(){
			this.obj=null;
			this.score=0;
		}
	}
	
	static class sortObjectComparator implements Comparator<Object>{
		@Override
		public int compare(Object arg0,Object arg1){
			sortObject s1=(sortObject)arg0;
			sortObject s2=(sortObject)arg1;
			Double score1=s1.score;
			Double score2=s2.score;
			return score2.compareTo(score1);
		}
	}
}
