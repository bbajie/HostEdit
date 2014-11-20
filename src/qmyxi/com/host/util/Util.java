package qmyxi.com.host.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import qmyxi.com.host.entity.HostEntity;
import qmyxi.com.host.util.RootRuntime.ResultListener;

public class Util {
	public static String hostFilePath = "/etc/hosts";

	public static String getHostText() {
		try {
			FileInputStream fos = new FileInputStream(hostFilePath);
			byte[] buf = new byte[1024];

			int len = -1;
			StringBuffer outStr = new StringBuffer();
			while ((len = fos.read(buf, 0, buf.length)) != -1) {
				outStr.append(new String(buf, 0, len));
			}

			if (outStr.toString() != null) {
				return outStr.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static List<HostEntity> getHost() {

		List<HostEntity> hostEnList = new ArrayList<HostEntity>();

		String outStr = getHostText();
		if (outStr != null) {
			String[] hostArr = outStr.toString().split("\n");
			if (hostArr != null) {
				for (int j = 0; j < hostArr.length; j++) {

					String hostTemp = hostArr[j];
					if (hostTemp.startsWith("#")) {
						continue;
					}

					String[] hostTempArr = hostTemp.split(" ");
					if (hostTempArr != null && hostTempArr.length > 1) {
						int index = 0;
						HostEntity hostEntity = new HostEntity();
						for (int i = 0; i < hostTempArr.length; i++) {

							if ("".equals(hostTempArr[i])) {
								continue;
							}

							switch (index) {
							case 0:
								hostEntity.setIp(hostTempArr[i]);
								break;
							case 1:
								hostEntity.setUrl(hostTempArr[i]);
								break;
							case 2:
								String desc = hostTempArr[i];
								hostEntity.setDesc(hostTempArr[i]);
								break;
							default:
								break;
							}
							index++;
						}
						if (hostEntity.getIp() != null
								&& hostEntity.getUrl() != null) {
							hostEntity.id = j;
							hostEnList.add(hostEntity);
						}
					}
				}
			}
		}

		return hostEnList;
	}

	/**
	 * 
	 * @param beforEntity
	 * @param changeEntity
	 *            改变后的
	 */
	public static void editHost(final HostEntity beforEntity,
			final HostEntity changeEntity) {

		RootRuntime.execCmd(new String[] { RootRuntime.USER_PERMISSION_SU,
				RootRuntime.CHMOD_777 + hostFilePath }, new ResultListener() {

			@Override
			public void onResult(int resultCode, String resultInfo) {
				if (resultCode == ResultListener.RESULT_CODE_SUCCESS) {
					// modify the hosts-file

					String afterText = changeEntity.getIp() + " "
							+ changeEntity.getUrl();
					String beforText = beforEntity.getIp() + " "
							+ beforEntity.getUrl();

					String hostText = getHostText();
					if (!"".equals(hostText)) {
						hostText = hostText.replace(beforText, afterText);
					}
					writeHost(hostText);
				}
			}
		});

	}

	public static void writeHost(String text) {
		try {
			FileOutputStream fos = new FileOutputStream(hostFilePath);
			fos.write(text.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addHost(HostEntity entity) {
		String hostText = getHostText();
		hostText = hostText + "\n" + entity.getIp() + " " + entity.getUrl()
				+ " #" + entity.getDesc() + "\n";
		writeHost(hostText);
	}

	public static void delHost(List<HostEntity> entityList) {
		if (entityList != null && entityList.size() > 0) {
			String hostText = getHostText();
			for (HostEntity hostEntity : entityList) {

				String target = hostEntity.ip + " "
						+ hostEntity.getUrl() + " " + (hostEntity.getDesc() == null ? ""
						: hostEntity.getDesc()) + "\n";
				hostText = hostText.replace(target, "");
			}
			
			writeHost(hostText);
		}
	}
}
