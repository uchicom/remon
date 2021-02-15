/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.uchicom.remon.Constants;
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
	private ConcurrentHashMap<String, Queue<Socket>> sendQueueMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Queue<Socket>> receiveQueueMap = new ConcurrentHashMap<>();

	private Map<String, String> ipMap = new HashMap<>();

	/**
	 *
	 */
	public RemonThrough(String hostName, int receivePort, int sendPort, String ip) {
		this.hostName = hostName;
		this.receivePort = receivePort;
		this.sendPort = sendPort;
		if (ip != null && ip.length() > 0) {
			Arrays.stream(ip.split(",")).forEach(ipPair -> {
				String[] ips = ipPair.split("-");
				ipMap.put(ips[0], ips[1]);
			});
		}
	}

	public void execute() {

		try (final ServerSocket receiveServer = new ServerSocket();
				final ServerSocket sendServer = new ServerSocket()) {

			sendServer.bind(new InetSocketAddress(hostName, sendPort));
			receiveServer.bind(new InetSocketAddress(hostName, receivePort));

			Thread sendThread = new Thread(() -> {
				while (true) {
					try {
						Socket socket = sendServer.accept();
						accept(sendQueueMap, socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			sendThread.setDaemon(true);
			sendThread.start();
			Thread receiveThread = new Thread(() -> {
				while (true) {
					try {
						Socket socket = receiveServer.accept();
						accept(receiveQueueMap, socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			receiveThread.setDaemon(true);
			receiveThread.start();
			// キューチェック処理
			while (true) {
				try {
					ipMap.forEach((key, value) -> {
						if (!sendQueueMap.containsKey(key)) {
							return;
						}
						if (!receiveQueueMap.containsKey(value)) {
							return;
						}
						Queue<Socket> sendQueue = sendQueueMap.get(key);
						if (sendQueue.peek() == null) {
							return;
						}
						Queue<Socket> receiveQueue = receiveQueueMap.get(value);
						if (receiveQueue.peek() == null) {
							return;
						}
						start(receiveQueue.poll(), sendQueue.poll());
					});
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(sendQueueMap);
			close(receiveQueueMap);
		}
	}

	/**
	 * 通信開始処理.
	 * 
	 * @param receiveSocket 受信ソケット
	 * @param sendSocket 送信ソケット
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

	/**
	 * ソケットの保持処理.
	 * 
	 * @param socketQueueMap ソケットキューマップ
	 * @param socket         ソケット
	 */
	public static void accept(ConcurrentHashMap<String, Queue<Socket>> socketQueueMap, Socket socket) {
		String hostAddress = socket.getInetAddress().getHostAddress();
		Queue<Socket> socketQueue = null;
		if (socketQueueMap.containsKey(hostAddress)) {
			socketQueue = socketQueueMap.get(hostAddress);
		} else {
			socketQueue = new ArrayBlockingQueue<>(16);
			socketQueueMap.put(hostAddress, socketQueue);
		}
		socketQueue.add(socket);
	}

	/**
	 * 保持ソケットのクローズ処理.
	 * 
	 * @param socketQueueMap ソケットキューマップ
	 */
	public static void close(ConcurrentHashMap<String, Queue<Socket>> socketQueueMap) {
		socketQueueMap.forEach((ip, socketQueue) -> socketQueue.forEach(socket -> {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
	}
}
