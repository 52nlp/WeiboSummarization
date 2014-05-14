package DirichletForest;

import java.util.*;

import util.File_util;
import cc.mallet.util.Maths;
public class DTNodes {
	public Vector<Double> edges = new Vector<Double>();
//	public ArrayList ichildren = new ArrayList();
	public Vector ichildren = new Vector();
	public Vector<Integer> maxind = new Vector<Integer>();
	public int leafstart = 0;
	public Vector<Double> orig_edges = new Vector<Double>();
	public double edgesum = 0;
	public double orig_edgesum = 0;
	public Maths maths = new Maths();
	public File_util filewrite;
	public String desPath = "TreeStructure";
	public DTNodes(Vector<Double> edges, Vector ichildren,Vector<Integer> maxind,int leafstart,double edgesum,Vector<Double> orig_edges,double orig_edgesum){
		this.edges = edges;
		this.ichildren = ichildren;
		this.maxind = maxind;
		this.leafstart = leafstart;
		this.edgesum = edgesum;
		this.orig_edges = orig_edges;
		this.orig_edgesum = orig_edgesum;
	}
	
	public DTNodes(){
		
	}
	
	public int numLeaves(){
		int n = 0;
		//System.out.println(edges);
		for(int i = 0;i<this.ichildren.size();i++){
//			n = n+ ((DTNodes)ichildren.get(i)).numLeaves();
			n = n+ ((DTNodes)ichildren.get(i)).numLeaves();
		}
		
		return (n+this.edges.size()-this.ichildren.size());
	}
	
	public static double unif01(){
		return Math.random();
	}
	
	public static int log_mult_sample(Vector<Double> vals){
		double maxval = vals.elementAt(0).doubleValue();
		for(int vi = 0;vi<vals.size();vi++){
			if(vals.elementAt(vi).doubleValue()>maxval){
				maxval = vals.elementAt(vi).doubleValue();
			}
		}
		
		Vector<Double> newvals = new Vector<Double>();
		double normsum = 0;
		for(int vi = 0;vi<vals.size();vi++){
			newvals.add(Math.exp(vals.elementAt(vi).doubleValue()-maxval));
			normsum += Math.exp(vals.elementAt(vi).doubleValue()-maxval);
		}
		//System.out.println(newvals);
		//System.out.println(normsum);
		return mult_sample(newvals,normsum);
	}
	
	public static int mult_sample(double [] vals, double norm_sum){
		
		double rand_sample = Math.random() * norm_sum;
		double tmp_sum = 0;
		int j = 0;
		while(tmp_sum < rand_sample || j == 0) {
		    tmp_sum += vals[j];
		    j++;
		  }
		  return j - 1;
	}
	
	public static int mult_sample(Vector<Double> vals, double norm_sum){
		  double rand_sample = Math.random() * norm_sum;
		  //System.out.println(rand_sample);
		  double tmp_sum = 0;
		  int j = 0;
		  while(tmp_sum < rand_sample || j == 0) {
		    tmp_sum += vals.elementAt(j).doubleValue();
		    j++;
		  }
		  return j - 1;
	}
	
	
	public Vector<MultiNodes> get_multinodes()
	{
	  Vector<MultiNodes> multinodes = new Vector<MultiNodes>();
	  //System.out.println("child:"+ichildren.size());
	  for(int i = 0; i < ichildren.size(); i++) 
	    {
	      // 1st, is this child a multinode?
	      String childtype = ichildren.elementAt(i).getClass().getName();
	      if(childtype.indexOf("MultiNodes") != -1)
	        {
	          multinodes.add((MultiNodes)ichildren.elementAt(i));
	        }

	      // Next, recursively have children check for multinodes
	      Vector<MultiNodes> childmulti = 
	        ((DTNodes)ichildren.elementAt(i)).get_multinodes();
	      //System.out.println("childmulti:"+childmulti.size()); 
	      for(int ci = 0; ci < childmulti.size(); ci++)
	        {
	          multinodes.add((MultiNodes)childmulti.elementAt(ci));
	        }
	    }
	  return multinodes;
	}
	
	public Vector<Integer> get_yvec(){
		Vector<MultiNodes> multi = get_multinodes();
		Vector<Integer> yvals = new Vector<Integer>();
		for(int mi = 0;mi<multi.size();mi++){
			yvals.add(((MultiNodes)ichildren.elementAt(mi)).get_y());
		}
		
		return yvals;
	}
	
