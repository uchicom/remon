/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.uchicom.remon.RemonClient;

/**
 * 接続アクション.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ConnectAction extends AbstractAction {

	/**
	 *
	 */
	public ConnectAction() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	RemonClient client;

	/**
	 * @param name
	 */
	public ConnectAction(RemonClient client) {
		super("接続");
		this.client = client;
	}

	/**
	 * @param name
	 * @param icon
	 */
	public ConnectAction(String name, Icon icon) {
		super(name, icon);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String hostPort = JOptionPane.showInputDialog(client, "接続先の「ホスト ポート」を入力してください。", "localhost 10000");
		String[] splits = hostPort.split(" ");
		if (splits.length > 1) {
			client.connect(splits[0], Integer.parseInt(splits[1]));
		} else {
			JOptionPane.showMessageDialog(client, "接続文字列が正しくありません");
		}
	}

}
