package DirichletForest;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;

import TM.Config.DFLDAConfig;

import DFLDA.SeedWords;

import util.File_util;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class DirichletForest {
	//public static int T = 0;//主题数目
	public static int W = 0;//tag数目
	//public static double [] alpha = new double[T];//alpha参数
	
	public static Vector<Vector<Integer>> mustlinks = new Vector<Vector<Integer>>();
	public static Vector<Vector<Integer>> cannotlinks = new Vector<Vector<Integer>>();
	public static Vector<Vector<Integer>> mlcc = new Vector<Vector<Integer>>();;
	public static Vector<Vector<Integer>> clcc = new Vector<Vector<Integer>>();;
	public static Vector<Vector<Vector<Integer>>> allowable = new Vector<Vector<Vector<Integer>>>();

	public DirichletForest(int K,int numTypes,InstanceList instances){
		int [][] w  =new int[K][];
		
		//将must-link变成数字的形式
		for(int rindex = 0 ; rindex < SeedWords.seedWordsList.size(); rindex++){
			 int [] w0  = new int[SeedWords.seedWordsList.get(rindex).size()];
			 w[rindex] = new int[SeedWords.seedWordsList.get(rindex).size()];
			 int index = 0;
			 for(String tag: SeedWords.seedWordsList.get(rindex)){
				 w0[index] = instances.getDataAlphabet().lookupIndex(tag);
				 w[rindex][index] = instances.getDataAlphabet().lookupIndex(tag);
				 index++;
			 }
			//构建must-link
			 merge(w0);
		 }	 
		 //构建cannot-link
		 for(int i = 0 ; i < w.length;i++){
			 if(w[i]==null){
				 w[i] = new int[0];
			 }
		 }
		 split(w);
		 this.W = numTypes;
		 
	}
	
	//输入是一个一维数组
	public  void merge(int [] w0){
		for(int i = 0; i < w0.length-1;i++){
			Vector<Integer> newML = new Vector<Integer>();
			newML.add(w0[i]);
			newML.add(w0[i+1]);
			mustlinks.add(newML);
		}
	}
	//输入是一个二维的数组
	public  void split(int [][] wset){
		for(int i = 0;i<wset.length;i++){
			for(int j = i+1;j<wset.length;j++){
				if(wset[i].length!=0){
					Vector<Integer> newCL = new Vector<Integer>();
					newCL.add(wset[i][0]);
					newCL.add(wset[j][0]);
					cannotlinks.add(newCL);
				}
			}
		}

	}
	
	//建立dirichlet树
	public  void compile(DTNodes root,Vector<Integer> leafmap,int num, double beta ,double eta){
		ConstraintCompiler cc = new ConstraintCompiler();
		mlcc = new Vector<Vector<Integer>>();;
		clcc = new Vector<Vector<Integer>>();;
		allowable = new Vector<Vector<Vector<Integer>>>();

		cc.processPairwise(mustlinks, cannotlinks, W, mlcc, clcc, allowable);
		cc.buildTree(mlcc, clcc, allowable, W, beta, eta,root,leafmap);
		System.out.println("successful!"+num);
	}
	
}
