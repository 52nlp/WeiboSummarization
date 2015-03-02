package naiveSummary;

import java.util.Vector;

public class Config {
	// path and file 
	protected final static String root = "./doc/yaan/";
	protected final static String path = "./doc/yaan/naive/";
	// given
	protected final static String wordSplitedFile = "splited.txt";
	// to generate
	protected final static String patternTreeFile = "single.tree";
	protected final static String singleSummaryFile = "single.summary";
	protected final static String doubleSummaryFile = "double.summary";
	// keywords
	protected final static Vector<String> keywords;
	// the base the log
	protected final static double base = 2;

	// the depth of the search
	protected final static int searchDeep = 100;

	// threshold value of the leaf to cut
	protected final static double valueThreshold = 100;

	static {
		keywords = new Vector<String>();
//		keywords.add("钓鱼岛/n");
		 keywords.add("雅安/n");
		 keywords.add("地震/n");
		 keywords.add("雅安地震/n");
	}
}

/*
public boolean printFrequentPattern(String outfile){
	try{
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outfile),"UTF-8"));
		for(int i=0;i<roots.size();i++){
			Node presentNode=roots.get(i);
			while(presentNode!=null){
				presentNode=presentNode.nextNode();
				if(presentNode!=null&&presentNode.depth>=Config.searchDeep-1&&presentNode.value>Config.valueThreshold){
					boolean extend=false;
					Node pt=presentNode.firstSon;
					if(pt!=null){
						do{
							if(pt.value>Config.valueThreshold){
								extend=true;
								break;
							}
							pt=pt.rightBrother;
						}while(pt!=presentNode.firstSon);
					}
					if(extend==true)
						continue;
					pt=presentNode;
					Vector<String> pattern=new Vector<String>();
					double value=0;
					while(pt!=null){
						pattern.add(pt.content.substring(0,pt.content.indexOf("/")));
						if(pt.father!=null)
							value+=pt.value;
						pt=pt.father;
					}
					String word="";
					for(int j=1;j<=pattern.size();j++){
						if(j%2==0){
							word=pattern.get(pattern.size()-j)+word;
						}else{
							word=word+pattern.get(pattern.size()-j);
						}
					}
					writer.write(word+" "+value+"\n");
				}
			}
		}
		writer.close();
		return true;
	}catch(IOException e){
		System.out.println("Error While Writing the File "+outfile);
		e.printStackTrace();
		return false;
	}
}
*/
