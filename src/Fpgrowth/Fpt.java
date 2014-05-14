package Fpgrowth;

import java.io.*;
import java.util.*;

class FPnode {
	public int item;
	public int count;
	public int numPath;
	FPnode parent;
	Childnode children;
	FPnode hlink;
}

class Childnode {
	public FPnode node;
	Childnode next;
}

public class Fpt {
	final static String DATA_FILE = "data.txt";
	final static String RESULT_FILE = "result.txt";
	final static String DICT_FILE = "dict.txt";
	final static String CONFIG_FILE = "config.txt";

	ItemSetNode[] largeItemset;
	FPnode root = null;
	FPnode[] headerTableLink;
	int[] numLarge;
	int[] support1;
	int[] largeItem1;

	int expectedK;
	int realK;
	int threshold;
	double thresholdPercent;
	int numItem;
	int numTrans;
	FileReader fr;
	FileWriter wr;

	public void swap(int[] support, int[] itemset, int x, int i) {
		int temp;
		temp = support[x];
		support[x] = support[i];
		support[i] = temp;
		temp = itemset[x];
		itemset[x] = itemset[i];
		itemset[i] = temp;
	}

	public void q_sortD(int[] support, int[] itemset, int low, int high,
			int size) {
		int pass;
		int highptr = high++;

		int pivot = low;

		if (low >= highptr)
			return;
		do {
			pass = 1;
			while (pass == 1) {
				if (++low < size) {
					if (support[low] > support[pivot])
						pass = 1;
					else
						pass = 0;
				} else
					pass = 0;
			}
			pass = 1;
			while (pass == 1) {
				if (high-- > 0) {
					if (support[high] < support[pivot])
						pass = 1;
					else
						pass = 0;
				} else
					pass = 0;
			}
			if (low < high)
				swap(support, itemset, low, high);
		} while (low <= high);

		swap(support, itemset, pivot, high);
		q_sortD(support, itemset, pivot, high - 1, size);
		q_sortD(support, itemset, high + 1, highptr, size);
	}

	public void q_sortA(int[] indexList, int[] freqItemP, int low, int high,
			int size) {
		int pass;
		int highptr = high++;
		int pivot = low;

		if (low >= highptr)
			return;
		do {
			pass = 1;
			while (pass == 1) {
				if (++low < size) {
					if (indexList[low] < indexList[pivot])
						pass = 1;
					else
						pass = 0;
				} else
					pass = 0;
			}

			pass = 1;
			while (pass == 1) {
				if (high-- > 0) {
					if (indexList[high] > indexList[pivot])
						pass = 1;
					else
						pass = 0;
				} else
					pass = 0;
			}

			if (low < high)
				swap(indexList, freqItemP, low, high);
		} while (low <= high);

		swap(indexList, freqItemP, pivot, high);

		q_sortA(indexList, freqItemP, pivot, high - 1, size);
		q_sortA(indexList, freqItemP, high + 1, highptr, size);
	}

