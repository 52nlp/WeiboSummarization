package util.mallet;

import java.io.File;
import java.util.List;

import cc.mallet.types.InstanceList;

public class CrossValidationInstance {

	public InstanceList instances;
	public InstanceList training;
	public InstanceList testing;
	public static int ratio = 5;
	
	public CrossValidationInstance(String fileName){
		this.instances = MalletInput.getInstanceList(fileName);
    	InstanceList.CrossValidationIterator iter = instances.crossValidationIterator(ratio);
    	InstanceList[] splitedList;
    	while(iter.hasNext()){
    		splitedList = iter.next();
			this.training = splitedList[0];
			this.testing = splitedList[1];
    	}

	}	
	public CrossValidationInstance(File file){
		this.instances = MalletInput.getInstanceList(file);
    	InstanceList.CrossValidationIterator iter = instances.crossValidationIterator(ratio);
    	InstanceList[] splitedList;
    	while(iter.hasNext()){
    		splitedList = iter.next();
			this.training = splitedList[0];
			this.testing = splitedList[1];
    	}

	}	
	public CrossValidationInstance(List<String> segSensList){
		this.instances = MalletInput.getInstanceList(segSensList);
    	InstanceList.CrossValidationIterator iter = instances.crossValidationIterator(ratio);
    	InstanceList[] splitedList;
    	while(iter.hasNext()){
    		splitedList = iter.next();
			this.training = splitedList[0];
			this.testing = splitedList[1];
    	}
    	
	}
	
	public CrossValidationInstance(InstanceList instances){
		this.instances = instances;
    	InstanceList.CrossValidationIterator iter = instances.crossValidationIterator(ratio);
    	InstanceList[] splitedList;
    	while(iter.hasNext()){
    		splitedList = iter.next();
			this.training = splitedList[0];
			this.testing = splitedList[1];
    	}

	}
	
	
}
