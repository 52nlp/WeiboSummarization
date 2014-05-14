package DirichletForest;

import java.util.*;

public class PrefGraph {
    public HashMap<Integer,HashSet<Integer>> adj = new HashMap<Integer,HashSet<Integer>>();
    
    public PrefGraph(HashSet<Integer> nodes, Vector<Vector<Integer>> mustlinks){
    	Iterator<Integer> node = nodes.iterator();
    	while(node.hasNext()){
    		HashSet<Integer> hs = new HashSet<Integer>();
    		int n = node.next().intValue(); 
    		for(int i = 0;i<mustlinks.size();i++){   			
    			if(mustlinks.elementAt(i).contains(n) == true)
    				if(mustlinks.elementAt(i).elementAt(0)==n)
    					hs.add(mustlinks.elementAt(i).elementAt(1));
    				else hs.add(mustlinks.elementAt(i).elementAt(0));
    		}
    		adj.put(n, hs);
    		//System.out.println(n+" "+adj.get(n));
    	}
    }
    
    public void connected_components(Vector<Vector<Integer>> cc){
    	HashSet<Integer> remaining = new HashSet<Integer>();
    	HashSet<Integer> visited = new HashSet<Integer>();
    	//将adj的key放入remaining数组中
    	Iterator<Map.Entry<Integer,HashSet<Integer>>> re = adj.entrySet().iterator();
    	while(re.hasNext()){
    		remaining.add(re.next().getKey());
    	}
    	//寻找连通分量
    	Iterator<Integer> rem = remaining.iterator();
    	while(rem.hasNext()){
    		HashSet<Integer> nbd = new HashSet<Integer>();
    		int key = rem.next().intValue();
    		nbd.add(key);
    		remaining.remove(key);
    		Iterator<Integer> nb = nbd.iterator();
    		while(nb.hasNext()){
    			int cur = nb.next();
    			
    			nbd.remove(cur);
    			visited.add(cur);
    			nbd.addAll(adj.get(cur));
    			nbd.removeAll(visited);
    			nb = nbd.iterator();
    		}
    		Vector<Integer> vis = new Vector<Integer>();
    		Iterator<Integer> vi = visited.iterator();
    		while(vi.hasNext()){
    			vis.add(vi.next().intValue());
    		}
    		cc.add(vis);
    		remaining.removeAll(visited);
    		visited.clear();
    		rem = remaining.iterator();
    	}
    	
    }
    
    PrefGraph subgraph(Vector<Integer> cc){
    	HashSet<Integer> hcc = new HashSet<Integer>();
    	Vector<Vector<Integer>> links = new Vector<Vector<Integer>>();
		for(int i = 0;i<cc.size();i++){
			hcc.add(cc.elementAt(i).intValue());
		}
    	for(int i = 0;i<cc.size();i++){
    		int key = cc.elementAt(i).intValue();
    		HashSet<Integer> wordset = adj.get(key);
//    		System.out.println(adj);
    			Iterator<Integer> ws = wordset.iterator();
    			while(ws.hasNext()){
    				int value = ws.next().intValue();
    				if(cc.contains(value) == true){
    					Vector<Integer> link  = new Vector<Integer>();
    					link.add(key);
    					link.add(value);
    					links.add(link);
    				}
    			}
    	}
    	
    	PrefGraph PGS = new PrefGraph(hcc,links);
    	return PGS;
    }
    
    PrefGraph complement(){
    	//将hashmap中的词放入nodes
    	HashSet<Integer> nodes = new HashSet<Integer>();
    	Vector<Integer> vnode = new Vector<Integer>();
    	Vector<Vector<Integer>> links = new Vector<Vector<Integer>>();
    	Iterator<Map.Entry<Integer,HashSet<Integer>>> re = adj.entrySet().iterator();
    	while(re.hasNext()){
    		int key = re.next().getKey();
    		nodes.add(key);
    		vnode.add(key);
    	}
    	
    	for(int i = 0;i<vnode.size();i++){
    		for(int j = 0;j<vnode.size();j++){
    			if(vnode.elementAt(j).intValue() != vnode.elementAt(i).intValue()&&!adj.get(vnode.elementAt(i).intValue()).contains(vnode.elementAt(j).intValue())){
    				Vector<Integer> link  = new Vector<Integer>();
					link.add(vnode.elementAt(i).intValue());
					link.add(vnode.elementAt(j).intValue());
					links.add(link);
    			}
    		}
    	}
//    	System.out.println(links.size());
//    	for(int i = 0;i<links.size();i++){
//			System.out.println(links.elementAt(i));
//		}
    	PrefGraph PGC = new PrefGraph(nodes,links);
    	return PGC;
    }
    
