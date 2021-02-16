// (c) 2016 uchicom
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	private long transfer;
	private long startTime;

	public void init(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		setPreferredSize(new Dimension(width, height));
	}

	public void setImage(int x, int y, int w, int h, int[] data, int offset, int scansize) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				image.setRGB(x, y, w, h, data, offset, scansize);
				repaint();
			}
		});
	}

	public void setImage(BufferedImage image, int x, int y, long transfer) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (ImagePanel.this.image == null) {
					ImagePanel.this.image = image;
					startTime = System.currentTimeMillis();
					setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
				} else {
					ImagePanel.this.image.getGraphics().drawImage(image, x, y, ImagePanel.this);
				}
				ImagePanel.this.transfer += transfer;
				repaint();
			}
		});
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
		if (Constants.DEBUG) {
			g.setXORMode(Color.WHITE);
			g.drawString(transfer * 1000d / 8 / 1024 / (System.currentTimeMillis() - startTime) + "[KBps]", 0, 10);
		}
	}
}
