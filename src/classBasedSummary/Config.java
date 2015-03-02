package classBasedSummary;

import java.util.Vector;

public class Config {
	// path and file
	protected final static String root = "./doc/diaoyudao/";
	protected final static String path = "./doc/diaoyudao/classbased/";
	// given
	protected final static String wordSplitedFile = "splited.txt";
	protected final static String wordVecFile = "word.vec";
	// to generate
	protected final static String phraseFile = "phrase.txt";
	protected final static String synBasedOnVecFile = "vec-based-syn.list";
	protected final static String newSynBasedOnVecFile = "vec-based-syn.new.list";

	protected final static String PatternTree = "pattern.tree";
	protected final static String newPatternTree = "pattern.new.tree";

	protected final static String singleSummaryFile = "single.summary";
	protected final static String doubleSummaryFile = "double.summary";
	// keywords
	protected static Vector<String> keywords;
	// the base of log
	protected final static double base = 2;

	// phrase discovery threshold value
	protected final static int phraseThresholdValue = 8;
	protected final static double phraseThresholdProValue = 0.9;
	// vec config
	protected final static double consineThresholdValue = 0.65;
	protected final static int freThresholdValue = 5;

	// merge concerning speech
	protected static final int[][] speech2merge = {
			// later: n v a d q m u other
			{ 1, 1, 0, 0, 0, 0, 0, 0 }, // n
			{ 1, 1, 0, 0, 0, 0, 0, 0 }, // v
			{ 0, 1, 1, 0, 0, 0, 0, 0 }, // a
			{ 0, 1, 0, 1, 0, 0, 0, 0 }, // d
			{ 0, 0, 0, 0, 1, 0, 0, 0 }, // q
			{ 0, 0, 0, 0, 0, 1, 0, 0 }, // m
			{ 0, 0, 0, 0, 0, 0, 1, 0 }, // u
			{ 0, 0, 0, 0, 0, 0, 0, 0 }	// other
	};

	static {
		keywords = new Vector<String>();
		keywords.add("钓鱼岛/n");
		// keywords.add("雅安/n");
		// keywords.add("地震/n");
		// keywords.add("雅安地震/n");
	}
}
