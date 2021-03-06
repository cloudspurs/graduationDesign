import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class fileUploadAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static final String relda = "/home/mqg/android/relda/";
	private static final String tail = "/user.xml";
	
	private File upload;	// 上传的文件
	private String uploadFileName;		// 文件名
	private String uploadContentType;	// 文件类型

/* *******************************************************************	
	private String savePath;			// 保存路径
	public void setSavePath(String path) {
		this.savePath = path;
	}
	// 获取上传文件的保存路径
	private String getSavePath() throws Exception {
		return ServletActionContext.getServletContext().getRealPath(savePath);
	}
*********************************************************************** */
	
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public File getUpload() {
		return(this.upload);
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}
	public String getUploadContentType() {
		return(this.uploadContentType);
	}
	
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getUploadFileName() {
		return(this.uploadFileName);
	}
	
	public String execute() throws Exception {
		// 防止没选中文件直接上传
		if(getUpload() == null) {
			return "input";
		}
		
		// 新建文件夹保存上传的文件和用户信息
		uuidFolder uf = new uuidFolder();
		String folder = uf.getFolder();
		String path = relda + folder;
		
		File pathUpload = new File(path);
		if (!pathUpload.exists()) {
			pathUpload.mkdir();
		}
		
		// 生成用户信息XML文件
		File xmlFile = new File(path + tail);
		if (!xmlFile.exists()) {
			xmlFile.createNewFile();
		}
		
		// 获取当前的登录邮箱
		String emailSigned = (String)ActionContext.getContext().getSession().get("emailSigned");
		
		// 新建用户信息
		userInfo userinfo = new userInfo(emailSigned, uploadFileName);
		
		xmlAction.createXml(userinfo, xmlFile.toString());
		
		// 新建上传后的文件做为输出流，接收上传文件
		FileOutputStream fos = new FileOutputStream(path + "/" + getUploadFileName());
		// 把上传文件作为输入流
		FileInputStream fis = new FileInputStream(getUpload());
		// 设置缓冲
		byte[] buffer = new byte[1024];
		int len = 0;
		// 上传文件
		while ((len = fis.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}
		fos.close();
		fis.close();
		
		return "success";	
	}
}
