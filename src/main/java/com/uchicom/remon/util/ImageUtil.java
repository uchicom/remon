// (C) 2016 uchicom
package com.uchicom.remon.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.ImageIcon;

/**
 * @author uchicom: Shigeki Uchiyama
 */
public class ImageUtil {

  public static ImageIcon getImageIcon(String path) {
    if (Files.exists(Paths.get("src/main/resources/" + path))) {
      return new ImageIcon(path);
    }
    return new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(path));
  }
}
