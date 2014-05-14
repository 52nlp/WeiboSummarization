package Fpgrowth;

public class FrequentItemResults {
	//存储FPtree抽取频繁项的中间结果
	public ItemSetNode []largeItemset;
	public int [] numLarge;
	public int [] largeItem1;	
	public int realK;
	public String [] wordList;
	
	public FrequentItemResults(Fpt fpt)
	{
		this.largeItemset = fpt.largeItemset;
		this.numLarge = fpt.numLarge;
		this.largeItem1 = fpt.largeItem1;
		this.realK = fpt.realK;
		this.wordList = fpt.wordList;
	}
}
