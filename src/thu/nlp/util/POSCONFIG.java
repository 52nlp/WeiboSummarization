package thu.nlp.util;

import java.util.ArrayList;


public class POSCONFIG {

	public static ArrayList<String> POSFeatureWordList = new ArrayList<String>();
	
	//词性选择
	static{
		POSFeatureWordList.add("n"); //名词
//		POSFeatureWordList.add("t"); //时间
//		POSFeatureWordList.add("s"); //处所
//		POSFeatureWordList.add("f"); //方位
		POSFeatureWordList.add("v"); //动词
		POSFeatureWordList.add("a"); //形容词
//		POSFeatureWordList.add("b"); //区别词
//		POSFeatureWordList.add("z"); //状态词
//		POSFeatureWordList.add("r"); //代词
//		POSFeatureWordList.add("m"); //数词
//		POSFeatureWordList.add("q"); //量词
//	    POSFeatureWordList.add("d"); //副词
//		POSFeatureWordList.add("x"); //外来词
	}
	
	
}
