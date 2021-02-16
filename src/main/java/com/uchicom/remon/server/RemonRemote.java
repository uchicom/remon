// (c) 2016 uchicom
package com.uchicom.remon.server;

import java.net.Socket;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;
import com.uchicom.remon.runnable.Sender;

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
	private boolean mono;
	private String aes;
	private String iv;

	public RemonRemote(String hostName, int port, boolean mono, String aes, String iv) {
		this.hostName = hostName;
		this.port = port;
		this.mono = mono;
		this.aes = aes;
		this.iv = iv;
	}

	public void execute() {
		try {
			Socket socket = new Socket(hostName, port);
			CommandReceiver commandReceiver = new CommandReceiver(socket);
			Thread receiver = new Thread(commandReceiver);
			receiver.start();
			Thread sender = new Thread(
					mono ? new Sender(socket, commandReceiver, aes, iv) : new ImageSender(socket, commandReceiver));
			sender.start();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
