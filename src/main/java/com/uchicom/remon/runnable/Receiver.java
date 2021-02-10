/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.uchicom.remon.service.EncryptionService;
import com.uchicom.remon.strategy.Analysis;
import com.uchicom.remon.type.AnalysisStrategy;
import com.uchicom.remon.util.ImagePanel;

/**
 * PNG画像受信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Receiver extends ImageReceiver {

	/** ソケット */
	private Socket socket;

	/** パネル */
	private ImagePanel panel;

	private String aes;

	private EncryptionService encryptionService;

	private boolean stoped;
	
	private BufferedImage image;

	/**
	 *
	 * @param socket
	 * @param client
	 */
	public Receiver(Socket socket, ImagePanel panel, String aes) {
		super(socket, panel);
		this.socket = socket;
		this.panel = panel;
		this.aes = aes;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try (InputStream is = (socket.getInputStream())) {

			int nowStrategy = -1;
			Analysis analysis = null;

			byte[] infoBytes = new byte[12];
			int infoBytesLength = infoBytes.length;
			byte[] imageBytes = new byte[5 * 1024 * 1024];
			while (!stoped && !socket.isInputShutdown()) {
				setByte(is, infoBytes, infoBytesLength);
				
				int length = bytesToInt(infoBytes, 0, 4);

				int x = bytesToInt(infoBytes, 4, 2);
				int y = bytesToInt(infoBytes, 6, 2);
				int strategy = bytesToInt(infoBytes, 8, 2);
				int option = bytesToInt(infoBytes, 10, 2);
				
				if (nowStrategy != strategy) {
					nowStrategy = strategy;
					analysis = AnalysisStrategy.getAnalysis(strategy);
				}
				imageBytes = setByteAuto(is, imageBytes, length);
				if (aes != null) {
					imageBytes = encryptionService.encrypt(imageBytes);
				}
				analysis.setImagePanel(panel);
				analysis.refrectImage(x, y, option, imageBytes, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private byte[] setByteAuto(InputStream is, byte[] bytes, int length) throws IOException {
		if (bytes.length < length) {
			bytes = new byte[length];
		}
		setByte(is, bytes, length);
		return bytes;
	}
	private void setByte(InputStream is, byte[] bytes, int length) throws IOException {
		int index = 0;
		while (index < length) {
			index += is.read(bytes, index, length - index);
		}
	}
	private int bytesToInt(byte[] bytes, int offset, int length) {
		int value = 0;
		for (int i = 0; i < length; i++) {
			value |= (bytes[offset + i] & 0xFF) << (8 * i);
		}
		return value;
	}
	public boolean isStoped() {
		return stoped;
	}

	public void setStoped(boolean stoped) {
		this.stoped = stoped;
	}
}
