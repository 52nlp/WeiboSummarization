package DirichletForest;

import java.util.*;
import java.awt.*;

public class ConstraintCompiler {
	public void buildTree(Vector<Vector<Integer>> mlcc, Vector<Vector<Integer>> clcc,
			       Vector<Vector<Vector<Integer>>> allowable,int W,double beta,double eta,
			       DTNodes root,Vector<Integer> leafmap){
		Vector<MLNode> ml_nodes = new Vector<MLNode>();
	    // Build M-nodes for each mlcc (will have only leaf children)

		for(int i = 0;i<mlcc.size();i++){
			double edgesum = 0;
			 Vector<Double> edges = new Vector<Double>();
			 Vector<Double> orig_edges = new Vector<Double>();
			 for(int j = 0;j<mlcc.elementAt(i).size();j++){
				 edges.add(eta*beta);
				 orig_edges.add(eta*beta);
				 edgesum += eta*beta;
			 }
			 Vector<MLNode> ichildren = new Vector<MLNode>();
			 Vector<Integer> maxind = new Vector<Integer>();
			
			 MLNode ml = new MLNode(edges, ichildren,maxind,-1,mlcc.elementAt(i),edgesum,orig_edges,edgesum);
			 ml_nodes.add(ml);
		}	 
		
//		 System.out.println(ml_nodes.elementAt(0).numLeaves());
		// Build multinodes for each clcc
		Vector<MultiNodes> multinodes = new Vector<MultiNodes>();
		//System.out.println(mlcc);
		for(int i = 0;i<clcc.size();i++){
			Vector<Integer> icids = new Vector<Integer>();
			Vector<Integer> lcids = new Vector<Integer>();
			Vector<Integer> fake_words = new Vector<Integer>();
			for(int z = 0;z<clcc.elementAt(i).size();z++){
				int key = clcc.elementAt(i).elementAt(z).intValue();
				if(key >= W)
					icids.add(key);
				else lcids.add(key);
			}
			
			for(int z = 0;z<icids.size();z++){
				fake_words.add(icids.elementAt(z).intValue());
			}
			for(int z = 0;z<lcids.size();z++){
				fake_words.add(lcids.elementAt(z).intValue());
			}
			
			Vector<DTNodes> variations = new Vector<DTNodes>();
			Vector<Double> variant_logweights = new Vector<Double>();;
			Vector<Vector<Integer>> fake_leafmap = new Vector<Vector<Integer>>();
			
			for(int j = 0;j<allowable.elementAt(i).size();j++){
				Vector<Integer> good = allowable.elementAt(i).elementAt(j);
				Vector<Integer> bad = new Vector<Integer>();
				for(int z = 0;z<fake_words.size();z++){
					if(!good.contains(fake_words.elementAt(z).intValue()))
						bad.add(fake_words.elementAt(z).intValue());
				}
				
				Vector<Double> aedges = new Vector<Double>();
				Vector<Double> orig_aedges = new Vector<Double>();
				for(int z = 0;z<good.size();z++){
					if(good.elementAt(z).intValue() >= W)
					{
						aedges.add(beta*(ml_nodes.elementAt(good.elementAt(z).intValue()-W)).numLeaves());
						orig_aedges.add(beta*(ml_nodes.elementAt(good.elementAt(z).intValue()-W)).numLeaves());
						
						//System.out.println(beta*(ml_nodes.elementAt(good.elementAt(z).intValue()-W)).numLeaves());
					}
					else
					{
						aedges.add(beta);
						orig_aedges.add(beta);
					}
				}
//				System.out.println(aedges);
				Vector<Double> fedges = new Vector<Double>();
				Vector<Double> orig_fedges = new Vector<Double>();
				
				double aedgesum = 0;
				for(int z = 0;z<aedges.size();z++){
					aedgesum = aedgesum + aedges.elementAt(z).doubleValue();
				}
				fedges.add(eta*aedgesum);
				orig_fedges.add(eta*aedgesum);
				for(int z = 0;z<bad.size();z++){
					if(bad.elementAt(z).intValue() >= W)
					{
						fedges.add(beta*(ml_nodes.elementAt(bad.elementAt(z).intValue()-W)).numLeaves());
						orig_fedges.add(beta*(ml_nodes.elementAt(bad.elementAt(z).intValue()-W)).numLeaves());
						
					}
					else
					{
						fedges.add(beta);
						orig_fedges.add(beta);
					}
				}
				
				Vector<DTNodes> ichildren = new Vector<DTNodes>();
				Vector<Integer> maxind = new Vector<Integer>();
				
				
				DTNodes likely_internal = new DTNodes(aedges,ichildren,maxind,0,aedgesum,orig_aedges,aedgesum);
				Vector<DTNodes> ichildrenN = new Vector<DTNodes>();
				Vector<Integer> maxindN = new Vector<Integer>();
				
				ichildrenN.add(likely_internal);
				maxindN.add(good.size()-1);
				double fedgesum = 0;
				for(int z = 0;z<fedges.size();z++){
					fedgesum = fedgesum + fedges.elementAt(z).doubleValue();
				}
				DTNodes fakeroot = new DTNodes(fedges, ichildrenN, maxindN,good.size(),fedgesum,orig_fedges,fedgesum);
				
				Vector<Integer> fake_wordmap = new Vector<Integer>();
				for(int z = 0;z<good.size();z++){
					fake_wordmap.add(good.elementAt(z).intValue());
				}
				for(int z = 0;z<bad.size();z++){
					fake_wordmap.add(bad.elementAt(z).intValue());
				}
				
				Vector<Integer> fake_leaf = new Vector<Integer>();
				for(int z = 0;z<fake_words.size();z++){
					fake_leaf.add(fake_wordmap.indexOf(fake_words.elementAt(z)));
				}
				
				variations.add(fakeroot);
				fake_leafmap.add(fake_leaf);
				variant_logweights.add(Math.log(aedgesum));
				//加上variant_logweights的初始化
				
			}
			
			Vector<MLNode> ichildrenM = new Vector<MLNode>();
			Vector<Integer> lchildren = new Vector<Integer>();
			for(int z = 0;z<clcc.elementAt(i).size();z++){
				int key = clcc.elementAt(i).elementAt(z).intValue();
				if(key >= W)
					ichildrenM.add(ml_nodes.elementAt(key-W));
				else lchildren.add(key);
			}
			
			Vector<Double> edges = new Vector<Double>();
			Vector<Integer> maxind = new Vector<Integer>();
			multinodes.add(new MultiNodes(edges,ichildrenM,maxind,0,lchildren,variations,variant_logweights,fake_leafmap));
			
		}
		
		int cur_ind = 0;
		Vector<Integer> wordmap = new Vector<Integer>();
		
		double edgesum = 0;

		for(int i = 0;i<multinodes.size();i++){
			MultiNodes multi = multinodes.elementAt(i);
			//System.out.println(multi.numLeaves());
			for(int j = 0;j<multi.ichildren.size();j++){
				MLNode ml_child = (MLNode)multi.ichildren.elementAt(j);
				wordmap.addAll(ml_child.words);
				ml_child.leafstart = cur_ind;
				cur_ind += ml_child.words.size();
				//System.out.println(cur_ind);
				multi.maxind.add(cur_ind-1);
			}
			
			if(multi.words.size() > 0){
				multi.leafstart = cur_ind;
				cur_ind += multi.words.size();
				
				wordmap.addAll(multi.words);
			}
			edgesum += beta*multi.numLeaves();
			root.edges.add(beta*multi.numLeaves());
			root.orig_edges.add(beta*multi.numLeaves());
			root.ichildren.add(multi);
			root.maxind.add(cur_ind-1);

		}

		for(int i =0;i<ml_nodes.size();i++){
			MLNode mln = ml_nodes.elementAt(i);
			//System.out.println(mln.leafstart);
			if(mln.leafstart == -1){
				mln.leafstart = cur_ind;
				cur_ind += mln.numLeaves();
				edgesum += beta*mln.numLeaves();
				root.edges.add(beta*mln.numLeaves());
				root.orig_edges.add(beta*mln.numLeaves());
				root.ichildren.add(mln);
				root.maxind.add(cur_ind-1);
				
				wordmap.addAll(mln.words);
			}
		}
		//System.out.println("12313 "+(W-cur_ind));
		for(int i = 0;i<W-cur_ind;i++){
			edgesum += beta;
			root.edges.add(beta);
			root.orig_edges.add(beta);
		}
		root.leafstart = cur_ind;
		root.edgesum = edgesum;
		root.orig_edgesum = edgesum;
		
		
		for(int i = 0;i<W;i++){
			if(!wordmap.contains(i)){
				wordmap.add(i);
			}
		}
		
		//root = root.tupleConvert(edges, tchildren, maxind, leafstart);
		for(int z = 0;z<W;z++){
			leafmap.add(wordmap.indexOf(z));
		}
		//System.out.println(root.edges);
		
	}
	
