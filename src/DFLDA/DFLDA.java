/* Copyright (C) 2005 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.	For further
   information, see the file `LICENSE' included with this distribution. */

package DFLDA;

import java.util.*;
import java.util.logging.*;
import java.util.zip.*;

import java.io.*;
import java.text.NumberFormat;

import util.File_util;
import util.mallet.MalletInput;

import TM.Config.DFLDAConfig;
import cc.mallet.topics.*;
import cc.mallet.types.*;
import cc.mallet.util.*;

import DirichletForest.*;
import java.util.Vector;
import util.*;
/**
 * A simple implementation of Latent Dirichlet Allocation using Gibbs sampling.
 * This code is slower than the regular Mallet LDA implementation, but provides a 
 *  better starting place for understanding how sampling works and for 
 *  building new topic models.
 * 
 * @author David Mimno, Andrew McCallum
 */

public class DFLDA implements Serializable {

	private static Logger logger = MalletLogger.getLogger(DFLDA.class.getName());
	public File_util filewrite;
	public String desPath = "TreeStructure";
	//public String desPath1 = "wyf.txt";
	public int word = 308;

	// the training instances and their topic assignments
	protected ArrayList<TopicAssignment> data;  

	// the alphabet for the input data
	protected Alphabet alphabet; 

	// the alphabet for the topics
	protected LabelAlphabet topicAlphabet; 
	
	// The number of topics requested
	protected int numTopics;
    protected int numDocument;
	// The size of the vocabulary
	protected int numTypes;

	private Counts c = new Counts();//记录每棵树的信息
	public Vector<Integer> leafmap= new Vector<Integer>();
	// Prior parameters
	protected double alpha;	 // Dirichlet(alpha,alpha,...) is the distribution over topics
	protected double _alpha;
	protected double alphaSum;
	protected double beta;   // Prior on per-topic multinomial distribution over words
	protected double betaSum;
	public static double sigma = DFLDAConfig.soft_eta;
	public static double _beta = 0.1;
	public static double eta = 100;
	public static final double DEFAULT_BETA = 0.01;
	public static final double DEFAULT_ALPHA = 3;
	
	// An array to put the topic counts for the current document. 
	// Initialized locally below.  Defined here to avoid
	// garbage collection overhead.
	protected int[] oneDocTopicCounts; // indexed by <document index, topic index>
	// Statistics needed for sampling.
	protected int[][] typeTopicCounts; // indexed by <feature index, topic index>
	protected int[] tokensPerTopic; // indexed by <topic index>

	public int showTopicsInterval = 50;
	public int wordsPerTopic = 10;
	public int [] setopic;
	public int wordcount = 0;
	protected Randoms random;
	protected NumberFormat formatter;
	protected boolean printLogLikelihood = false;
	
	public DFLDA (int numberOfTopics) {
		this (numberOfTopics, numberOfTopics, DEFAULT_BETA, DEFAULT_BETA, 0.1 ,100, 0.8);
	}
	
	public DFLDA (int numberOfTopics, double alphaSum, double beta, double _alpha, double _beta, double eta, double sigma) {
		this (numberOfTopics, alphaSum, beta, new Randoms(), _alpha, _beta, eta, sigma);
	}
	
	private static LabelAlphabet newLabelAlphabet (int numTopics) {
		LabelAlphabet ret = new LabelAlphabet();
		for (int i = 0; i < numTopics; i++)
			ret.lookupIndex("topic"+i);
		return ret;
	}
	
	public DFLDA (int numberOfTopics, double alphaSum, double beta, Randoms random, double _alpha, double _beta, double eta, double sigma) {
		this (newLabelAlphabet (numberOfTopics), alphaSum, beta, random, _alpha, _beta, eta, sigma);
	}
	
	public DFLDA (LabelAlphabet topicAlphabet, double alphaSum, double beta, Randoms random, double _alpha, double _beta, double eta, double sigma)
	{
		this.data = new ArrayList<TopicAssignment>();
		this.topicAlphabet = topicAlphabet;
		this.numTopics = topicAlphabet.size();

		this.alphaSum = alphaSum;
		this.alpha = alphaSum / numTopics;
		this.beta = beta;
		this.random = random;
        this._alpha = _alpha;
        this._beta = _beta;
        this.eta = eta;
        this.sigma = sigma;
		System.out.println(alpha);
		oneDocTopicCounts = new int[numTopics];
		tokensPerTopic = new int[numTopics];
		
		formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(5);

		logger.info("Simple LDA: " + numTopics + " topics");
	}
	
