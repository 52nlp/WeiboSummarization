package naiveSummary;

public class Node {
	public int depth;
	public int number;
	public double value;
	public String content;
	public int sonNum;
	public Node firstSon;
	public Node father;
	protected Node leftBrother;
	protected Node rightBrother;
	
	//construction function 1
	public Node(String _content){
		depth=0;
		number=0;
		value=0;
		sonNum=0;
		content=_content;
		firstSon=null;
		father=null;
		leftBrother=null;
		rightBrother=null;
	}
	
	//construction function 2
	public Node(Node _father,String _content,int _depth,int _number){
		depth=_depth;
		number=_number;
		content=_content;
		value=0;
		sonNum=0;
		firstSon=null;
		father=_father;
		leftBrother=null;
		rightBrother=null;
	}
	
	//search for a node with a particular word
	public Node search(String tofind){
		Node pt=firstSon;
		if(pt==null)
			return null;
		if(pt.content.equals(tofind))
			return pt;
		else{
			pt=pt.rightBrother;
			while(pt!=firstSon){
				if(pt.content.equals(tofind))
					return pt;
				pt=pt.rightBrother;
			}
		}
		return null;
	}
	
	//add a new son node
	public Node addSon(String name){
		Node toadd=new Node(this,name,depth+1,1);
		if(sonNum!=0){
			toadd.rightBrother=firstSon;
			toadd.leftBrother=firstSon.leftBrother;
			firstSon.leftBrother.rightBrother=toadd;
			firstSon.leftBrother=toadd;
		}else{
			firstSon=toadd;
			firstSon.leftBrother=firstSon;
			firstSon.rightBrother=firstSon;
		}
		sonNum++;
		return toadd;
	}
		
	//find the next node
	public Node nextNode(){
		if(sonNum!=0){
			return firstSon;
		}else{
			Node presentNode=this;
			while(presentNode!=null){
				if(presentNode.father==null||presentNode.rightBrother==presentNode.father.firstSon){
//					if(presentNode.father!=null)
//						System.out.println(presentNode.content+"->father"+presentNode.father.content);
					presentNode=presentNode.father;
				}
				else{
//					System.out.println(presentNode.content+"->rightbrother"+presentNode.rightBrother.content);
					return presentNode.rightBrother;
				}
			}
		}
		return null;
	}
		
	//calculate the number of nodes in a certain level
	public int size(int level){
		int nodesize=0;
		if(level==0)
			return 1;
		else{
			if(firstSon==null)
				return 0;
			Node presentNode=firstSon;
			do{
				nodesize+=presentNode.size(level-1);
				presentNode=presentNode.rightBrother;
			}while(presentNode!=firstSon);
		}
		return nodesize;
	}
	
	//calculate the number of nodes in the whole tree
	public int totalSize(){
		if(firstSon==null)
			return 1;
		Node presentNode=firstSon;
		int nodesize=1;
		do{
			nodesize+=presentNode.totalSize();
			presentNode=presentNode.rightBrother;
		}while(presentNode!=firstSon);
		return nodesize;
	}
	
	//calculate the value of each node
	public double calc(){
		if(number!=0)
			value=(double)number-((double)depth-1)*Math.log10((double)number)/Math.log10((double)Config.base);
		else
			value=0;
		return value;
				
	}
	
	//print the information of Node
	public void print(){
		System.out.println(content+" "+depth+" "+number+" "+value);
	}
}