	public void pass1() {
		int transSize;
		int item;

		int maxSize = 0;
		// ifstream fin; /**IO stream */
		int i, j;

		support1 = new int[numItem];
		largeItem1 = new int[numItem];
		if (support1 == null || largeItem1 == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		for (i = 0; i < numItem; i++) {
			support1[i] = 0;
			largeItem1[i] = i;
		}
		try {
			fr = new FileReader(DATA_FILE);
		} catch (Exception e) {
			System.out.println("错误:不能打开data.txt文件\n" + e);

		}

		BufferedReader br = new BufferedReader(fr);

		Scanner fin = new Scanner(br);

		for (i = 0; i < numTrans; i++) {
			transSize = fin.nextInt();
			if (transSize > maxSize) {
				maxSize = transSize;
			}
			for (j = 0; j < transSize; j++) {
				item = fin.nextInt();
				support1[item]++;
			}
		}
		try {
			fin.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		realK = expectedK;
		if ((maxSize < expectedK) || (expectedK <= 0)) {
			realK = maxSize;
		}
		System.out.println("最大项数:" + maxSize);
		System.out.println("挖掘的最大项目集数:" + realK);

		largeItemset = new ItemSetNode[realK];
		numLarge = new int[realK];
		if (largeItemset == null || numLarge == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		for (i = 0; i < realK; i++) {
			largeItemset[i] = null;
			numLarge[i] = 0;
		}

		q_sortD(support1, largeItem1, 0, numItem - 1, numItem);

		if(realK ==0){   //wangwei add
			numLarge = new int[1];
		}
		numLarge[0] = 0;
		System.out.println("支持度大小为：" + threshold);
		while ((numLarge[0] < numItem) && (support1[numLarge[0]] >= threshold))
			(numLarge[0])++;

		// for(i = 0;i<numItem;i++)
		// cout<<i+1<<" "<<support1[i]<<endl;
		System.out.println("1-频繁项集数量为" + numLarge[0]);
	}

	public void input() {
		try {
			fr = new FileReader(CONFIG_FILE);
		} catch (Exception e) {
			System.out.println("错误:不能打开data.txt文件\n" + e);
		}
		BufferedReader br = new BufferedReader(fr);

		Scanner fin = new Scanner(br);
		expectedK = fin.nextInt();
		thresholdPercent = fin.nextDouble();
		numItem = fin.nextInt();
		numTrans = fin.nextInt();

		threshold = (int) (thresholdPercent * numTrans);
		if (threshold < 2)
			threshold = 2;
		System.out.print(threshold + " " + expectedK);

		try {
			fin.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println("文件无法关闭:\n" + e);
		}
	}

	public void buildTree() {
		int[] freqItemP;
		int[] indexList;
		int count;

		int transSize;
		int item;
		int i, j, m;
		int[] path = { 0 };

		headerTableLink = new FPnode[numLarge[0]];
		if (headerTableLink == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		for (i = 0; i < numLarge[0]; i++)
			headerTableLink[i] = null;

		root = new FPnode();
		if (root == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		root.numPath = 1;
		root.children = null;
		root.parent = null;
		root.hlink = null;

		freqItemP = new int[numItem];// realK
		indexList = new int[numItem];

		if (freqItemP == null || indexList == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		try {
			fr = new FileReader(DATA_FILE);
		} catch (Exception e) {
			System.out.println("错误:不能打开data.txt文件\n" + e);

		}

		BufferedReader br = new BufferedReader(fr);

		Scanner fin = new Scanner(br);

		for (i = 0; i < numTrans; i++) {
			transSize = fin.nextInt();
			count = 0;
			path[0] = 0;

			for (j = 0; j < transSize; j++) {
				item = fin.nextInt();
				for (m = 0; m < numLarge[0]; m++) {
					if (item == largeItem1[m]) {
						freqItemP[count] = item;
						indexList[count] = m;
						count++;
						break;
					}
				}
			}
			q_sortA(indexList, freqItemP, 0, count - 1, count);
			insert_tree(freqItemP, indexList, 1, 0, count, root,
					headerTableLink, path);

		}
		try {
			fin.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void insert_tree(int[] freqItemP, int[] indexList, int count,
			int ptr, int length, FPnode T, FPnode[] headerTableLink, int[] path) {
		Childnode newNode;
		FPnode hNode;
		FPnode hPrevious = null;
		Childnode previous = null;
		Childnode aNode;

		if (ptr == length)
			return;

		if (T.children == null) {
			newNode = new Childnode();
			newNode.node = new FPnode();

			if (newNode == null || newNode.node == null) {
				System.out.println("内存不足");
				System.exit(1);
			}

			newNode.node.item = freqItemP[ptr];
			newNode.node.count = count;
			newNode.node.numPath = 1;
			newNode.node.children = null;
			newNode.node.hlink = null;
			newNode.node.parent = T;
			newNode.next = null;
			T.children = newNode;
			// to be finished;
			hNode = headerTableLink[indexList[ptr]];
			if (hNode == null) {
				headerTableLink[indexList[ptr]] = newNode.node;
			} else {
				while (hNode != null) {
					hPrevious = hNode;
					hNode = hNode.hlink;
				}
				hPrevious.hlink = newNode.node;
			}
			insert_tree(freqItemP, indexList, count, ptr + 1, length,
					T.children.node, headerTableLink, path);

			T.numPath += path[0];
		} else {
			aNode = T.children;
			while ((aNode != null) && (aNode.node.item != freqItemP[ptr])) {
				previous = aNode;
				aNode = aNode.next;
			}

			if (aNode == null) {
				newNode = new Childnode();
				newNode.node = new FPnode();

				if (newNode == null || newNode.node == null) {
					System.out.println("内存不足");
					System.exit(1);
				}

				newNode.node.children = null;
				newNode.node.count = count;
				newNode.node.hlink = null;
				newNode.node.item = freqItemP[ptr];
				newNode.node.numPath = 1;
				newNode.node.parent = T;
				newNode.next = null;
				previous.next = newNode;

				hNode = headerTableLink[indexList[ptr]];
				if (hNode == null) {
					headerTableLink[indexList[ptr]] = newNode.node;
				} else {
					while (hNode != null) {
						hPrevious = hNode;
						hNode = hNode.hlink;
					}
					hPrevious.hlink = newNode.node;
				}
				insert_tree(freqItemP, indexList, count, ptr + 1, length,
						newNode.node, headerTableLink, path);
				(path[0])++;
				T.numPath += path[0];
			} else {
				aNode.node.count += count;
				insert_tree(freqItemP, indexList, count, ptr + 1, length,
						aNode.node, headerTableLink, path);
				T.numPath += path[0];
			}
		}
	}

	void FPgrowth(FPnode T, FPnode[] headerTableLink, int headerSize,
			int[] baseItems, int baseSize) {
		int count;
		int i, j;
		int[] pattern;
		int patternSupport;
		FPnode aNode = null;
		int[] conLargeItem;
		int[] conLargeItemSupport;
		if (baseSize > realK)
			return;
		if (T == null)
			return;

		conLargeItem = new int[headerSize];
		conLargeItemSupport = new int[headerSize];
		if ((conLargeItem == null) || (conLargeItemSupport == null)) {
			System.out.println("内存不足");
			System.exit(1);
		}

		if (T.numPath == 1) {
			count = 0;
			if (T.children != null)
				aNode = T.children.node;
			while (aNode != null) {
				conLargeItem[count] = aNode.item;
				conLargeItemSupport[count] = aNode.count;
				count++;
				if (aNode.children != null) {
					aNode = aNode.children.node;
				} else
					aNode = null;
			}
			combine(conLargeItem, conLargeItemSupport, 0, count, baseItems,
					baseSize);

		} else {
			/* Multiple Path */
			pattern = new int[baseSize + 1];
			if (pattern == null) {
				System.out.println("内存不足");
				System.exit(1);
			}

			for (i = 0; i < headerSize; i++) {
				pattern[0] = headerTableLink[i].item;

				for (j = 0; j < baseSize; j++) {
					pattern[j + 1] = baseItems[j];
				}
				aNode = headerTableLink[i];
				patternSupport = 0;
				while (aNode != null) {
					patternSupport += aNode.count;
					aNode = aNode.hlink;
				}

				addToLargeList(pattern, patternSupport, baseSize);
				genConditionalPatternTree(pattern, baseSize, patternSupport,
						conLargeItem, conLargeItemSupport, T, i, headerSize,
						headerTableLink);
			}
		}
	}

	/*
	 * 
	 * void buildConTree(FPnode []conRoot, FPnode [][]conHeader, int
	 * conHeaderSize, int []conLargeItem, int []conLargeItemSupport, FPnode T,
	 * FPnode []headerTable, int baseIndex, int headerSize) { FPnode aNode;
	 * FPnode ancestorNode;
	 * 
	 * int [] freqItemP; int [] indexList;
	 * 
	 * int path[]={0}; int count; int i; conHeader[0] = new
	 * FPnode[conHeaderSize]; if(conHeader == null) {
	 * System.out.println("内存不足"); System.exit(1); } for(i =
	 * 0;i<conHeaderSize;i++) { (conHeader)[i] = null; } (conRoot[0]) = new
	 * FPnode(); if(conHeader == null) { System.out.println("内存不足");
	 * System.exit(1);
	 * 
	 * } (conRoot[0]).numPath = 1; (conRoot[0]).parent = null;
	 * (conRoot[0]).children = null; (conRoot[0]).hlink = null;
	 * 
	 * freqItemP = new int[conHeaderSize]; if(freqItemP == null) {
	 * System.out.println("内存不足"); System.exit(1); }
	 * 
	 * indexList = new int[conHeaderSize]; if (indexList == null) {
	 * System.out.println("内存不足"); System.exit(1); }
	 * 
	 * aNode = headerTable[baseIndex]; while (aNode != null) { ancestorNode =
	 * aNode.parent; count = 0; while (ancestorNode != T) { for (i=0; i <
	 * conHeaderSize; i++) { if (ancestorNode.item == conLargeItem[i]) {
	 * freqItemP[count] = ancestorNode.item; indexList[count] = i; count++;
	 * break; } } ancestorNode = ancestorNode.parent; } q_sortA(indexList,
	 * freqItemP, 0, count-1, count); path[0] = 0; insert_tree(freqItemP,
	 * indexList, aNode.count, 0, count, conRoot[0], conHeader[0], path); aNode
	 * = aNode.hlink; }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * void genConditionalPatternTree(int [] pattern, int baseSize, int
	 * patternSupport,int [] conLargeItem, int [] conLargeItemSupport, FPnode
	 * T,int headerIndex, int headerSize, FPnode [] headerTableLink) { int
	 * conHeaderSize; FPnode [][] conHeader = null; FPnode [] conRoot = null;
	 * FPnode aNode,ancestorNode; int j; for(j = 0;j<headerSize;j++) {
	 * conLargeItemSupport[j] = 0; }
	 * 
	 * aNode = headerTableLink[headerIndex]; conHeaderSize = 0;
	 * 
	 * while(aNode != null) { ancestorNode = aNode.parent;
	 * 
	 * while(ancestorNode != T) { for(j = 0;j<headerSize;j++) {
	 * if(ancestorNode.item == headerTableLink[j].item) { conLargeItemSupport[j]
	 * += aNode.count; if((conLargeItemSupport[j] >= threshold) &&
	 * (conLargeItemSupport[j] - aNode.count<threshold)) { conLargeItem[j] =
	 * ancestorNode.item; conHeaderSize++; } break; } } ancestorNode =
	 * ancestorNode.parent; } aNode = aNode.hlink; }
	 * q_sortD(conLargeItemSupport, conLargeItem, 0, headerSize-1, headerSize);
	 * 
	 * if(conHeaderSize>0) {
	 * buildConTree(conRoot,conHeader,conHeaderSize,conLargeItem
	 * ,conLargeItemSupport,T,headerTableLink,headerIndex,headerSize);
	 * FPgrowth(conRoot[0],conHeader[0],conHeaderSize,pattern,baseSize+1);
	 * 
	 * } }
	 */

	public void buildConTree(FPnode conRoot, FPnode[] conHeader,
			int conHeaderSize, int[] conLargeItem, int[] conLargeItemSupport,
			FPnode T, FPnode[] headerTable, int baseIndex, int headerSize) {

	}

	public void genConditionalPatternTree(int[] pattern, int baseSize,
			int patternSupport, int[] conLargeItem, int[] conLargeItemSupport,
			FPnode T, int headerIndex, int headerSize, FPnode[] headerTableLink) {
		int conHeaderSize;
		FPnode[] conHeader = null;
		FPnode conRoot = null;
		FPnode aNode, ancestorNode;
		int j;
		for (j = 0; j < headerSize; j++) {
			conLargeItemSupport[j] = 0;
		}

		aNode = headerTableLink[headerIndex];
		conHeaderSize = 0;

		while (aNode != null) {
			ancestorNode = aNode.parent;

			while (ancestorNode != T) {
				for (j = 0; j < headerSize; j++) {
					if (ancestorNode.item == headerTableLink[j].item) {
						conLargeItemSupport[j] += aNode.count;
						if ((conLargeItemSupport[j] >= threshold)
								&& (conLargeItemSupport[j] - aNode.count < threshold)) {
							conLargeItem[j] = ancestorNode.item;
							conHeaderSize++;
						}
						break;
					}
				}
				ancestorNode = ancestorNode.parent;
			}
			aNode = aNode.hlink;
		}
		q_sortD(conLargeItemSupport, conLargeItem, 0, headerSize - 1,
				headerSize);

		if (conHeaderSize > 0) {
			// start

			FPnode aNode2;
			FPnode ancestorNode2;

			int[] freqItemP;
			int[] indexList;

			int[] path = { 0 };
			int count;
			int i;
			conHeader = new FPnode[conHeaderSize];
			if (conHeader == null) {
				System.out.println("内存不足");
				System.exit(1);
			}
			for (i = 0; i < conHeaderSize; i++) {
				(conHeader)[i] = null;
			}
			conRoot = new FPnode();
			if (conHeader == null) {
				System.out.println("内存不足");
				System.exit(1);
			}
			(conRoot).numPath = 1;
			(conRoot).parent = null;
			(conRoot).children = null;
			(conRoot).hlink = null;

			freqItemP = new int[conHeaderSize];
			if (freqItemP == null) {
				System.out.println("内存不足");
				System.exit(1);
			}

			indexList = new int[conHeaderSize];
			if (indexList == null) {
				System.out.println("内存不足");
				System.exit(1);
			}

			aNode2 = headerTableLink[headerIndex];
			while (aNode2 != null) {
				ancestorNode2 = aNode2.parent;
				count = 0;
				while (ancestorNode2 != T) {
					for (i = 0; i < conHeaderSize; i++) {
						if (ancestorNode2.item == conLargeItem[i]) {
							freqItemP[count] = ancestorNode2.item;
							indexList[count] = i;
							count++;
							break;
						}
					}
					ancestorNode2 = ancestorNode2.parent;
				}
				q_sortA(indexList, freqItemP, 0, count - 1, count);
				path[0] = 0;
				insert_tree(freqItemP, indexList, aNode2.count, 0, count,
						conRoot, conHeader, path);
				aNode2 = aNode2.hlink;
			}

			// end
			FPgrowth(conRoot, conHeader, conHeaderSize, pattern, baseSize + 1);

		}
	}

	public void combine(int[] itemList, int[] support, int start,
			int itemListSize, int[] base, int baseSize) {
		int[] pattern;
		int i, j;

		if (baseSize >= realK)
			return;
		if (start == itemListSize)
			return;
		pattern = new int[baseSize + 1];
		if (pattern == null) {
			System.out.println("内存不足");
			System.exit(1);
		}
		for (j = 0; j < baseSize; j++) {
			pattern[j] = base[j];
		}
		for (i = start; i < itemListSize; i++) {
			pattern[baseSize] = itemList[i];
			addToLargeList(pattern, support[i], baseSize);
			combine(itemList, support, i + 1, itemListSize, pattern,
					baseSize + 1);
		}
	}

	void addToLargeList(int[] pattern, int patternSupport, int index) {
		ItemSetNode aLargeItemset;
		ItemSetNode aNode = null;
		ItemSetNode previous = null;

		int i;

		aLargeItemset = new ItemSetNode();
		aLargeItemset.itemset = new int[index + 1];
		if (aLargeItemset.itemset == null || aLargeItemset == null) {
			System.out.println("内存不足");
			System.exit(1);
		}

		aLargeItemset.support = patternSupport;

		for (i = 0; i <= index; i++) {
			aLargeItemset.itemset[i] = pattern[i];
			// out3<<pattern[i]<<" ";
			// System.out.print (pattern[i]+" ");
		}
		// out3<<" ["<<patternSupport<<"]"<<endl;
		// System.out.println("["+ patternSupport+"]");

		aLargeItemset.next = null;
		try {
			aNode = largeItemset[index];
			if (aNode == null) {
				largeItemset[index] = aLargeItemset;
			} else {
				while ((aNode != null) && (aNode.support > patternSupport)) {
					previous = aNode;
					aNode = aNode.next;
				}
				if (previous != null) {
					previous.next = aLargeItemset;
					aLargeItemset.next = aNode;
				} else {
					aLargeItemset.next = largeItemset[index];
					largeItemset[index] = aLargeItemset;
				}
			}
			(numLarge[index])++;
		} catch (Exception e) {
			System.out.println("Error: " + largeItemset.length + "    " + index);
		}
	}

	public String[] wordList;

	public void loadfile() {
		int i;
		wordList = new String[numItem];
		// ifstream fin;
		try {
			fr = new FileReader(DICT_FILE);
		} catch (Exception e) {
			System.out.println("错误:不能打开data.txt文件\n" + e);
		}

		BufferedReader br = new BufferedReader(fr);

		Scanner fin = new Scanner(br);

		i = 0;

		while (fin.hasNextLine()) {
			wordList[i] = fin.nextLine().trim();
			i++;
		}
		try {
			fin.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public void displayResult() {
		ItemSetNode aLargeItemset;

		int i, j;
		try {
			wr = new FileWriter(RESULT_FILE);
		} catch (Exception e) {
			System.out.println("错误:不能打开data.txt文件\n" + e);

		}

		BufferedWriter br = new BufferedWriter(wr);
		PrintWriter pw = new PrintWriter(br);

		try {
			for (i = 0; i < realK; i++) {
				pw.println(numLarge[i] + "个" + (i + 1) + "-频繁项目集:");
				if (numLarge[i] == 0)
					break;
				aLargeItemset = largeItemset[i];
				while (aLargeItemset != null) {
					for (j = 0; j <= i; j++) {
						pw.print(wordList[aLargeItemset.itemset[j]] + " ");
					}
					pw.println("[" + aLargeItemset.support + "]");

					aLargeItemset = aLargeItemset.next;
				}
				pw.println();
			}

			br.flush();
			pw.close();
			br.close();
			wr.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public String displayResultToString() {
		String result = "";
		ItemSetNode aLargeItemset;

		int i, j;

		try {
			for (i = 0; i < realK; i++) {
				result += (numLarge[i] + "个" + (i + 1) + "-频繁项目集:\r\n");
				if (numLarge[i] == 0)
					break;
				aLargeItemset = largeItemset[i];
				while (aLargeItemset != null) {
					for (j = 0; j <= i; j++) {
						result += (wordList[aLargeItemset.itemset[j]] + " ");
					}
					result += ("[" + aLargeItemset.support + "]\r\n");
					aLargeItemset = aLargeItemset.next;
				}
				result += " \r\n";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}

	public static void main(String[] args) {
		int headerTableSize;
		System.out.println("输入参数数据...");
		long startTime, endTime;
		startTime = System.currentTimeMillis();

		Fpt fpt = new Fpt();
		fpt.input();
		System.out.println("找出1-频繁项集");
		fpt.pass1();

		if (fpt.numLarge[0] > 0) {
			System.out.println("生成树...");
			fpt.buildTree();
			System.out.println("算法运行中...");

			headerTableSize = fpt.numLarge[0];
			fpt.numLarge[0] = 0;
			fpt.FPgrowth(fpt.root, fpt.headerTableLink, headerTableSize, null,
					0);
			System.out.println("频繁项目集为...");
			fpt.loadfile();
			fpt.displayResult();
		}

		endTime = System.currentTimeMillis();
		System.out.println((double) (endTime - startTime) / 1000.0 + "秒");
	}

	public static String runFpt() {
		String temp = "";
		int headerTableSize;
		// System.out.println("输入参数数据...");
		long startTime, endTime;
		startTime = System.currentTimeMillis();

		Fpt fpt = new Fpt();
		fpt.input();
		System.out.println("找出1-频繁项集");
		fpt.pass1();

		if (fpt.numLarge[0] > 0) {
			System.out.println("生成树...");
			fpt.buildTree();
			System.out.println("算法运行中...");

			headerTableSize = fpt.numLarge[0];
			fpt.numLarge[0] = 0;
			fpt.FPgrowth(fpt.root, fpt.headerTableLink, headerTableSize, null,
					0);
			System.out.println("频繁项目集为...");
			fpt.loadfile();
			temp = fpt.displayResultToString();
		}

		endTime = System.currentTimeMillis();
		temp += "执行时间：" + ((double) (endTime - startTime) / 1000.0 + "秒");
		return temp;
	}

	public FrequentItemResults runFptNew() {
		FrequentItemResults result = null;
		int headerTableSize;
		System.out.println("输入参数数据...");

		Fpt fpt = new Fpt();
		fpt.input();
		System.out.println("找出1-频繁项集");
		fpt.pass1();

		if (fpt.numLarge[0] > 0) {
			System.out.println("生成树...");
			fpt.buildTree();
			System.out.println("算法运行中...");

			headerTableSize = fpt.numLarge[0];
			fpt.numLarge[0] = 0;
			fpt.FPgrowth(fpt.root, fpt.headerTableLink, headerTableSize, null,
					0);
			System.out.println("频繁项目集为...");
			fpt.loadfile();
			result = new FrequentItemResults(fpt);
		}
		return result;
	}
}