	public Alphabet getAlphabet() { return alphabet; }
	public LabelAlphabet getTopicAlphabet() { return topicAlphabet; }
	public int getNumTopics() { return numTopics; }
	public ArrayList<TopicAssignment> getData() { return data; }
	
	public void setTopicDisplay(int interval, int n) {
		this.showTopicsInterval = interval;
		this.wordsPerTopic = n;
	}

	public void setRandomSeed(int seed) {
		random = new Randoms(seed);
	}
	
	public int[][] getTypeTopicCounts() { return typeTopicCounts; }
	public int[] getTopicTotals() { return tokensPerTopic; }

	public void addInstances (InstanceList training,int burnin) {	
		alphabet = training.getDataAlphabet(); //VSM DATA
		numTypes = training.getDataAlphabet().size(); // vocabulary size
		numDocument = training.size();  // document number
		setopic = new int[numTypes];  // must - link
		if(DFLDAConfig.setopic){
		//初始化setopic数组, DFLDA(must-link, cannot-link)
			int topic = 1;
			for(int i = 0 ; i < SeedWords.seedWordsList.size(); i++){
//				for(String s: SeedWords.seedWordsList.get(i)){
					for(int j = 0;j<SeedWords.seedWordsList.get(i).size();j++){
						setopic[training.getDataAlphabet().lookupIndex(SeedWords.seedWordsList.get(i).get(j))] = topic;
					}
//				}
				topic++;
			}				
		}
		//System.out.println(training.getDataAlphabet().lookupIndex("健康"));
		betaSum = beta * numTypes;
		//V * K 
		typeTopicCounts = new int[numTypes][numTopics];

		int doc = 0;

		//为每一个主题建一个森林
		if(DFLDAConfig.dirichletforest){
			DirichletForest DF = new DirichletForest(numTopics,numTypes,training);
			int lm = 0;
			for(int i = 0;i<numTopics;i++){
				DTNodes dt = new DTNodes();//根节点
				if(lm ==0 ){//分两种情况的原因是用同一个leafmap容器，每次建森林的时候leafmap会增加冗余信息		
					DF.compile(dt, leafmap,i,_beta,eta);
					lm++;
				}
				else {				
					Vector<Integer> LEAFMAP = new Vector<Integer>();
					DF.compile(dt, LEAFMAP,i,_beta,eta);
				}
				dt.y_sampling();
				c.topics.add(dt);
				c.nd = new int[training.size()][numTopics];
				//System.out.println(c.topics.elementAt(i).edges.elementAt(0));	
				
				}
			}
		System.out.println("wordnum: "+numTypes);	
		//模型初始化的过程
		for (Instance instance : training) {
			FeatureSequence tokens = (FeatureSequence) instance.getData();
			LabelSequence topicSequence =
				new LabelSequence(topicAlphabet, new int[ tokens.size() ]);
			//topics size = instance.size();
			int[] topics = topicSequence.getFeatures();
			//初始化每个词的topic
			for (int position = 0; position < tokens.size(); position++) {			
				double norm_sum = 0;
				double [] num = new double[numTopics];
				int type = tokens.getIndexAtPosition(position);				
				int topic;
				if(setopic[type]==0){
				if(DFLDAConfig.dirichletforest){
					for(int j = 0;j<numTopics;j++){
						try{
							double wordterm = c.topics.elementAt(j).calc_wordterm(1, leafmap.elementAt(type));
								num[j] = wordterm * (c.nd[doc][j]+alpha);
							norm_sum += num[j];
							//System.out.print("num[j]:"+num[j]+" wordterm:"+wordterm+" alpha:"+alpha+"\n");
						}catch(Exception e){
							System.out.println("leafmap: "+leafmap.elementAt(type)+"type: "+ type);
					    }
				     }//for
					//select topic 
					topic = DTNodes.mult_sample(num, norm_sum);
					//topic = random.nextInt(numTopics);
				}
				else {
					topic = random.nextInt(numTopics);
				}	
				}
				else topic = setopic[type]-1;
				topics[position] = topic;
				tokensPerTopic[topic]++;
				
				if(DFLDAConfig.dirichletforest){
				   c.nd[doc][topic]++;
				   c.topics.elementAt(topic).modify_count(1, leafmap.elementAt(type));
				}
				typeTopicCounts[type][topic]++;
				wordcount++;
			}
			//System.out.println();
			TopicAssignment t = new TopicAssignment (instance, topicSequence);
			data.add (t);
			doc++;
		}
		
		//do burn-in
		
		for(int si = 1;si<=burnin;si++){
			long iterationStart = System.currentTimeMillis();
			// Loop over every document in the corpus
			for (doc = 0; doc < data.size(); doc++) {
				FeatureSequence tokenSequence =
					(FeatureSequence) data.get(doc).instance.getData();
				LabelSequence topicSequence =
					(LabelSequence) data.get(doc).topicSequence;

				sampleTopicsForOneDoc (tokenSequence, topicSequence,doc);
			}	
            long elapsedMillis = System.currentTimeMillis() - iterationStart;
			logger.fine("burn-in"+si + "\t" + elapsedMillis + "ms\t");

			// Occasionally print more information
			//loglikelihood是否在增大
			if(showTopicsInterval != 0 && si % showTopicsInterval == 0) {
				logger.info("< burn-in " + si + "> Log Likelihood: " + modelLogLikelihood() + "\n" +
							topWords (wordsPerTopic));
			}
		}
		

	}

