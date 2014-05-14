package LDAVariety;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.text.NumberFormat;

import util.File_util;

import cc.mallet.topics.*;
import cc.mallet.types.*;
import cc.mallet.util.*;


public class LDABase {

	private static Logger logger = MalletLogger.getLogger(SimpleLDA.class.getName());
	
	// the training instances and their topic assignments
	protected ArrayList<TopicAssignment> data;  

	// the alphabet for the input data
	protected Alphabet alphabet; 

	// the alphabet for the topics
	protected LabelAlphabet topicAlphabet; 
	
	// The number of topics requested
	protected int numTopics;

	// The size of the vocabulary
	protected int numTypes;
	
	protected double logLikelihood;

	// Prior parameters
	protected double alpha;	 // Dirichlet(alpha,alpha,...) is the distribution over topics
	protected double alphaSum;
	protected double beta;   // Prior on per-topic multinomial distribution over words
	protected double betaSum;
	public static final double DEFAULT_BETA = 0.1;
	

	// Statistics needed for sampling.
	protected int[][] typeTopicCounts; // indexed by <feature index, topic index>
	protected int[] tokensPerTopic; // indexed by <topic index>
	protected int[][] documentTopicCounts; // index by <document index, topic index>
	
	protected double[][] theta;
	protected double[][] phi;
	
	public int wordCount;
	public int showTopicsInterval = 50;
	public int wordsPerTopic = 10;
	
	protected Randoms random;
	protected NumberFormat formatter;
	protected boolean printLogLikelihood = true;
	
	public LDABase (int numberOfTopics) {
		this (numberOfTopics, numberOfTopics, DEFAULT_BETA);
	}
	
	public LDABase(int numberOfTopics, double alphaSum, double beta) {
		this (numberOfTopics, alphaSum, beta, new Randoms());
	}
	
	private static LabelAlphabet newLabelAlphabet (int numTopics) {
		LabelAlphabet ret = new LabelAlphabet();
		for (int i = 0; i < numTopics; i++)
			ret.lookupIndex("topic"+i);
		return ret;
	}
	
	public LDABase (int numberOfTopics, double alphaSum, double beta, Randoms random) {
		this (newLabelAlphabet (numberOfTopics), alphaSum, beta, random);
	}
	
