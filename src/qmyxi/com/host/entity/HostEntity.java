package qmyxi.com.host.entity;

public class HostEntity {

	public int id;
	
	public String ip;
	public String url;
	public String desc;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return "HostEntity [ip=" + ip + ", url=" + url + ", desc=" + desc + "]";
	}
	
	
}
