/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.uchicom.remon.Constants;
import com.uchicom.remon.runnable.Througher;

/**
 * 経由サーバー.<br/>
 * (リモートクライアント)-(経由サーバ)-(ローカルクライアント)構成で使用する.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonThrough {

	private String hostName;
	private int receivePort;
	private int sendPort;
	private Queue<Socket> sendQueue = new ArrayBlockingQueue<>(16);
	private Queue<Socket> receiveQueue = new ArrayBlockingQueue<>(16);

	/**
	 *
	 */
	public RemonThrough(String hostName, int receivePort, int sendPort) {
		this.hostName = hostName;
		this.receivePort = receivePort;
		this.sendPort = sendPort;
	}

	public void execute() {

		try (final ServerSocket receiveServer = new ServerSocket();
				final ServerSocket sendServer = new ServerSocket()) {

			sendServer.bind(new InetSocketAddress(hostName, sendPort));
			receiveServer.bind(new InetSocketAddress(hostName, receivePort));

			Thread sendThread = new Thread(() -> {
				while (true) {
					try {
						sendQueue.add(sendServer.accept());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			sendThread.setDaemon(true);
			sendThread.start();
			Thread receiveThread = new Thread(() -> {
				while (true) {
					try {
						receiveQueue.add(receiveServer.accept());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			receiveThread.setDaemon(true);
			receiveThread.start();
			// キューチェック処理
			while (true) {
				try {
					if (sendQueue.peek() == null) {
						Thread.sleep(500);
						continue;
					}
					if (receiveQueue.peek() == null) {
						Thread.sleep(500);
						continue;
					}
					start(receiveQueue.poll(), sendQueue.poll());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sendQueue.forEach(socket->{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			receiveQueue.forEach(socket->{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
	}

	public void start(Socket receiveSocket, Socket sendSocket) {

		Thread remoteThrougher = new Thread(new Througher(receiveSocket, sendSocket));
		remoteThrougher.setDaemon(true);
		remoteThrougher.start();
		if (Constants.DEBUG)
			System.out.println("send起動");

		Thread localThrougher = new Thread(new Througher(sendSocket, receiveSocket));
		localThrougher.setDaemon(true);
		localThrougher.start();
		if (Constants.DEBUG)
			System.out.println("receive起動");
	}
}
