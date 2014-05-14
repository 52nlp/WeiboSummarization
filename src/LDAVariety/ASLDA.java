package LDAVariety;

import java.io.IOException;
import util.mallet.MalletInput;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;

public class ASLDA extends LDABase {

	public double _alpha = 50;
	
	public ASLDA(int numberOfTopics) {
		super(numberOfTopics);
		// TODO Auto-generated constructor stub
	}
	
	public ASLDA(int numberOfTopics, double alphaSum, double beta){
		super(numberOfTopics, alphaSum, beta);
	}
	
	public double topicUpdatingCompute(int topic, int doc, int type){	
		double ratio = (tokensPerTopic[topic] + _alpha)/(wordCount - 1 + _alpha*numTopics);
	    double as_prior = ratio * alphaSum;		
	    double score =
			(as_prior + documentTopicCounts[doc][topic]) *
			((beta + typeTopicCounts[type][topic]) /
			 (betaSum + tokensPerTopic[topic]));	    
		return score;
	}	
	
	public void computeTheta(){
		for (int doc = 0; doc < data.size(); doc++) {
			FeatureSequence tokenSequence =(FeatureSequence) data.get(doc).instance.getData();
			for (int topic = 0; topic < numTopics; topic++) {
				double ratio = (tokensPerTopic[topic] + _alpha)/(wordCount - 1 + _alpha*numTopics);
				theta[doc][topic] = (ratio * alphaSum + documentTopicCounts[doc][topic])/(alphaSum + tokenSequence.getLength());
			}		
			
		}
	}
	
	public static void main(String[] args) throws IOException{	
		String text = "LineSentence.txt";
//		List<String> sensList = File_util.readFile(text);
		InstanceList training = MalletInput.getInstanceList(text);
		int topicNum = 20;
		LDABase lda = new ASLDA(topicNum,0.1,0.1);
		lda.addInstances(training);
		lda.sample(500);
	}

}
