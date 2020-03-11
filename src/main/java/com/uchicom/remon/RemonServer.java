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

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;
import com.uchicom.remon.runnable.MonoSender;

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
	private boolean ssl;
//	private boolean udp;
//	private boolean multicast;
	private boolean mono;

	/**
	 *
	 */
	public RemonServer(String hostName, int port, boolean ssl, boolean udp, boolean multicast, boolean mono) {
		this.hostName = hostName;
		this.port = port;
		this.ssl = ssl;
//		this.udp = udp;
//		this.multicast = multicast;
		this.mono = mono;
	}

	public void execute() {
		ServerSocket server = null;
		try {
			if (ssl) {
				SSLContext sslContext = SSLContext.getDefault();
				ServerSocketFactory ssf = sslContext.getServerSocketFactory();
				server = ssf.createServerSocket();
			} else {
				server = new ServerSocket();
			}
			server.bind(new InetSocketAddress(hostName, port));
			print();
			while (true) {
				Socket socket = server.accept();

				print();
				Thread receiver = new Thread(new CommandReceiver(socket));
				receiver.setDaemon(true);
				receiver.start();
				Thread sender = new Thread(mono ? new MonoSender(socket) : new ImageSender(socket));
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