	public void sample (int iterations) throws IOException {
		for (int iteration = 1; iteration <= iterations; iteration++) {
			int flag = 0;
			System.out.println("iteration: "+iteration);
			long iterationStart = System.currentTimeMillis();
			for (int doc = 0; doc < data.size(); doc++) {
				FeatureSequence tokenSequence =
					(FeatureSequence) data.get(doc).instance.getData();
				LabelSequence topicSequence =
					(LabelSequence) data.get(doc).topicSequence;
				sampleTopicsForOneDoc(tokenSequence, topicSequence,doc);	
				flag = 1;
			}
			//没迭代N次，采样一次森林
			if(DFLDAConfig.dirichletforest){
			    //y-sampling
				for(int ti = 0;ti<numTopics;ti++){
					c.topics.elementAt(ti).y_sampling();
				}
				filewrite.write("iteration:"+iteration+"\n", desPath);
				for(int ti = 0;ti<numTopics;ti++){
					filewrite.write("topic:"+ti+"\n", desPath);
					double wordterm = c.topics.elementAt(ti).calc_wordterm1(1, leafmap.elementAt(word));
					filewrite.write("wordterm:"+wordterm+"\n", desPath);
				}
			}
		
			
            long elapsedMillis = System.currentTimeMillis() - iterationStart;
			logger.fine(iteration + "\t" + elapsedMillis + "ms\t");

			if (showTopicsInterval != 0 && iteration % showTopicsInterval == 0) {
//				logger.info("<" + iteration + "> Log Likelihood: " + modelLogLikelihood() + "\n" +
//							topWords (wordsPerTopic));
				logger.info("<" + iteration + "> " +
						topWords (wordsPerTopic));
			}
			
			if(iteration == iterations){
				File_util.write(topWords(100), DFLDAConfig.finalWordDes);
			}	
			
		}
	}
	
