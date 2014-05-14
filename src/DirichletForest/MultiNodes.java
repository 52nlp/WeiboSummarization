package DirichletForest;

import java.util.Vector;

public class MultiNodes extends DTNodes{
//	public Vector<Double> edges = new Vector<Double>();
//	public Vector<MLNode> ichildren = new Vector<MLNode>();
//	public Vector<Integer> maxind = new Vector<Integer>();;
//	public int leafstart;
	public Vector<Integer> words = new Vector<Integer>();
	public Vector<DTNodes> variants = new Vector<DTNodes>();
	public Vector<Double> variant_logweights = new Vector<Double>();
	public Vector<Vector<Integer>> fake_leafmap = new Vector<Vector<Integer>>();
	int y ;
	
	public MultiNodes(Vector<Double> edges, Vector<MLNode> ichildren,Vector<Integer> maxind,int leafstart,Vector<Integer> words,Vector<DTNodes> variants,Vector<Double> variant_logweights,Vector<Vector<Integer>> fake_leafmap){
		this.edges = edges;
		this.ichildren = ichildren;
		this.maxind = maxind;
		this.leafstart = leafstart;
		this.words = words;
		this.variants = variants;
		this.variant_logweights = variant_logweights;
		this.fake_leafmap = fake_leafmap;
	}
	
	public int numLeaves(){
		int n = 0;
		for(int i = 0;i<this.ichildren.size();i++){
			n = n + ((DTNodes)this.ichildren.elementAt(i)).numLeaves();
		}
		//System.out.println(this.words.size());
		return (n+this.words.size());
	}
	
	public int get_y()
	{
	  return y;
	}
	
	public double var_logweight(int given_y) 
	{
	  return variant_logweights.elementAt(given_y).doubleValue();
	}

	/**
	 * Calculate logpwz term for a given y value
	 */
	public double calc_logpwz(int given_y)
	{
	  return ((DTNodes)variants.elementAt(given_y)).calc_logpwz();
	}

	/**
	 * Set a new y value
	 */
	public void set_y(int given_y)
	{
	  y = given_y;
	}
	
	/**
	 * Return number of variants represented by this multinode
	 */
	public int num_variants()
	{
	  return variants.size();
	}

	public void report()
	{
		//System.out.println(ichildren.size());
		  for(int mi = 0; mi < ichildren.size(); mi++)
		    {
		      System.out.println(this.variant_logweights);
//		      System.out.println("Edge = "+e+"\n");
//		      System.out.println("node "+mi+",words = " +this.words +" y = "+((MLNode)ichildren.elementAt(mi)).words);
		    }
	}
	
	/**
	 * Calculate topic-word term of Gibbs sampling eqn
	 */
	public double calc_wordterm(double val, int leaf)
	{ 
	  // Is this leaf under one of our interior children?
	  double newval;
	  for(int i = 0; i < ichildren.size(); i++)
	    {
		  //System.out.println(leaf +" "+ maxind.elementAt(i));
	      if(leaf <= maxind.elementAt(i))
	        {
	          // Update value with call to appropriate variant
	          newval = ((variants.elementAt(y))).calc_wordterm(val,fake_leafmap.elementAt(y).elementAt(i));
	          //System.out.print("newval3: "+newval+" ");
	          // Pass value down to actual child
	          return (((DTNodes)(this.ichildren.elementAt(i)))).calc_wordterm(newval,leaf);
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - this.leafstart);
	  // Update value with call to appropriate variant
	  return ((variants.elementAt(y))).calc_wordterm(val,fake_leafmap.elementAt(y).elementAt(ei));
	}
    
	public double calc_wordterm1(double val, int leaf)
	{ 
	  // Is this leaf under one of our interior children?
	  double newval;
	  for(int i = 0; i < ichildren.size(); i++)
	    {
		  //System.out.println(leaf +" "+ maxind.elementAt(i));
	      if(leaf <= maxind.elementAt(i))
	        {
	          // Update value with call to appropriate variant
	          newval = ((variants.elementAt(y))).calc_wordterm1(val,fake_leafmap.elementAt(y).elementAt(i));
	          //System.out.print("=newval3: "+newval);
	          // Pass value down to actual child
	          return (((DTNodes)(this.ichildren.elementAt(i)))).calc_wordterm1(newval,leaf);
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - this.leafstart);
	  // Update value with call to appropriate variant
	  return ((variants.elementAt(y))).calc_wordterm1(val,fake_leafmap.elementAt(y).elementAt(ei));
	}
	/**
	 * Update all branch counts associated with a given leaf
	 */
	public void modify_count(double val, int leaf)
	{
	  // Is this leaf under one of our interior children?
	  for(int i = 0; i < ichildren.size(); i++)
	    {
	      if(leaf <= maxind.elementAt(i))
	        {
	          // Update all variants
	          for(int v = 0; v < variants.size(); v++)
	            {
	              ((variants.elementAt(v))).modify_count(val,fake_leafmap.elementAt(v).elementAt(i));
	            }

	          // Recursively update child cts
	          (((DTNodes)(ichildren.elementAt(i)))).modify_count(val,leaf);
	          return;
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - leafstart);  
	  // Update all variants
	  for(int v = 0; v < variants.size(); v++)
	    {
	      (variants.elementAt(v)).modify_count(val,fake_leafmap.elementAt(v).elementAt(ei));
	    }

	  return;  
	}

}
