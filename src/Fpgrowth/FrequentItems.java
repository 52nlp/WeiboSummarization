package Fpgrowth;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import util.SentencesUtil;




public class FrequentItems {

	public static FrequentItemResults getFrequentItem(int maxItems,
			double support) {
		Gen_File.GenerateFile(maxItems, support);
		Fpt test = new Fpt();
		return test.runFptNew();
	}

	private class Mycomparator implements Comparator {
		public Mycomparator() {
		}

		public int compare(Object o1, Object o2) {
			ItemNode i1 = (ItemNode) o1;
			ItemNode i2 = (ItemNode) o2;
			if (i1.support < i2.support)
				return 1;
			else
				return 0;
		}

	}

	public ArrayList<ItemNode> sort(ArrayList<ItemNode> src) {
		Collections.sort(src, new Mycomparator());//根据出现频率排序
		return src;
	}

	//调用FPTree算法，抽取一维、二维频繁项集，写入FreqItems.txt
	public static ArrayList<ItemNode> run() {
	//	FrequentItemResults test = FrequentItems.getFrequentItem(2, 0.01);
		FrequentItemResults test = FrequentItems.getFrequentItem(2, SentencesUtil.CONFIDENCE);
		ItemSetNode aLargeItemset;
		ArrayList<ItemNode> freqItems = new ArrayList<ItemNode>();
		try {

			for (int i = 0; i < test.realK; i++) {
				System.out
						.print((test.numLarge[i] + "个" + (i + 1) + "-频繁项目集:\r\n"));
				if (test.numLarge[i] == 0)
					break;
				aLargeItemset = test.largeItemset[i];
				while (aLargeItemset != null) {
					String word = "";
					for (int j = 0; j <= i; j++) {
						word += test.wordList[aLargeItemset.itemset[j]];
						if (j < i)
							word += " ";
						// System.out.print(test.wordList[aLargeItemset.itemset[j]]+" ");
					}
					freqItems.add(new ItemNode(word, aLargeItemset.support));
					// System.out.print(("FREITE" + word + " ["
					// + aLargeItemset.support + "]\r\n"));
					aLargeItemset = aLargeItemset.next;
				}
			}
			PrintWriter pw = new PrintWriter("FreqItems.txt");
			for (int i = 0; i < freqItems.size(); i++) {
				// System.out.println(freqItems.get(i));
				pw.println(freqItems.get(i).content);
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return freqItems;
	}
}