	public void processPairwise(Vector<Vector<Integer>> mustlinks,Vector<Vector<Integer>> cannotlinks,
			             int W,Vector<Vector<Integer>> mlcc, Vector<Vector<Integer>> clcc,
			             Vector<Vector<Vector<Integer>>> allowable){
		getML(mustlinks,mlcc);
		
//		
		HashMap<Integer,Integer> invmlcc = new HashMap<Integer,Integer>();//将must-link词映射到相应的component上
		if(mlcc.size()>0)
		for(int mli = 0;mli<mlcc.size();mli++){
			for(int wi = 0;wi<mlcc.elementAt(mli).size();wi++){
				invmlcc.put(mlcc.elementAt(mli).elementAt(wi),mli+W);
			}
		}
		
		//将词与词之间的cannot-link转换成连通分量之间的cannot-link
		Vector<Vector<Integer>> newcannotlinks = new Vector<Vector<Integer>>();
		for(int i = 0;i<cannotlinks.size();i++){
			int y1 = cannotlinks.elementAt(i).elementAt(0);
			int y2 = cannotlinks.elementAt(i).elementAt(1);
			int ny1,ny2;
			if(invmlcc.get(y1) == null)
				ny1 = y1;
			else
				ny1 = invmlcc.get(y1);
			
			if(invmlcc.get(y2) == null)
				ny2 = y2;
			else
				ny2 = invmlcc.get(y2);
			
			Vector<Integer> nc = new Vector<Integer>();
			nc.add(ny1);
			nc.add(ny2);
			newcannotlinks.add(nc);
		}

		//检查有没有冲突的cannot-link
		
		int [] conflicts = new int[W];
		int index = 0;
		for(int i = 0;i<newcannotlinks.size();i++){
			if(newcannotlinks.elementAt(i).elementAt(0).intValue() == newcannotlinks.elementAt(i).elementAt(1).intValue())
			{
				conflicts[index++] = newcannotlinks.elementAt(i).elementAt(0);
			}
		}
		
		if(index > 0){
			System.out.println("Word set Conflicts!!!!");
			System.exit(0);
		}
		
		getCL(newcannotlinks,clcc,allowable);

		
	}
	
