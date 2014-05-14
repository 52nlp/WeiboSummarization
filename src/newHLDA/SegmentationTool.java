package newHLDA;

import ICTCLAS.I3S.AC.SegTagICTCLAS50;

public class SegmentationTool {
	
	public static final int USING_ICTCLAS = 1;
	public static final int USING_STANFORD_NLP = 2;
	
	public static int CUR_METHOD = USING_ICTCLAS;
	
	public static String segTag(String text){
		switch (CUR_METHOD) {
		case USING_ICTCLAS:
			return SegTagICTCLAS50.segTag(text);
		default:
			break;
		}
		return null;
	}

}
