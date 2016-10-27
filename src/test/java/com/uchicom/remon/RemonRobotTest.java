/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;

import org.junit.Test;

/**
 * 動作確認用クラス.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class RemonRobotTest {

	/**
	 *
	 */
	public RemonRobotTest() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Test
	public void captureThread() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		RemonRobot robot;
		int[][] results = new int[100][screenRect.width * screenRect.height];
		Capture[] t = new Capture[results.length];
		try {
			robot = new RemonRobot();
			for (int i = 0; i < results.length; i++) {
				t[i] = new Capture(screenRect, robot, results[i], i);
			}
			for (int i = 0; i < results.length; i++) {
				t[i].start();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			try {
				for (Capture thread : t) {
					thread.join();
					System.out.println(thread.index + ":" + thread.res);
				}
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			//			for (int j = 0; j < results.length ; j++) {
			//				for (int i = 0; i < results.length; i++) {
			//					System.out.println(i + ":" + Arrays.equals(results[j], results[i]));
			//				}
			//			}
		} catch (AWTException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Test
	public void capture() {
		long[] res = new long[100];
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		int[][] results = new int[100][screenRect.width * screenRect.height];
		RemonRobot robot;
		try {
			robot = new RemonRobot();
			for (int i = 0; i < 100; i++) {
				long start = System.currentTimeMillis();
				robot.getPixels(screenRect, results[i]);
				res[i] = (System.currentTimeMillis() - start);
			}
			for (long result : res) {
				System.out.println(result);
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private class Capture extends Thread {
		RemonRobot robot;
		Rectangle rectangle;
		int[] result;
		public int index;
		public long res;

		public Capture(Rectangle rectangle, RemonRobot robot, int[] result, int index) {
			this.rectangle = rectangle;
			this.robot = robot;
			this.result = result;
			this.index = index;
		}

		public void run() {
			try {
				long start = System.currentTimeMillis();
				System.out.println(index + " start:" + start);
				robot.getPixels(rectangle, result);
				res = (System.currentTimeMillis() - start);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}
}
