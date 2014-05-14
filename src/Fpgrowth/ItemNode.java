package Fpgrowth;

public class ItemNode {

	public ItemNode(String str, int su){
		content = str;
		support = su;
	}
	public int getSupport(){
		return support;
	}
	public String content;
	public int support;
}
