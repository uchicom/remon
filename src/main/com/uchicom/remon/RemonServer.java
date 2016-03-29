/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;

/**
 * リモートサーバ.<br/>
 * (リモートサーバ)-(ローカルクライアント)構成で使用する.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonServer {

	private String hostName;
	private int port;

	/**
	 *
	 */
	public RemonServer(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}

	public void execute() {
		ServerSocket server;
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(hostName, port));
			print();
			while (true) {
				Socket socket = server.accept();

				print();
				Thread receiver = new Thread(new CommandReceiver(socket));
				receiver.setDaemon(true);
				receiver.start();
				Thread sender = new Thread(new ImageSender(socket));
				sender.setDaemon(true);
				sender.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				System.out.println(gc[i]);
			}
		}
	}
}
