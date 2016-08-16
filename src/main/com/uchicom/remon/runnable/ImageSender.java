/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.runnable;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

import com.uchicom.remon.Constants;
import com.uchicom.remon.RemonRobot;

/**
 * 画像送信処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ImageSender implements Runnable {

	private Socket socket;

	/**
	 *
	 */
	public ImageSender(Socket socket) {
		this.socket = socket;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

		try (GZIPOutputStream gos = new GZIPOutputStream(socket.getOutputStream(), true)) {
			RemonRobot robot = new RemonRobot();
			int size = screenRect.width * screenRect.height;
			int[][] image = new int[2][size];
			int[] previousImage = null;
			long base = System.currentTimeMillis();
			int index = 0;
			int maxSize = 11 + (screenRect.width * screenRect.height + 7) / 8 + screenRect.width * screenRect.height
					* 3;

			int cnt = 0;

			byte[] bytes = new byte[maxSize];
			while (!socket.isOutputShutdown()) {
				if (Constants.DEBUG)
					printLog("start", base);
				if (Constants.DEBUG)
					System.out.println("送信" + cnt++);
				//				BufferedImage bi = robot.createScreenCapture(screenRect);//90msec
				//				BufferedImage  bi = gc[0].createCompatibleImage(screenRect.width, screenRect.height);

				//				now = System.currentTimeMillis();
				//				System.out.println("d" + (now - base));
				//				bi.getRGB(0, 0, screenRect.width, screenRect.height, image[index], 0, screenRect.width);//190msec

				//				DataBufferInt buffer = (DataBufferInt) bi.getData().getDataBuffer();//22msec
				//				image[index] = buffer.getData();
				//				image[index] = robot.getPixels(screenRect);//92msec
				robot.getPixels(screenRect, image[index]);//60msec
				if (Constants.DEBUG)
					printLog("getPixels", base);
				//ここでイメージを2回チェックしてるから遅いんだな。
				//				if (!Arrays.equals(previousImage, image[index])) {//5msec
				if (Constants.DEBUG)
					printLog("Arrays.equals", base);
				if (previousImage != null) {
					int length = getByte(bytes, screenRect.width, screenRect.height, previousImage, image[index]);//50msec
					if (length == -1)
						continue;
					if (Constants.DEBUG)
						printLog("getByte2", base);
					gos.write(bytes, 0, length);
				} else {
					int length = getByte(bytes, screenRect.width, screenRect.height, image[index]);//50msec
					if (Constants.DEBUG)
						printLog("getByte1", base);
					gos.write(bytes, 0, length);
				}

				gos.flush();
				if (Constants.DEBUG)
					printLog("flush", base);
				previousImage = image[index];
				index = (++index) % 2;
				//				}
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

	private void printLog(String tag, long base) {
		//	System.out.println(tag + ":" + (System.currentTimeMillis() - base));
	}

	/**
	 * とりあえず 0.0
	 *
	 * @param baos
	 * @param width
	 * @param height
	 * @param oldImage
	 * @param newImage
	 * @return
	 */
	public static int getByte(byte[] bytes, int width, int height, int[] oldImage, int[] newImage) {
		int count = 0;
		int x = -1;
		int y = -1;
		int x2 = -1;
		int y2 = -1;

		//		System.out.println(newImage.length + ":" + width * height );
		//x,yの探査
		//y探索
		for (int i = 0; i < newImage.length; i++) {
			if (oldImage[i] != newImage[i]) {
				y = i / width;
				x = i % width;

				break;
			}
		}
		if (x == -1) {
			return -1;
		}
		x2 = x;
		y2 = y;
		//xの探索
		for (int i = y + 1; i < height; i++) {
			for (int j = 0; j < x; j++) {
				int index = i * width + j;
				if (oldImage[index] != newImage[index]) {
					x = j;
					y2 = i;
					break;
				}
			}
		}
		//height探索y2
		LAB: for (int i = height - 1; i >= y2; i--) {
			for (int j = width - 1; j >= x; j--) {
				int index = i * width + j;
				if (oldImage[index] != newImage[index]) {
					y2 = i;
					if (x2 < j) {
						x2 = j;
					}

					break LAB;
				}
			}
		}
		//width探索
		for (int i = y; i < y2; i++) {
			for (int j = width - 1; j > x2; j--) {
				int index = i * width + j;
				if (oldImage[index] != newImage[index]) {
					x2 = j;
					break;
				}
			}
		}

		int imageWidth = x2 - x + 1;
		int imageHeight = y2 - y + 1;
		int rgbIndex = 11 + (imageWidth * imageHeight + 7) / 8;
		//		int rgbIndex = 11 + ((x2 - x +1) * (y2 - y + 1) + 7) / 8;
		for (int i = 11; i < rgbIndex; i++) {
			bytes[i] = 0;
		}
		for (int i = y; i <= y2; i++) {
			for (int j = x; j <= x2; j++) {
				int index = i * width + j;
				int mapsIndex = ((i - y) * imageWidth + (j - x));
				//+1 のほうがきれい　－１は変わらない
				if (oldImage[index] != newImage[index]) {
					//ここでAlphaのチェックする
					count++;
					bytes[11 + mapsIndex / 8] |= (0x01 << (mapsIndex % 8));
					int rgb = newImage[index];
					//B
					bytes[rgbIndex++] = (byte) (0xFF & rgb);
					//G
					rgb = rgb >> 8;
					bytes[rgbIndex++] = (byte) (0xFF & rgb);
					//R
					rgb = rgb >> 8;
					bytes[rgbIndex++] = (byte) (0xFF & rgb);
				} else {
					bytes[11 + mapsIndex / 8] &= ~(0x01 << (mapsIndex % 8));
				}
			}
		}
		if (Constants.DEBUG)
			System.out.println(x + "," + y + "," + imageWidth + "," + imageHeight + "," + count);
		//
		//		for (int i = 0; i < newImage.length; i++) {
		//			if (oldImage[i] != newImage[i]) {
		//				count++;
		//				bytes[11 + i / 8] |= 0x01 <<(i % 8);
		//				int rgb = newImage[i];
		//				//B
		//				bytes[rgbIndex++] = (byte)(0xFF & rgb);
		//				//G
		//				rgb = rgb >> 8;
		//				bytes[rgbIndex++] = (byte)(0xFF & rgb);
		//				//R
		//				rgb = rgb >> 8;
		//				bytes[rgbIndex++] = (byte)(0xFF & rgb);
		//			}
		//		}
		//いまだけ
		//		x2 = width;
		//		y2 = height;
		bytes[0] = (byte) (x >> 8);
		bytes[1] = (byte) (0xFF & x);
		bytes[2] = (byte) (y >> 8);
		bytes[3] = (byte) (0xFF & y);
		bytes[4] = (byte) (imageWidth >> 8);
		bytes[5] = (byte) (0xFF & imageWidth);
		bytes[6] = (byte) (imageHeight >> 8);
		bytes[7] = (byte) (0xFF & imageHeight);
		bytes[8] = (byte) (count & 0xFF);
		bytes[9] = (byte) ((count >> 8) & 0xFF);
		bytes[10] = (byte) ((count >> 16) & 0xFF);
		return rgbIndex;
	}

	private int getByte(byte[] bytes, int width, int height, int[] newImage) {
		bytes[4] = (byte) (width >> 8);
		bytes[5] = (byte) (0xFF & width);
		bytes[6] = (byte) (height >> 8);
		bytes[7] = (byte) (0xFF & height);
		int rgbIndex = 11;
		int length = newImage.length;
		for (int i = 0; i < length; i++) {
			int rgb = newImage[i];
			//B
			bytes[rgbIndex++] = (byte) (0xFF & rgb);
			//G
			rgb = rgb >> 8;
			bytes[rgbIndex++] = (byte) (0xFF & rgb);
			//R
			rgb = rgb >> 8;
			bytes[rgbIndex++] = (byte) (0xFF & rgb);
		}
		return 11 + length * 3;
	}
	/**
	 * x,y座標の検知方法についての考察
	 * 0,0→1,0でyの探査、yが見つかったら、0,y+1→x-1,y+1→0,y+2と探査して最初に現れたyの地点よりも小さなxを探索する
	 * 。なければ最初の点がx,yとなる これなら無駄がない。 width,heightも同様に検知しないとだめだな。
	 * 下から検知してheigh探索heightと最初yのx地点の大きいほうを基準にwidth探索する
	 */
}
