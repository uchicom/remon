/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.GZIPInputStream;

import com.uchicom.remon.Constants;
import com.uchicom.remon.util.ImagePanel;

/**
 * 画像受信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ImageReceiver implements Runnable {

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
	public ImageReceiver(Socket socket, ImagePanel panel) {
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
		byte[] bytes = new byte[11];
		byte[] maps = null;
		int[] data = null;
		byte[] rgbs = null;
		int width = 0;
		int height = 0;
		boolean first = true;
		try (GZIPInputStream gis = new GZIPInputStream(socket.getInputStream())) {
			while (!stoped && !socket.isInputShutdown()) {
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
				width = ((bytes[4] & 0xFF) << 8) | (bytes[5] & 0xFF);
				height = ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
				int count = ((bytes[8] & 0xFF) |
						((bytes[9] & 0xFF) << 8) |
						((bytes[10] & 0xFF) << 16));
				if (Constants.DEBUG) System.out.println(x + "," + y + "," + width + "," + height + "," + count);
				int dataLength = width * height;
				int mapsLength = (dataLength + 7) / 8;
				if (first) {
					data = new int[dataLength];
					maps = new byte[mapsLength];
					rgbs = new byte[width * height * 3];
					panel.init(width, height);
					image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				} else {
					index = 0;
					length = 0;
					while (index < mapsLength) {
						length = gis.read(maps, index, mapsLength - index);
						if (length > 0) {
							index += length;
						}
					}

					transfer += index;
				}
				index = 0;
				length = 0;
//				data = new int[width * height];

				int rgbsLength = 0;
				if (count <= 0) {
//					rgbs = new byte[width * height * 3];
					rgbsLength = width * height * 3;
				} else {
//					rgbs = new byte[count * 3];
					rgbsLength = count * 3;
				}
				//RGB取得
				while (index < rgbsLength) {
					length = gis.read(rgbs, index, rgbsLength - index);
					if (length > 0) {
						index += length;
					}
				}

				transfer += index;
				//RGB再配置
				int rgbIndex = 0;
				if (first) {
					first = false;
					for (int i = 0; i < width * height ; i++) {
						//sabunn
						int rgb = (rgbs[rgbIndex++] & 0xFF);
						rgb |= (rgbs[rgbIndex++] & 0xFF) << 8;
						rgb |= (rgbs[rgbIndex++] & 0xFF) << 16;
						rgb |= 0xFF000000;
						data[i] = rgb;
					}
				} else {
					for (int i = 0; i < width * height ; i++) {
						if ((maps[ i / 8] & ( 0x01 << (i % 8))) != 0) {
							//sabunn
							int rgb = (rgbs[rgbIndex++] & 0xFF);
							rgb |= (rgbs[rgbIndex++] & 0xFF) << 8;
							rgb |= (rgbs[rgbIndex++] & 0xFF) << 16;
							rgb |= 0xFF000000;
							data[i] = rgb;
						} else {
							data[i] = 0;
						}
					}
				}
//				panel.setImage(x, y, width, height, data, 0, width);
//				panel.setImage(createImage(data, width, height), x, y, transfer);
				setImage(x, y, width, height, data, transfer);
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
