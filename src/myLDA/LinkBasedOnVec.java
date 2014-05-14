package myLDA;

import java.util.Vector;

public class LinkBasedOnVec extends Summary{
	public VecLoader loader;
	public SynPair pairs;
	
	public LinkBasedOnVec(Vector<String> seeds,String vecFile){
		super(seeds);
		loader=new VecLoader(vecFile);
		pairs=new SynPair();
	}

	public boolean genLink(String outfile){
		if(!loader.loadFromFile())
			return false;
		
		for(int i=0;i<rootsbackward.size();i++){
			Node presentNode=rootsbackward.get(i);
			while(presentNode!=null){
				if(presentNode.firstSon!=null&&presentNode.sonNum!=0){
					Vector<String> elem=new Vector<String>();
					double[][] value=new double[presentNode.sonNum][loader.dimension];
					int[] f=new int[presentNode.sonNum];
					Node pt=presentNode.firstSon;
					do{
						String presentWord=pt.content;
						int index=elem.size();
//						System.out.println(value.length+" "+index);
						value[index]=loader.lookUp(presentWord);
						f[index]=pt.number;
						if(value[index]!=null){
							double length=0;
//							System.out.println(value[index].length+" "+loader.dimension);
							for(int k=0;k<loader.dimension;k++)
								length+=value[index][k]*value[index][k];
							length=Math.sqrt(length);
							for(int k=0;k<loader.dimension;k++)
								value[index][k]/=length;
						}
						for(int j=0;j<index;j++){
							double cosine=0;
							if(value[index]==null)
								break;
							if(presentWord.contains("/x")||presentWord.contains("/m"))
								break;
							if(value[j]==null)
								continue;
							if(elem.get(j).contains("/x")||elem.get(j).contains("/m"))
								continue;
							for(int k=0;k<loader.dimension;k++)
								cosine+=value[j][k]*value[index][k];
//							System.out.println(cosine);
							int frequency=f[index]>f[j]?f[j]:f[index];
							if(cosine>Config.consineThresholdValue){
								pairs.addPair(presentWord,elem.get(j),frequency,cosine);
							}
						}
						elem.add(presentWord);
						pt=pt.rightBrother;
					}while(pt!=presentNode.firstSon);
				}
				presentNode=presentNode.nextNode();
			}
		}
		for(int i=0;i<rootsforward.size();i++){
			Node presentNode=rootsforward.get(i);
			while(presentNode!=null){
				if(presentNode.firstSon!=null&&presentNode.sonNum!=0){
					Vector<String> elem=new Vector<String>();
					double[][] value=new double[presentNode.sonNum][loader.dimension];
					int[] f=new int[presentNode.sonNum];
					Node pt=presentNode.firstSon;
					do{
						String presentWord=pt.content;
						int index=elem.size();
						value[index]=loader.lookUp(presentWord);
						f[index]=pt.number;
						if(value[index]!=null){
							double length=0;
							for(int k=0;k<loader.dimension;k++)
								length+=value[index][k]*value[index][k];
							length=Math.sqrt(length);
							for(int k=0;k<loader.dimension;k++)
								value[index][k]/=length;
						}
						for(int j=0;j<index;j++){
							double cosine=0;
							if(value[index]==null)
								break;
							if(presentWord.contains("/x")||presentWord.contains("/m"))
								break;
							if(value[j]==null)
								continue;
							if(elem.get(j).contains("/x")||elem.get(j).contains("/m"))
								continue;
							for(int k=0;k<loader.dimension;k++)
								cosine+=value[j][k]*value[index][k];
//							System.out.println(cosine);
							int frequency=f[index]>f[j]?f[j]:f[index];
							if(cosine>Config.consineThresholdValue){
								pairs.addPair(presentWord,elem.get(j),frequency,cosine);
							}
						}
						elem.add(presentWord);
						pt=pt.rightBrother;
					}while(pt!=presentNode.firstSon);
				}
				presentNode=presentNode.nextNode();
			}				
		}
		pairs.filter(Config.freThresholdValue);
		return pairs.print(outfile);
	}
	
	public static void main(String args[]){
		LinkBasedOnVec link=new LinkBasedOnVec(Config.keywords,Config.path+Config.wordVecFile);
		System.out.println("established");
		link.draw(Config.path+Config.wordSplitedFile);
		System.out.println("tree set up");
//		link.genPhrase(Config.phraseFile);
		System.out.println("find link");
		link.genLink(Config.path+Config.synBasedOnVecFile);
		System.out.println("all finished");
		return;
	}
}
