package newHLDA.item;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


import newHLDA.Global;
import newHLDA.HLDAUtil;
import newHLDA.NodeDict;

import org.knowceans.util.Gamma;

import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;

//HLDA中的NCRPNode
//HLDA中，每一个节点表示一个主题
//因此需要记录如下的信息：
//树结构上来看：ID,所在层次，节点的父亲节点，孩子节点
//HLDA的主题节点来看：顾客数目， 包含的总的单词的词的数目，每一维度的词的数目
//ncrp是保存每一个文档的路径，weight是保存每一个文档对应的weight
public class HldaTopic {
	public int nodeID; //节点的名称，ID
	public int level; //所在层次
	public int dimensions; 
	public HldaTopic parent; //父亲节点
	public ArrayList<HldaTopic> children;
	
	public int customers; //顾客数目	
	public int[] typeCounts;//分配在该节点的每一个维度的词的数目
	public int totalTokens; //分配在该节点的词的数目		
	 
	public double ncrp;
	//保存当前节点对应某个文档的路径的每一个点的权值
	public ArrayList<Double> pathWeights;
	public double weights;
	
	//gamma采样的时候用来计算的
	public double scaling;
	
	//初始化一个结点，dimensions对应单词的数目，即当前的维度
	public HldaTopic(HldaTopic parent, int dimensions, int level) {
		nodeID = Global.totalNodes;
		this.dimensions = dimensions;
		Global.totalNodes++;
		this.level = level;
		this.parent = parent;
		children = new ArrayList<HldaTopic>();
		
		customers = 0;	    
		totalTokens = 0;
		typeCounts = new int[dimensions];
		ncrp = 0;
		weights = 0;
		pathWeights = new ArrayList<Double>();
		scaling = Global.SCALING_SCALE * Global.SCALING_SHAPE;
	}
		
	public HldaTopic(int dimensions) {
		this(null, dimensions, 0);
	}
	
	//维度增加
	public void dimensionsExtend(int newDimensions){
		if(newDimensions > this.dimensions){
			this.dimensions = newDimensions;
			this.typeCounts = HLDAUtil.arrayExtend(this.typeCounts, newDimensions);
		}
	}
		
	//判断是不是根节点
	public boolean IsRoot(){
	    return level == 0;
	}
	
	//判断是否是叶子结点
	public boolean isLeaf() {
		return level == Global.numLevels - 1;
	}
		 
	//新增加一个孩子
	public HldaTopic addChild() {
		HldaTopic node = new HldaTopic(this, typeCounts.length, level + 1);
		children.add(node);
		return node;
	}
	
	//移除孩子节点
	public void remove(HldaTopic node) {
		children.remove(node);
	}
		
	//从当前节点，增加一条从根到新的叶子结点的路径
	public HldaTopic getNewLeaf() {
		HldaTopic node = this;
		for (int i=level; i<Global.numLevels - 1; i++) {
			node = node.addChild();
		}
		return node;
	}

	//回溯，相当于回收这条路径
	public void dropPath() {
		HldaTopic node = this;
		node.customers--;
		if (node.customers == 0) {
			node.parent.remove(node);
		}
		for (int i = 1; i < Global.numLevels; i++) {
			node = node.parent;
			node.customers--;
			if (node.customers == 0) {
				node.parent.remove(node);
			}
		}
	}
		
	
	//选择好路径，因此这条路径的顾客会增多
	public void addPath() {
		HldaTopic node = this;
		node.customers++;
		for (int l = 1; l < Global.numLevels; l++) {
			node = node.parent;
			node.customers++;
		}
	}
		
	//统计一个节点为跟的子树的所有节点
	 public int CountTree(){
	      int count = 0;
	      for(HldaTopic child : children){
	             count += child.CountTree();
	      }
	      return 1 + count;
	 }

	 //获得所有子节点
	 public void GetAllTopics(ArrayList<HldaTopic> all){
         all.add(this);
         for(HldaTopic c : children){
             c.GetAllTopics(all);
         }
     }
	 

