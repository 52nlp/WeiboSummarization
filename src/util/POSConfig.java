package util;

import java.util.ArrayList;

/*
 * 定义不同步骤中抽取的词语词性
 */
public class POSConfig {
	public static ArrayList<String> hotWordPOSList = new ArrayList<String>(); //隐含特征抽取——热点词词性列表
	public static ArrayList<String> frequentItemPOSList = new ArrayList<String>(); //隐含特征抽取——频繁项词性列表

	
	static {
		
		hotWordPOSList.add("n"); //名词
		hotWordPOSList.add("t"); //时间
		hotWordPOSList.add("s"); //处所
		hotWordPOSList.add("f"); //方位
		hotWordPOSList.add("v"); //动词
		hotWordPOSList.add("a"); //形容词
		hotWordPOSList.add("b"); //区别词
		hotWordPOSList.add("z"); //状态词
		hotWordPOSList.add("r"); //代词
		hotWordPOSList.add("m"); //数词
		hotWordPOSList.add("q"); //量词
		//hotWordPOSList.add("d"); //副词
		hotWordPOSList.add("x");
				
		frequentItemPOSList.add("n"); //名词
		frequentItemPOSList.add("t"); //时间
		frequentItemPOSList.add("s"); //处所
		frequentItemPOSList.add("f"); //方位
		frequentItemPOSList.add("v"); //动词
		frequentItemPOSList.add("a"); //形容词
		frequentItemPOSList.add("b"); //区别词
		frequentItemPOSList.add("z"); //状态词
		frequentItemPOSList.add("r"); //代词
		frequentItemPOSList.add("m"); //数词
		frequentItemPOSList.add("q"); //量词
		//frequentItemPOSList.add("d"); //副词
		frequentItemPOSList.add("x");

	}
}
