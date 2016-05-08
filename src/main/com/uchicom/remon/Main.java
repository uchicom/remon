/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.Dimension;

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
		boolean ssl = false;
		boolean udp = false;
		boolean multicast = false;
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
					System.out.println("host error");
				}
				break;
			case "-port":
				if (++i < args.length) {
					port = Integer.parseInt(args[i]);
				} else {
					System.out.println("port error");
				}
				break;
			case "-sendPort":
				if (++i < args.length) {
					sendPort = Integer.parseInt(args[i]);
				} else {
					System.out.println("port error");
				}
				break;
			case "-receivePort":
				if (++i < args.length) {
					receivePort = Integer.parseInt(args[i]);
				} else {
					System.out.println("port error");
				}
				break;
			case "-ssl":
				ssl = true;
				break;
			case "-udp":
				udp = true;
				break;
			case "-multicast":
				multicast = true;
				break;

			}
		}
		if (server) {
			RemonServer remonServer = new RemonServer(host, port, ssl, udp, multicast);
			remonServer.execute();
		} else if (remote) {
			RemonRemote remonRemote = new RemonRemote(host, port);
			remonRemote.execute();
		} else if (through) {
			RemonThrough remonThrough = new RemonThrough(host, receivePort, sendPort);
			remonThrough.execute();
		} else {
			RemonClient client = new RemonClient(ssl);
			client.setPreferredSize(new Dimension(320, 320));
			client.pack();
			client.setVisible(true);
		}
	}

}
