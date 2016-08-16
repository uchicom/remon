/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.uchicom.remon.Constants;

/**
 * 画像表示.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ImagePanel extends JPanel {

	private BufferedImage image;
	private long transfer;
	private long startTime;

	public void setImage(BufferedImage image, int x, int y, long transfer) {

		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if (ImagePanel.this.image == null) {
					ImagePanel.this.image = image;
					startTime = System.currentTimeMillis();
					setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
				} else {
					ImagePanel.this.image.getGraphics().drawImage(image, x, y, ImagePanel.this);
					ImagePanel.this.transfer += transfer;
				}
				repaint();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
		g.setXORMode(Color.WHITE);
		if (Constants.DEBUG) {
			g.drawString(transfer * 1000d / 8 / 1024 / (System.currentTimeMillis() - startTime) + "[KBps]", 0, 10);
		}
	}
}