	/**
	 * Do y-sampling for this topic
	 */
	public void y_sampling()
	{
	  // Sample a new y-value for each multinode
	  //
	  Vector<MultiNodes> multi = get_multinodes();
	  //System.out.println(multi.size());
	  for(int mi = 0; mi < multi.size(); mi++)
	    {
	      MultiNodes mu = (MultiNodes)ichildren.elementAt(mi);
	         
	      // Get y-sampling values foreach variant
	      //
	      Vector<Double> vals = new Vector<Double>();
	      int numvar = (mu).num_variants();
	      for(int vi = 0; vi < numvar; vi++)
	        {
	          // Log-contribution of the agreeable subtree
	          // +
	          // Log-contribution of likely set weight
	          //
	          vals.add(((mu)).calc_logpwz(vi) + 
	                         ((mu)).var_logweight(vi));
	        }
	       //System.out.println(vals);
	      // Sample a new multinomial value from these log-likelihoods
	      int y = log_mult_sample(vals);
	      ((mu)).set_y(y);
	     // System.out.println("y: "+y);
	    }
	}
	
	/**
	 * Do a simple q-report on multinodes
	 */
	public void qreport()
	{
	  Vector<MultiNodes> multi = get_multinodes();
	  for(int mi = 0; mi < multi.size(); mi++)
	    {
	     System.out.println(((MultiNodes)ichildren.elementAt(mi)).get_y());
	    }
	}

	/**
	 * Do a report on multinodes
	 */
	public void report()
	{
	  Vector<MultiNodes> multi = get_multinodes();
	  //System.out.println(multi.size());
	  for(int mi = 0; mi < multi.size(); mi++)
	    {
	      double e = this.edges.elementAt(mi);
	      System.out.println("Edge = "+e+"\n");
	      System.out.println("Multinode "+mi+", y = {\n");
	      ((MultiNodes)ichildren.elementAt(mi)).report();
	      System.out.println("} (of "+((MultiNodes)ichildren.elementAt(mi)).num_variants()+" variants)\n");
	    }
	}


	/**
	 * Calculate the log of the (uncollapsed) P(w|z) expression
	 * (used for the y-sampling step)
	 */
	public double calc_logpwz()
	{ 
	  // Calculate for this node
	  double logpwz = maths.logGamma(orig_edgesum) - maths.logGamma(edgesum);
	  //System.out.print("orig_edgesum:"+orig_edgesum+" "+"edgesum:"+edgesum+" "+logpwz + " ");
	  for(int ei = 0; ei < this.edges.size(); ei++)
	    {
	      logpwz += (maths.logGamma(edges.elementAt(ei)) -
	    		  maths.logGamma(orig_edges.elementAt(ei)));
	    }
	  //System.out.print(logpwz + " ");
	  // Calculate for all interior children
	 // System.out.println(((MLNode)ichildren.elementAt(0)).maxind);
	  for(int i = 0; i < ichildren.size(); i++)
	    {
	      logpwz += ((DTNodes)this.ichildren.elementAt(i)).calc_logpwz();
	    }
	 // System.out.print(logpwz + "\n");
	  return logpwz;
	}