	public LDABase (LabelAlphabet topicAlphabet, double alphaSum, double beta, Randoms random)
	{
		this.data = new ArrayList<TopicAssignment>();
		this.topicAlphabet = topicAlphabet;
		this.numTopics = topicAlphabet.size();

		this.alphaSum = alphaSum;
		this.alpha = alphaSum / numTopics;
		this.beta = beta;
		this.random = random;
		
		this.logLikelihood = 0;
		
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

	public void addInstances (InstanceList training) {

		alphabet = training.getDataAlphabet();
		numTypes = alphabet.size();
		
		betaSum = beta * numTypes;
		
		typeTopicCounts = new int[numTypes][numTopics];
		documentTopicCounts = new int[training.size()][numTopics];
		
		theta = new double[training.size()][numTopics];
		phi = new double[numTopics][numTypes];


		int documentIndex = 0;
		for (Instance instance : training) {
			FeatureSequence tokens = (FeatureSequence) instance.getData();
			LabelSequence topicSequence =
				new LabelSequence(topicAlphabet, new int[ tokens.size() ]);
			
			int[] topics = topicSequence.getFeatures();
			for (int position = 0; position < tokens.size(); position++) {

				int topic = random.nextInt(numTopics);
				topics[position] = topic;
				documentTopicCounts[documentIndex][topic]++;
				tokensPerTopic[topic]++;
				
				int type = tokens.getIndexAtPosition(position);
				typeTopicCounts[type][topic]++;
				wordCount++;
			}

			TopicAssignment t = new TopicAssignment (instance, topicSequence);
			data.add (t);
			documentIndex++;
		}

	}

	public void sample (int iterations) throws IOException {

		for (int iteration = 1; iteration <= iterations; iteration++) {

//			long iterationStart = System.currentTimeMillis();

			logLikelihood = 0;
			// Loop over every document in the corpus
			for (int doc = 0; doc < data.size(); doc++) {
//				FeatureSequence tokenSequence =
//					(FeatureSequence) data.get(doc).instance.getData();
//				LabelSequence topicSequence =
//					(LabelSequence) data.get(doc).topicSequence;

//				sampleTopicsForOneDoc (tokenSequence, topicSequence);
				sampleTopicsForOneDoc(doc);
			}
			if(printLogLikelihood){
				logLikelihood = logLikelihood();
				double wordPerplexity = Math.exp(((-logLikelihood)/wordCount));
				logger.info("<" + iteration + ">  log-likelihood: " +  logLikelihood
				+ "\t wordPerplexity: " +  wordPerplexity);
			}
//            long elapsedMillis = System.currentTimeMillis() - iterationStart;
//			logger.fine(iteration + "\t" + elapsedMillis + "ms\t");

			// Occasionally print more information
			if (showTopicsInterval != 0 && iteration % showTopicsInterval == 0) {
				logger.info(topWords (wordsPerTopic));
			}

		}
	}
	
	protected void sampleTopicsForOneDoc (int doc){
//	protected void sampleTopicsForOneDoc (FeatureSequence tokenSequence,
//										  FeatureSequence topicSequence) {

	   FeatureSequence tokenSequence =
			(FeatureSequence) data.get(doc).instance.getData();
		LabelSequence topicSequence =
			(LabelSequence) data.get(doc).topicSequence;
		int[] oneDocTopics = topicSequence.getFeatures();

		int type, oldTopic, newTopic;
		int docLength = tokenSequence.getLength();

//		int[] localTopicCounts = new int[numTopics];

		//		populate topic counts
//		for (int position = 0; position < docLength; position++) {
//			localTopicCounts[oneDocTopics[position]]++;
//		}

		double score, sum;
		double[] topicTermScores = new double[numTopics];

		//	Iterate over the positions (words) in the document 
		for (int position = 0; position < docLength; position++) {
			type = tokenSequence.getIndexAtPosition(position);
			oldTopic = oneDocTopics[position];

			// Grab the relevant row from our two-dimensional array
//			currentTypeTopicCounts = typeTopicCounts[type];

			//	Remove this token from all counts. 
//			localTopicCounts[oldTopic]--;
			documentTopicCounts[doc][oldTopic]--;
			tokensPerTopic[oldTopic]--;
			assert(tokensPerTopic[oldTopic] >= 0) : "old Topic " + oldTopic + " below 0";
			typeTopicCounts[type][oldTopic]--;

			// Now calculate and add up the scores for each topic for this word
			sum = 0.0;
			
			// Here's where the math happens! Note that overall performance is 
			//  dominated by what you do in this loop.
			for (int topic = 0; topic < numTopics; topic++) {
//				score =
//					(alpha + documentTopicCounts[doc][topic]) *
//					((beta + typeTopicCounts[type][topic]) /
//					 (betaSum + tokensPerTopic[topic]));
				score = topicUpdatingCompute(topic, doc, type);
//				System.out.print(score + "\t");
				sum += score;
				topicTermScores[topic] = score;
			}
//			System.out.println();
			
			// Choose a random point between 0 and the sum of all topic scores
			double sample = random.nextUniform() * sum;

			// Figure out which topic contains that point
			newTopic = -1;
			while (sample > 0.0) {
				newTopic++;
				sample -= topicTermScores[newTopic];
			}

			// Make sure we actually sampled a topic
			if (newTopic == -1) {
				throw new IllegalStateException ("SimpleLDA: New topic not sampled.");
			}

			// Put that new topic into the counts
			oneDocTopics[position] = newTopic;
//			localTopicCounts[newTopic]++;
			documentTopicCounts[doc][newTopic]++;
			tokensPerTopic[newTopic]++;
			typeTopicCounts[type][newTopic]++;
			
		}
	}
	
	
	
	public double logLikelihood(){
		double loglikelihood = 0;
		computePhi();
		computeTheta();
		for (int doc = 0; doc < data.size(); doc++) {
			FeatureSequence tokenSequence =(FeatureSequence) data.get(doc).instance.getData();	
			for (int position = 0; position < tokenSequence.getLength(); position++) {
				int type = tokenSequence.getIndexAtPosition(position);
				double sum = 0;
				for (int topic = 0; topic < numTopics; topic++) {		
					sum += phi[topic][type] * theta[doc][topic];
				}		
				loglikelihood += Math.log(sum);
			}
		}
		return loglikelihood;	
	}
	
	public double topicUpdatingCompute(int topic, int doc, int type){
		double score =
				(alpha + documentTopicCounts[doc][topic]) *
				((beta + typeTopicCounts[type][topic]) /
				 (betaSum + tokensPerTopic[topic]));
		return score;
	}
	
	public void computePhi(){
		for(int topic = 0 ; topic < numTopics; topic++){
			for(int v = 0 ; v < numTypes; v++){
				phi[topic][v] = ((beta + typeTopicCounts[v][topic]) / (betaSum + tokensPerTopic[topic]));
			}
		}
	}
	
	public void computeTheta(){
		for (int doc = 0; doc < data.size(); doc++) {
			FeatureSequence tokenSequence =(FeatureSequence) data.get(doc).instance.getData();
//			LabelSequence topicSequence = (LabelSequence) data.get(doc).topicSequence;	
//			int[] localTopicCounts = new int[numTopics];

//			for (int position = 0; position < tokenSequence.getLength(); position++) {
//				localTopicCounts[topicSequence.getFeatures()[position]]++;
//			}
			for (int topic = 0; topic < numTopics; topic++) {
				theta[doc][topic] = (alpha + documentTopicCounts[doc][topic])/(alphaSum + tokenSequence.getLength());
			}		
			
		}
	}
		
	// 
	// Methods for displaying and saving results
	//
	public String topWords (int numWords) {
		StringBuilder output = new StringBuilder();
		IDSorter[] sortedWords = new IDSorter[numTypes];
		for (int topic = 0; topic < numTopics; topic++) {
			for (int type = 0; type < numTypes; type++) {
				sortedWords[type] = new IDSorter(type, typeTopicCounts[type][topic]);
			}
			Arrays.sort(sortedWords);		
			output.append(topic + "\t" + tokensPerTopic[topic] + "\t");
			for (int i=0; i < numWords; i++) {
				if(typeTopicCounts[sortedWords[i].getID()][topic]!=0){
					output.append(alphabet.lookupObject(sortedWords[i].getID()) + ": " + typeTopicCounts[sortedWords[i].getID()][topic] + "   ");
				}
			}
			output.append("\n");
		}
		return output.toString();
	}

	public void printToFile(String fileName, int numWords){
		List<String> res = new ArrayList<String>();
		res.add(topWords (numWords));
		File_util.write(res, fileName);
	}
	
	public void printToFile(String fileName){
		printToFile(fileName,wordsPerTopic);
	}
	
	public static void main (String[] args) throws IOException {
	}
	
}
