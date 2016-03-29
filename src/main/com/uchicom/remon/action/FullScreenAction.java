/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.action;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.remon.RemonClient;

/**
 * フルスクリーンアクション.<br/>
 * 実装途中.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class FullScreenAction extends AbstractAction {

	RemonClient client;

	/**
	 * @param name
	 */
	public FullScreenAction(RemonClient client) {
		super("全画面表示");
		this.client = client;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = device.getDefaultConfiguration();
		client = new RemonClient(gc);
		try {
			client.setVisible(false);
			client.setUndecorated(true);

			device.setFullScreenWindow(client);
			client.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			device.setFullScreenWindow(null);
		}
	}

}
