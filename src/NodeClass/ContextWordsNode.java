package NodeClass;

public class ContextWordsNode {
//ContextWordsNode为概念类ConceptNode中离散上下文中含有的词节点
	public int dictPos;//在vocabulary里的下标位置
	public double tf;//词频

	public ContextWordsNode(int dictPos, double tf) {
		this.dictPos = dictPos;
		this.tf = tf;
	}

}
