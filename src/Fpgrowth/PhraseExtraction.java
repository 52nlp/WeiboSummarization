package Fpgrowth;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

//用于提取名词性短语
public class PhraseExtraction {
	private static ArrayList<String> sens_tag, phrase;

	public PhraseExtraction(ArrayList<String> sens_tag) {
		this.sens_tag = sens_tag;
		phrase = new ArrayList<String>();
	}
//主调用函数
	public static ArrayList<String> GetPhrase() {
		run();
		return phrase;
	}
//主函数
	//名词性短语构成形式：
//	符号说明：n-名词，a-形容词，r-代词，v-动词
//	n/a+n
//	r/a/v/n+“的”+n
//	n+“的”+a/v
	//将结果写入input.txt，以便FPtree算法调用
	private static void run() {
		PrintWriter pw;
		try {
			pw = new PrintWriter("input.txt");
			for (int i = 0; i < sens_tag.size(); i++) {
				String[] tmpstr = sens_tag.get(i).split(" ");
				String line = "";
				for (int j = 0; j < tmpstr.length; j++) {
					String phrase = "", noun = "";
					if (tmpstr[j].contains("/v"))
						line += tmpstr[j].substring(0, tmpstr[j].indexOf("/v")) + " ";
					if (tmpstr[j].contains("/n")) {
						noun = tmpstr[j].substring(0, tmpstr[j].indexOf("/n"));
						line += noun + " ";
						if (j > 0) {
							if (tmpstr[j - 1].contains("/n")
									|| tmpstr[j - 1].contains("/a")/*
																	 * ||tmpstr[j
																	 * -
																	 * 1].contains
																	 * (
																	 * "/v")||tmpstr
																	 * [
																	 * j-1].contains
																	 * ("/r")
																	 */) {
								phrase = tmpstr[j - 1].substring(0,
										tmpstr[j - 1].indexOf("/")) + noun;
								line += phrase + " ";
								// System.out.println("a/n+n: "+phrase);
							}
							if (tmpstr[j - 1].contains("的/u")
									&& (j - 1 > 0)
									&& (tmpstr[j - 2].contains("/r")
											|| tmpstr[j - 2].contains("/a")
											|| tmpstr[j - 2].contains("/n") || tmpstr[j - 2]
											.contains("/v"))) {
								phrase = tmpstr[j - 2].substring(0,
										tmpstr[j - 2].indexOf("/"))
										+ "的"
										+ noun;
								line += phrase + " ";
								// System.out.println("r/a/n/v+的+n: "+phrase);
							}
						}
						if (j < tmpstr.length - 1) {
							/*
							 * if(tmpstr[j+1].contains("/r")||tmpstr[j+1].contains
							 * ("/n")||tmpstr[j+1].contains("/a")||tmpstr[j+1].
							 * contains("/v")){ phrase =
							 * noun+tmpstr[j+1].substring(0,
							 * tmpstr[j+1].indexOf("/")); line += phrase + " ";
							 * System.out.println("n+r/a/n/v/: "+phrase); }
							 */
							if (tmpstr[j + 1].contains("的/u")
									&& j + 2 < tmpstr.length
									&& (tmpstr[j + 2].contains("/v") || tmpstr[j + 2]
											.contains("/a"))) {
								phrase += noun
										+ "的"
										+ tmpstr[j + 2].substring(0,
												tmpstr[j + 2].indexOf("/"));
								line += phrase + " ";
								// System.out.println("n+的+a/v: "+phrase);
							}
						}
					}
				}
				if (!(line == "")) {
				//	System.out.println("Phrase"+phrase);
					phrase.add(line);
					pw.println(line);
				}
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
