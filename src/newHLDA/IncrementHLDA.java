package newHLDA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import newHLDA.item.HldaDoc;
import newHLDA.item.HldaTopic;
import newHLDA.item.HldaWord;

import org.knowceans.util.Gamma;

import util.mallet.IncrementInstance;
import cc.mallet.types.InstanceList;

public class IncrementHLDA {

  
  public IncrementInstance incrementInstances;

  public HldaTopic rootNode;
  public ArrayList<HldaDoc> documents;
  int numDocuments;  //文档的数目
  
  public ArrayList<HldaDoc> incrementDocuments;
  int numIncrementDocuments;
  
  public ArrayList<ArrayList<HldaDoc>> incrementDocuemtsList;
  public ArrayList<Integer>  numIncrementDocumentsList;
  
  public double maxScore;
  
  public IncrementHLDA(){
  	maxScore = 0;
  	this.rootNode = null;
  }
  
  public IncrementHLDA(List<String> segSensList, int iterNum){
	  this.incrementInstances = new IncrementInstance(segSensList);
	  initialize(this.incrementInstances.curAllInstanceList);
	  estimate(iterNum, this.documents);
  }
  
  public IncrementHLDA(File file, int iterNum){
	  this.incrementInstances = new IncrementInstance(file);
	  initialize(this.incrementInstances.curAllInstanceList);
	  estimate(iterNum, this.documents);
  }
  
  public void addingData(InstanceList incrementInstances){
	 this.incrementInstances.addInstanceList(incrementInstances);
	 this.incrementInstances.currentInstanceListIncrement();
  }
  
  public void addingData(List<String> incrementSegSensList){
	   this.incrementInstances.addInstanceList(incrementSegSensList);
  }
  
  public void incrementEstimate(int iterNum){
	  initializeIncrement(this.incrementInstances.curCandidateInstanceList);
	  estimate(iterNum, this.incrementDocuments);
  }
 
  //初始化
  private void initialize(InstanceList instances){
  	 Global.instances = instances;
  	 //初始化文档
  	 documents = new ArrayList<HldaDoc>();
  	 incrementDocuemtsList =new ArrayList<ArrayList<HldaDoc>>();
  	 numIncrementDocumentsList = new ArrayList<Integer>();
  	 for(int i = 0 ; i < instances.size(); i++){
		documents.add(new HldaDoc(instances.get(i)));
	 }
  	 numDocuments = instances.size();
  	 incrementDocuemtsList.add(documents);
   	 numIncrementDocumentsList.add(numDocuments);
  	 //初始化其他参数
  	 Global.vocalSize = instances.getDataAlphabet().size();
  	 if(!Global.singlePathInit){
  		 InitPath(this.documents);
  	 }
  	 else{
  		 InitSinglePath();
  	 }
  	 InitWordTopics(this.documents); 	 
  }
  
  //初始化增量的集合
  private void initializeIncrement(InstanceList incremtnInstances){
  	this.numIncrementDocuments = incremtnInstances.size();
  	this.incrementDocuments = new ArrayList<HldaDoc>();
  	for(int i = 0 ; i < incremtnInstances.size(); i++){
  		incrementDocuments.add(new HldaDoc(incremtnInstances.get(i)));
	}
  	incrementDocuemtsList.add(this.incrementDocuments);
   	numIncrementDocumentsList.add(this.numIncrementDocuments);
  	//修改增量的一些部分
  	if(this.incrementInstances.curAllInstanceList.getDataAlphabet().size()!=Global.vocalSize){
  		System.out.println("revise the vocalSize!");
  		System.out.println("old size:  " + Global.vocalSize);
  		Global.vocalSize = this.incrementInstances.curAllInstanceList.getDataAlphabet().size();
  		Global.instances = this.incrementInstances.curAllInstanceList;
  		System.out.println("new size:  " + Global.vocalSize);
  		//修改每一个Hlda
  		reviseNode(rootNode);
//  		System.out.println(rootNode.dimensions);
  		
  		for(int i = 0 ; i < this.incrementDocuemtsList.size();i++){
  			for(int j = 0 ; j < this.incrementDocuemtsList.get(i).size(); j++){
  				this.incrementDocuemtsList.get(i).get(j).dimensionsExtend(Global.vocalSize);
  			}
  		}	
//  		System.out.println(this.incrementDocuemtsList.get(0).get(0).dimensions);
  	}
  	
  	//初始化
  	InitPath(this.incrementDocuments);
	InitWordTopics(this.incrementDocuments);
  	
	this.incrementInstances.candidateInstanceListClear();
  }
  