	 //获得指定层次的节点
	 public void GetLevelAllTopics(ArrayList<HldaTopic> all,int level){
         if(this.level == level){
        	 all.add(this);
         }
         else{
        	 for(HldaTopic c : children){
                 c.GetLevelAllTopics(all,level);
             } 
         }   
     }
	 
	 
	//向下选择，就是单纯的一个CRP的过程
	public HldaTopic select(HldaDoc doc) {
		//仅仅是在根节点选择下一步的时候，进行约束
		if(Global.PATH_INIT_CONSTRAINT && this.level == 0){
			if(this.children.size()<NodeDict.realTopicNum){
				for(int i = 0 ; i < NodeDict.realTopicNum; i++){
					this.addChild();
				}
			}
//			int pos = instanceJudge.getPos(doc.instance, NodeDict.id2topicWord);
//			ArrayList<Integer> topicSet = instanceJudge.getInstanceResults(doc.instance, NodeDict.id2topicWord).topicSet;
			ArrayList<Integer> topicSet = doc.insRes.topicSet;
			if(topicSet.size()!=0){
				if(topicSet.size()==1){
					int pos = topicSet.get(0);
					return children.get(pos);
				}
				else{
					//放到约束的节点下
					if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_CONSTRAINT){
						int totalCandidateCustomer = 0;
						for(Integer i: topicSet){
							if( i < this.children.size()){
								totalCandidateCustomer += this.children.get(i).customers;
							}
							else{
								System.err.println("nodeDict error!");
							}
						}
						double[] weights = new double[topicSet.size()] ;
						for(int i = 0 ; i < topicSet.size(); i++){
							weights[i] = (double)this.children.get(i).customers/totalCandidateCustomer;
						}
						int choice = Global.random.nextDiscrete(weights);
						return this.children.get(choice);
					}
					//分配到未约束的节点上
					else if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_NO_CONSTRAINT){
						int constraintCustomer = 0;
						for(int i = 0 ; i < NodeDict.realTopicNum; i++){
							constraintCustomer += children.get(i).customers;
						}
						//
						double[] weights = new double[children.size() + 1 - NodeDict.realTopicNum];
						weights[0] = this.scaling / (this.scaling + customers - 1 - constraintCustomer);
						for (int i = NodeDict.realTopicNum; i < children.size(); i++) {
							weights[i - NodeDict.realTopicNum + 1] = (double) children.get(i).customers / 
									(this.scaling + customers - 1 - constraintCustomer);
//							i++;
						}
						int choice = Global.random.nextDiscrete(weights);
						if (choice == 0) {
							return(addChild());
						}
						else{
							return children.get(choice - 1 + NodeDict.realTopicNum);
						}
					}
					//选择全部的节点空间
					else if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_ALL){
						double[] weights = new double[children.size() + 1];
					    //新节点的概率
						weights[0] = this.scaling / (this.scaling + customers - 1);
						int i = 1;
						for (HldaTopic child: children) {
							weights[i] = (double) child.customers / (this.scaling + customers - 1);
							i++;
						}
						int choice = Global.random.nextDiscrete(weights);
						if (choice == 0) {
							//新增节点，也就是说，新增的节点的概率和gamma有关
							return(addChild());
						}
						else {
							//第几个孩子
							return children.get(choice - 1);
						}
					}
					else{
						System.err.println("error!");
						return children.get(0);
					}
					
				}
			}
			else{
				//无监督的节点,限定至约束节点的后面
				//这个应该没有问题，感觉，因为在路径选择过程中，会重新生成
				int constraintCustomer = 0;
				for(int i = 0 ; i < NodeDict.realTopicNum; i++){
					constraintCustomer += children.get(i).customers;
				}
				//
				double[] weights = new double[children.size() + 1 - NodeDict.realTopicNum];
				weights[0] = this.scaling / (this.scaling + customers - 1 - constraintCustomer);
				for (int i = NodeDict.realTopicNum; i < children.size(); i++) {
					weights[i - NodeDict.realTopicNum + 1] = (double) children.get(i).customers / 
							(this.scaling + customers - 1 - constraintCustomer);
//					i++;
				}
				int choice = Global.random.nextDiscrete(weights);
				if (choice == 0) {
					return(addChild());
				}
				else{
					return children.get(choice - 1 + NodeDict.realTopicNum);
				}
			}
		}
		else{
			double[] weights = new double[children.size() + 1];
		    //新节点的概率
			weights[0] = this.scaling / (this.scaling + customers - 1);
			int i = 1;
			for (HldaTopic child: children) {
				weights[i] = (double) child.customers / (this.scaling + customers - 1);
				i++;
			}
			int choice = Global.random.nextDiscrete(weights);
			if (choice == 0) {
				//新增节点，也就是说，新增的节点的概率和gamma有关
				return(addChild());
			}
			else {
				//第几个孩子
				return children.get(choice - 1);
			}
		}
	}
	
	//向下选择一个已经存在的,用于单一的初始化测试
	public HldaTopic selectExisting(){
		double[] weights = new double[children.size()];
        int i = 0;	
        for (HldaTopic child: children) {
			weights[i] = (double) child.customers / (this.scaling + customers - 1);
			i++;
		}
        return children.get(Global.random.nextDiscrete(weights));
	}
	
	//获得根节点到当前节点的路径
	public HldaTopic[] getPath(){
		int level = this.level;
		HldaTopic[] path = new HldaTopic[level+1];
		HldaTopic temp = this;
		for(int i = level; i >= 0; i--){
			path[i] = temp;
			temp = temp.parent;
		}
		return path;
	}
	
	//输出当前的节点的词的数目
	public String getTopWords() {
		//单纯的比较器
		IDSorter[] sortedTypes = new IDSorter[Global.vocalSize];    
		//落在当前的节点的词的数目
		for (int type=0; type < Global.vocalSize; type++) {
			sortedTypes[type] = new IDSorter(type, typeCounts[type]);
		}
		Arrays.sort(sortedTypes); 
		Alphabet alphabet = Global.instances.getDataAlphabet();
		StringBuffer out = new StringBuffer();	
		for (int i=0; i< Global.outputWordNum; i++) {
			//获得在VSM模型中，ID和词的映射关系，根据ID所得到的的词
			if(typeCounts[sortedTypes[i].getID()] > 0 ){
				switch (Global.cuurentDisplayCONFIG) {
				case Global.displayWord:
					out.append(alphabet.lookupObject(sortedTypes[i].getID()) + " "
//							+ " ： " + typeCounts[sortedTypes[i].getID()] + " "
//							+ " : " + (double)(typeCounts[sortedTypes[i].getID()] + Global.etaList.get(level))
//							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;
				case Global.displayWordNum:
					out.append(alphabet.lookupObject(sortedTypes[i].getID()) + " "
							+ " ： " + typeCounts[sortedTypes[i].getID()] + " "
//							+ " : " + (double)(typeCounts[sortedTypes[i].getID()] + Global.etaList.get(level))
//							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;
				case Global.displayWordWeight:
					out.append(alphabet.lookupObject(sortedTypes[i].getID()) + " "
//							+ " ： " + typeCounts[sortedTypes[i].getID()] + " "
							+ " : " + (double)(typeCounts[sortedTypes[i].getID()] + Global.etaList.get(level))
							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;

				default:
					break;
				}
		}
		else{
			break;
		}
	   }
		return out.toString();
	}
	
	//输出当前的节点的词的数目
	public String getTopMultiWords() {
		//单纯的比较器
		IDSorter[] sortedTypes = new IDSorter[Global.vocalSize];    
		//落在当前的节点的词的数目
		for (int type=0; type < Global.vocalSize; type++) {
			sortedTypes[type] = new IDSorter(type, typeCounts[type]);
		}
		Arrays.sort(sortedTypes); 
		Alphabet alphabet = Global.instances.getDataAlphabet();
		ArrayList<Integer> idList = new ArrayList<Integer>(); 
		for (int i=0; i < Global.vocalSize; i++){
			String str = alphabet.lookupObject(sortedTypes[i].getID()).toString();
			if(str.length() >1){
				idList.add(sortedTypes[i].getID());
			}
			if( idList.size() >= Global.outputWordNum ){
				break;
			}
		}
		StringBuffer out = new StringBuffer();	
		for (int i=0; i< idList.size(); i++) {
			//获得在VSM模型中，ID和词的映射关系，根据ID所得到的的词
			if(typeCounts[idList.get(i)] > 0 ){
				switch (Global.cuurentDisplayCONFIG) {
				case Global.displayWord:
					out.append(alphabet.lookupObject(idList.get(i)) + " "
//							+ " ： " + typeCounts[sortedTypes[i].getID()] + " "
//							+ " : " + (double)(typeCounts[sortedTypes[i].getID()] + Global.etaList.get(level))
//							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;
				case Global.displayWordNum:
					out.append(alphabet.lookupObject(idList.get(i)) + " "
							+ " ： " + typeCounts[idList.get(i)] + " "
//							+ " : " + (double)(typeCounts[sortedTypes[i].getID()] + Global.etaList.get(level))
//							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;
				case Global.displayWordWeight:
					out.append(alphabet.lookupObject(idList.get(i)) + " "
//							+ " ： " + typeCounts[sortedTypes[i].getID()] + " "
							+ " : " + (double)(typeCounts[idList.get(i) ] + Global.etaList.get(level))
							/(totalTokens + Global.vocalSize * Global.etaList.get(level)) + " "
						);
					break;

				default:
					break;
				}
		}
		else{
			break;
		}
	   }
		return out.toString();
	}	
		
		
	//计算路径到叶子节点的似然函数
	//f[][]为某一个文档每一层每一个维度的节点的数目
	// a = lgamma(V*eta + sum(node.typeCount[level]))
	// b = sum(lgamma(eta + node.typeCount[level][type]))
	// c = sum(lgamma(eta + node.typeCount[level][type] + f[level][type]))
	// d = lgamma(V*eta + sum(node.typeCount[level])+sum(f[level]) )
	// 计算每一层的weight(a-b+c-d),同时加上父亲节点的weight，得到该节点的权值
	// 其中叶子节点无需计算，而内部节点则还需要计算
	//思考一个问题，如果当前节点在当前文档上，没有词的分布，会如何
   public void CalculatePathToLeaves(int[][] f){
	  //计算当前节点
	  int curLevel = this.level;
      double a,b,c,d;
      a = Global.vocalSize * Global.etaList.get(curLevel) + this.totalTokens; 
      b = 0;
      c = 0;
      d = a;
      double weight;
      if(f[curLevel].length!=Global.vocalSize){
    	  //维度不符合。报错
    	  System.err.println("vocalSize is error!!");
      }
      //遍历每一维度
      for(int v = 0 ; v < f[curLevel].length; v++){
      	if(f[curLevel][v]>0){
      		b+=Gamma.lgamma(Global.etaList.get(curLevel)+this.typeCounts[v]);
      		c+=Gamma.lgamma(Global.etaList.get(curLevel)+this.typeCounts[v]+f[curLevel][v]);
//      		System.out.println(String.format("level is : %s, type is: %s ", curLevel, v ));
      	}
      	d += f[curLevel][v];
      }
      //当前节点和当前文档的似然权值
      weight = (Gamma.lgamma(a) - b) + (c - Gamma.lgamma(d));
      if (IsRoot()){
          ncrp = 0;
          weights = weight;
//          pathWeights[0] = weight;
          pathWeights.add(weight);
      }
      else{
          ncrp = parent.ncrp + Math.log(customers / (parent.customers + parent.scaling));
          weights = parent.weights + weight;
          for(int i = 0 ; i < curLevel; i++){
//        	  pathWeights[i] = parent.pathWeights[i];
        	  pathWeights.add(parent.pathWeights.get(i));
          }
//          pathWeights[curLevel] = weight;
          pathWeights.add(weight);
       }
      if(Global.leafNodeWeightPrint){
    	  System.out.println("node id: " + this.nodeID + " current leaf weight:" 
    			  + this.weights);
      }
      for(HldaTopic child : children){
      		  child.CalculatePathToLeaves(f);
      }
	}
      
  //计算到内部节点的路径，也就是说，会产生新的路径
  //对于每一个内部节点，计算内部节点到叶节点的路径
  public void CalculatePathToInternalNodes(int[][] f){
      double a,b,c,d;
      //计算的为下一层开始
      a = 0;
      b = 0;
      c = 0;
      d = 0;
      //从下一层开始逐层计算
      for (int curLevel = this.level + 1; curLevel < Global.numLevels; curLevel++){
      		a = b = c = d = 0; 
      		for (int v = 0; v < f[curLevel].length; v++){
      			if(f[curLevel][v]>0){
              		b+=Gamma.lgamma(Global.etaList.get(curLevel));
              		c+=Gamma.lgamma(Global.etaList.get(curLevel) + f[curLevel][v]);
              	}
              	d += f[curLevel][v];
      		 }
      		 double etaSum = Global.vocalSize * Global.etaList.get(curLevel);
      		 a = Gamma.lgamma(etaSum);
             weights += (a - b) + (c - Gamma.lgamma(d+etaSum)); 
//             pathWeights[curLevel] = (a - b) + (c - Gamma.lgamma(d+etaSum));
             pathWeights.add((a - b) + (c - Gamma.lgamma(d+etaSum)));
      		
             
      }	
      //当前节点的nCRP值
      ncrp += Math.log(this.scaling / (customers + this.scaling));
      for(HldaTopic child : children){
          if (!child.isLeaf()){
             child.CalculatePathToInternalNodes(f);
          }
      }
    }
  
  //清空所有的pathWeights
  public void clearPathWeight(){
	  this.pathWeights.clear();
	  for(HldaTopic child : this.children){
		  child.clearPathWeight();
	  }
  }
  public void vertifyPathWeight(){
	 if(this.pathWeights.size()!=Global.numLevels){
		 System.err.println("pathWeight error!");
	 }
	 double sum = 0;
	 for(Double d : this.pathWeights){
		 sum += d;
	 }
	 if(Math.abs(this.weights-sum) > 0.01){
		 System.err.println("weight compute error! " );
	 }
	 for(HldaTopic child : this.children){
		  child.vertifyPathWeight();
	  }	  
  }
  
  public void writeToFile(PrintWriter pw){
	  for(int i = 0 ; i < level; i++){
		  pw.print(" ");
	  }
	  pw.print(this.totalTokens + "|" + this.customers + "  nodeID: " + this.nodeID + "  ");
	  pw.println(getTopWords());
	  for(HldaTopic child : this.children){
		  child.writeToFile(pw);
	  }
  }
  //计算etaScore
  public double etaScore(){
	  double score = 0;
	  double curEta = Global.etaList.get(this.level);
	  score = Gamma.lgamma(Global.vocalSize*curEta)-Global.vocalSize*Gamma.lgamma(curEta);
	  for(int i = 0 ; i < Global.vocalSize; i++){
		  score += Gamma.lgamma(typeCounts[i]+curEta);
	  }
	  score -= Gamma.lgamma(totalTokens+Global.vocalSize*curEta);
	  for(HldaTopic child : children){
		  if(child.totalTokens>0){
			  score += child.etaScore();
		  }
	  }
	  return score;
  }
  //计算gammaScore
  public double gammaScore(){
	  double score = 0;
	  if(this.children.size()>0){
		  score -= Gamma.lgamma(this.scaling + this.customers);
		  for(HldaTopic child: this.children){
			  score += Gamma.lgamma(this.scaling + child.customers);
			  score += child.gammaScore();
		  }
	  }
	  return score;
  }
  
  //计算当前新的GammaScore
  public double sampleDPScaling(){
	  double curEta = Global.random.nextBeta(this.scaling + 1, this.customers);
	  double curPi = Global.SCALING_SHAPE + this.children.size() -1;
	  double curRate = 1.0/Global.SCALING_SCALE - Math.log(curEta);
	  curPi = curPi / ( curPi + curRate * this.customers);
	  double r = Global.random.nextFloat();
	  int c;
	  if(r<curPi){
		  c = 1;
	  }
	  else{
		  c = 0;
	  }
	  double alphaNew = 0;
	  if(c==0){
		  alphaNew = Global.random.nextGamma(Global.SCALING_SHAPE + this.children.size() -1, 1.0/curRate);
	  }
	  else{
		  alphaNew = Global.random.nextGamma(Global.SCALING_SHAPE + this.children.size(), 1.0/curRate);

	  }
//	  System.out.println("current gamma:" + alphaNew);
	  return alphaNew;
  }
  
  //gamma采样
  public void sampleScaling(){
	  if(this.children.size() > 0){
		  this.scaling = sampleDPScaling();
		  for(HldaTopic child: this.children){
			  child.sampleScaling();
		  }
	  }
  }
  
  //输出当前子树的每个节点的gamma
  public void printNodeScaling(){
	  for(int i = 0 ; i <= this.level; i++){
		  System.out.print(" ");
	  }
	  System.out.println("nodeID: " + this.nodeID + "  gammma: " + this.scaling );
	  for(HldaTopic child : this.children){
		  child.printNodeScaling();
	  }
  }
  

	//输出当前的节点的词的数目
	public HashSet<String> getTopSeveralWords() {
		//单纯的比较器
		HashSet<String> res = new HashSet<String>();
		IDSorter[] sortedTypes = new IDSorter[Global.vocalSize];    
		//落在当前的节点的词的数目
		for (int type=0; type < Global.vocalSize; type++) {
			sortedTypes[type] = new IDSorter(type, typeCounts[type]);
		}
		Arrays.sort(sortedTypes); 
		Alphabet alphabet = Global.instances.getDataAlphabet();
		for (int i=0; i< Global.outputWordNum; i++) {
			//获得在VSM模型中，ID和词的映射关系，根据ID所得到的的词
			if(typeCounts[sortedTypes[i].getID()] > 0 ){
				res.add((String) alphabet.lookupObject(sortedTypes[i].getID()));
		    }
		    else{
			   break;
		    } 
	   }
		return res;
	}
}
