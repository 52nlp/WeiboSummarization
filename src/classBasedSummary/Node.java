package classBasedSummary;

public class Node extends naiveSummary.Node{
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
		super(_content);
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
		super(_father,_content,_depth,_number);
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
	
	//insert a node as a son
	public void insertAsSon(Node n){
		if(n==null)
			return;
		n.leftBrother.rightBrother=n.rightBrother;
		n.rightBrother.leftBrother=n.leftBrother;
		n.leftBrother=n;
		n.rightBrother=n;
		n.father.sonNum--;
		n.father=this;
		n.depth=depth+1;
		if(firstSon==null){
			firstSon=n;
			firstSon.rightBrother=firstSon;
			firstSon.leftBrother=firstSon;
			sonNum++;
			return;
		}
		Node presentNode=firstSon;
		boolean hit=false;
		do{
			if(presentNode.content.equalsIgnoreCase(n.content)){
				hit=true;
				sonNum++;
				presentNode.merge(n,true);
				break;
			}
			presentNode=presentNode.rightBrother;
		}while(presentNode!=firstSon);
		if(hit==false){
			n.rightBrother=firstSon;
			n.leftBrother=firstSon.leftBrother;
			firstSon.leftBrother.rightBrother=n;
			firstSon.leftBrother=n;
			sonNum++;
		}
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
	
	//merge two nodes
	public void merge(Node tomerge,boolean order){
//		int mode=0;
		if(tomerge==null)
			return;
		if(father==tomerge.father&&father!=null){
			number+=tomerge.number;
			int f_index=content.indexOf("/");
			int s_index=tomerge.content.indexOf("/");
			String f_content=content.substring(0,f_index);
			if(f_content.contains("("))
				f_content=f_content.substring(1,f_content.length()-1);
			String s_content=tomerge.content.substring(0,s_index);
			if(s_content.contains("("))
				s_content=s_content.substring(1,s_content.length()-1);
			String f_speech=content.substring(f_index+1);
			String s_speech=tomerge.content.substring(s_index+1);
			if(f_content.equalsIgnoreCase(s_content)){
				if(!f_speech.equalsIgnoreCase(s_speech)){
					if(order==true)
						content=f_content+"/"+f_speech+s_speech;
					else
						content=f_content+"/"+s_speech+f_speech;
				}
			}else{
				if(f_speech.equalsIgnoreCase(s_speech)){
					if(order==true)
						content="("+f_content+"|"+s_content+")/"+f_speech;
					else
						content="("+s_content+"|"+f_content+")/"+f_speech;
				}else{
					if(order==true)
						content="("+f_content+"|"+s_content+")/"+f_speech+s_speech;
					else
						content="("+s_content+"|"+f_content+")/"+s_speech+f_speech;
				}
			}
//			if(!content.equalsIgnoreCase(tomerge.content)){
//				content+="|"+tomerge.content;
//				mode=1;
//			}else{
//				mode=2;
//			}
			if(tomerge.father.firstSon==tomerge){
				tomerge.father.firstSon=tomerge.rightBrother;
			}
			tomerge.leftBrother.rightBrother=tomerge.rightBrother;
			tomerge.rightBrother.leftBrother=tomerge.leftBrother;
			tomerge.father.sonNum--;
			if(tomerge.sonNum!=0){
				if(sonNum==0){
					firstSon=tomerge.firstSon;
					sonNum=tomerge.sonNum;
					Node presentNode=firstSon;
					do{
						presentNode.father=this;
						presentNode=presentNode.rightBrother;
					}while(presentNode!=firstSon);
					tomerge.sonNum=0;
				}else{
					Node presentNode=tomerge.firstSon;
					int sonNumSaved=tomerge.sonNum;
					do{
						Node nextNode=presentNode.rightBrother;
						insertAsSon(presentNode);
						presentNode=nextNode;
						sonNumSaved--;
					}while(sonNumSaved!=0);
				}
			}
			tomerge.firstSon=null;
			tomerge.leftBrother=null;
			tomerge.rightBrother=null;
			tomerge.father=null;
			calc();
		}else if(tomerge.father==this){
//			mode=3;
			int f_index=content.indexOf("/");
			int s_index=tomerge.content.indexOf("/");
			String f_content=content.substring(0,f_index);
			String s_content=tomerge.content.substring(0,s_index);
			String f_speech=content.substring(f_index+1);
			String s_speech=tomerge.content.substring(s_index+1);
			if(f_content.equalsIgnoreCase(s_content)){
				if(!f_speech.equalsIgnoreCase(s_speech)){
					if(order==true)
						content=f_content+"/"+f_speech+s_speech;
					else
						content=f_content+"/"+s_speech+f_speech;
				}
			}else{
				if(f_speech.equalsIgnoreCase(s_speech)){
					if(order==true)
						content=f_content+s_content+"/"+f_speech;
					else
						content=s_content+f_content+"/"+f_speech;
				}else{
					if(order==true)
						content=f_content+s_content+"/"+f_speech+s_speech;
					else
						content=s_content+f_content+"/"+s_speech+f_speech;
				}
			}
//			if(!content.equalsIgnoreCase(tomerge.content)){
//				if(order==true)
//					content=content+"("+tomerge.content+")";
//				else
//					content="("+tomerge.content+")"+content;
//			}
			if(sonNum==1){
				firstSon=tomerge.firstSon;
				sonNum=tomerge.sonNum;
				if(tomerge.sonNum!=0){
					Node presentNode=firstSon;
					do{
						presentNode.father=this;
						presentNode=presentNode.rightBrother;
						presentNode.depth=this.depth+1;
					}while(presentNode!=firstSon);
				}
				tomerge.sonNum=0;
			}else{
				if(firstSon==tomerge)
					firstSon=tomerge.rightBrother;
				tomerge.leftBrother.rightBrother=tomerge.rightBrother;
				tomerge.rightBrother.leftBrother=tomerge.leftBrother;
				sonNum--;
				if(tomerge.sonNum!=0){
					Node presentNode=tomerge.firstSon;
					int sonNumSaved=tomerge.sonNum;
					do{
						Node nextNode=presentNode.rightBrother;
						insertAsSon(presentNode);
						presentNode=nextNode;
						sonNumSaved--;
					}while(sonNumSaved!=0);
				}
			}
			tomerge.firstSon=null;
			tomerge.leftBrother=null;
			tomerge.rightBrother=null;
			tomerge.father=null;
			calc();
		}else if(tomerge==father){
			tomerge.merge(this,order);
		}
		//check sonNum
//		int number=0;
//		if(firstSon==null){
//			if(sonNum!=0){
//				System.out.println("mode "+mode+" sonNum: "+sonNum+" real: 0");
//			}
//		}else{
//			Node presentNode=firstSon;
//			do{
//				number++;
//				presentNode=presentNode.rightBrother;
//			}while(presentNode!=firstSon);
//			if(sonNum!=number){
//				System.out.println("mode "+mode+" sonNum: "+sonNum+" real: "+number);
//			}
//		}
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
