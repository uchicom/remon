// (c) 2016 uchicom
package com.uchicom.remon.client.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.remon.client.RemonClient;

/**
 * フルスクリーンアクション.<br/>
 * 実装途中.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class FullScreenAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RemonClient client;

	/**
	 * @param name
	 */
	public FullScreenAction(RemonClient client) {
		super("全画面表示");
		this.client = client;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		client.fullScreen();
	}
}
