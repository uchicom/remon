/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.net.Socket;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;

/**
 * リモートクライアント.<br/>
 * 経由サーバーと一緒に使用する.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonRemote {

	private String hostName;
	private int port;

	public RemonRemote(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}

	public void execute() {
		try {
			Socket socket = new Socket(hostName, port);
			Thread receiver = new Thread(new CommandReceiver(socket));
			receiver.start();
			Thread sender = new Thread(new ImageSender(socket));
			sender.start();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