  public void reviseNode(HldaTopic node){
	  node.dimensionsExtend(Global.vocalSize);
	  for(int i = 0 ; i < node.children.size(); i++){
		  reviseNode(node.children.get(i));
	  }
  }
  
  public void dropPreviousDocument(){
	 System.out.println("Unassigned a part of Paths");
	 for(int i = 0 ; i < this.incrementDocuemtsList.get(0).size(); i++){
		 this.incrementDocuemtsList.get(0).get(i).UnassignPath();
	 }
	 this.incrementDocuemtsList.remove(0);
	 this.incrementInstances.currentInstancesRevome();
	 this.incrementInstances.currentInstanceListRevome();
  }
  
    
  //按NCRP过程进行初始化
  public void InitPath(ArrayList<HldaDoc> documents){
  	if(rootNode == null){
  		rootNode = new HldaTopic(Global.vocalSize);
  		Global.rootNode = this.rootNode;
  	}	
		for(int i = 0; i < documents.size(); i++){
			 HldaTopic[] path = new HldaTopic[Global.numLevels];
			 path[0] = rootNode;
			 rootNode.customers++;
			//随机选择一条路径
			 for (int level = 1; level < Global.numLevels; level++) {
				 path[level] = path[level-1].select(documents.get(i));
				 path[level].customers++;
			 }
			 documents.get(i).path = path;
		 }
		System.out.println("Init tree size: " + rootNode.CountTree());
  }
  
  //初始化单一的路径
  public void InitSinglePath(){
		rootNode = new HldaTopic(Global.vocalSize);
		rootNode.getNewLeaf();
	    for(int i = 0; i < numDocuments; i++){
			 HldaTopic[] path = new HldaTopic[Global.numLevels];
			 path[0] = rootNode;
			 rootNode.customers++;
			//只选择当前的路径
			 for (int level = 1; level < Global.numLevels; level++) {
				 path[level] = path[level-1].selectExisting();
				 path[level].customers++;
			 }
			 documents.get(i).path = path;
		}
		System.out.println("Init tree size: " + rootNode.CountTree());
  }
  
  
  public void print(HldaTopic node){
		  for(int i = 0 ; i <= node.level; i++){
			  System.out.print(" ");
		  }
		  System.out.print(node.totalTokens + "|" + node.customers + "  nodeID: " + node.nodeID + "  ");
		  System.out.println(node.getTopWords());
		  for(int i = 0 ; i < node.children.size(); i++){
			  print(node.children.get(i));
		  }
	 }
  
