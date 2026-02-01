// (C) 2016 uchicom
package com.uchicom.remon.client.action;

import com.uchicom.remon.client.RemonClient;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * 接続アクション.
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class ConnectAction extends AbstractAction {

  /** */
  private static final long serialVersionUID = 1L;

  private RemonClient client;

  /**
   * @param name
   */
  public ConnectAction(RemonClient client) {
    super("接続");
    this.client = client;
  }

  /*
   * (非 Javadoc)
   *
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String hostPort =
        JOptionPane.showInputDialog(client, "接続先の「ホスト:ポート」を入力してください。", "localhost:10000");
    if (hostPort != null) {
      String[] splits = hostPort.split(":");
      if (splits.length > 1) {
        client.connect(splits[0], Integer.parseInt(splits[1]));
      } else {
        JOptionPane.showMessageDialog(client, "接続文字列が正しくありません");
      }
    }
  }
}
