/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.uchicom.remon.Constants;

/**
 * 画像送信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Sender implements Runnable {

	private Socket socket;

	private CommandReceiver strategy;

	/**
	 *
	 */
	public Sender(Socket socket, CommandReceiver strategy) {
		this.socket = socket;
		this.strategy = strategy;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		byte[] previous = null;
		try (OutputStream os = (socket.getOutputStream())) {
			Robot robot = new Robot();

			int cnt = 0;

			byte[] infoBytes = new byte[12];
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			int[] buff = new int[d.width * d.height];
			while (!socket.isOutputShutdown()) {
				if (Constants.DEBUG)
					System.out.println("送信" + cnt++);

				// キャプチャ
				BufferedImage bi = robot.createScreenCapture(screenRect);// 90msec

				// 画像変換（白黒、グレースケール、カラー）
				ByteArrayOutputStream boas = new ByteArrayOutputStream();

				switch (strategy.getImageKind()) {
				case 0:// mono
					BufferedImage mono = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
					mono.getGraphics().drawImage(bi, 0, 0, null);
					mono.getData().getPixels(0, 0, mono.getWidth(), mono.getHeight(), buff);

					ImageIO.write(mono, "PNG", boas);
					System.out.println("mono" + mono.getType());

					break;
				case 1:// gray
					BufferedImage gray = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
					gray.getGraphics().drawImage(bi, 0, 0, null);
					gray.getData().getPixels(0, 0, gray.getWidth(), gray.getHeight(), buff);

					ImageIO.write(gray, "PNG", boas);
					System.out.println("gray" + gray.getType());
					break;
				case 2:// color
					ImageIO.write(bi, "PNG", boas);
					System.out.println("color" + bi.getType());
					break;
				}
				byte[] now = boas.toByteArray();

				// 送信判定（差分、全部）
				if (Arrays.equals(previous, now)) {
					continue;
				}
				// 画像送信
				previous = now;
				int length = now.length;
				intToBytes(infoBytes, 0, 4, length); // length
				intToBytes(infoBytes, 4, 2, 0); // x
				intToBytes(infoBytes, 6, 2, 0); // y
				intToBytes(infoBytes, 8, 2, 1); // strategy 1 png

				intToBytes(infoBytes, 10, 2, 0); // option

				os.write(infoBytes);
				os.write(now);
				os.flush();
				//遅延
				int delay = strategy.getDelay();
				if (delay > 0) {
					Thread.sleep(delay);
				}
			}
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (AWTException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void setStrategy(int strategy) {
//		this.strategy = strategy;
//	}

	private void intToBytes(byte[] bytes, int offset, int length, int value) {
		for (int i = 0; i < length; i++) {
			bytes[offset + i] = (byte) (value >> (i * 8) & 0xFF);
		}
	}
}
