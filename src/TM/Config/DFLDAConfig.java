package TM.Config;

public class DFLDAConfig {
	public static boolean dirichletforest = false;
	public static boolean as_lda = true;
	//must-link
	public static boolean setopic = true;
	
	//最后输出文件
	public static String finalWordDes = "text5.txt";
	
	//must-link 文件
	public static String seedPath = "seed.txt";

	
	//LDA参数
	public static int topic = 16;//主题数
	public static double alphasum = 200;//LDA
	public static double beta = 0.1;//LDA
	public static double _alpha = 200 / topic;//as-lda
	public static double _beta = 0.1;//df-lda
	public static double eta = 100;//df-lda
	public static double soft_eta = 1;//soft-constraint
	
}