	protected void sampleTopicsForOneDoc (FeatureSequence tokenSequence,
										  FeatureSequence topicSequence,int doc) {
		int[] oneDocTopics = topicSequence.getFeatures();

		int[] currentTypeTopicCounts;
		int type, oldTopic, newTopic;
		double topicWeightsSum;
		int docLength = tokenSequence.getLength();

		int[] localTopicCounts = new int[numTopics];

		//populate topic counts
		for (int position = 0; position < docLength; position++) {
			localTopicCounts[oneDocTopics[position]]++;
		}

		double score, sum;
		double[] topicTermScores = new double[numTopics];
		
		//	Iterate over the positions (words) in the document 
		for (int position = 0; position < docLength; position++) {
			type = tokenSequence.getIndexAtPosition(position);
			oldTopic = oneDocTopics[position];

			// Grab the relevant row from our two-dimensional array
			currentTypeTopicCounts = typeTopicCounts[type];

			//	Remove this token from all counts. 
			localTopicCounts[oldTopic]--;
			tokensPerTopic[oldTopic]--;
			assert(tokensPerTopic[oldTopic] >= 0) : "old Topic " + oldTopic + " below 0";
			currentTypeTopicCounts[oldTopic]--;
			if(DFLDAConfig.dirichletforest){
				c.nd[doc][oldTopic]--;
				c.topics.elementAt(oldTopic).modify_count(-1, leafmap.elementAt(type));
			}
			// Now calculate and add up the scores for each topic for this word
			sum = 0.0;
			if(setopic[type]==0){

			// Here's where the math happens! Note that overall performance is 
			//  dominated by what you do in this loop.
			for (int topic = 0; topic < numTopics; topic++) {
				if(DFLDAConfig.dirichletforest){
				    if(DFLDAConfig.as_lda){
					   double ratio = (tokensPerTopic[topic] + _alpha)/(wordcount - 1 + _alpha*numTopics);
				       double as_prior = ratio * alpha*numTopics;
					   double wordterm = c.topics.elementAt(topic).calc_wordterm(1, leafmap.elementAt(type));
					   score = wordterm * (localTopicCounts[topic]+as_prior);
					}
					else {
						double wordterm = c.topics.elementAt(topic).calc_wordterm(1, leafmap.elementAt(type));
						score = wordterm * (localTopicCounts[topic]+alpha);
						
					}
				}
				else{
					if(DFLDAConfig.as_lda){
						double ratio = (tokensPerTopic[topic] + _alpha)/(wordcount - 1 + _alpha*numTopics);
					    double as_prior = ratio * alpha*numTopics;
						
						score =
							(as_prior + localTopicCounts[topic]) *
							((beta + currentTypeTopicCounts[topic]) /
							 (betaSum + tokensPerTopic[topic]));
					}
					else{
					score =
						(alpha + localTopicCounts[topic]) *
						((beta + currentTypeTopicCounts[topic]) /
						 (betaSum + tokensPerTopic[topic]));
				    }
				}
				sum += score;
				topicTermScores[topic] = score;
			}
			// Choose a random point between 0 and the sum of all topic scores
			// Figure out which topic contains that point
			newTopic = DTNodes.mult_sample(topicTermScores, sum);			
		}
		else {


			// Here's where the math happens! Note that overall performance is 
			//  dominated by what you do in this loop.
			for (int topic = 0; topic < numTopics; topic++) {
				if(DFLDAConfig.dirichletforest){
				    if(DFLDAConfig.as_lda){
					   double ratio = (tokensPerTopic[topic] + _alpha)/(wordcount - 1 + _alpha*numTopics);
				       double as_prior = ratio * alpha*numTopics;
					   double wordterm = c.topics.elementAt(topic).calc_wordterm(1, leafmap.elementAt(type));
					   score = wordterm * (localTopicCounts[topic]+as_prior) ;
					}
					else {
						double wordterm = c.topics.elementAt(topic).calc_wordterm(1, leafmap.elementAt(type));
						score = wordterm * (localTopicCounts[topic]+alpha);
						
					}
				}
				else{
					if(DFLDAConfig.as_lda){
						double ratio = (tokensPerTopic[topic] + _alpha)/(wordcount - 1 + _alpha*numTopics);
					    double as_prior = ratio * alpha*numTopics;
						
						score =
							(as_prior + localTopicCounts[topic]) *
							((beta + currentTypeTopicCounts[topic]) /
							 (betaSum + tokensPerTopic[topic]));
					}
					else{
					score =
						(alpha + localTopicCounts[topic]) *
						((beta + currentTypeTopicCounts[topic]) /
						 (betaSum + tokensPerTopic[topic]));
				    }
				}
				score *= ((setopic[type]-1)==topic? 1:(1-sigma));
				sum += score;
				topicTermScores[topic] = score;
			}
			// Choose a random point between 0 and the sum of all topic scores
			// Figure out which topic contains that point
			newTopic = DTNodes.mult_sample(topicTermScores, sum);			
//			newTopic = setopic[type]-1;
		}
		// Make sure we actually sampled a topic
		if (newTopic == -1) {
			throw new IllegalStateException ("SimpleLDA: New topic not sampled.");
		}

		// Put that new topic into the counts
		//System.out.println("\n"+newTopic);
		oneDocTopics[position] = newTopic;
		localTopicCounts[newTopic]++;
		tokensPerTopic[newTopic]++;
		currentTypeTopicCounts[newTopic]++;
		if(DFLDAConfig.dirichletforest){
			c.nd[doc][newTopic]++;
			c.topics.elementAt(newTopic).modify_count(1, leafmap.elementAt(type));
		}	
	}
  }
	
