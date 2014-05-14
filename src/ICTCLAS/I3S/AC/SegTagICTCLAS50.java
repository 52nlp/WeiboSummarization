package ICTCLAS.I3S.AC;

import java.io.UnsupportedEncodingException;

public class SegTagICTCLAS50 {
	public static ICTCLAS50 myICTCLAS50 = new ICTCLAS50();
	static
	{
		try{
			String argu = "TopicModelData/";
			//初始化
			if (myICTCLAS50.ICTCLAS_Init(argu.getBytes("UTF-8")) == false)
			{
				System.out.println("Init Fail!");
			}
			
			//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			myICTCLAS50.ICTCLAS_SetPOSmap(1);

			//导入用户字典
			String usrdir = "TopicModelData/IAR_Dict/userdict.txt"; //用户字典路径
			byte[] usrdirb = usrdir.getBytes();//将string转化为byte类型
			//导入用户字典,返回导入用户词语个数第一个参数为用户字典路径，第二个参数为用户字典的编码类型
			myICTCLAS50.ICTCLAS_ImportUserDictFile(usrdirb, 0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}						
	}
	
	public static String segTag(String str)
	{
		byte nativeBytes[];
		try {
			nativeBytes = myICTCLAS50.ICTCLAS_ParagraphProcess(str.getBytes("GB2312"), 2, 1);
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
		
			//nativeStr = nativeStr.replace("","");	
			replace(nativeStr);
			return nativeStr;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return "";
	}
	
	
	public static void replace(String nativeStr){
//		if(nativeStr.indexOf("救援/d")!=-1){
//			System.err.println("err");
//		}
		nativeStr = nativeStr.replace("救援/d", "救援/n");
		nativeStr = nativeStr.replace("救援/un", "救援/n");
		nativeStr = nativeStr.replace("救援/c", "救援/n");
		if(nativeStr.indexOf("救援/d")!=-1){
			System.err.println("err");
		}
//		nativeStr = nativeStr.replace("\n", "\n/w ");
		
		nativeStr = nativeStr.replace("价格便宜/n", "价格/n 便宜/a");
		nativeStr = nativeStr.replace("点卡/n", "点/m 卡/v");
		nativeStr = nativeStr.replace("电池容量/n", "电池/n 容量/n");	
		nativeStr = nativeStr.replace("产品质量/n", "产品/n 质量/n");
		nativeStr = nativeStr.replace("分辨率高/n", "分辨率/n 高/a");
		nativeStr = nativeStr.replace("价格比较/n", "价格/n 比较/d");
		nativeStr = nativeStr.replace("触摸屏/n", "触摸/v 屏/n");
		nativeStr = nativeStr.replace("电容屏/n", "电容/n 屏/n");
		nativeStr = nativeStr.replace("电池电量/n", "电池/n 电量/n");
		nativeStr = nativeStr.replace("电池寿命/n", "电池/n 寿命/n");
		nativeStr = nativeStr.replace("非常高/n", "非常/d 高/a");
		nativeStr = nativeStr.replace("最高/a", "最/d 高/a");
		nativeStr = nativeStr.replace("1/a 天/q", "一/m 天/q");
		nativeStr = nativeStr.replace("2/a 天/q", "二/m 天/q");
		nativeStr = nativeStr.replace("3/a 天/q", "三/m 天/q");
		nativeStr = nativeStr.replace("值得/v", "值/v 得/u");
		nativeStr = nativeStr.replace("按键/n", "按/v 键/n");
		nativeStr = nativeStr.replace("在黑屏/n", "在/p 黑/a 屏/n");
		nativeStr = nativeStr.replace("开屏/v", "开/v 屏/n");
		nativeStr = nativeStr.replace("高清屏/n", "高清/a 屏/n");
		nativeStr = nativeStr.replace("高/a 分屏/n", "高分/a 屏/n");
		nativeStr = nativeStr.replace("除屏幕/n", "除/p 屏幕/n");
		nativeStr = nativeStr.replace("话音质量/n", "话音/n 质量/n");
		nativeStr = nativeStr.replace("质量上乘/n", "质量/n 上乘/a");
		nativeStr = nativeStr.replace("对/p 焦/n", "对焦/n");
		nativeStr = nativeStr.replace("低分辨率/n", "低/a 分辨率/n");
		nativeStr = nativeStr.replace("分辨率较/n", "分辨率/n 较/d");
		nativeStr = nativeStr.replace("待机状态/n", "待机/n 状态/n");
		nativeStr = nativeStr.replace("待/p 机长/n", "待机/n 长/a");
		nativeStr = nativeStr.replace("待/p 机长/n", "待机/n 长/a");
		nativeStr = nativeStr.replace("显/v 示好/un", "显示/n 好/a");
		nativeStr = nativeStr.replace("高清晰/n", "高/d 清晰/a");
		nativeStr = nativeStr.replace("画面清晰/n", "画面/n 清晰/a");
		
		nativeStr = nativeStr.replace("电池高/n", "电池/n 高/n");
		nativeStr = nativeStr.replace("节电池/n","节/q 电池/n");
		nativeStr = nativeStr.replace("充电电池/n","充电/v 电池/n");
		nativeStr = nativeStr.replace(" 电量耗尽/n"," 电量/n 耗尽/v");	
		nativeStr = nativeStr.replace("功能齐全/n","功能/n 齐全/a");
		nativeStr = nativeStr.replace("内/f 存/v","内存/n");
		nativeStr = nativeStr.replace("跌价/v","跌/v 价/n");
		nativeStr = nativeStr.replace("说是/v","说/v 是/v");
		nativeStr = nativeStr.replace("性价比/n 高大/a 分辨率/n","性价比/n 高/a 大/a 分辨率/n");
		nativeStr = nativeStr.replace("反应灵敏/n","反应/v 灵敏/n");
		nativeStr = nativeStr.replace("安装程序/n","安装/v 程序/n");
		nativeStr = nativeStr.replace("对/p 的/u 住/v","对的住/v");	
		nativeStr = nativeStr.replace("对/p 得/v 住/v","对得住/v");
		nativeStr = nativeStr.replace("不大/a","不/d 大/a");
		nativeStr = nativeStr.replace("不怕/v","不/d 怕/v");
		nativeStr = nativeStr.replace("好多/m 了/y","好/a 多/m 了/y");	
		nativeStr = nativeStr.replace("有/v 点/m","有点/d");
		nativeStr = nativeStr.replace("多/m 点/q","多点/n");	
		nativeStr = nativeStr.replace("客/n 服/v","客服/n");			
		nativeStr = nativeStr.replace("高/a 通/a","高通/n");	
		nativeStr = nativeStr.replace("有/v 点/q","有点/v");
		nativeStr = nativeStr.replace(" 买/v"," 买/u");
		nativeStr = nativeStr.replace(" 手机/n"," 手机/u");
		nativeStr = nativeStr.replace("发现/v","发现/u");
		nativeStr = nativeStr.replace("能/v","能/u");
		nativeStr = nativeStr.replace("掉/v","掉/u");
		nativeStr = nativeStr.replace("Android2/x ./w 2/n ", "Android2.2/n ");
		nativeStr = nativeStr.replace("高分辨率/n", "高/a 分辨率/n");
		nativeStr = nativeStr.replace("反应速度/n ", "反应/v 速度/n ");
		nativeStr = nativeStr.replace("安装软件/n", "安装/v 软件/n");
		nativeStr = nativeStr.replace("大屏幕/n ", "大/a 屏幕/n ");
		nativeStr = nativeStr.replace("蓝牙耳机/n", "蓝牙/n 耳机/n");
		nativeStr = nativeStr.replace("HTCdiamondwm6/x ./w 5/b", "HTC/n diamond/n wm6.5/n");
		nativeStr = nativeStr.replace("www/x ./w efeihu/x ./w com/x ", " www.efeihu.com/x ");
		nativeStr = nativeStr.replace("android2/x ./w 2/b", "android2.2/n");
		nativeStr = nativeStr.replace("AZ2/x ./w 3/n", "AZ2.3/x");
		nativeStr = nativeStr.replace("android2/x ./w 2/n ", "android2.2/n ");
		nativeStr = nativeStr.replace("Android2/x ./w 2/b", "Android2.2/n");
		nativeStr = nativeStr.replace("android2/x ./w 3.4/m", "android2.3.4/n");
		nativeStr = nativeStr.replace("FLASH10/x ./w 1/a ", "FLASH10.1/n ");
		nativeStr = nativeStr.replace("H/x ./w 264/m", " H.264/n");
		nativeStr = nativeStr.replace("自带软件/n ", "自/p 带/v 软件/n");
		nativeStr = nativeStr.replace("接收信号/n", "接收/v 信号/n");
		nativeStr = nativeStr.replace("功能全/n", "功能/n 全/a");
		nativeStr = nativeStr.replace("备用电池/n", "备用/v 电池/n");
		nativeStr = nativeStr.replace("电池盒/n ", "电池/n 盒/g ");
		nativeStr = nativeStr.replace("自带系统/n", "自/p 带/v 系统/n");
		nativeStr = nativeStr.replace("质量关/n", "质量/n 关/n");
		nativeStr = nativeStr.replace("发现问题/n", "发现/v 问题/n");
		nativeStr = nativeStr.replace("费/v 电/n", "费电/n");
		nativeStr = nativeStr.replace("费/n 电/n", "费电/n");
		nativeStr = nativeStr.replace("费/n 电费电/n", " 费电/n 费电/n");
		nativeStr = nativeStr.replace("价格低廉/n", "价格/n 低廉/a");
		nativeStr = nativeStr.replace("价格调整/n", "价格/n 调整/a");
		nativeStr = nativeStr.replace("绝对值/n", "绝对/d 值/a");
		nativeStr = nativeStr.replace("外/f 型/k", "外型/n");
		nativeStr = nativeStr.replace("超薄/b", "超/d 薄/a");
		nativeStr = nativeStr.replace("Z4ROOT/x", "Z4/x ROOT/x");
		nativeStr = nativeStr.replace("应/v 用/p", "应用/n");
		nativeStr = nativeStr.replace("程序运行/n", "程序/n 运行/v");
		nativeStr = nativeStr.replace("", "");

//		nativeStr = nativeStr.replace("", "");

		
		
		nativeStr = nativeStr.replace("\"/x","\"/w");	
		
	}
	public void finalize(){
		//保存用户字典
		myICTCLAS50.ICTCLAS_SaveTheUsrDic();
		//释放分词组件资源
		myICTCLAS50.ICTCLAS_Exit();
	}
	
	
	
}
