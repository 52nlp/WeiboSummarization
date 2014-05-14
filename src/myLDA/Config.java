package myLDA;

import java.util.Vector;

public class Config {
	// file path
	protected final static String path = "./doc/diaoyudao/";
	protected final static String srcFile = "weibo.htm";
	protected final static String inputFile = "text.input";
	protected final static String wordSplitedFile = "text.splited";
	protected final static String wordSelectedFile = "text.selected";
	protected final static String offStopWordFile = "text.offstopword";
	protected final static String speechChosenFile = "text.speech";
	protected final static String destFile = "text.processed";
	protected final static String speechChosendestFile = "text.chosenspeech";
	protected final static String stopWordFile = "stopwords.dat";
	protected final static String wordIndexFile = "word-topic.txt";
	protected final static String topicIndexFile = "topic-word.txt";
	protected final static String synIndexFile = "syn.word";
	protected final static String sortedSynIndexFile = "syn.sorted";
	protected final static String phraseFile = "phrase";
	protected final static String cluterFile = "wordclass.unsorted";
	protected final static String wordVecFile = "word.vec";
	protected final static String synBasedOnVecFile = "syn.vec";
	protected final static String newSynBasedOnVecFile = "syn.vec.new";

	// the parameters of LDA
	protected final static int topicNum = 10;
	protected final static int iterationTimes = 1000;

	// switch
	protected static boolean useStopWords = true;

	// words
	protected static Vector<String> keywords;

	// frequency threshold rate
	protected final static double frequencyThresholdRate = 0.0001;

	// the threshold value of building the words categories
	protected final static int thresholdValue = -1;

	// syn threshold value
	protected final static int synThresholdValue = 200;
	protected final static double synThresholdConsineValue = 0.7;
	// phrase discovery threshold value
	protected final static int phraseThresholdValue = 8;
	protected final static double phraseThresholdProValue = 0.9;

	// word2vec
	protected final static double consineThresholdValue = 0.65;
	protected final static int freThresholdValue = 5;
	protected final static String newPatternTree = "tree.vec";
	protected final static String newSummary = "summary.vec";

	// the base of log
	protected final static double base = 2;
	// the depth of the keyword search tree
	protected final static int searchDeep = 5;
	// threshold value of the leaf to cut
	protected final static double valueThreshold = 100;
	// file that presents the pattern-tree
	protected final static String patternTreeFile = "pattern.tree";
	// file that presents the frequent pattern
	protected final static String patternFrequencyFile = "pattern.frequency";
	protected final static String patternFrequencyForBothwayFile = "pattern.bothway";

	// the switch of print
	protected static boolean print = false;

	// the speech filtered
	protected static boolean noun = false;
	protected static boolean verb = false;
	protected static boolean adj = true;
	protected static boolean adv = false;
	protected static boolean qua = false;
	protected static boolean num = false;
	protected static boolean unk = false;
	protected static boolean def = false;

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
			{ 0, 0, 0, 0, 0, 0, 0, 0 } // other
	};

	// initialize
	static {
		keywords = new Vector<String>();
		keywords.add("钓鱼岛/n");
//		keywords.add("雅安/n");
//		keywords.add("地震/n");
//		keywords.add("雅安地震/n");
	}
}