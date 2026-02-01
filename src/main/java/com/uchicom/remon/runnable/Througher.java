// (C) 2016 uchicom
package com.uchicom.remon.runnable;

import com.uchicom.remon.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 転送処理.
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class Througher implements Runnable {

  private Socket receive;
  private Socket send;

  public Througher(Socket from, Socket to) {
    this.receive = from;
    this.send = to;
  }

  @Override
  public void run() {
    int length = 0;
    byte[] bytes = new byte[4 * 1024 * 1024];
    try (InputStream is = receive.getInputStream();
        OutputStream os = send.getOutputStream(); ) {
      while ((length = is.read(bytes)) > 0) {
        os.write(bytes, 0, length);
        os.flush();
      }
    } catch (IOException e) {
      if (Constants.DEBUG) System.out.println(receive.getLocalPort());
      e.printStackTrace();
    }
  }
}
