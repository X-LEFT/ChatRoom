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
				System.out.println("�������Ѿ���8000�˿�������");
				while (true) {
					Socket s = ss.accept();
					System.out.println("һ���ͻ���������");
					TongXinThread t = new TongXinThread(s);
					t.start();
				}
			} catch (Exception e) {
				System.out.println("������8000�˿�����������,���߷����쳣ֹͣ");
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
				
				//�����ǳ�, ���ͻ�ӭ��Ϣ, ��ӵ�����, Ⱥ��������Ϣ
				this.name = in.readLine();
				send(name+",��ӭ���뼤��������");
				synchronized (list) {
					list.add(this);
				}
				sendAll(name+"������������, ����: "+list.size());
				
				String line;
				while((line = in.readLine()) != null) {
					sendAll(name+"˵: "+line);
				}
				//�Ͽ�
			} catch (Exception e) {
				//�Ͽ�
			}
			
			synchronized (list) {
				list.remove(this);
			}
			sendAll(name+"�뿪��������, ����: "+list.size());
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
















