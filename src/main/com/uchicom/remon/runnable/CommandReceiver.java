/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.uchicom.remon.Constants;

/**
 * マウス、キーボード送信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class CommandReceiver implements Runnable {

	private Socket socket;

	/**
	 *
	 */
	public CommandReceiver(Socket socket) {
		this.socket = socket;
	}

	/* (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try (InputStream is = socket.getInputStream()) {
			Robot robot = new Robot();
			byte[] bytes = new byte[3];
			while (!socket.isInputShutdown()) {
				// この部分をバイトストリームに変える。コマンドは一バイト目の文字列で判別。
				//ここでコマンドを復元
				switch (is.read()) {
				case Constants.COMMAND_KEY_PRESS:
					is.read(bytes, 0, 3);
					robot.keyPress(getInt(bytes));
					break;
				case Constants.COMMAND_KEY_RELEASE:
					is.read(bytes, 0, 3);
					robot.keyRelease(getInt(bytes));
					break;
				case Constants.COMMAND_MOUSE_MOVE:
					is.read(bytes, 0, 3);
					int x = getInt(bytes);
					is.read(bytes, 0, 3);
					int y = getInt(bytes);
					robot.mouseMove(x, y);
					break;
				case Constants.COMMAND_MOUSE_PRESS:
					is.read(bytes, 0, 3);
					int max = getInt(bytes);
					is.read(bytes, 0, 3);
					int modifier = getInt(bytes);
					for (int i = 0; i < max; i++) {
						robot.mousePress(modifier);
					}
					break;
				case Constants.COMMAND_MOUSE_RELEASE:
					is.read(bytes, 0, 3);
					robot.mouseRelease(getInt(bytes));
					break;
				case Constants.COMMAND_MOUSE_WHEEL:
					is.read(bytes, 0, 3);
					robot.mouseWheel(getInt(bytes));
					break;
				case Constants.COMMAND_IMAGE_1BYTE:
					//ここで画像送信を切り替える処理
					break;
				case Constants.COMMAND_IMAGE_3BYTE:
					//
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public int getInt(byte[] bytes) {
		return ((bytes[0] & 0xFF) |
				((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16));
	}
}
