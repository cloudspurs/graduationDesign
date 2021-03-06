import com.opensymphony.xwork2.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class signinAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String password;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String execute() throws Exception {
		
		// 获取Hibernate配置，通过SessionFactory获取Session来开启事物
		Configuration conf = new Configuration().configure();
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
			.applySettings(conf.getProperties()).build();
		SessionFactory sf = conf.buildSessionFactory(serviceRegistry);
		Session sess = sf.openSession();
		Transaction tx = sess.beginTransaction();
		
		// 查询用户信息
		Query result = sess.createQuery("from Users as u where u.email = :uemail");
		
		// 从网页获取用户名
		result.setParameter("uemail", getEmail());
		
		// 查询结果为空（用户不存在），提示错误信息
		if (result.list().isEmpty()) {
			return "error";
		}
		
		// 用户存在，从列表获取第一个用户信息
		Object list = result.list().get(0);
		
		// 关闭处理
		tx.commit(); 
		sess.close();
		sf.close();
		
		// 转换为Users类型
		Users root = (Users)list;
		
		// 判断密码是否正确
		if (root.getPassword().equals(getPassword())) {
			
			ActionContext.getContext().getSession().put("emailSigned",  root.getEmail());
			return "success";
		}
		else {
			return "error";
		}				
	}
}
