package server;
public class userInfo {
	
	private String email;	// 用户邮箱
	private String file;	// 待分析的文件

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public userInfo() {};
	
	public userInfo(String email, String file) {
		this.email = email;
		this.file = file;
	}

}
