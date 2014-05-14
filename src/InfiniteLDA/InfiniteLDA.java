package InfiniteLDA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.knowceans.util.ArrayUtils;
import org.knowceans.util.DirichletEstimation;
import org.knowceans.util.IndexQuickSort;
import org.knowceans.util.Samplers;
import org.knowceans.util.Vectors;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class InfiniteLDA {
 private InstanceList instances;
	 
	 private int currentIter;
	 
	 private Random rand;
	 private double beta;
	 private double gamma;
	 private double alpha;
	 private ArrayList<Double> tau; 
	 

	 private double a_alpha;
	 private double a_beta;
	 private double a_gamma;
	 private double b_alpha;
	 private double b_beta;
	 private double b_gamma;
	 
	
	 private int numDocuments; //num of doc
	 private int numTypes; //num of vocabulary
	 private int totalWord=0;
	 private int topWordNum = 30; //display number of top word
	 

     //每个文档，每个主题下的词的数目
	 private List<Integer>[] nmk;
	 //每个主题每个维度的词的数目
	 private List<int[]> nkt;
	 //每个主题的词的数目
	 private List<Integer> nk;
	
	 //每个文档的每个token的主题
	 private int[][] z; //topic indicator
	 private double[] pp; //topic distribution
	 private final int ppStep =10;
	 
	 private double tables;
	 
	
	 private int parameterSampleNum=10;
	 
	 private int K; //initial topic number
	 //保存维度上为空的主题的号码
	 private List<Integer> kgaps;
	 //保存维度上不为空的主题的号码
	 private List<Integer> kactive;
	 

	 private boolean initialized = false;
	 //fixed K
	 private boolean fixedK = false;
	 //hyper sampling
	 private boolean fixedHyper = true;
	 private boolean showResult = true;
	 
	 public InfiniteLDA(InfiniteLDA model){
		 this(model.alpha, model.beta, model.gamma, model.K);
	 }
	 
	 public InfiniteLDA(double alpha, double beta, double gamma, int initialK){
		 this.alpha = alpha;
		 this.beta = beta;
		 this.gamma = gamma;
		 
		 this.a_alpha=1.0;
		 this.a_beta =1.0;
		 this.a_gamma=5.0;
		 this.b_alpha=1.0;
		 this.b_beta=1.0;
		 this.b_gamma=5.0;
	
		 this.K = initialK;	 
		 this.currentIter=0;
		 
		 rand = new Random();
		 
		 if(gamma== 0){
			 this.fixedK=true;
		 }
	 }
	 
	 /*set parameters*/
	 public void setAlpha(double a){
		 this.alpha=a; 
		 //once parameters changed, need to re-initialize
		 initialized=false;
	 }
	 
	 /*initialization*/
	 public void initialize(InstanceList instances){
		 
		 if (!(instances.get(0).getData() instanceof FeatureSequence)) {
				throw new IllegalArgumentException("Input must be a FeatureSequence, using the --feature-sequence option when impoting data, for example");
			}
		 
		 this.instances=instances;
		 numDocuments=instances.size();
		 numTypes=instances.getDataAlphabet().size();
		 
		 FeatureSequence fs;
		 for(int i=0;i<numDocuments ; i++){
			 fs =(FeatureSequence) instances.get(i).getData();
			 totalWord+=fs.getLength();
		 }
		 
		 /*initialize algorithm parameter*/
		 init();
		 
	 } 
	 //init parameter
	 public void init(){
		 nmk = new ArrayList[numDocuments];
		 nkt = new ArrayList<int[]>();
		 nk = new ArrayList<Integer>();
		 z = new int[numDocuments][];
		 
		 FeatureSequence fs;
		 for(int m=0; m < numDocuments; m++){
			 nmk[m] = new ArrayList<Integer>();
			 
			 for(int k=0; k<K ; k++){
				 nmk[m].add(0);
			 }
			 fs =(FeatureSequence) instances.get(m).getData();
			 z[m]= new int[fs.getLength()];
		 }
		 
		 /*topic index*/
		 kgaps = new ArrayList<Integer>();
		 kactive = new ArrayList<Integer>();
		 tau = new ArrayList<Double>();
		 
		 for(int k=0 ; k<K ; k++){
			 kactive.add(k);
			 nkt.add(new int[numTypes]);
			 nk.add(0);
			 tau.add(1.0/K);
		 }
		 
		 //add one more topic
		 tau.add(1.0/K);
		 pp = new double[(K+ppStep)];
		 
		 //initialize
		 randomAssignTopics();
		 
		 if(!fixedK){
			 updateTau();
		 }
		 
		 initialized=true;	 
	 }
	 
	 //主题随机初始化
	 private void randomAssignTopics(){ 
		 //uniform multinomial distribution for initial assignment
		 for(int kk=0; kk<K ; kk++){	 
			 //equal probability for each topic
			 pp[kk] = 1.0/K;
		 }
		 
		 for(int m=0 ; m < numDocuments ; m++){		 	 
			 FeatureSequence fs = (FeatureSequence) instances.get(m).getData();
			 int seqLen = fs.getLength();
			 int type, token, k;
			 double sum;	 
			 //对每个词采样
			 for(token=0 ; token < seqLen ; token++){		 
				 type = fs.getIndexAtPosition(token);	 
				 int u = rand.nextInt(K);
				 //assign topics
				 k = kactive.get(u);
				 z[m][token]=k;
				 //add z back
				 nmk[m].set(k, nmk[m].get(k)+1);
				 nkt.get(k)[type]++;
				 nk.set(k, nk.get(k)+1);
			 }
		 }
		 //remove empty topic if topic number are not fixed
		 if(!fixedK){
			 for(int k=0 ; k<nk.size(); k++){
				 if(nk.get(k)==0){
					 kactive.remove((Integer)k);
					 kgaps.add(k);
					 assert(Vectors.sum(nkt.get(k))==0);
					 K--;
					 updateTau();
				 }
			 }
		 }
	 }
	 
	 //tau是一个K+1维的dir vector
	 //prune topics and update tau, the root DP mixture weights
	 private void updateTau() {
		double[] mk = new double[K+1];	
		for(int kk=0 ; kk<K ; kk++){
			int k = kactive.get(kk);
			//每个文档第k个主题的词的数目
			for(int m=0 ; m < numDocuments ; m++){
				if(nmk[m].get(k) > 1){
					//number of tables a CRP(alpha tau) produce nmk items
					mk[kk] +=Samplers.randAntoniak(alpha * tau.get(k), 
						nmk[m].get(k));
				}
				else{ //nmk[m].get(k) = 0 or 1   
					mk[kk] +=nmk[m].get(k);
				}	
			}
		}// end outter for loop
		//get number of tables
		tables = Vectors.sum(mk);
		mk[K] = gamma;
		//sample tau
		double[] tt =Samplers.randDir(mk);
		for(int kk=0 ; kk < K ; kk++){	
			int k=kactive.get(kk);
			tau.set(k, tt[kk]);
		}
		tau.set(K, tt[K]);
	}
	 
	 /*estimate*/
	 public void estimate(int iterations){
		 estimate(iterations, showResult);
	 }
	 
	 private void estimate(int iterations, boolean printResult){		 
		 for(int iter=0; iter < iterations ; iter++){	 
			 if(!initialized){
				 throw new IllegalStateException("Initialize HDP first!");
			 }
			 for(int i=0 ; i < numDocuments ; i++){		 
				 updateDocs(i);
			 } 
			 if(!fixedK){
				 updateTau();
			 }
			 //超参数升级？
			 if(iter > 10 && !fixedHyper){
				 updateHyper();
			 }	 
			 //print current status
			 if( iter !=0 && (iter % 50) == 0){ 
				 if(!fixedK){
					 System.out.println("Iteration=" + iter +"\tTopic=" + K );
				 }
				 else{
					 System.out.print(iter + " ");
				 }
				 if(!fixedHyper){
					 printParameter();
				 }
			 }
		 }//end iter
		 //accumulate iterations
		 currentIter+=iterations;
		 if(printResult){
			 //print a summary of estimation
			 System.out.println();
			 printParameter();
			 System.out.println();
			 printTopWord(topWordNum);
		}	 
	 }
	 
	 //对当前的文档采样
     private void updateDocs(int m) {	 
		 FeatureSequence fs = (FeatureSequence) instances.get(m).getData();
		 int seqLen = fs.getLength();
		 int type, token;
		 double sum;
		 for(token=0 ; token < seqLen ; token++){	
			 //维度
			 type = fs.getIndexAtPosition(token);
			 int k, kold = -1;		 
			 if(initialized){
				 //get old topic
				 k=z[m][token];
				 //decrement
				 //回收
				 nmk[m].set(k, nmk[m].get(k)-1);
				 nkt.get(k)[type]--;
				 nk.set(k, nk.get(k)-1);
				 kold=k;
			 }
			 
			 sum=0.0;
			 for(int kk=0; kk<K ; kk++){
				 k=kactive.get(kk);
				 //gibbs sampling for z
				 pp[kk] = (nmk[m].get(k) + alpha*tau.get(k))*
						 (nkt.get(k)[type]+beta) / (nk.get(k)+ numTypes*beta);
				 
				 sum+=pp[kk];
			 }
			 //最后一维的情况
			 if(!fixedK){
				 pp[K] = alpha * tau.get(K) / numTypes;
				 sum+=pp[K];
			 }
			 //主题数目为K的情况，对K+1维度的MC进行采样
			 //sampling
			 double u = rand.nextDouble();
			 u *= sum;
			 sum=0.0;
			 int kk;
			 
			 for(kk=0 ; kk<=K ; kk++){
				 sum+=pp[kk];
				 if(u <= sum) break;
			 }
			 
			 //check kk is old or new topic
			 if(kk < K){ //in old topic		  
				 k = kactive.get(kk);
				 z[m][token]=k;
				 //add z back
				 nmk[m].set(k, nmk[m].get(k)+1);
				 nkt.get(k)[type]++;
				 nk.set(k, nk.get(k)+1);
			 }
			 else{  //add new topic	 
	    		 assert(!fixedK);
				 z[m][token]=addTopic(m,type);
				 updateTau();
				 //System.out.println("add K="+K);
			 }		 
			 //disable empty topic
			 if(initialized && !fixedK && nk.get(kold)==0){
				 kactive.remove((Integer)kold);
				 kgaps.add(kold);
				 assert(Vectors.sum(nkt.get(kold))==0 && 
						 nk.get(kold)==0 && nmk[m].get(kold)==0);
				 K--;
				 //System.out.println("remove K="+K);			 
				 updateTau();
			 }
		 }
	 }

	/*auto update hyper parameter need refine*/
	private void updateHyper(){
		for(int r=0 ; r<parameterSampleNum ; r++){
			
			//check paper!
			//gamma: root level
			double eta = Samplers.randBeta(gamma+1 , tables);
			double bloge =b_gamma - Math.log(eta);
			double pie = (1.0 / (tables * bloge/ (gamma+K-1)));
			int u = Samplers.randBernoulli(pie);
			gamma = Samplers.randGamma(a_gamma+K-1+u, 1.0/bloge);
			
			//alpha: document level
			double qs = 0.0;
			double qw = 0.0;
			
			FeatureSequence fs;
			int seqLen;
			for(int m=0 ; m< numDocuments ; m++){
				fs = (FeatureSequence) instances.get(m).getData();
				seqLen = fs.getLength();
				qs += Samplers.randBernoulli(seqLen/(seqLen+alpha));
				qw += Math.log( Samplers.randBeta(alpha+1, seqLen));
			}
			alpha = Samplers.randGamma(a_alpha+ tables -qs, 1.0/(b_alpha-qw));
		}	
		//estimate beta
		//convert nk & akt to array
		int[] ak = (int[]) ArrayUtils.asPrimitiveArray(nk);
		int[][] akt =new int[K][numTypes];
		for(int k=0; k<K; k++){
			akt[k] = nkt.get(k);
		}
		beta = DirichletEstimation.estimateAlphaMap(akt, ak, beta, a_beta, b_beta);
		
	 }	 
	
	//第m个文档，维度为type的词
	private int addTopic(int m, int type) {
		int k;
		if(kgaps.size()>0){ //reuse gaps
			k=kgaps.remove(kgaps.size()-1);
			kactive.add(k);
			nmk[m].set(k,1);
			nkt.get(k)[type]=1;
			nk.set(k,1);
		}
		else{
			k=K;		
			for(int i=0 ; i< numDocuments ; i++){
				nmk[i].add(0);
			}	
			kactive.add(K);
			nmk[m].set(K,1);
			nkt.add(new int[numTypes]);
			nk.add(1);
			tau.add(0.0);
		}
		//add topic number 
		K++;
		if(pp.length <=K){
			pp = new double[K + ppStep];
		}
		return k;
	}
	
	 /*print*/
	 public void printTopWord(int numWords){
		 
		//sort topic from largest to smallest 
		trimTopics();
		
		int wordCount=0;
		
		for(int k=0 ; k<nk.size(); k++){
				
			if(nk.get(k)!=0){
				
				int count=nk.get(k);
				
				//check word count
				wordCount+=count;
				
				IDSorter[] sortedTypes = new IDSorter[numTypes];
				//sort word in topic k
				for (int type=0; type < numTypes; type++) {
					sortedTypes[type] = new IDSorter(type, nkt.get(k)[type]);
				}
				
				Arrays.sort(sortedTypes);
			
				Alphabet alphabet = instances.getDataAlphabet();
				StringBuffer out = new StringBuffer();
				out.append("topic"+k + ": ");
				out.append("word:"+ count + ", ");
//				if(k< kactive.size()){
//					out.append("matched topic "+kactive.get(k) + ", ");
//				}
				double prop = (double)count/totalWord;
				out.append(String.format("prop:%2.4f, ", prop));
			
				for (int i=0; i<numWords; i++) {
					if(nkt.get(k)[sortedTypes[i].getID()]!=0){
						out.append(alphabet.lookupObject(sortedTypes[i].getID()) + ":" + nkt.get(k)[sortedTypes[i].getID()] + " ");
					}
				}
				System.out.println(out);
			}
			else{
				if(k < kactive.size() )
					System.out.println("Topic"+k+": matched topic " + kactive.get(k));
				else
					System.out.println("Topic"+k+": empty");
			}
		}
		System.out.println("Total Word count: "+ wordCount );
	 }
	 
 public void printParameter(){
		 
		 String out = String.format("Summary: Docs=%d, topics=%d, totalWords=%d, alpha=%2.3f, beta=%2.3f, gamma=%2.3f",numDocuments, K,totalWord, alpha, beta, gamma);
		 System.out.println(out);
	 
	 }
	 
	 private void trimTopics(){
		 
		 //System.out.println("start trim");
		 
		 int[] new_nk = IndexQuickSort.sort(nk);
		 IndexQuickSort.reverse(new_nk);
		 
		 //remove empty topic
		 IndexQuickSort.reorder(nk, new_nk);
		 IndexQuickSort.reorder(nkt, new_nk);
		 
		 for(int i=0; i < kgaps.size(); i++){
			 nk.remove(nk.size()-1 );
			 nkt.remove(nkt.size()-1 );
		 }
		 
		 for(int m=0 ; m < numDocuments; m++){
			 IndexQuickSort.reorder(nmk[m], new_nk);
			 //remove gaps
			 for(int i=0 ; i <kgaps.size() ; i++){
				 nmk[m].remove(nmk[m].size()-1);
			 }
		 }
		 //clean kgaps
		 kgaps.clear();
		 int[] k2knew = IndexQuickSort.inverse(new_nk);
		 
		 //rewrite topic
		 for(int i=0; i<K; i++){
			 kactive.set(i, k2knew[kactive.get(i)]);
		 }
		 
		 for(int m=0; m<numDocuments ; m++){
			 FeatureSequence fs = (FeatureSequence) instances.get(m).getData();
			 
			 for(int n=0 ; n<fs.getLength() ; n++){
				 z[m][n]=k2knew[z[m][n]]; 
			 }
		 }
		 
	 }
	 
}
