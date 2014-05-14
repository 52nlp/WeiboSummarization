package LDAVariety;

import java.io.IOException;
import util.mallet.MalletInput;
import cc.mallet.types.InstanceList;

public class SimpleGibbsLDA extends LDABase {

	public SimpleGibbsLDA(int numberOfTopics) {
		super(numberOfTopics);
		// TODO Auto-generated constructor stub
	}
	public SimpleGibbsLDA(int numberOfTopics, double alphaSum, double beta){
		super(numberOfTopics, alphaSum, beta);
	}
	
	public double topicUpdatingCompute(int topic, int doc, int type){
		double score =
				(alpha + documentTopicCounts[doc][topic]) *
				((beta + typeTopicCounts[type][topic]) /
				 (betaSum + tokensPerTopic[topic]));
		return score;
	}
	
	public void addInstances (InstanceList training) {
		super.addInstances(training);
		//building 
	}
	
	public static void main(String[] args) throws IOException{	
		String text = "LineSentence.txt";
//		List<String> sensList = File_util.readFile(text);
		InstanceList training = MalletInput.getInstanceList(text);
		int topicNum = 20;
		LDABase lda = new SimpleGibbsLDA(topicNum,1,0.1);
		lda.addInstances(training);
		lda.sample(500);
	}

}