  public void printToFile(HldaTopic node, String fileName){
		 File des = new File(fileName);
		 try {
			PrintWriter pw = new PrintWriter(des);
			printToFile(node, pw);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
  
  private void printToFile(HldaTopic node, PrintWriter pw){
  	if(Global.numLevels == node.level + 1 && node.getTopWords().split(" ").length < Global.outputWordNum/2){		
  	}
  	else{
  	for(int i = 0 ; i <= node.level; i++){
 			 pw.print(" ");
 		  }
 		  pw.print(node.totalTokens + "|" + node.customers + "  nodeID: " + node.nodeID + "  ");
 		  if(node.level==0){
 			pw.println(node.getTopWords());
 		  }
 		  else{
 			 pw.println(node.getTopMultiWords());	  
 		  }
 		  for(int i = 0 ; i < node.children.size(); i++){
 			  printToFile(node.children.get(i),pw);
 		}
  	}
  }
  
  //初始化每个文档的每个词
  public void InitWordTopics(ArrayList<HldaDoc> documents){
  	 for(HldaDoc doc : documents){
  		 doc.f = new int[Global.numLevels][];
  		 for(int i = 0 ; i < Global.numLevels; i++){
  			  doc.f[i] = new int[Global.vocalSize];
  			  for(int j = 0 ; j < Global.vocalSize; j++){
  				 doc.f[i][j] = 0;
  			  }
  		  }
  		  //分配每一个词
  		  for(HldaWord w : doc.words){
  			  int level = Global.random.nextInt(Global.numLevels); 			
  			  doc.f[level][w.type]++;              
                doc.levelCount[level]++;
                w.AssignTopic(doc.path[level]);
  		  }
  		  //计算stick-breaking部分
  		  double[] restlevelCountTotal = new double[Global.numLevels];
  		
  		   for(int i = 0 ; i < Global.numLevels ; i ++){
  	        	 restlevelCountTotal[i] = 0;
  	        	 for(int j = i; j < Global.numLevels ; j++){
  	        		 restlevelCountTotal[i] += doc.levelCount[j];
  	        	 }
  	        }
  		  for(int k = 0 ; k < Global.numLevels; k++){
                double tmp = (Global.mpi + doc.levelCount[k]) / (Global.pi + restlevelCountTotal[k]);          
                doc._stickLength[k] = Math.log(tmp);
                doc._stickRemaining[k] = Math.log(1 - tmp);
  		  }
  	  }
  }
  
  
  //迭代的进行估计
  private void estimate(int iterations, ArrayList<HldaDoc> documents){
//  	int iterval = 2;
  	for(int i = 0; i < iterations; i++){
		 System.out.println("iteration: " + i);     
		 for(int j=0;j<documents.size();j++){
//			 System.out.println("current doc index:" + j);
            SamplePath(documents.get(j));
            SampleLevel(documents.get(j));
            
        }
		 if( i !=0 && i % Global.displayNum == 0  ){
     	 print(rootNode);
      }
		 if( i !=0 && i % Global.writeFileNum == 0){
			 writeFile(i, false);
		 }
		 if( i !=0 && i % Global.SAMPLE_HYPER_ITER == 0){
			 if( Global.SAMPLE_ETA ){
				 updateEta();
				 System.out.println(Global.etaList);

			 } 
			 if( Global.SAMPLE_GAMMA){
				 rootNode.sampleScaling();
				 rootNode.printNodeScaling();
			 }
			 if( Global.SAMPLE_GEM){
				 updateGEM_M();
				 updateGEM_Pi();
				 Global.mpi = Global.m * Global.pi;
				 System.out.println("mean is: " + Global.m + "  scale is : " + Global.pi);
			 }
			 computeScore();
		 }
		 
	 }
	 writeFile(iterations, true);	 
}
  
  //对每一个文档采样
  //对路径回收的时候，文档的f[][]和levelCount是不变的
  public void SamplePath(HldaDoc doc){  
  	if(Global.vertifyPath){
  		doc.pathVertify();
  	}  	
  	doc.UnassignPath();
  	//清空临时变量
  	rootNode.clearPathWeight();
  	//计算
  	rootNode.CalculatePathToLeaves(doc.f);
  	rootNode.CalculatePathToInternalNodes(doc.f);
  	//计算完毕之后进行测试
  	rootNode.vertifyPathWeight();
  	
  	  //get the full list of topics
      ArrayList<HldaTopic> all = new ArrayList<HldaTopic>();
      if(Global.PATH_SAMPLING_CONSTRAINT){
//      	int pos = instanceJudge.getPos(doc.instance, NodeDict.id2topicWord);
//			ArrayList<Integer> topicSet = instanceJudge.getInstanceResults(doc.instance, NodeDict.id2topicWord).topicSet;
      	ArrayList<Integer> topicSet = doc.insRes.topicSet;
//      	System.out.println(doc.insRes.topicSet);
      	//约束的节点
			if(topicSet.size() != 0){
//      		rootNode.children.get(pos).GetAllTopics(all);
				if(topicSet.size()==1){
					rootNode.children.get(topicSet.get(0)).GetAllTopics(all);
				}
				else{
					if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_CONSTRAINT){
						for(Integer i: topicSet){
							if( i < rootNode.children.size()){
								rootNode.children.get(i).GetAllTopics(all);
							}
							else{
								System.err.println("nodeDict error!");
							}
						}
					}
					else if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_NO_CONSTRAINT){
						all.add(rootNode);
	            		for(int i = NodeDict.realTopicNum; i < rootNode.children.size(); i++){
	            			rootNode.children.get(i).GetAllTopics(all);
	            		}
					}
					else if(Global.MULTI_NODE_METHOD == Global.MULTI_NODE_ALL){
						rootNode.GetAllTopics(all);
					}
					else{
						rootNode.GetAllTopics(all);
					}
					
				}
      	}
			//找不到约束位置的节点
      	else{
      		//对于找不到约束位置的节点，尝试使用两种约束方式进行尝试
      		//在全部结点进行搜索
      		if(Global.NOKEY_DOCUMENT_PATH_SAMPLING_CONSTRAINT){
      			rootNode.GetAllTopics(all);	
      		}
      		//在非约束的节点范围内搜索
      		else{
      			all.add(rootNode);
          		for(int i = NodeDict.realTopicNum; i < rootNode.children.size(); i++){
          			rootNode.children.get(i).GetAllTopics(all);
          		}
      		}	
      	}
      }
      else{
          rootNode.GetAllTopics(all);	
      }     
      double[] nodeWeights = new double[all.size()];
      for (int i = 0; i < nodeWeights.length; i++){
    	  nodeWeights[i] = all.get(i).ncrp + all.get(i).weights;
      }
      HldaTopic path = all.get(HLDAUtil.sampleLog(nodeWeights));
      doc.AssignPath(path);
  }
  
