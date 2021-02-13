/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;
import com.uchicom.remon.runnable.Sender;

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
	private boolean mono;
	private String aes;
	private String iv;

	/**
	 *
	 */
	public RemonServer(String hostName, int port, boolean mono, String aes, String iv) {
		this.hostName = hostName;
		this.port = port;
		this.mono = mono;
		this.aes = aes;
		this.iv = iv;
	}

	public void execute() {
		try (ServerSocket server = new ServerSocket()) {

			server.bind(new InetSocketAddress(hostName, port));
			print();
			while (true) {
				Socket socket = server.accept();

				print();
				CommandReceiver receiver = new CommandReceiver(socket);
				Thread receiverT = new Thread(receiver);
				receiverT.setDaemon(true);
				receiverT.start();
				Thread sender = new Thread(
						mono ? new Sender(socket, receiver, aes, iv) : new ImageSender(socket, receiver));
				sender.setDaemon(true);
				sender.start();

			}
		} catch (Exception e) {
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
