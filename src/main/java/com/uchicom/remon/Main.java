// (c) 2016 uchicom
package com.uchicom.remon;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import com.uchicom.remon.client.RemonClient;
import com.uchicom.remon.server.RemonRemote;
import com.uchicom.remon.server.RemonServer;
import com.uchicom.remon.server.RemonThrough;

/**
 * メイン処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean server = false;
		boolean remote = false;
		boolean through = false;
		boolean mono = false;
		String aes = null;
		String iv = null;
		String ip = null;
		String host = null;
		int port = 10000;
		int sendPort = 10000;
		int receivePort = 10001;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-server":
				server = true;
				break;
			case "-remote":
				remote = true;
				break;
			case "-through":
				through = true;
				break;
			case "-host":
				if (++i < args.length) {
					host = args[i];
				} else {
					System.err.println("host error");
				}
				break;
			case "-port":
				if (++i < args.length) {
					port = Integer.parseInt(args[i]);
				} else {
					System.err.println("port error");
				}
				break;
			case "-sendPort":
				if (++i < args.length) {
					sendPort = Integer.parseInt(args[i]);
				} else {
					System.err.println("port error");
				}
				break;
			case "-receivePort":
				if (++i < args.length) {
					receivePort = Integer.parseInt(args[i]);
				} else {
					System.err.println("port error");
				}
				break;
			case "-mono":
				mono = true;
				break;
			case "-aes":
				if (++i < args.length) {
					aes = args[i];
					if (aes.length() != 16) {
						throw new RuntimeException("aes length is not 16.");
					}
				} else {
					System.err.println("aes error");
				}
				break;
			case "-iv":
				if (++i < args.length) {
					iv = args[i];
					if (iv.length() != 16) {
						throw new RuntimeException("iv length is not 16.");
					}
				} else {
					System.err.println("iv error");
				}
				break;
			case "-ip":
				if (++i < args.length) {
					ip = args[i];
				} else {
					System.err.println("ip error");
				}
				break;
			}
		}
		if (server) {
			RemonServer remonServer = new RemonServer(host, port, mono, aes, iv);
			remonServer.execute();
		} else if (remote) {
			RemonRemote remonRemote = new RemonRemote(host, port, mono, aes, iv);
			remonRemote.execute();
		} else if (through) {
			RemonThrough remonThrough = new RemonThrough(host, receivePort, sendPort, ip);
			remonThrough.execute();
		} else {
			final boolean mono2 = mono;
			final String aes2 = aes;
			final String iv2 = iv;
			SwingUtilities.invokeLater(() -> {
				RemonClient client = new RemonClient(mono2, aes2, iv2);
				client.setPreferredSize(new Dimension(320, 320));
				client.pack();
				client.setVisible(true);
			});
		}
	}

}
