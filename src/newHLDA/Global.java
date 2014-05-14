package newHLDA;

import java.util.ArrayList;

import newHLDA.item.HldaTopic;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;

public class Global {
	public static Randoms random = new Randoms();
//	public static double gamma = 1.0;
	public static double eta = 5.0;
	public static ArrayList<Double> etaList = new ArrayList<Double>();
	public static double m = 0.25;
	public static double pi = 500;
	public static double mpi = m * pi;
	public static double alpha = 10.0;
	
	public static int outputWordNum = 10;
	public static InstanceList instances;
	public static HldaTopic rootNode;
	public static int totalNodes = 0;
	public static int numLevels = 3;
	public static int vocalSize = 0;
	
	static{
		for(int i = 0 ; i < numLevels; i++){
			etaList.add(eta);
		}
	}
	
	/* -----------------------------------------------------------*/
	//输出计算的叶子节点
	public static boolean leafNodeWeightPrint = false;
	//初始化路径的形式，是否开启单一路径的初始化
	public static boolean singlePathInit = false;
	//测试文档的路径
	public static boolean vertifyPath = true;
	//主题分布是否给予GEM分布
	public static boolean usingGEM = true;
	//每迭代N次显示一次当前的主题分布
	public static int displayNum = 10;
	//每迭代N次输出一次当前的主题分布至本地文件
	public static int writeFileNum = 10;
	//只显示单词
	public final static int displayWord = 0;
	//显示每个单词的数目
	public final static int displayWordNum = 1;
	//显示每个单词的主题权值
	public final static int displayWordWeight = 2;
	public static int cuurentDisplayCONFIG = displayWord;
	//输出的时候，某些节点以剪枝的形式不输出, not complete
//	public static boolean pruneOutput = false;
	
	/* -----------------------------------------------------------*/
	 //超参数采样的一些设置
	 public static int SAMPLE_HYPER_ITER = 20;
	 public static int updateNum = 100;
	 public static double ETASTDEV = 0.005;
	 
	 public static double SCALING_SHAPE = 1.0;
	 public static double SCALING_SCALE = 0.5;
	 
	 public static boolean SAMPLE_ETA = true;
	 public static boolean SAMPLE_GAMMA = true;
	 public static boolean SAMPLE_GEM = true;
	 public static double GEM_MEAN_STDEV = 0.05;
	 public static double GEM_STDEV = 0.05;	 
	 
	/* -----------------------------------------------------------*/
	 
	 //测试层次，是否适合增加一层，not complete
	 public static boolean NEWLEVEL_DETECT = false;
	
	 //初始化路径约束
	 public static boolean PATH_INIT_CONSTRAINT = false;
	 //采样路径约束
	 public static boolean PATH_SAMPLING_CONSTRAINT = false;
	 //对于没有关键字的文档是否约束
	 //为true的时候，分配整个树，false的时候，分配那些为约束的节点
	 public static boolean NOKEY_DOCUMENT_PATH_SAMPLING_CONSTRAINT = true;
	 //对于包含多个Node的处理的方法
	 //0为分配到候选的那些节点中，1为分配至未约束的节点，2为全部节点中
	 public static int MULTI_NODE_CONSTRAINT = 0;
	 public static int MULTI_NODE_NO_CONSTRAINT = 1;
	 public static int MULTI_NODE_ALL = 2;
	 
	 /* -------------------------------------------------------------------------*/
	 //非单字进行约束
	 public static boolean MULTI_WORD_BOTTOM = false; 	 	
	 //单字比例
	 public static boolean SINGLE_WORD_TOP_RATIO_CONSTRAINT = false;
	 public static double SINGLE_WORD_TOP_RATIO = 0.25;
	 //对于短文本来说，这样的效果比较好
	 public static int MULTI_NODE_METHOD = MULTI_NODE_CONSTRAINT;
	 
	 /* -------------------------------------------------------------------------*/
	 
	 public static boolean FUNTION_WORD_TOP = false;
	 public static double FUNTION_WORD_TOP_RATIO = 0.015;
	 public static boolean NONFUNTION_WORD_BOTTOM = false;
	 public static double NONFUNTOPM_WORD_BOTTOM_RATIO = 0.01; 
}
