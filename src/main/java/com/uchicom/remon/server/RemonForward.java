// (c) 2021 uchicom
package com.uchicom.remon.server;

import java.io.IOException;
import java.net.Socket;

import com.uchicom.remon.Constants;
import com.uchicom.remon.runnable.Througher;

/**
 * 転送サーバ\.<br/>
 * 転送先-転送サーバ-経由サーバ-ローカルクライアント構成で使用する.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonForward {

	private String hostName;
	private int port;
	private String forwardHostName;
	private int forwardPort;

	/**
	 *
	 */
	public RemonForward(String hostName, int port, String forwardHostName, int forwardPort) {
		this.hostName = hostName;
		this.port = port;
		this.forwardHostName = forwardHostName;
		this.forwardPort = forwardPort;
	}

	public void execute() {
		try (final Socket socket = new Socket(hostName, port);
				final Socket forwardSocket = new Socket(forwardHostName, forwardPort)) {
			start(socket, forwardSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通信開始処理.
	 * 
	 * @param receiveSocket 受信ソケット
	 * @param sendSocket    送信ソケット
	 */
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
