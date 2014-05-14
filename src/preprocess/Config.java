package preprocess;

public class Config {
	// path and file
	// given
	protected final static String path = "./doc/diaoyudao/";
	protected final static String srcFile = "weibo.htm";
	protected final static String stopWordFile = "stopwords.dat";
	// to generate
	protected final static String inputFile = "input.txt";
	protected final static String wordSplitedFile = "splited.txt";
	protected final static String offStopWordFile = "offstopword.txt";
	protected final static String wordSelectedFile = "selected.txt";
	protected final static String speechChosenFile = "speechchosen.txt";
	protected final static String offSpeechTagFile = "offspeechtag.txt";
	protected final static String speechChosenOffSpeechTagFile = "speechchosenoffspeechtag.txt";

	// frequency threshold rate
	protected final static double frequencyThresholdRate = 0.0001;

	// whether or not use the stopwords
	protected static boolean useStopWords = true;

	// the speech filtered
	protected static boolean noun = false;
	protected static boolean verb = false;
	protected static boolean adj = true;
	protected static boolean adv = false;
	protected static boolean qua = false;
	protected static boolean num = false;
	protected static boolean unk = false;
	protected static boolean def = false;
}
