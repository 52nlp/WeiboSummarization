package DirichletForest;

import java.util.Vector;

public class MLNode extends DTNodes{
//	public Vector<Double> edges;
//	public Vector<MLNode> ichildren;
//	public Vector<Integer> maxind;
//	public int leafstart;
	public Vector<Integer> words;
	
	public MLNode(Vector<Double> edges, Vector<MLNode> ichildren,Vector<Integer> maxind,int leafstart,Vector<Integer> words, double edgesum,Vector<Double> orig_edges,double orig_edgesum){
		this.edges = edges;
		this.ichildren = ichildren;
		this.maxind = maxind;
		this.leafstart = leafstart;
		this.words = words;
		this.edgesum = edgesum;
		this.orig_edges = orig_edges;
		this.orig_edgesum = orig_edgesum;
	}
	
//	public int numLeaves(){
//		int n = 0;
////		System.out.println(this.edges);
//		for(int i = 0;i<this.ichildren.size();i++){
////			n = n+ ((DTNodes)ichildren.get(i)).numLeaves();
//			n = n+ (ichildren.get(i)).numLeaves();
//		}
//		
//		return (n+this.edges.size()-this.ichildren.size());
//	}
}