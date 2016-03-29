/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.uchicom.remon.Constants;
import com.uchicom.remon.RemonClient;

/**
 * 画像受信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ImageReceiver implements Runnable {

	/** ソケット */
	private Socket socket;

	/** クライアント */
	private RemonClient client;

	/**
	 *
	 * @param socket
	 * @param client
	 */
	public ImageReceiver(Socket socket, RemonClient client) {
		this.socket = socket;
		this.client = client;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		byte[] bytes = new byte[11];
		byte[] maps = null;
		int[] image = null;
		byte[] rgbs = null;
		int width = 0;
		int height = 0;
		try (InputStream gis = (socket.getInputStream())) {
			while (!socket.isInputShutdown()) {
				long transfer = 0;
				int index = 0;
				int length = 0;
				while (index < bytes.length) {
					length = gis.read(bytes, index, bytes.length - index);
					if (length > 0) {
						index+= length;
					}
				}
				transfer += index;
				int x = ((bytes[0] & 0xFF) << 8) |  (bytes[1] & 0xFF);
				int y = ((bytes[2] & 0xFF) << 8) |  (bytes[3] & 0xFF);
				if (width == 0 && height == 0) {
					width = ((bytes[4] & 0xFF) << 8) | (bytes[5] & 0xFF);
					height = ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
				}else {
					width = ((bytes[4] & 0xFF) << 8) | (bytes[5] & 0xFF);
					height = ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
				}
				int count = ((bytes[8] & 0xFF) |
						((bytes[9] & 0xFF) << 8) |
						((bytes[10] & 0xFF) << 16));
				if (Constants.DEBUG) System.out.println(x + "," + y + "," + width + "," + height + "," + count);
				if (image != null) {
					maps = new byte[(width * height + 7) / 8];

					index = 0;
					length = 0;
					while (index < maps.length) {
						length = gis.read(maps, index, maps.length - index);
						if (length > 0) {
							index += length;
						}
					}

					transfer += index;
				}
				index = 0;
				length = 0;
				image = new int[width * height];

				if (count <= 0) {
					rgbs = new byte[width * height * 3];
				} else {
					rgbs = new byte[count * 3];
				}
				//RGB取得
				while (index < rgbs.length) {
					length = gis.read(rgbs, index, rgbs.length - index);
					if (length > 0) {
						index += length;
					}
				}

				transfer += index;
				//RGB再配置
				int rgbIndex = 0;
				if (maps == null) {
					for (int i = 0; i < width * height ; i++) {
						//sabunn
						int rgb = (rgbs[rgbIndex++] & 0xFF);
						rgb |= (rgbs[rgbIndex++] & 0xFF) << 8;
						rgb |= (rgbs[rgbIndex++] & 0xFF) << 16;
						rgb |= 0xFF000000;
						image[i] = rgb;
					}
				} else {
					for (int i = 0; i < width * height ; i++) {
						if ((maps[ i / 8] & ( 0x01 << (i % 8))) != 0) {
							//sabunn
							int rgb = (rgbs[rgbIndex++] & 0xFF);
							rgb |= (rgbs[rgbIndex++] & 0xFF) << 8;
							rgb |= (rgbs[rgbIndex++] & 0xFF) << 16;
							rgb |= 0xFF000000;
							image[i] = rgb;
						} else {
							image[i] = 0;
						}
					}
				}
				client.setImage(createImage(image, width, height), x, y, transfer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static BufferedImage createImage(int[] data,int width,int height){
//		long base = System.currentTimeMillis();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0,  0,  width, height, data, 0,  width);

//		System.out.println((System.currentTimeMillis() - base) +"[msec]");
		return image;
	}
}