	public double modelLogLikelihood() {
		double logLikelihood = 0.0;
		int nonZeroTopics;

		// The likelihood of the model is a combination of a 
		// Dirichlet-multinomial for the words in each topic
		// and a Dirichlet-multinomial for the topics in each
		// document.

		// The likelihood function of a dirichlet multinomial is
		//	 Gamma( sum_i alpha_i )	 prod_i Gamma( alpha_i + N_i )
		//	prod_i Gamma( alpha_i )	  Gamma( sum_i (alpha_i + N_i) )

		// So the log likelihood is 
		//	logGamma ( sum_i alpha_i ) - logGamma ( sum_i (alpha_i + N_i) ) + 
		//	 sum_i [ logGamma( alpha_i + N_i) - logGamma( alpha_i ) ]

		// Do the documents first

		int[] topicCounts = new int[numTopics];
		double[] topicLogGammas = new double[numTopics];
		int[] docTopics;

		for (int topic=0; topic < numTopics; topic++) {
			topicLogGammas[ topic ] = Dirichlet.logGamma( alpha );
		}
	
		for (int doc=0; doc < data.size(); doc++) {
			LabelSequence topicSequence = (LabelSequence) data.get(doc).topicSequence;

			docTopics = topicSequence.getFeatures();

			for (int token=0; token < docTopics.length; token++) {
				topicCounts[ docTopics[token] ]++;
			}

			for (int topic=0; topic < numTopics; topic++) {
				if (topicCounts[topic] > 0) {
					logLikelihood += (Dirichlet.logGamma(alpha + topicCounts[topic]) -
									  topicLogGammas[ topic ]);
				}
			}

			// subtract the (count + parameter) sum term
			logLikelihood -= Dirichlet.logGamma(alphaSum + docTopics.length);

			Arrays.fill(topicCounts, 0);
		}
	
		// add the parameter sum term
		logLikelihood += data.size() * Dirichlet.logGamma(alphaSum);

		// And the topics

		// Count the number of type-topic pairs
		int nonZeroTypeTopics = 0;

		for (int type=0; type < numTypes; type++) {
			// reuse this array as a pointer

			topicCounts = typeTopicCounts[type];

			for (int topic = 0; topic < numTopics; topic++) {
				if (topicCounts[topic] == 0) { continue; }
				
				nonZeroTypeTopics++;
				logLikelihood += Dirichlet.logGamma(beta + topicCounts[topic]);

				if (Double.isNaN(logLikelihood)) {
					System.out.println(topicCounts[topic]);
					System.exit(1);
				}
			}
		}
	
		for (int topic=0; topic < numTopics; topic++) {
			logLikelihood -= 
				Dirichlet.logGamma( (beta * numTopics) +
											tokensPerTopic[ topic ] );
			if (Double.isNaN(logLikelihood)) {
				System.out.println("after topic " + topic + " " + tokensPerTopic[ topic ]);
				System.exit(1);
			}

		}
	
		logLikelihood += 
			(Dirichlet.logGamma(beta * numTopics)) -
			(Dirichlet.logGamma(beta) * nonZeroTypeTopics);

		if (Double.isNaN(logLikelihood)) {
			System.out.println("at the end");
			System.exit(1);
		}


		return logLikelihood;
	}

	// 
	// Methods for displaying and saving results
	//

