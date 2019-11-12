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
	private boolean flag; //���뿪��
	
	public void launch() {
		try {
			s = new Socket("192.168.17.109", 8000);
			in = new BufferedReader(new InputStreamReader(
					s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(
					s.getOutputStream(), "UTF-8"));			
			System.out.print("���Լ���һ������: ");
			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();

			//�����߳�
			new Thread() {
				public void run() {
					receive();
				}
			}.start();
			
			//��ӡ�߳�
			new Thread() {
				public void run() {
					print();
				}
			}.start();
			
			//�����߳�
			new Thread() {
				public void run() {
					input();
				}
			}.start();

		} catch (Exception e) {
			System.out.println("�޷����������������,���Ѵӷ������Ͽ�");
		}
	}
	
	protected void print() {
		while (true) {
			synchronized (list) {
				//������û������ʱ,��ͣ�ȴ�����
				while (list.size() == 0 || flag) {
					try {
						list.wait();
					} catch (InterruptedException e) {
					}
				}
				String msg = list.removeFirst();//�Ӽ����Ƴ�ͷ�����ݲ���ӡ
				System.out.println(msg);
			}
		}
	}

	protected void input() {
		System.out.println("���س�������������");
		
		while (true) {
			new Scanner(System.in).nextLine();//�ȴ����س�
			flag = true; //���뿪�ش�
			
			System.out.print("������������:");
			String msg = new Scanner(System.in).nextLine();
			out.println(msg);//������ln
			out.flush();
			
			flag = false;//���뿪�عر�
			synchronized (list) {
				list.notifyAll();
			}
		}
	}
	
	protected void receive() {
		//�������ҷ�������������
		try {
			String line;
			while((line = in.readLine()) != null) {
				synchronized (list) {
					list.add(line);//�ӷ��������յ�����,���뼯��
					list.notifyAll();
				}
			}
			//�Ͽ�
		} catch (Exception e) {
			//�Ͽ�
		}
		
		System.out.println("�Ѿ��������Ͽ�����");
	}
	
	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.launch();
	}
}















