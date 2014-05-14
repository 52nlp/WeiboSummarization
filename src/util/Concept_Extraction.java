package util;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import org.ictclas4j.segment.SegTag;

import Fpgrowth.ItemNode;
import ICTCLAS.I3S.AC.ICTCLAS50;
import ICTCLAS.I3S.AC.SegTagICTCLAS50;

//利用三次剪枝，将频繁项集提取出概念集合，主函数为getConceptCandidates()
//单字剪枝SingleCharPrune：修剪单字的频繁项，例如：床、房、人、楼
//紧密度剪枝CompactPrune：修剪本身不是特征短语的二维频繁项，例如：酒店时候、房间态度
//冗余项剪枝RedundancyPrune：修剪其父集为特征，但本身不是特征的频繁项，例如：地理(地理位置)、问题(交通问题)
//其中紧密度剪枝和冗余项剪枝可参见论文：Mining Opinion Features in Customer Reviews, Minqing Hu and Bing Liu
public class Concept_Extraction {
	// static ArrayList<String> concept = new ArrayList<String>();

	static ArrayList<String> cand1 = new ArrayList<String>();
	static ArrayList<String> cand2 = new ArrayList<String>();
	int minDistance, minAppearance, minPureSupport;
	private static ArrayList<String> sens;
	private ArrayList<ItemNode> items, items1, items2, prune1, prune2, prune3;

	// private ArrayList<ConceptNode> concepts;

	public Concept_Extraction(ArrayList<ItemNode> items,
			ArrayList<String> sens, int dist, int app, int sup) {
		this.sens = sens;
		this.items = items;
		minDistance = dist;
		minAppearance = app;
		minPureSupport = sup;
	}

	public ArrayList<ItemNode> getConceptCandidates() {
		SingleCharPrune();
		CompactPrune();
		RedundancyPrune();
		ArrayList<ItemNode> result = new ArrayList<ItemNode>();
		for (int i = 0; i < prune3.size(); i++) {
			if (i > 0
					&& prune3.get(i).content.equals(prune3.get(i - 1).content))
				continue;
			if (!result.contains(prune3.get(i))) {
	//			System.out.println(prune3.get(i).content);
				result.add(prune3.get(i));
			}
		}
		// System.out.println("Num"+result.size());
		return result;
	}

	//单字剪枝函数，修建单字，合并二维频繁项，合并过程中确定两项之间的顺序
	//二维频繁项合并规则：遍历所有含有两个项的语句，统计先后出现的次数，选择概率大的顺序
	private void SingleCharPrune() {
		// System.out.println("Single Prune");
		prune1 = new ArrayList<ItemNode>();
		for (int i = 0; i < items.size(); i++) {			
			String content = items.get(i).content;
			if (content.length() > 1) {
				if (content.contains(" ") && (content.length() > 4)
						&& (!content.contains("&quot;"))) {
					String[] tmp = content.split(" ");
					if ((!tmp[1].contains(tmp[0]))
							&& (!tmp[0].contains(tmp[1]))) {
						int count1 = 0, count2 = 0;
						boolean flag = false;
						for (int j = 0; j < sens.size(); j++) {
							String sen = sens.get(j);
							if (sen.contains(tmp[0]) && sen.contains(tmp[1])) {
								String[] clause = sen.split("/w");
								for(int k = 0; k < clause.length; k++){
									if(clause[k].contains(tmp[0])&&clause[k].contains(tmp[1])){
										flag = true;
										if (clause[k].indexOf(tmp[0]) < clause[k].indexOf(tmp[1]))
											count1++;
										else
											count2++;
									}
								}
							}
						}
						if(flag){
							String co = "";
							if (count1 > count2) {
								co = tmp[0] + " " + tmp[1];
							} else
								co = tmp[1] + " " + tmp[0];
							prune1.add(new ItemNode(co, items.get(i).support));
						}
					}
				} else if ((!content.contains(" "))
						&& (!content.contains("&quot;")))
					prune1.add(items.get(i));
			}
		}
		
		System.out.println("Single Prune" + prune1.size());
	}

//	private static void SingleCharPrune(String filename) {
//		try {
//			FileReader fr = new FileReader(new File(filename));
//			BufferedReader br = new BufferedReader(fr);
//			String content = null;
//			while ((content = br.readLine()) != null) {
//				if (content.length() > 1) {
//					if (content.contains("-") && (!content.contains("&quot;"))) {
//						String[] tmp = content.split("-");
//						content = "";
//						for (int i = 0; i < tmp.length; i++) {
//							if (tmp[i].length() > 1)
//								content += tmp[i] + " ";
//						}
//						if ((content.length() > 4)
//								&& (!tmp[1].contains(tmp[0]))
//								&& (!tmp[0].contains(tmp[1])))
//							items2.add(content);
//					} else if ((!content.contains("-"))
//							&& (!content.contains("&quot;")))
//						items1.add(content);
//				}
//				// i++;
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// for (int i = 0; i < items1.size(); i++) {
//		// System.out.println(items1.get(i));
//		// }
//	}

