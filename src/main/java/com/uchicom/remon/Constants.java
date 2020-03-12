/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

/**
 * 定数クラス
 *
 * @author shigeki
 *
 */
public class Constants {

	/** デバッグフラグ */
	public static final boolean DEBUG = false;


	// キーボード操作
	public static final int COMMAND_KEY_FLAG = 100;
	
	public static final int COMMAND_KEY_PRESS = 0;
	public static final int COMMAND_KEY_RELEASE = 1;
	// マウス操作
	public static final int COMMAND_MOUSE_FLAG = 200;
	
	public static final int COMMAND_MOUSE_MOVE= 2;
	public static final int COMMAND_MOUSE_PRESS= 3;
	public static final int COMMAND_MOUSE_RELEASE = 4;
	public static final int COMMAND_MOUSE_WHEEL = 5;
	// 画像の種類
	/** モノクロ画像 */
	public static final int COMMAND_IMAGE_KIND = 6;
	/** グレースケール画像 */
//	public static final int COMMAND_IMAGE_GRAYSCALE = 7;
//	/** カラー画像 */
//	public static final int COMMAND_IMAGE_COLOR = 8;
	
	// 送信種別
	/** 差分送信 */
	public static final int COMMAND_SEND_KIND = 9;
	/** 全送信 */
//	public static final int COMMAND_SEND_ALL = 10;
	
	// 抽出種別
	/** 差分抽出 */
	public static final int COMMAND_EXTRACT_KIND = 11;
	/** 全抽出 */
//	public static final int COMMAND_EXTRACT_ALL = 12;
	
	public static final int COMMAND_DELAY = 13;
}