	/**
	 * Calculate topic-word term of Gibbs sampling eqn
	 */
	public double calc_wordterm(double val, int leaf)
	{
	  // Is this leaf under one of our interior children?
	  double newval;
	  //System.out.println(ichildren.size());
	  for(int i = 0; i < ichildren.size(); i++)
	    {
		  
	      if(leaf <= maxind.elementAt(i))
	        {
	    	  //System.out.println("leaf: "+ leaf +"maxind: "+maxind.elementAt(i));
	          // Update value
	          newval = ((double)this.edges.elementAt(i))/this.edgesum;
	          //System.out.print("newval1: "+newval+" ");
	          // Recurisively mult value by child
	          //System.out.println(this.ichildren.elementAt(i).getClass().getName());
	          if(this.ichildren.elementAt(i).getClass().getName().indexOf("MultiNodes")!=-1){
	        	  //System.out.println("enterMulti!!!!!");
	        	  //System.out.println(((MultiNodes)this.ichildren.elementAt(i)).ichildren.size());
	        	  return ((MultiNodes)(this.ichildren.elementAt(i))).calc_wordterm(newval*val,leaf);
	          }
	          else
	        	  {
	        	  //System.out.println("enterDT!!!!!");
	        	  //System.out.println(((DTNodes)this.ichildren.elementAt(i)).ichildren.size());
	        	  return ((DTNodes)(this.ichildren.elementAt(i))).calc_wordterm(newval*val,leaf);
	        	  }
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - this.leafstart);
	  // Update value
	  //System.out.println(ichildren.size()+" "+leaf+" "+this.leafstart);
	  newval = ((double)this.edges.elementAt(ei)) / this.edgesum;
	  //System.out.print("edges: "+this.edges+" ");
	  //System.out.print("newval2: "+newval+"="+(double)this.edges.elementAt(ei)+"/"+this.edgesum+"\n");
	 // if(Math.abs(edgesum-3207)>2)
	  //System.out.println(this.edges);
	  return (val * newval);
	}
	public double calc_wordterm1(double val, int leaf)
	{
	  // Is this leaf under one of our interior children?
	  double newval;
	  //System.out.println();
	  for(int i = 0; i < ichildren.size(); i++)
	    {
		  
	      if(leaf <= maxind.elementAt(i))
	        {
	    	  //System.out.println("leaf: "+ leaf +"maxind: "+maxind.elementAt(i));
	          // Update value
	          newval = ((double)this.edges.elementAt(i))/this.edgesum;
	          filewrite.write("edge:"+(double)this.edges.elementAt(i)+" edgesum:"+this.edgesum+"\n", desPath);
	          //System.out.print("newval1: "+newval+"="+(double)this.edges.elementAt(i)+"/"+this.edgesum+"\t");
	          // Recurisively mult value by child
	          //System.out.println(this.ichildren.elementAt(i).getClass().getName());
	          if(this.ichildren.elementAt(i).getClass().getName().indexOf("MultiNodes")!=-1){
	        	  //System.out.println("enterMulti!!!!!");
	        	  //System.out.println(((MultiNodes)this.ichildren.elementAt(i)).ichildren.size());
	        	  return ((MultiNodes)(this.ichildren.elementAt(i))).calc_wordterm1(newval*val,leaf);
	          }
	          else
	        	  {
	        	  //System.out.println("enterDT!!!!!");
	        	  //System.out.println(((DTNodes)this.ichildren.elementAt(i)).ichildren.size());
	        	  return ((DTNodes)(this.ichildren.elementAt(i))).calc_wordterm1(newval*val,leaf);
	        	  }
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - this.leafstart);
	  // Update value
	  //System.out.print(" "+ichildren.size()+" "+leaf+" "+this.leafstart+" ");
	  newval = ((double)this.edges.elementAt(ei)) / this.edgesum;
	  filewrite.write("edge:"+(double)this.edges.elementAt(ei)+" edgesum:"+this.edgesum+"\n", desPath);
      
	  //System.out.print("edges: "+this.edges+" ");
	  //System.out.print("newval2: "+(double)this.edges.size()+" "+(double)this.edges.elementAt(0)+" "+newval+"="+(double)this.edges.elementAt(ei)+"/"+this.edgesum+"\t");
	  //if(ei == 146&&(double)this.edges.elementAt(ei)>1500){System.out.println("\n"+this.edges);}
	  // if(Math.abs(edgesum-3207)>2)
	  //System.out.println(this.edges);
	  return (val * newval);
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
	          // Update edge and sum
	          this.edges.set(i, this.edges.elementAt(i).doubleValue()+val);
	          this.edgesum += val;
	          // Recursively update child cts
	          if(this.ichildren.elementAt(i).getClass().getName().indexOf("MultiNodes")!=-1)
	        	  ((MultiNodes)(ichildren.elementAt(i))).modify_count(val,leaf);
	          else ((DTNodes)(ichildren.elementAt(i))).modify_count(val,leaf);
	          return;
	        }
	    }

	  // No, this leaf must be one of our leaves
	  
	  // Get leaf edge index
	  int ei = ichildren.size() + (leaf - leafstart);
	  // Update edge and sum
	  this.edges.set(ei, this.edges.elementAt(ei).doubleValue()+val);
	  this.edgesum += val;
	  return;  
	}
	
//	public double gamma(double x){
//		if( x > 2 && x<= 3 )
//		{
//			 double c0 =  0.0000677106;
//			 double c1 = -0.0003442342;
//			 double c2 =  0.0015397681;
//			 double c3 = -0.0024467480;
//			 double c4 =  0.0109736958;
//			 double c5 = -0.0002109075;
//			 double c6 =  0.0742379071;
//			 double c7 =  0.0815782188;
//			 double c8 =  0.4118402518;
//			 double c9 =  0.4227843370;
//			 double c10 = 1.0000000000;
//			double temp = 0;
//			temp = temp + c0*Math.pow( x-2.0, 10.0) + c1*Math.pow( x-2.0, 9.0);
//			temp = temp + c2*Math.pow( x-2.0, 8.0) + c3*Math.pow( x-2.0 , 7.0);
//			temp = temp + c4*Math.pow( x-2.0, 6.0) + c5*Math.pow( x-2.0, 5.0 );
//			temp = temp + c6*Math.pow( x-2.0, 4.0 ) + c7*Math.pow( x-2.0, 3.0 );
//			temp = temp + c8*Math.pow( x-2.0, 2.0 ) + c9*( x-2.0) + c10;
//			return temp;
//		}
//		else if( x>0 && x<=1 )
//		{
//			return gamma( x+2 )/(x*(x+1) );
//		}
//		else if( x > 1 && x<=2 )
//		{
//			return gamma( x+1 )/x;
//		}
//		else if( x > 3 )
//		{
//			int i = 1;
//			double temp = 1;
//			while( ((x-i)>2 && (x-i) <= 3 ) == false )
//			{
//				temp = (x-i) * temp;
//				i++;
//			}
//			temp = temp*(x-i);
//			return temp*gamma( x-i);
//		}
//		else
//		{
//			return 0;
//		}
//	}
}
