/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.peer.RobotPeer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Robotクラスの委譲クラス.
 *
 * @author shigeki
 *
 */
public class RemonRobot {

	/** Robotクラス */
	Robot robot = new Robot();

	/** Robotクラスが委譲しているクラス */
	private RobotPeer robotPeer;

	/** 画像取得用メソッド */
	Method method;

	/**
	 * コンストラクタ.
	 *
	 * @throws AWTException
	 */
	public RemonRobot() throws AWTException {

		try {
			Field field = new Robot().getClass().getDeclaredField("peer");
			field.setAccessible(true);
			robotPeer = (RobotPeer) field.get(robot);
			method = robotPeer.getClass().getDeclaredMethod("getRGBPixels", int.class, int.class, int.class, int.class,
					int[].class);
			if (method != null) {
				method.setAccessible(true);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Robotの内部でキャプチャ処理をしているクラスから直接画像データを取得する.<br>
	 * 90msec
	 *
	 * @param rectangle
	 * @return
	 */
	public int[] getPixels(Rectangle rectangle) {
		return robotPeer.getRGBPixels(rectangle);
	}

	/**
	 * Robotの内部でキャプチャ処理をしているクラスから配列を渡して画像データを取得する.<br>
	 * 60msec
	 *
	 * @param rectangle
	 * @param val
	 */
	public void getPixels(Rectangle rectangle, int[] val) throws Exception {
			method.invoke(robotPeer, 0, 0, rectangle.width, rectangle.height, val);
	}

}
