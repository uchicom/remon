/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import com.uchicom.remon.util.ImagePanel;

/**
 * 画像受信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class MonoReceiver extends ImageReceiver {

	/** ソケット */
	private Socket socket;

	/** パネル */
	private ImagePanel panel;

	private boolean stoped;
	
	private BufferedImage image;

	/**
	 *
	 * @param socket
	 * @param client
	 */
	public MonoReceiver(Socket socket, ImagePanel panel) {
		super(socket, panel);
		this.socket = socket;
		this.panel = panel;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try (InputStream gis = (socket.getInputStream())) {

			System.out.println("start:");
			while (!stoped && !socket.isInputShutdown()) {
				// 4byte
				long transfer = 0;
				byte[] countBytes = new byte[4];
				gis.read(countBytes);
				
				int length = ((countBytes[0] & 0xFF) |
						((countBytes[1] & 0xFF) << 8) |
						((countBytes[2] & 0xFF) << 16)|
						((countBytes[3] & 0xFF) << 24));
				transfer = length;
				byte[] imageBytes = new byte[length];
				int index = 0;
				int length2 = 0;
				while (index < length) {
					length2 = gis.read(imageBytes, index, length - index);
					index += length2;
				}
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
//				BufferedImage image = ImageIO.read(gis);
				if (image == null) {
					continue;
				}
				panel.setImage(image, 0, 0, transfer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void setImage(int x, int y, int w, int h, int[] data, long transfer) {
		image.setRGB(x, y, w, h, data, 0,  w);
		panel.setImage(image, 0, 0, transfer);
	}
	public static BufferedImage createImage(int[] data,int width,int height){
//		long base = System.currentTimeMillis();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0,  0,  width, height, data, 0,  width);

//		System.out.println((System.currentTimeMillis() - base) +"[msec]");
		return image;
	}

	public boolean isStoped() {
		return stoped;
	}

	public void setStoped(boolean stoped) {
		this.stoped = stoped;
	}
}