	public void getML(Vector<Vector<Integer>> mustlinks,Vector<Vector<Integer>> mlcc){
		//Check if mustlinks is empty;
		if(mustlinks.size() == 0)
			return ;
		//将must-link中的所有词放到nodes集合中
		HashSet<Integer> nodes = new HashSet<Integer>();
		for(int i = 0;i<mustlinks.size();i++){
			for(int j = 0;j<mustlinks.elementAt(i).size();j++){
				nodes.add(mustlinks.elementAt(i).elementAt(j));
			}
		}
		
		PrefGraph PG = new PrefGraph(nodes, mustlinks);
		PG.connected_components(mlcc);
	}
	
	public void getCL(Vector<Vector<Integer>> newcannotlinks, Vector<Vector<Integer>> clcc,
            		  Vector<Vector<Vector<Integer>>> allowable){
		if(newcannotlinks.size() == 0){
			return;
		}
		//将cannot-link中的所有词放到nodes集合中
		HashSet<Integer> nodes = new HashSet<Integer>();
		for(int i = 0;i<newcannotlinks.size();i++){
			for(int j = 0;j<newcannotlinks.elementAt(i).size();j++){
				nodes.add(newcannotlinks.elementAt(i).elementAt(j));
			}
		}
		
		PrefGraph PG = new PrefGraph(nodes, newcannotlinks);
		PG.connected_components(clcc);
		
		for(int i = 0;i<clcc.size();i++){
			//System.out.println(clcc.elementAt(i));
			PrefGraph PGS = PG.subgraph(clcc.elementAt(i));
			PrefGraph PGC = PGS.complement();
			allowable.add(PGC.cliques());
		}
	}
	
	static public void main(String []arg){
		HashSet<Integer> nodes = new HashSet<Integer>();
		nodes.add(1);
		nodes.add(1);
		nodes.add(2);
		System.out.println(nodes.toString());
	}
}