  //对文档的每个单词采样
  public void SampleLevel(HldaDoc doc){
	 for (HldaWord w : doc.words){
		if(Global.usingGEM){
	        w.SampleLevelByGEM(doc);
	  	}
	  	else{
	  		w.SampleLevel(doc);
	  	}; 
      }
  }
  
  public void writeFile(int iterations, boolean finalFlag){
  	try{
  		if(finalFlag){
  			File src = new File("HLDAModelDir/assign_final"  + ".txt");
          	PrintWriter pw = new PrintWriter(src);
          	rootNode.writeToFile(pw);
          	pw.close();
  		}
  		else{
  			File src = new File("HLDAModelDir/assign_" + iterations + ".txt");
          	PrintWriter pw = new PrintWriter(src);
          	rootNode.writeToFile(pw);
          	pw.close();
  		}
  	}catch(Exception e){
  		e.printStackTrace();
  	}
  }
  
  public void updateEta(){
//  	ArrayList<Double> etaVec = Global.etaList;
  	int depth = Global.numLevels;
  	int[] accept = new int[depth];
  	for(int i = 0 ; i < depth; i++){
  		accept[i] = 0;
  	}
  	for(int i = 0; i < Global.updateNum; i++){
  		double currentScore = rootNode.etaScore();
  		for(int j = 0 ; j < depth; j++){
  			double old = Global.etaList.get(j);
  			double newValue = Global.random.nextGaussian(old, Global.ETASTDEV);
  			if(newValue>0){
  				Global.etaList.set(j, newValue);
  				double newScore = rootNode.etaScore();
  				double r = Global.random.nextFloat();
  				if( r > Math.exp(newScore-currentScore)){
  					Global.etaList.set(j, old);
  				}
  				else{
  					currentScore = newScore;
  					accept[j]++;
  				}
  			}
  		}
  	}
  }
  
