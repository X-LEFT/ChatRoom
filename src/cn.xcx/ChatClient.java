package cn.xcx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ChatClient {
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private String name;	
	private LinkedList<String> list = new LinkedList<String>();
	private boolean flag; //输入开关
	
	public void launch() {
		try {
			s = new Socket("192.168.17.109", 8000);
			in = new BufferedReader(new InputStreamReader(
					s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(
					s.getOutputStream(), "UTF-8"));			
			System.out.print("给自己起一个名字: ");
			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();

			//接收线程
			new Thread() {
				public void run() {
					receive();
				}
			}.start();
			
			//打印线程
			new Thread() {
				public void run() {
					print();
				}
			}.start();
			
			//输入线程
			new Thread() {
				public void run() {
					input();
				}
			}.start();

		} catch (Exception e) {
			System.out.println("无法与服务器建立连接,或已从服务器断开");
		}
	}
	
	protected void print() {
		while (true) {
			synchronized (list) {
				//集合中没有数据时,暂停等待数据
				while (list.size() == 0 || flag) {
					try {
						list.wait();
					} catch (InterruptedException e) {
					}
				}
				String msg = list.removeFirst();//从集合移出头部数据并打印
				System.out.println(msg);
			}
		}
	}

	protected void input() {
		System.out.println("按回车输入聊天内容");
		
		while (true) {
			new Scanner(System.in).nextLine();//等待按回车
			flag = true; //输入开关打开
			
			System.out.print("输入聊天内容:");
			String msg = new Scanner(System.in).nextLine();
			out.println(msg);//必须有ln
			out.flush();
			
			flag = false;//输入开关关闭
			synchronized (list) {
				list.notifyAll();
			}
		}
	}
	
	protected void receive() {
		//从聊天室服务器接收数据
		try {
			String line;
			while((line = in.readLine()) != null) {
				synchronized (list) {
					list.add(line);//从服务器接收的数据,加入集合
					list.notifyAll();
				}
			}
			//断开
		} catch (Exception e) {
			//断开
		}
		
		System.out.println("已经服务器断开连接");
	}
	
	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.launch();
	}
}















