package thu.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * 预处理，将繁体字转换为简体字，将全角字符转换为半角字符
 * 参照中科院分词系统：ICTCLAS开源版/SharpICTCLAS分词系统 1.0/SharpICTCLAS
 * by 万伟 (2011-04-01)
 */
public class PreProcessUtility {
	
	private static String simp;
	private static String trad;
    private static String cnChars = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ０１２３４５６７８９　";
    private static String enChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
	
	static
	{
		try {
			File fileread = new File("TopicModelData/IAR_Dict/trad_simp.txt");
			BufferedReader bf = new BufferedReader(new UnicodeReader(
					new FileInputStream(fileread), "UTF-8")); // 可以去掉UTF-8文件头的BOM信息
			simp = bf.readLine();
			trad = bf.readLine();
			bf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ========================================================================
	// 将繁体字转换为简体字
	// ========================================================================
	public static String ToSimplifyString(String TraditionalString) {		
		StringBuilder sb = new StringBuilder();
		try {
			char[] tradArray = TraditionalString.toCharArray();
			for (int i = 0; i < tradArray.length; i++) {
				if (trad.indexOf(tradArray[i]) >= 0)
					sb.append(simp.substring(trad.indexOf(tradArray[i]), trad.indexOf(tradArray[i])+1));
				else
					sb.append(tradArray[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	// ========================================================================
	// 将全角字符转换为半角字符
	// ========================================================================
	public static String CnChar2EnChar(String s) {
		StringBuilder sb = new StringBuilder();
		try {
			char[] cArray = s.toCharArray();
			for (int i = 0; i < cArray.length; i++) {
				if (cnChars.indexOf(cArray[i]) >= 0)
					sb.append(enChars.substring(cnChars.indexOf(cArray[i]),
							cnChars.indexOf(cArray[i]) + 1));
				else
					sb.append(cArray[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	// ========================================================================
	// 预处理，包括新版分词程序不能区分的标点替换，繁简体转换，全角半角转换
	// ========================================================================
	public static String preProcess(String s) {
		//s = s.replace('，', ',');
		//s = s.replace('…', ' ');
		//s = s.replace('（', '(');
		//s = s.replace('）', ')');
		//s = s.replace('【', ' ');
		//s = s.replace('】', ' ');
		//s = s.replace('{', ' ');
		//s = s.replace('}', ' ');
		//s = s.replace('：', ' ');
		//s = s.replace(':', ' ');
		//s = s.replace('．', ' ');
		//s = s.replace('～', ' ');
		s = s.replace("“", "\"");
		s = s.replace("”", "\"");
		s = s.replace("—", "");
		s = s.replace("——", "");			
		s = s.replace('~', '。');
		s = s.replace('·', '.');
		s = s.replace('﹗', '!');
		s = s.replace("、", "");
		s = s.replace('`', ',');
//		s = s.replace("\r\n", "。");
//		s = s.replace("\n\r", "。");
//		s = s.replace('\n', '。');
//		s = s.replace('\r', '。');
		s = s.replace("        ", " ");
		s = s.replace("       ", " ");
		s = s.replace("      ", " ");
		s = s.replace("     ", " ");
		s = s.replace("    ", " ");
		s = s.replace("   ", " ");
		s = s.replace("IPHONE 5", "IPHONE5");
		s = s.replace("IPHONE 4", "IPHONE4");
		s = s.replace("IPHONE 4S", "IPHONE4S");
		s = s.replace("ONE X", "ONEX");
		s = s.replace("FIND 5", "FIND5");
		s = s.replace("MI 2", "MI2");
		s = s.replace("5寸", "五寸");
		s = s.replace("5 寸", "五寸");
		s = s.replace("NOTE 2", "NOTE2");
		s = s.replace("LUMIA 920", "LUMIA920");
		s = s.replace("NEXUS 4", "NEXUS4");
		s = s.replace("XPERIA Z", "XPERIA-Z");
		s = s.replace("XPERIAZ", "XPERIA-Z");
//		s = s.replace(" ", "。");
		
		s = ToSimplifyString(s);
		s = CnChar2EnChar(s);
		//大小写，此处全部变换成大写
		//2012-10-20
		//王玮添加
		s = s.toUpperCase();
		return s;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(ToSimplifyString("五星級飯店，不過早餐的服務速度比較慢，要檸檬片，等很久，切了個檸檬丁給我:("));
		System.out.println(CnChar2EnChar("ａｂｃｄｅｆａｆｄｓｆｄｓａｆｄｓａｊｆｏｊｏｊａｆｅｒｗｅｑ０４ｕ０４３５２０　　ｄａｓｊｆｐｄ"));
	}
}
