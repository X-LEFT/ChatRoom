package cn.xcx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	private ArrayList<TongXinThread> list = new ArrayList<TongXinThread>();
	
	
	class ServerThread extends Thread {
		@Override
		public void run() {
			try {
				ServerSocket ss = new ServerSocket(8000);
				System.out.println("服务器已经在8000端口上启动");
				while (true) {
					Socket s = ss.accept();
					System.out.println("一个客户端已上线");
					TongXinThread t = new TongXinThread(s);
					t.start();
				}
			} catch (Exception e) {
				System.out.println("不能在8000端口上启动服务,或者服务异常停止");
				e.printStackTrace();
			}
		}
	}
	
	class TongXinThread extends Thread {
		private Socket s;
		BufferedReader in;
		PrintWriter out;
		private String name;

		public TongXinThread(Socket s) {
			this.s = s;
		}
		
		public void send(String msg) {
			out.println(msg);
			out.flush();
		}
		
		public void sendAll(String msg) {
			synchronized (list) {
				for (TongXinThread t : list) {
					t.send(msg);
				}
			}
		}

		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(
						s.getInputStream(), "UTF-8"));
				out = new PrintWriter(new OutputStreamWriter(
						s.getOutputStream(), "UTF-8"));
				
				//接收昵称, 发送欢迎信息, 添加到集合, 群发上线消息
				this.name = in.readLine();
				send(name+",欢迎进入激情聊天室");
				synchronized (list) {
					list.add(this);
				}
				sendAll(name+"进入了聊天室, 在线: "+list.size());
				
				String line;
				while((line = in.readLine()) != null) {
					sendAll(name+"说: "+line);
				}
				//断开
			} catch (Exception e) {
				//断开
			}
			
			synchronized (list) {
				list.remove(this);
			}
			sendAll(name+"离开了聊天室, 在线: "+list.size());
		}
	}

	public void launch() {
		new ServerThread().start();
	}
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.launch();
	}
}
















