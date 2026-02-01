// (C) 2020 uchicom
package com.uchicom.remon.strategy;

import com.uchicom.remon.util.ImagePanel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PngAnalysis implements Analysis {

  private ImagePanel panel;

  @Override
  public void refrectImage(int x, int y, int option, byte[] imageBytes, int length) {

    BufferedImage image;
    try {
      image = ImageIO.read(new ByteArrayInputStream(imageBytes, 0, length));
      if (image == null) {
        return;
      }

      panel.setImage(image, 0, 0, length);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void setImagePanel(ImagePanel panel) {
    this.panel = panel;
  }
}
