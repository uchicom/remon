/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
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

	private boolean key;

	private boolean mouse;

	private Integer imageKind;
	private int sendKind;
	private int extractKind;
	private int delay;

	/**
	 *
	 */
	public CommandReceiver(Socket socket) {
		this.socket = socket;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try (InputStream is = socket.getInputStream()) {
			Robot robot = new Robot();
			byte[] bytes = new byte[3];
			while (!socket.isInputShutdown()) {
				// この部分をバイトストリームに変える。コマンドは一バイト目の文字列で判別。
				// ここでコマンドを復元
				switch (is.read()) {
				case Constants.COMMAND_KEY_FLAG:
					key = getBoolean(is.read());
					break;
				case Constants.COMMAND_KEY_PRESS:
					is.read(bytes, 0, 3);
					if (key) {
						int keyCode = getInt(bytes);
						if (keyCode == KeyEvent.VK_UNDERSCORE) {
							Toolkit kit = Toolkit.getDefaultToolkit();
							Clipboard clip = kit.getSystemClipboard();

							StringSelection ss = new StringSelection("_");
							clip.setContents(ss, ss);
							robot.keyPress(KeyEvent.VK_V | KeyEvent.CTRL_DOWN_MASK);
							robot.keyRelease(KeyEvent.VK_V | KeyEvent.CTRL_DOWN_MASK);
						} else {
							robot.keyPress(keyCode);
						}
					}
					break;
				case Constants.COMMAND_KEY_RELEASE:
					is.read(bytes, 0, 3);
					if (key) {
						robot.keyRelease(getInt(bytes));
					}
					break;
				case Constants.COMMAND_MOUSE_FLAG:
					mouse = getBoolean(is.read());
					break;
				case Constants.COMMAND_MOUSE_MOVE:
					is.read(bytes, 0, 3);
					int x = getInt(bytes);
					is.read(bytes, 0, 3);
					int y = getInt(bytes);
					if (mouse) {
						robot.mouseMove(x, y);
					}
					break;
				case Constants.COMMAND_MOUSE_PRESS:
					is.read(bytes, 0, 3);
					int max = getInt(bytes);
					is.read(bytes, 0, 3);
					int modifier = getInt(bytes);
					if (mouse) {
						for (int i = 0; i < max; i++) {
							robot.mousePress(modifier);
						}
					}
					break;
				case Constants.COMMAND_MOUSE_RELEASE:
					is.read(bytes, 0, 3);
					if (mouse) {
						robot.mouseRelease(getInt(bytes));
					}
					break;
				case Constants.COMMAND_MOUSE_WHEEL:
					is.read(bytes, 0, 3);
					if (mouse) {
						robot.mouseWheel(getInt(bytes));
					}
					break;
				case Constants.COMMAND_IMAGE_KIND:
					is.read(bytes, 0, 3);
					imageKind = getInt(bytes);
					break;
				case Constants.COMMAND_SEND_KIND:
					is.read(bytes, 0, 3);
					sendKind = getInt(bytes);
					break;
				case Constants.COMMAND_EXTRACT_KIND:
					is.read(bytes, 0, 3);
					extractKind = getInt(bytes);
					break;
				case Constants.COMMAND_DELAY:
					is.read(bytes, 0, 3);
					delay = getInt(bytes);
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
		return ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16));
	}

	public boolean getBoolean(int value) {
		return value != 0;
	}

	public Integer getImageKind() {
		return imageKind;
	}

	public int getDelay() {
		return delay;
	}

	public int getSendKind() {
		return sendKind;
	}

	public int getExtractKind() {
		return extractKind;
	}
}
