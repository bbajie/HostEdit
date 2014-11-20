package qmyxi.com.host.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.util.Log;

/**
 * 
 * 运行执行命令封装类 结果在ResultHolder类里
 * 
 */
public class RootRuntime {
	private final static String TAG = "RootRuntime";

	/** 最高的su 命令 **/
	public final static String USER_PERMISSION_SU = "su";

	/**
	 * 需要root 的命令 <br>
	 * 绑定命令：mount -o bind destDir(映射挂载的dest位置) srcDir(需要绑定的src位置) <br>
	 * 
	 * **/
	public final static String MOUNT_BIND = "mount -o bind "; // 绑定映射挂载
	/**
	 * 需要root 的命令 <br>
	 * 解除映射命令: umount srcDir(被映射的原路径)<br>
	 * **/
	public final static String UNMOUNT_BIND = "umount "; // 解绑挂载

	/**
	 * 修改某目录下所有的文件和目录为 777 <br>
	 * 后面加文件路径
	 **/
	public final static String CHMOD_R_777 = "chmod -R 777 ";

	/**
	 * 修改单个文件或是目录为 777 <br>
	 * 后面加文件路径
	 **/
	public final static String CHMOD_777 = "chmod 777 ";

	/**
	 * 关闭app应用 <br>
	 * 后面加包名
	 **/
	public final static String KILL_APP_PROCESS = "service call activity 79 s16 ";

	/** apk 的安装命令 **/
	public static final String APK_INSTALL = "pm install -r ";
	public static final String APK_UNINSTALL = "pm uninstall ";

	/** 递归移除  文件或是文件夹**/
	public static final String RM_R = "rm -R ";

	public static final String MKDIR = "mkdir ";

	/**
	 * 执行命令，完毕会退出exit并返回
	 * 
	 * @param cmd
	 *            运行的命令组
	 * @param resultListener
	 *            成功与否的回调监听器
	 * 
	 * @return
	 */
	public static void execCmd(List<String> cmd, ResultListener resultListener) {
		if(cmd == null)
			return;
		String[] cmds = new String[cmd.size()];
		for(int i=0;i<cmd.size();i++){
			cmds[i] = cmd.get(i);
		}
		execCmd(cmds,resultListener);
	}
	
	public static void execCmd(String cmd[], ResultListener resultListener) {

		if (cmd == null) {
			return;
		}

		int resultCode = -1;
		DataOutputStream os = null;
		InputStream err = null;
		InputStream succ = null;
		StringBuffer resultError = new StringBuffer();
		StringBuffer resultSuccSB = new StringBuffer();

		boolean isSucess = false;

		try {
			Process process = Runtime.getRuntime().exec(cmd[0]);
			err = process.getErrorStream();
			succ = process.getInputStream();

			BufferedReader errorBr = new BufferedReader(new InputStreamReader(
					err), 1024 * 2);
			BufferedReader sucBr = new BufferedReader(new InputStreamReader(
					succ), 1024 * 2);

			os = new DataOutputStream(process.getOutputStream());

			if (cmd.length > 0) {
				for (int i = 1; i < cmd.length; i++) {

					os.writeBytes(cmd[i] + "\n");

				}
			}
			os.writeBytes("exit\n");
			os.flush();

			String resp = null;
			while ((resp = errorBr.readLine()) != null) {
				resultError.append(resp);
				Log.v(TAG, resp);
			}

			while ((resp = sucBr.readLine()) != null) {
				resultSuccSB.append(resp + "\n");
				Log.v(TAG, resp);
			}
			resultCode = process.waitFor();
			int exitValue = process.exitValue();
			if(resultError.toString().contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")){
				isSucess = false;
			}
			else if (exitValue==0&&resultCode == 0 &&( resultSuccSB.toString().equals("")
					||resultSuccSB.toString().equals("Success"))) {
				
				isSucess = true;
			} else {
				Log.e(TAG, cmd + " exec with result" + resultCode);
			}

			os.close();
			process.destroy();
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
			}
			try {
				if (succ != null) {
					succ.close();
				}
			} catch (IOException e) {
			}
			try {
				if (err != null) {
					err.close();
				}
			} catch (IOException e) {
			}
			
			System.out.println("success cmd:" + resultSuccSB.toString());
			System.out.println("error cmd:" + resultError.toString());
			if (resultListener != null) {
				if (isSucess) {
					resultListener.onResult(ResultListener.RESULT_CODE_SUCCESS,
							resultSuccSB.toString());
				} else {
					resultListener.onResult(ResultListener.RESULT_CODE_ERROR,
							resultError.toString());
				}
			}
		}
	}

	/**
	 * 命令 的信息事件回调
	 * 
	 * @author Chen Qiang
	 * 
	 */
	public static interface ResultListener {
		public static final int RESULT_CODE_ERROR = -1;
		public static final int RESULT_CODE_SUCCESS = 1;

		/**
		 * 
		 * @param resultCode
		 *            是否成功代码
		 * @param resultInfo
		 *            结果信息
		 */
		public void onResult(int resultCode, String resultInfo);
	}

}
