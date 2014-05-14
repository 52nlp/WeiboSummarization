package util.mallet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.InstanceList;

//增量的Instance
public class IncrementInstance {

	//当前所有的InstanceList;
	public InstanceList curAllInstanceList;
	//记录当前的InstanceList的位置,开区间
	public int beginInstancePosition;
	public int endInstancePosition;
	
	public List<InstanceList> currentInstanceList;
	
	public InstanceList curCandidateInstanceList;
	
	public IncrementInstance(List<String> segSensList){
		this.beginInstancePosition = 0 ;
		this.endInstancePosition = 1;
		
		this.currentInstanceList = new ArrayList<InstanceList>();
		
		this.curAllInstanceList = MalletInput.getInstanceList(segSensList);
		this.currentInstanceList.add(MalletInput.getInstanceList(segSensList));
//		this.curCandidateInstanceList = MalletInput.getInstanceList(segSensList);
		
		this.curCandidateInstanceList = MalletInput.getInstanceList(segSensList).subList(
				MalletInput.getInstanceList(segSensList).size(), MalletInput.getInstanceList(segSensList).size());

	}
	
	public IncrementInstance(String filePath){
		this.beginInstancePosition = 0 ;
		this.endInstancePosition = 1;
		
		this.currentInstanceList = new ArrayList<InstanceList>();

		this.curAllInstanceList =  MalletInput.getInstanceList(filePath);
		this.currentInstanceList.add( MalletInput.getInstanceList(filePath));
//		this.curCandidateInstanceList =  MalletInput.getInstanceList(filePath);
		this.curCandidateInstanceList = MalletInput.getInstanceList(filePath).subList(
				MalletInput.getInstanceList(filePath).size(), MalletInput.getInstanceList(filePath).size());

	}
	
	public IncrementInstance(File file){
		this.beginInstancePosition = 0 ;
		this.endInstancePosition = 1;
		
		this.currentInstanceList = new ArrayList<InstanceList>();
		this.currentInstanceList.add(MalletInput.getInstanceList(file));
		this.curAllInstanceList = MalletInput.getInstanceList(file);
//		this.curCandidateInstanceList = MalletInput.getInstanceList(file);
		this.curCandidateInstanceList = MalletInput.getInstanceList(file).subList(
				MalletInput.getInstanceList(file).size(), MalletInput.getInstanceList(file).size());
		
	}
	
	//当前的IntanceList上面增加
	public void addInstanceList(List<String> incrementSegSensList){
		this.currentInstanceList.add(MalletInput.getInstanceList(incrementSegSensList));
	}
	
	//当前的IntanceList上面增加
	public void addInstanceList(InstanceList incrementInstanceList){
		this.currentInstanceList.add(incrementInstanceList);
	}
	
	//当前使用的InstanceList上增加
	public void currentInstanceListIncrement(){
		if(this.endInstancePosition >= this.currentInstanceList.size()){
			System.err.println("size error!");
		}
		else{
			this.curAllInstanceList.addAll(this.currentInstanceList.get(endInstancePosition));
			this.curCandidateInstanceList.addAll(this.currentInstanceList.get(endInstancePosition));
			endInstancePosition++;			
		}
	}
	
	public void currentInstancesRevome(){	
		if(this.beginInstancePosition >= this.endInstancePosition 
				|| this.beginInstancePosition >= this.currentInstanceList.size()){
			System.err.println("size error!");
		}
		else{
			int removeSize = this.currentInstanceList.get(this.beginInstancePosition).size();
			System.out.println("remove size: " + removeSize);
			if(removeSize <= this.curAllInstanceList.size()){
				this.curAllInstanceList = this.curAllInstanceList.subList(removeSize, this.curAllInstanceList.size());
				this.beginInstancePosition++;
			}
			else{
				System.err.println("error!");
			}					
		}
	}
	
	public void currentInstanceListRevome(){
		if(this.beginInstancePosition==0){
			System.err.println("remove error!");
		}
		else{
			this.currentInstanceList.remove(0);
			this.beginInstancePosition--;
			this.endInstancePosition--;
		}
	}
	
	public void candidateInstanceListClear(){
		this.curCandidateInstanceList = this.curCandidateInstanceList.subList(this.curCandidateInstanceList.size()
				, this.curCandidateInstanceList.size());
	}
	
	
	public static void main(String[] args){
		List<String> sens1 = new ArrayList<String>();
		List<String> sens2 = new ArrayList<String>();
		List<String> sens3 = new ArrayList<String>();
		
		sens1.add("中国");
		
		sens2.add("人民");
		sens2.add("共和国");
		
		sens3.add("就是");
		sens3.add("一个");
		sens3.add("SB");
		
		IncrementInstance test = new IncrementInstance(sens1);
		
		test.addInstanceList(sens2);
		test.addInstanceList(sens3);
		
//		System.out.println(test.curAllinstanceList.size());
//		System.out.println(test.curAllinstanceList.getDataAlphabet());
		
		
		test.currentInstanceListIncrement();	
		test.currentInstanceListIncrement();
		
		
		System.out.println(test.currentInstanceList.size());
		System.out.println(test.curCandidateInstanceList.size());
		test.currentInstancesRevome();
		test.currentInstancesRevome();
		test.currentInstancesRevome();
		System.out.println(test.curAllInstanceList.size());
		System.out.println(test.currentInstanceList.size());
		System.out.println(test.curCandidateInstanceList.size());

		test.candidateInstanceListClear();
		System.out.println(test.curCandidateInstanceList.size());
//		for(InstanceList i: test.currentInstanceList){
//			System.out.println(i.size());
//		}
//		System.out.println(test.curAllinstanceList.size());
//		System.out.println(test.curAllinstanceList.getDataAlphabet());
		
		
//		test.currentInstanceListRevome();
//		System.err.println(test.currentInstanceList.get(0).size());
//		System.out.println(test.curAllInstanceList.getDataAlphabet());		
//		System.out.println(test.currentInstanceList.get(0)==test.curAllinstanceList);
		
		
		
	}
}