	public String topWords (int numWords) {

		StringBuilder output = new StringBuilder();

		IDSorter[] sortedWords = new IDSorter[numTypes];
		for (int topic = 0; topic < numTopics; topic++) {
			double [] typeProb = new double [numTypes];
			output.append("topic"+topic+" ");
			for (int type = 0; type < numTypes; type++) {
				if(DFLDAConfig.dirichletforest)
				{
				
			    if(((String)alphabet.lookupObject(type)).compareTo("时尚")==0||((String)alphabet.lookupObject(type)).compareTo("汽车")==0||((String)alphabet.lookupObject(type)).compareTo("财经")==0||((String)alphabet.lookupObject(type)).compareTo("时尚")==0||((String)alphabet.lookupObject(type)).compareTo("elf")==0||((String)alphabet.lookupObject(type)).compareTo("玉米")==0||((String)alphabet.lookupObject(type)).compareTo("少儿教育")==0||((String)alphabet.lookupObject(type)).compareTo("游戏达人")==0||((String)alphabet.lookupObject(type)).compareTo("鹿晗")==0||((String)alphabet.lookupObject(type)).compareTo("营销")==0||((String)alphabet.lookupObject(type)).compareTo("强迫症")==0)
			    {
			    System.out.print(alphabet.lookupObject(type)+" ");
				double wordterm = c.topics.elementAt(topic).calc_wordterm1(1, leafmap.elementAt(type));
				sortedWords[type] = new IDSorter(type, wordterm);
				typeProb[type] = wordterm;
			    }
			    else{
			    	double wordterm = c.topics.elementAt(topic).calc_wordterm(1, leafmap.elementAt(type));
					sortedWords[type] = new IDSorter(type, wordterm);
					typeProb[type] = wordterm;
			    }
				}
				else
				{
					sortedWords[type] = new IDSorter(type, typeTopicCounts[type][topic]);
				}
			}

			Arrays.sort(sortedWords);
			//output.append(topic +"y:"+ c.topics.elementAt(topic).get_yvec());
//			if(topic == 0){
//				output.append("旅行 "+typeProb[alphabet.lookupIndex("旅行")]+" ");
//			}
//			else if(topic == 1){
//				output.append("爱美食 "+typeProb[alphabet.lookupIndex("爱美食")]+" ");
//			}
//			else if(topic == 2){
//				output.append("时尚 "+typeProb[alphabet.lookupIndex("时尚")]+" ");
//			}
//			else if(topic == 3){
//				output.append("健康 "+typeProb[alphabet.lookupIndex("健康")]+" ");
//			}
//			else if(topic == 4){
//				output.append("移动互联网 "+typeProb[alphabet.lookupIndex("移动互联网")]+" ");
//			}
//			else if(topic == 5){
//				output.append("运动 "+typeProb[alphabet.lookupIndex("运动")]+" ");
//			}
//			else if(topic == 6){
//				output.append("汽车 "+typeProb[alphabet.lookupIndex("汽车")]+" ");
//			}
//			else if(topic == 7){
//				output.append("游戏达人 "+typeProb[alphabet.lookupIndex("游戏达人")]+" ");
//			}
//			else if(topic == 8){
//				output.append("动漫 "+typeProb[alphabet.lookupIndex("动漫")]+" ");
//			}
//			else if(topic == 9){
//				output.append("电影迷 "+typeProb[alphabet.lookupIndex("电影迷")]+" ");
//			}
//			else if(topic == 10){
//				output.append("少儿教育 "+typeProb[alphabet.lookupIndex("少儿教育")]+" ");
//			}
//			else if(topic == 11){
//				output.append("写小说 "+typeProb[alphabet.lookupIndex("写小说")]+" ");
//			}
//			else if(topic == 12){
//				output.append("财经 "+typeProb[alphabet.lookupIndex("财经")]+" ");
//			}
//			else if(topic == 13){
//				output.append("传媒 "+typeProb[alphabet.lookupIndex("传媒")]+" ");
//			}
//			else if(topic == 14){
//				output.append("摄影爱好者 "+typeProb[alphabet.lookupIndex("摄影爱好者")]+" ");
//			}
//			else if(topic == 15){
//				output.append("营销 "+typeProb[alphabet.lookupIndex("营销")]+" ");
//			}
//			output.append("\t" + tokensPerTopic[topic]);
			for (int i=0; i < numWords; i++) {
				if(typeTopicCounts[topic][sortedWords[i].getID()]!=0){
					output.append(alphabet.lookupObject(sortedWords[i].getID()) + " : " + typeTopicCounts[topic][sortedWords[i].getID()] + " ");
				}
			}
			output.append("\n");
			System.out.print("\n");
		}

		return output.toString();
	}
	

	/**
	 *  @param file        The filename to print to
	 *  @param threshold   Only print topics with proportion greater than this number
	 *  @param max         Print no more than this many topics
	 */
	public void printDocumentTopics (File file, double threshold, int max) throws IOException {
		PrintWriter out = new PrintWriter(file);

		out.print ("#doc source topic proportion ...\n");
		int docLen;
		int[] topicCounts = new int[ numTopics ];

		IDSorter[] sortedTopics = new IDSorter[ numTopics ];
		for (int topic = 0; topic < numTopics; topic++) {
			// Initialize the sorters with dummy values
			sortedTopics[topic] = new IDSorter(topic, topic);
		}

		if (max < 0 || max > numTopics) {
			max = numTopics;
		}

		for (int doc = 0; doc < data.size(); doc++) {
			LabelSequence topicSequence = (LabelSequence) data.get(doc).topicSequence;
			int[] currentDocTopics = topicSequence.getFeatures();

			out.print (doc); out.print (' ');

			if (data.get(doc).instance.getSource() != null) {
				out.print (data.get(doc).instance.getSource()); 
			}
			else {
				out.print ("null-source");
			}

			out.print (' ');
			docLen = currentDocTopics.length;

			// Count up the tokens
			for (int token=0; token < docLen; token++) {
				topicCounts[ currentDocTopics[token] ]++;
			}

			// And normalize
			for (int topic = 0; topic < numTopics; topic++) {
				sortedTopics[topic].set(topic, (float) topicCounts[topic] / docLen);
			}
			
			Arrays.sort(sortedTopics);

			for (int i = 0; i < max; i++) {
				if (sortedTopics[i].getWeight() < threshold) { break; }
				
				out.print (sortedTopics[i].getID() + " " + 
						  sortedTopics[i].getWeight() + " ");
			}
			out.print (" \n");

			Arrays.fill(topicCounts, 0);
		}
		
	}
	
