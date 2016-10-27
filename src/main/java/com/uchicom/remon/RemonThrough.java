/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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

	/**
	 *
	 */
	public RemonThrough(String hostName, int receivePort, int sendPort) {
		this.hostName = hostName;
		this.receivePort = receivePort;
		this.sendPort = sendPort;
	}

	public void execute() {
		ServerSocket receiveServer;
		ServerSocket sendServer;
		try {
			sendServer = new ServerSocket();
			sendServer.bind(new InetSocketAddress(hostName, sendPort));
			receiveServer = new ServerSocket();
			receiveServer.bind(new InetSocketAddress(hostName, receivePort));
			Socket sendSocket = null;
			Socket receiveSocket = null;
			while (true) {
				if (sendSocket == null || sendSocket.isClosed()) {
					sendSocket = sendServer.accept();
					if (Constants.DEBUG) System.out.println("send接続");
					if (receiveSocket != null && receiveSocket.isConnected()) {
						start(receiveSocket, sendSocket);
					}
				} else if (receiveSocket == null || receiveSocket.isClosed()) {
					receiveSocket = receiveServer.accept();
					if (Constants.DEBUG) System.out.println("receive接続");
					if (sendSocket != null && sendSocket.isConnected()) {
						start(receiveSocket, sendSocket);
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(Socket receiveSocket, Socket sendSocket) {

		Thread remoteThrougher = new Thread(new Througher(receiveSocket, sendSocket));
		remoteThrougher.setDaemon(true);
		remoteThrougher.start();
		if (Constants.DEBUG) System.out.println("send起動");

		Thread localThrougher = new Thread(new Througher(sendSocket, receiveSocket));
		localThrougher.setDaemon(true);
		localThrougher.start();
		if (Constants.DEBUG) System.out.println("receive起動");
	}
}
