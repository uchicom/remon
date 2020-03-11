/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.remon.RemonClient;

/**
 * 切断アクション.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class CloseAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemonClient client;

	/**
	 * @param name
	 */
	public CloseAction(RemonClient client) {
		super("切断");
		this.client = client;
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		client.close();
	}

}