	//辅助函数，删除二维频繁项两个项之间的空格
	private String[] deleteWhiteSpace(String[] words_P){
		ArrayList<String> temp = new ArrayList<String>();
		for(int m = 0; m < words_P.length; m++){
			if(words_P[m].contains("/"))
				temp.add(words_P[m]);
		}
		String[] words = new String[temp.size()];
		for(int m = 0; m < temp.size(); m++){
			words[m] = temp.get(m);
		}
		return words;
	}
	//辅助函数，获取句子中频繁项的位置
	private int[] getAllPos(String[] sen, String word){
		ArrayList<Integer> pos = new ArrayList<Integer>();
		for(int m = 0; m < sen.length; m++){
			if(sen[m].equals(word))
				pos.add(m);
		}
		int[] p = new int[pos.size()];
		for(int m = 0; m < pos.size(); m++)
			p[m] = pos.get(m);
		return p;
	}
	//紧密度剪枝
	private void CompactPrune() {
//		SegTag st = new SegTag(1);
	//	SegTagICTCLAS50 segtag = new SegTagICTCLAS50();
		/*
		try	{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}
		*/
		
		prune2 = new ArrayList<ItemNode>();
		items1 = new ArrayList<ItemNode>();
		items2 = new ArrayList<ItemNode>();

		for (int i = 0; i < prune1.size(); i++) {
			String content = prune1.get(i).content+" ";
	//		System.out.println("紧密度剪枝" + content);
	//		System.out.println("紧密度剪枝  " + st.split(content).getFinalResult());
			
			//String[] words_P = st.split(content).getFinalResult().split(" ");		
			String tempstr = "";
			
			
		//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
		//		testICTCLAS50.ICTCLAS_SetPOSmap(1);
		//	byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(content.getBytes("GB2312"), 0, 1);//分词处理
		//	String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
		//	tempstr = nativeStr;						
			
			tempstr = SegTagICTCLAS50.segTag(content);
			
			String[] words_P = tempstr.split(" ");	
			if (words_P.length == 1) {
				items1.add(prune1.get(i));
				prune2.add(prune1.get(i));
//				if(words_P[0].contains("/nx"))
//					System.out.println(words_P[0]);
				continue;
			}
			String[] words = deleteWhiteSpace(words_P);
			int count = 0, appearSen = 0;	
			for (int j = 0; j < sens.size(); j++) {
				boolean flag = true;
				for (int k = 0; k < words.length; k++) {
					if (!sens.get(j).contains(words[k])) {
						flag = false;
						break;
					}
				}
				if (flag) {
					appearSen++;
//					System.out.println("含所有词的句子："+sens.get(j));
					String[] tmp = sens.get(j).split(" ");
					ArrayList<Integer> positions = new ArrayList<Integer>();				
					boolean flag2 = true;
					for(int k = 1; k < words.length; k++){
						int minD = Integer.MAX_VALUE;
						int[] pos0 =  getAllPos(tmp, words[k-1]);
						int[] pos1 = getAllPos(tmp, words[k]);
						for(int p = 0; p < pos0.length; p++){
							for(int q = 0; q < pos1.length; q++){
								if(pos1[q] > pos0[p]){ 
									if(pos1[q]-pos0[p] < minD)
										minD = pos1[q] -pos0[p] ;
									break;
								}
							}
						}
						if(minD > minDistance){
							flag2 = false;
							break;
						}
					}
//					System.out.println("MinD"+minD);
					if (flag2)
						count++;
				}
				if (count >= minAppearance) {
					items2.add(prune1.get(i));
					prune2.add(prune1.get(i));
					break;
				}
			}
//			System.out.println("Appearance"+appearSen+"Count"+count);
		}
		for(int i = 0; i < prune1.size(); i++){
			if(!prune2.contains(prune1.get(i)))
			System.out.println(prune1.get(i).content);
		}
		System.out.println("Compact Prune" + prune2.size());
	//	testICTCLAS50.ICTCLAS_Exit();

//	}
//	catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	
	
//冗余度剪枝
	private void RedundancyPrune() {
//		SegTag st = new SegTag(1);
		
	//	SegTagICTCLAS50 segtag = new SegTagICTCLAS50();
		
		/*
		try{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}
		*/
		
		
		prune3 = new ArrayList<ItemNode>();
		ArrayList<ItemNode> remove = new ArrayList<ItemNode>();
		
		for(int i = 0; i < items1.size(); i++){
	//	for(int i = 0; i < 1/*items1.size()*/; i++){
			String check = items1.get(i).content;
			int count = 0;
			for(int j = 0; j < sens.size(); j++){
				String sentence = sens.get(j);
				if(!sentence.contains(check))
					continue;
				boolean flag_sen = true;
				for(int k = 0; k < items2.size(); k++){
					String phrase = items2.get(k).content;
					if(!phrase.contains(check))
						continue;
//					System.out.println(check+"*"+phrase);
					boolean flag_phrase = true;
				//	String[] words = deleteWhiteSpace(st.split(phrase+" ").getFinalResult().split(" "));
					
					
					String tempstr = "";
					phrase = phrase + " ";
					
						
						//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			//			testICTCLAS50.ICTCLAS_SetPOSmap(1);
			//			byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(phrase.getBytes("GB2312"), 0, 1);//分词处理
			//			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			//			tempstr = nativeStr;
						//System.out.println(nativeStr);							
					
					tempstr = SegTagICTCLAS50.segTag(phrase);
					 
					String[] words =deleteWhiteSpace(tempstr.split(" "));	
					
					
					
					for(int p = 0; p < words.length; p++){
						if(!words[p].contains(check)&&sentence.contains(words[p])){
							flag_phrase = false;
							break;
						}
					}
					if(!flag_phrase){
						flag_sen = false;
						break;
					}
				}
				if(flag_sen){
//					System.out.println(sentence);
					count++;
				}
			}
			if(count < minPureSupport)
				remove.add(items1.get(i));
		}
		for(int i = 0; i < prune2.size(); i++){
			if(!remove.contains(prune2.get(i)))
				prune3.add(prune2.get(i));		
		}
		System.out.println("Redundancy Prune" + prune3.size());
		
	//	testICTCLAS50.ICTCLAS_Exit();
//		}
	//	catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	}

//	private static void CompactPrune(int minDistance, int minAppearance) {
//		// for(int i = 0; i < items2.size(); i++){
//		// System.out.println(items2.get(i));
//		// }
//		for (int i = 0; i < items2.size(); i++) {
//			String[] words = st.split(items2.get(i)).getFinalResult()
//					.split(" ");
//			int count = 0;
//			for (int j = 0; j < sens.size(); j++) {
//				boolean flag = true;
//				for (int k = 0; k < words.length; k++) {
//					if (!sens.get(j).contains(words[k])) {
//						flag = false;
//						break;
//					}
//				}
//				if (flag) {
//					String[] tmp = sens.get(j).split(" ");
//					int[] pos = new int[words.length];
//					for (int k = 0; k < tmp.length; k++) {
//						tmp[k] = tmp[k].substring(0, tmp[k].indexOf("/"));
//						for (int p = 0; p < words.length; p++) {
//							if (tmp[k].equals(words[p]))
//								pos[p] = k;
//						}
//					}
//					boolean flag2 = true;
//					for (int q = 1; q < pos.length; q++) {
//						if (pos[q] - pos[q - 1] > minDistance) {
//							flag2 = false;
//							break;
//						}
//					}
//					if (flag2)
//						count++;
//					// else
//					// System.out.println("@"+items2.get(i));
//				}
//				if (count >= minAppearance) {
//					cand1.add(items2.get(i));
//					break;
//				}
//			}
//		}
//	}
//
//	private static void RedundancyPrune(int minPureSupport) {
//		// System.out.println("Redundancy Pruning starts...");
//		ArrayList<String> it1, tmp, it2;
//		tmp = RPcore(items1, items1, minPureSupport);
//		it1 = RPcore(tmp, cand1, minPureSupport);
//		it2 = RPcore(cand1, cand1, minPureSupport);
//		// items2.clear();
//		System.out.println("1-d num: " + it1.size());
//		System.out.println("2-d num: " + it2.size());
//		cand2.addAll(it1);
//		cand2.addAll(it2);
//		// System.out.println("Redundancy Pruning succeed!");
//	}

//	private static ArrayList<String> RPcore(ArrayList<String> src1,
//			ArrayList<String> src2, int minPureSupport) {
//	SegTag st = new SegTag(1);
//		ArrayList<String> result = new ArrayList<String>();
//		for (int i = 0; i < src1.size(); i++) {
//			String check = src1.get(i);
//			// System.out.println("#"+i+"check: "+check);
//			boolean flag1 = true, flag2 = false;
//			for (int j = 0; j < src2.size(); j++) {
//				// System.out.print(j+".");
//				if (src2.get(j).equals(check))
//					continue;
//				if (src2.get(j).contains(check)) {
//					// System.out.println(check+""+src2.get(j));
//					flag1 = false;
//					int count = 0;
//					String parent = st.split(src2.get(j)).getFinalResult();
//					for (int k = 0; k < sens.size(); k++) {
//						if (sens.get(k).contains(check)
//								&& !sens.get(k).contains(parent))
//							count++;
//						if (count >= minPureSupport) {
//							flag2 = true;
//							break;
//						}
//					}
//				}
//			}
//			if (flag1)
//				result.add(check);
//			else if (!flag1 && flag2)
//				result.add(check);
//		}
//		return result;
//	}
}
