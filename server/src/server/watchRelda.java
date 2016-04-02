package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.text.SimpleDateFormat;
import java.util.Date;

public class watchRelda extends Thread {
	
	// 检测的路径
	private final static String relda = "/home/mengqg/android/relda/";
	
	private final static String tail = "/user.xml";
	
	// Relda路径
	private final static String Relda2 = "/home/mengqg/android/servers/Relda2";
	
	// 服务器路径
	private final static String client = " mqg@121.42.139.144:/home/mqg/android/reldaResult";
	
	public void run() {
		Path path = Paths.get(relda);
		 
		try {
			watchPath(path);
		} catch(IOException | InterruptedException e) {  
	        System.err.println(e);  
	    }
	}
	
	public static void watchPath(Path path)throws IOException, InterruptedException {
		
	    WatchService watcher = FileSystems.getDefault().newWatchService();
	    
	    // 设置path路径文件观察服务（新建，修改，删除）
	    path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
	    
	    while (true) {
	    	WatchKey key = watcher.take();
	    	
	    	for (WatchEvent<?> event : key.pollEvents()) {
	    		Kind<?> kind = event.kind();
	    		
	    		if (kind == StandardWatchEventKinds.OVERFLOW){
	    			continue;
	    		}
	    		
	    		if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
	    			@SuppressWarnings("unchecked")
	    			// get the filename for the event
					WatchEvent<Path> watchEventPath = (WatchEvent<Path>)event;
	        		Path filename = watchEventPath.context();
	        		
	        		// 设置日期格式
		    		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		
		    		// 在控制台打印文件夹印创建事件
		    		System.out.printf("event: %s    filename: %s    time: %s%n"  
		                    ,kind.name(), filename, now.format(new Date())); 
		    		
	        		// 新建用户信息
	        		userInfo userinfo = new userInfo();
	        		
	        		// 在控制台打印新建的文件夹
	        		System.out.println(relda + filename.toString());
	        		
	        		boolean b = true;
	        		
	        		File f = new File(relda + filename.toString() + tail);
	        		
	        		// 在控制台打印user.xml
	        		System.out.println(f.toString());
	        		
	        		while (b) {
	        			System.out.println("while ");
	        			Thread.sleep(1000);
	        			if (f.exists()) {
	        				System.out.println("ifif");
	        				b = false;
	        			}
	        		}
	        		
	        		// 读取用户信息
	        		userinfo = xmlAction.readXml(relda + filename + tail);
	        		
	        		System.out.println("读取完xml文件");
	        		
	        		System.out.println(userinfo.getFile());
	        		System.out.println("读取完userinfo.getfile()");
	        		
	        		boolean bb = true;
	        		File ff = new File(relda + filename.toString() + "/" + userinfo.getFile());
	        		
	        		while (bb) {
	        			System.out.println("while ");
	        			Thread.sleep(1000);
	        			if (ff.exists()) {
	        				System.out.println("ifif");
	        				bb = false;
	        			}
	        		}
	        		
	        		// 在控制台打印apk文件
	        		System.out.println(userinfo.getFile());
	        		
	        		// 分析apk
	        		System.out.println("relda开始分析文件");
	        		relda(relda + filename.toString() + "/" + userinfo.getFile(),
	        				relda + filename.toString(), Relda2);
	        		System.out.println("relda分析完文件");
	        		
	        		// 返回文件夹
	        		System.out.println("开始传输分析报告");
	        		scp(filename.toString(), relda);
	        		System.out.println("分析报告传输完毕");
	        		

	    		}
	    		    		
	    		if (!key.reset()) {
	    			break;
	    		}
	    	}
	    }
	}
	
	public static void relda(String file, String result, String directory) throws InterruptedException {
		String command = "./Relda2.py -r "+ result + " "+ file;
		String cmd[] = {"/bin/sh", "-c", command};
		File dir = new File(directory);
		
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmd, null, dir);
			// 等待process执行完成
			process.waitFor();
			// 取得命令结果的输出流  
		    InputStream is = process.getInputStream();  
		    // 用一个读输出流类去读  
		    InputStreamReader isr = new InputStreamReader(is);  
		    // 用缓冲器读行  
		    BufferedReader br = new BufferedReader(isr);  
		    String line = null;  
		    while ((line = br.readLine()) != null) {  
		        System.out.println(line);  
		    }  
		    is.close();  
		    isr.close();  
		    br.close();  
		} catch (IOException e) {  
		    e.printStackTrace();  
		}
	}
		
	public static void scp(String file, String directory) throws InterruptedException {
		
		String command = "scp -r " + file + client;
		
		String cmd[] = {"/bin/sh", "-c", command};
		File dir = new File(directory);
		
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmd, null, dir);
			process.waitFor();
			// 取得命令结果的输出流  
		    InputStream is = process.getInputStream();  
		    // 用一个读输出流类去读  
		    InputStreamReader isr = new InputStreamReader(is);  
		    // 用缓冲器读行  
		    BufferedReader br = new BufferedReader(isr);  
		    String line = null;  
		    while ((line = br.readLine()) != null) {  
		        System.out.println(line);  
		    }  
		    is.close();  
		    isr.close();  
		    br.close();
		    
		} catch (IOException e) {  
		    e.printStackTrace();  
		}
	}

}