  //计算GEMSCORE
  public double gemScore(){
  	double score = 0;
  	int depth = Global.numLevels;
  	double prior_a = (1 - Global.m) * Global.pi;
      double prior_b = Global.m * Global.pi;
      //遍历每一个文档
      for(int i = 0 ; i < documents.size(); i++){
      	documents.get(i).score = 0;
      	double[] restlevelCountTotal = new double[depth];
      	for(int l = 0 ; l < depth; l++){
      		restlevelCountTotal[l] = 0;
      		double count = documents.get(i).levelCount[l];
      		//restlevelCountTotal[k]不包括当前的k层的levelCount[k]
      		for (int k = 0; k < l; k++){
      			restlevelCountTotal[k] += count;
      		}	
      	}
      	double levelSum = documents.get(i).words.size();
      	double sumLogProb = 0;
      	 double lastLogProb = 0;
      	for (int l = 0; l < depth-1; l++){
      		// a 就是 V_{l} , b是（1-V_{l}） 
      		 double a = documents.get(i).levelCount[l] + prior_a;
      		 double b = restlevelCountTotal[l] + prior_b;
      		 documents.get(i).score += ( Gamma.lgamma(a) + Gamma.lgamma(b) -Gamma.lgamma(a+b) )
      				 - ( Gamma.lgamma(prior_a) + Gamma.lgamma(prior_b) -Gamma.lgamma(prior_a+prior_b));
      		 
      		 levelSum -=documents.get(i).levelCount[l];
      		 double expectedStickLength = (prior_a + documents.get(i).levelCount[l])
      				 /(Global.pi + documents.get(i).levelCount[l] + levelSum);
      		 //每一层期望的棍子长度
      		 double logP = Math.log(expectedStickLength) + sumLogProb;	 
      		 if (l==0){
      			 lastLogProb = logP;
      		 }
               else{
              	 lastLogProb += HLDAUtil.logSum(logP, lastLogProb);
               }
      		 sumLogProb += Math.log(1 - expectedStickLength);            	                
      	}
      	lastLogProb = Math.log(1 - Math.exp(lastLogProb));
      	//计算最后一层的
      	documents.get(i).score += documents.get(i).levelCount[depth-1] * lastLogProb;
      	score += documents.get(i).score;
      }
      score += -Global.pi;
      return score;
  }
  
  
  //采样超参数M
  public void updateGEM_M(){
      double currentScore = gemScore();
      int accept = 0;
      for(int i = 0 ; i < Global.updateNum; i++){
      	double oldMean = Global.m;
      	double newMean = Global.random.nextGaussian(oldMean, Global.GEM_MEAN_STDEV);
      	if(newMean>0 && newMean<1){
      		Global.m = newMean;
      		double newScore = gemScore();
      		double r = Global.random.nextFloat();
      		if (r > Math.exp(newScore - currentScore)){
      			Global.m = oldMean;
              }
              else{
              	currentScore = newScore;
                  accept++;
              }
      	}
      }
  }
  
//采样超参数Pi
  public void updateGEM_Pi(){
  	 double currentScore = gemScore();
  	 int accept = 0;
       for(int i = 0 ; i < Global.updateNum; i++){
      	 double oldScale = Global.pi;
       	 double newScale = Global.random.nextGaussian(oldScale, Global.GEM_STDEV);
       	 if(newScale>0){
       		Global.pi = newScale;
       		double newScore = gemScore();
      		double r = Global.random.nextFloat();
      		if (r > Math.exp(newScore - currentScore)){
      			Global.pi = oldScale;
              }
              else{
              	currentScore = newScore;
                  accept++;
              }
       	 }
       }
  }
  
  //统计当前的每一个词的情况
  public void countWordSituation(){
  	int totalWord = 0;
  	int newLevelWord = 0;
  	for(HldaDoc doc: documents){
  		for(HldaWord w: doc.words){
  			totalWord++;
  			if(w.newLevel){
  				newLevelWord++;
  			}
  		}
  	}
  	System.out.println(String.format("word situation: %s / %s = %s ", newLevelWord, totalWord, (double)newLevelWord/totalWord));
  }
  
  //计算当前的score
  public double computeScore(){
  	double score = this.gemScore() + rootNode.gammaScore() + rootNode.etaScore();
  	if(score>this.maxScore){
  		maxScore = score;
  	}
  	return score;
  }

}
