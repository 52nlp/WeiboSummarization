package newHLDA.item;

import java.util.ArrayList;

import util.mallet.CrossValidationInstance;

public class HldaCrossValidationInstances {
	public CrossValidationInstance crossValidationInstance;
	
	public ArrayList<HldaDoc> documents;
	public ArrayList<HldaDoc> testingDocuments;
	public int numDocuments;  //文档的数目
	public int numTestDocuments;
	
	
	
}
