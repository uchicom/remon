/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.net.Socket;

import com.uchicom.remon.runnable.CommandReceiver;
import com.uchicom.remon.runnable.ImageSender;
import com.uchicom.remon.runnable.MonoSender;

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

	public RemonRemote(String hostName, int port, boolean mono) {
		this.hostName = hostName;
		this.port = port;
		this.mono = mono;
	}

	public void execute() {
		try {
			Socket socket = new Socket(hostName, port);
			Thread receiver = new Thread(new CommandReceiver(socket));
			receiver.start();
			Thread sender = new Thread(mono ? new MonoSender(socket) : new ImageSender(socket));
			sender.start();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