	public void printState (File f) throws IOException {
		PrintStream out =
			new PrintStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(f))));
		printState(out);
		out.close();
	}
	
	public void printState (PrintStream out) {

		out.println ("#doc source pos typeindex type topic");

		for (int doc = 0; doc < data.size(); doc++) {
			FeatureSequence tokenSequence =	(FeatureSequence) data.get(doc).instance.getData();
			LabelSequence topicSequence =	(LabelSequence) data.get(doc).topicSequence;

			String source = "NA";
			if (data.get(doc).instance.getSource() != null) {
				source = data.get(doc).instance.getSource().toString();
			}

			for (int position = 0; position < topicSequence.getLength(); position++) {
				int type = tokenSequence.getIndexAtPosition(position);
				int topic = topicSequence.getIndexAtPosition(position);
				out.print(doc); out.print(' ');
				out.print(source); out.print(' '); 
				out.print(position); out.print(' ');
				out.print(type); out.print(' ');
				out.print(alphabet.lookupObject(type)); out.print(' ');
				out.print(topic); out.println();
			}
		}
	}
	
	
	// Serialization
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	private static final int NULL_INTEGER = -1;
	
	public void write (File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(f));
			oos.writeObject(this);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + f + ": " + e);
		}
	}
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);

		// Instance lists
		out.writeObject (data);
		out.writeObject (alphabet);
		out.writeObject (topicAlphabet);

		out.writeInt (numTopics);
		out.writeObject (alpha);
		out.writeDouble (beta);
		out.writeDouble (betaSum);

		out.writeInt(showTopicsInterval);
		out.writeInt(wordsPerTopic);

		out.writeObject(random);
		out.writeObject(formatter);
		out.writeBoolean(printLogLikelihood);

		out.writeObject (typeTopicCounts);

		for (int ti = 0; ti < numTopics; ti++) {
			out.writeInt (tokensPerTopic[ti]);
		}
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int featuresLength;
		int version = in.readInt ();

		data = (ArrayList<TopicAssignment>) in.readObject ();
		alphabet = (Alphabet) in.readObject();
		topicAlphabet = (LabelAlphabet) in.readObject();

		numTopics = in.readInt();
		alpha = in.readDouble();
		_alpha = in.readDouble();
		alphaSum = alpha * numTopics;
		beta = in.readDouble();
		betaSum = in.readDouble();

		showTopicsInterval = in.readInt();
		wordsPerTopic = in.readInt();

		random = (Randoms) in.readObject();
		formatter = (NumberFormat) in.readObject();
		printLogLikelihood = in.readBoolean();
		
		int numDocs = data.size();
		this.numTypes = alphabet.size();

		typeTopicCounts = (int[][]) in.readObject();
		tokensPerTopic = new int[numTopics];
		for (int ti = 0; ti < numTopics; ti++) {
			tokensPerTopic[ti] = in.readInt();
		}
	}

	public static void main (String[] args) throws IOException {

		String text = "Goodusers.txt";
		List<String> sensList = File_util.readFile(text);
		InstanceList training = MalletInput.getInstanceList(sensList);		
		int burnin = 0;
		DFLDA lda = new DFLDA (DFLDAConfig.topic,DFLDAConfig.alphasum, DFLDAConfig.beta, DFLDAConfig._alpha, DFLDAConfig._beta, DFLDAConfig.eta,DFLDAConfig.soft_eta);
		lda.addInstances(training,burnin);
		lda.sample(400);
	}
}