    Vector<Vector<Integer>> cliques(){
    	Vector<Integer> notlist = new Vector<Integer>();
    	Vector<Integer> compsub = new Vector<Integer>();
    	Vector<Integer> candlist = new Vector<Integer>();
    	Iterator<Map.Entry<Integer,HashSet<Integer>>> re = adj.entrySet().iterator();
    	while(re.hasNext()){
    		candlist.add(re.next().getKey());
    	}
    	return _extend(compsub,candlist,notlist);
    }
    
    Vector<Vector<Integer>> _extend(Vector<Integer> compsub, Vector<Integer> candlist, Vector<Integer> notlist){
    	
    	if(candlist.isEmpty()&&notlist.isEmpty()){
    		Vector<Vector<Integer>> Comp = new Vector<Vector<Integer>>();
    		Comp.add(compsub);
    		//
    		return Comp;
    	}
    	else{
    		for(int i = 0;i<notlist.size();i++){
    			if(connToAll(notlist.elementAt(i).intValue(),candlist))
    			{
    				//返回一个空的cliques
    				Vector<Vector<Integer>> cliques = new Vector<Vector<Integer>>();
    				return cliques;
    			}
    				
    		}
    	}
    	
    	Vector<Vector<Integer>> cliques = new Vector<Vector<Integer>>();
    	
    	int fixpt = get_fixpt(candlist, notlist);
    	int len = candlist.size();
    	for(int i = 0;i<len;i++){
    		int cand;
//    		System.out.println(i+" " + candlist);
    		if(candlist.contains(fixpt)){
    			cand = fixpt;
    			candlist.remove(candlist.indexOf(fixpt));
    		}
    		else{
    			Vector<Integer> disconnected_candidates = new Vector<Integer>();
    			
    			for(int j = 0;j<candlist.size();j++){
    				if(!connected(candlist.elementAt(j).intValue(),fixpt))
    					disconnected_candidates.add(candlist.elementAt(j).intValue());
    			}
    			
    			if(!disconnected_candidates.isEmpty()){
    				cand = disconnected_candidates.elementAt(0).intValue();
    				candlist.remove(candlist.indexOf(cand));
    			}
    			else return cliques;
    		}
    		
    		Vector<Integer> r_compsub = new Vector<Integer>();
    		r_compsub.addAll(compsub);
    		r_compsub.add(cand);
    		//System.out.println(compsub);
    		Vector<Integer> r_candlist = new Vector<Integer>();
    		for(int j = 0;j<candlist.size();j++){
    			if(connected(candlist.elementAt(j).intValue(),cand))
    				r_candlist.add(candlist.elementAt(j).intValue());
    		}
    		
    		Vector<Integer> r_notlist = new Vector<Integer>();
    		for(int j = 0;j<candlist.size();j++){
    			if(connected(candlist.elementAt(j).intValue(),cand))
    				r_notlist.add(candlist.elementAt(j).intValue());
    		}
    		
    		cliques.addAll(_extend(r_compsub,r_candlist,r_notlist));
    		
    		//System.out.println(cliques);
    		notlist.add(cand);
    		
    	}
    	
    	
    	return cliques;
    }
    
    int get_fixpt(Vector<Integer> candlist,Vector<Integer> notlist){
    	Vector<Integer> cscores = new Vector<Integer>();
    	Vector<Integer> nscores = new Vector<Integer>();
    	
    	for(int i = 0;i<candlist.size();i++){
    		int len = 0;
    		for(int j = 0;j<candlist.size();j++){
    			if(!connected(candlist.elementAt(i).intValue(),candlist.elementAt(j).intValue()))
    				len++;
    		}
    		cscores.add(len);
    	}
    	for(int i = 0;i<notlist.size();i++){
    		int len = 0;
    		for(int j = 0;j<candlist.size();j++){
    			if(!connected(notlist.elementAt(i).intValue(),candlist.elementAt(j).intValue()))
    				len++;
    		}
    		cscores.add(len-1);
    	}
    	
    	int min = cscores.elementAt(0).intValue();
    	int minnum = candlist.elementAt(0).intValue();
    	for(int i = 1;i<cscores.size()+nscores.size();i++){
    		if(i<cscores.size()){
    			if(cscores.elementAt(i).intValue()<min)
    				min = cscores.elementAt(i).intValue();
    				minnum = candlist.elementAt(i).intValue();
    		}
    		else{
    			if(nscores.elementAt(i).intValue()<min)
    				min = nscores.elementAt(i).intValue();
    				minnum = notlist.elementAt(i).intValue();
    		}
    	}
    	
    	return minnum;
    }
    
    
    boolean connToAll(int node, Vector<Integer> rest){
    	if(rest.isEmpty()){
    		return true;
    	}
    	else{
    		for(int i = 0;i<rest.size();i++){
    			if(!connected(node,rest.elementAt(i).intValue()))
    				return false;
    		}
    		return true;
    	}
    	
    }
    
    boolean connected(int a,int b)
    {
    	if(adj.get(a).contains(b) == true || a == b)
    		return true;
    	else return false;
    }
    
}
