/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.AWTException;
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
public class MonoSender implements Runnable {

	private Socket socket;

	private CommandReceiver strategy;

	/**
	 *
	 */
	public MonoSender(Socket socket, CommandReceiver strategy) {
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
		try (OutputStream gos = (socket.getOutputStream())) {
			Robot robot = new Robot();

			int cnt = 0;

			while (!socket.isOutputShutdown()) {
				if (Constants.DEBUG)
					System.out.println("送信" + cnt++);
				BufferedImage bi = robot.createScreenCapture(screenRect);// 90msec
				BufferedImage mono = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				mono.getGraphics().drawImage(bi, 0, 0, null);
				ByteArrayOutputStream boas = new ByteArrayOutputStream();
				ImageIO.write(mono, "PNG", boas);
				byte[] now = boas.toByteArray();
				if (Arrays.equals(previous, now)) {
					continue;
				}
				previous = now;
				int length = now.length;
				byte[] lengthBytes = new byte[] { (byte) (length & 0xFF), (byte) (length >> 8 & 0xFF),
						(byte) (length >> 16 & 0xFF), (byte) (length >> 24 & 0xFF) };
				gos.write(lengthBytes);
				gos.write(now);
				gos.flush();
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
}
