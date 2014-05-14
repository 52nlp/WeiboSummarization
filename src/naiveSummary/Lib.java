package naiveSummary;

import java.util.Comparator;

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
}
