/**
 * (c) 2016 uchicom
 */
package com.uchicom.remon;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 2byte画像処理.
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Image256 {
	static BufferedImage img;

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				File file = new File(args[i]);

				img = ImageIO.read(file);
				/*
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createYuv(img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "_Yuv.png"));

				img = ImageIO.read(file);
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createRgbAve(img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "_RgbAve.png"));

				img = ImageIO.read(file);
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createMono(img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "_Mono.png"));

				img = ImageIO.read(file);
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createYRgb(img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "_YRgb.png"));

				img = ImageIO.read(file);
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createYrg(img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "_Yrg.png"));
				img = ImageIO.read(file);
				*/
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						img.setRGB(x, y, createMonoRgb(x, y, img.getRGB(x, y)));
					}
				}
				ImageIO.write(img, "png", new File(file.getParent(), file.getName() + "0_MonoRgb.png"));
				/*
				JFrame frame = new JFrame() {

					public void paint(Graphics g) {
						g.drawImage(img, 0, 0, this);
					//	g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
					}
				};
				frame.setVisible(true);
				*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int createMono(int rgb) {
		int y = (int) (0.299 * (double) (0xFF & (rgb >> 16))
				+ 0.587 * (double) (0xFF & (rgb >> 8))
				+ 0.114 * (double) (0xFF & (rgb)));

		return ((0xFF & y) << 16) | ((0xFF & y) << 8) | (0xFF & y);
	}

	public static int createYuv(int rgb) {

		int y = (int) (0.299 * (double) (0xFF & (rgb >> 16))
				+ 0.587 * (double) (0xFF & (rgb >> 8))
				+ 0.114 * (double) (0xFF & (rgb)));
		y /= 64;
		y *= 64;
		int u = (int) (-0.14713 * (double) (0xFF & (rgb >> 16))
				+ -0.28886 * (double) (0xFF & (rgb >> 8))
				+ 0.436 * (double) (0xFF & (rgb)));
		u /= 32;
		u *= 32;
		int v = (int) (0.615 * (double) (0xFF & (rgb >> 16))
				+ -0.51499 * (double) (0xFF & (rgb >> 8))
				+ -0.10001 * (double) (0xFF & (rgb)));
		v /= 32;
		v *= 32;

		return ((0xFF & (int) (y + 1.13983 * v)) << 16) |
				((0xFF & (int) (y - 0.39465 * u - 0.58060 * v)) << 8) |
				((0xFF & (int) (y + 2.03211 * u)));
	}

	public static int createRgbAve(int rgb) {

		int r = 0xFF & (rgb >> 16);
		int r2 = r;
		boolean black = false;
		r -= 31;
		r /= 32; //2
		r *= 32;
		r += 31;

		int g = 0xFF & (rgb >> 8);
		int g2 = g;
		g -= 31;
		g /= 32;//3
		g *= 32;

		g += 31;
		int b = 0xFF & (rgb);
		int b2 = b;
		b -= 63;
		b /= 64;//2
		b *= 64;

		b += 63;
		;/*
			if (r2 == g2 && g2 == b2) {
			return ((0xFF & g) << 16) | ((0xFF & g )<< 8) | (0xFF & g);
			}*/
		return ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
	}

	public static int createYRgb(int rgb) {
		int y = (int) (0.299 * (double) (0xFF & (rgb >> 16))
				+ 0.587 * (double) (0xFF & (rgb >> 8))
				+ 0.114 * (double) (0xFF & (rgb)));
		y /= 32;
		y *= 32;
		int r = 0xFF & (rgb >> 16);
		r /= 128;
		r *= 128;
		r = (r | y);
		int g = 0xFF & (rgb >> 8);
		g /= 128;
		g *= 128;
		g = (g | y);
		int b = 0xFF & (rgb);
		b /= 128;
		b *= 128;
		b = (b | y);
		if (rgb == 0 || (rgb & 0xFFFFFF) == 0xFFFFFF) {
			return rgb;
		}
		return ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
	}

	public static int createYrg(int rgb) {
		int y = (int) (0.299 * (double) (0xFF & (rgb >> 16))
				+ 0.587 * (double) (0xFF & (rgb >> 8))
				+ 0.114 * (double) (0xFF & (rgb)));
		int org_y = y;
		y -= 31;
		y /= 32;//3���g�p��128,64,32
		y *= 32;
		y += 31;

		int r = 0xFF & (rgb >> 16);
		double val = 0;
		if (y != 0) {
			val = (double) r / y;//2���g�p��128,64 0,1,2,1/2
			if (val > 0 && val < 0.5) {
				val = 0;
			} else if (val >= 0.5 && val < 1) {
				val = 0.5;
			} else if (val >= 1 && val < 2) {
				val = 1;
			} else if (val >= 2) {
				val = 2;
			}
		}
		r = (int) (val * y);
		int g = 0xFF & (rgb >> 8);
		val = 0;
		if (y != 0) {
			val = (double) g / y;//2���g�p��128,64 0,1,2,1/2
			if (val > 0 && val < 0.25) {
				val = 0;
			} else if (val >= 0.25 && val < 0.5) {
				val = 0.25;
			} else if (val >= 0.5 && val < 1) {
				val = 0.5;
			} else if (val >= 1 && val < 2) {
				val = 1;
			} else if (val >= 2 && val < 4) {
				val = 2;
			} else if (val >= 4) {
				val = 4;
			}
		}
		g = (int) (val * y);

		int b = (int) ((y - (0.299 * r + 0.587 * g)) / 0.114);

		return ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
	}

	static int tmpR = 0xFF;
	static int tmpG = 0xFF;
	static int tmpB = 0xFF;

	public static int createMonoRgb(int x2, int y2, int rgb) {
		int y = (int) (0.299 * (double) (0xFF & (rgb >> 16))
				+ 0.587 * (double) (0xFF & (rgb >> 8))
				+ 0.114 * (double) (0xFF & (rgb)));

		int org_y = y;

		int r = 0xFF & (rgb >> 16);
		int g = 0xFF & (rgb >> 8);
		int b = 0xFF & (rgb);

		if ((r & g & b & 0xC0) != 0xC0 ||
				(r & g & b & 0xC0) == 0xC0) {

			if (Math.abs(r - g) < 0x08 && Math.abs(g - b) < 0x08 && Math.abs(b - r) < 0x08) {
				if ((rgb & ~0x030303) > 0) {
					return 0;
				} else {
					return rgb | 0x030303;
				}
			}

			if (r >> 5 == tmpR >> 5 &&
					g >> 5 == tmpG >> 5 &&
					b >> 5 == tmpB >> 5) {
				tmpR = r;
				tmpG = g;
				tmpB = b;
				r /= 16;
				r *= 16;
				g /= 16;
				g *= 16;
				b /= 16;
				b *= 16;

				if (x2 > 0) {

					//img.setRGB(x2-1,y2, ((0xFF & r) << 16) | ((0xFF & g )<< 8) | (0xFF & b));
				}
			} else {
				r /= 32; //3
				r *= 32;
				/*
				if ((r & 0xE0) == 0xE0) {
					r = 0xFF;
				}*/

				g /= 32;//3
				g *= 32;
				/*
				if ((g & 0xE0) == 0xE0) {
					g = 0xFF;
				}*/
				b /= 32;//3
				b *= 32;
			}
			/*
			if ((b & 0xE0) == 0xE0) {
				b = 0xFF;
			}*/
			/*		if ((r & g & b & 0x80) != 0) {
						r |= 0x1F;
						g |= 0x1F;
						b |= 0x1F;
					}*/
		} else {
			if (Math.abs(r - g) < 0x10 && Math.abs(g - b) < 0x10 && Math.abs(b - r) < 0x10) {
				if ((rgb & ~0x030303) > 0) {
					return 0;
				} else {
					return rgb | 0x030303;
				}
			}

			if (r >> 6 == tmpR >> 6 &&
					g >> 6 == tmpG >> 6 &&
					b >> 6 == tmpB >> 6) {
				tmpR = r;
				tmpG = g;
				tmpB = b;
				r /= 32;
				r *= 32;
				g /= 32;
				g *= 32;
				b /= 32;
				b *= 32;
				if (x2 > 0) {

					//img.setRGB(x2-1,y2, ((0xFF & r) << 16) | ((0xFF & g )<< 8) | (0xFF & b));
				}
			} else {
				int r2 = r;
				r /= 64; //2
				r *= 64;
				if ((r & 0x80) != 0) {
					//			r |= 0x3F;
				}
				int g2 = g;
				g /= 64;//2
				g *= 64;
				if ((g & 0x80) != 0) {
					//			g |= 0x3F;
				}
				int b2 = b;
				b /= 64;//2
				b *= 64;

				if ((b & 0x80) != 0) {
					//			b |= 0x3F;
				}
			}
		}
		//
		//1 bit �Ł@color �� ���m�N�����̔�������Ă��� ,����1 ��Ԍ�낪0�������烂�m�N��,����ȊO�͂���[11 01 �� 2�p�^�[������B
		//���ׂ�128�ȏォ�A�ۂ��Ŕ��肷��B
		if (r == g && g == b) {
			if (y > 0x03) {
				return ((0xFF & y) << 16) | ((0xFF & y) << 8) | (0xFF & y);
			} else {
				return 0;
			}
		}
		return ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
		//6�p�^�[���̗\��������̂ŁA
		/*
		 4  4  4   16byte 2byte
		8 16 32 64 128 1920 byte�������Ƃ�\������B
		6Byte���g�p���ē���byte�̏ꍇ�Ɉ��k����
		1920x1080 = 2073600 1.97MByte 2025KByte
		ping        1286144 62%�̈��k��
		*/
	}
}