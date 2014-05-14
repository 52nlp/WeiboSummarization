package demo;

import java.io.IOException;
import java.util.List;

import TM.Config.DFLDAConfig;

import util.File_util;
import util.mallet.MalletInput;
import DFLDA.DFLDA;
import cc.mallet.types.InstanceList;

public class DFLDADemo {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String text = "Goodusers.txt";
		List<String> sensList = File_util.readFile(text);
		InstanceList training = MalletInput.getInstanceList(sensList);		
		int burnin = 0;
		DFLDA lda = new DFLDA (DFLDAConfig.topic,DFLDAConfig.alphasum, DFLDAConfig.beta, DFLDAConfig._alpha, DFLDAConfig._beta, DFLDAConfig.eta,DFLDAConfig.soft_eta);
		lda.addInstances(training,burnin);
		lda.sample(10);
	}

}
