// (C) 2016 uchicom
package com.uchicom.remon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uchicom.remon.runnable.ImageSender;
import org.junit.jupiter.api.Test;

/**
 * 画像処理テスト.
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class ImageTest {

  /*
   * 1ポイント
   */
  @Test
  public void getBytes1() {
    int width = 10;
    int height = 10;
    int[] orgImage = new int[width * height];
    orgImage[width + 1] = 0xFFFFFF;
    int[] newImage = new int[width * height];
    ;
    newImage[width + 1] = 0xFFFFFE;
    int rgbIndex = 11 + (1 + 7) / 8;
    int diffCount = 1;
    byte[] bytes = new byte[rgbIndex + width * height * 3];

    int result = ImageSender.getByte(bytes, width, height, orgImage, newImage);
    assertEquals(rgbIndex + diffCount * 3, result);
    assertEquals(0x00, 0xFF & bytes[0]); // x
    assertEquals(0x01, 0xFF & bytes[1]); // x
    assertEquals(0x00, 0xFF & bytes[2]); // y
    assertEquals(0x01, 0xFF & bytes[3]); // y
    assertEquals(0x00, 0xFF & bytes[4]); // width
    assertEquals(0x01, 0xFF & bytes[5]); // width
    assertEquals(0x00, 0xFF & bytes[6]); // height
    assertEquals(0x01, 0xFF & bytes[7]); // height
    assertEquals(0xFE, 0xFF & bytes[rgbIndex]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 1]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 2]);
  }

  /*
   * 4ポイント
   */
  @Test
  public void getBytes2x2() {
    int width = 10;
    int height = 10;
    int[] orgImage = new int[width * height];
    orgImage[width + 1] = 0xFFFFFF;
    orgImage[width + 2] = 0xFFFFFF;
    orgImage[width * 2 + 1] = 0xFFFFFF;
    orgImage[width * 2 + 2] = 0xFFFFFF;
    int[] newImage = new int[width * height];
    newImage[width + 1] = 0xFFFFFE;
    newImage[width + 2] = 0xFFFFFE;
    newImage[width * 2 + 1] = 0xFFFFFE;
    newImage[width * 2 + 2] = 0xFFFFFE;
    int rgbIndex = 11 + (2 * 2 + 7) / 8;
    int diffCount = 4;
    byte[] bytes = new byte[rgbIndex + width * height * 3];

    int result = ImageSender.getByte(bytes, width, height, orgImage, newImage);
    assertEquals(rgbIndex + diffCount * 3, result);
    assertEquals(0x00, bytes[0]); // x
    assertEquals(0x01, bytes[1]); // x
    assertEquals(0x00, bytes[2]); // y
    assertEquals(0x01, bytes[3]); // y
    assertEquals(0x00, bytes[4]); // width
    assertEquals(0x02, bytes[5]); // width
    assertEquals(0x00, bytes[6]); // height
    assertEquals(0x02, bytes[7]); // height
    assertEquals(0xFE, 0xFF & bytes[rgbIndex]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 1]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 2]);

    assertEquals(0xFE, 0xFF & bytes[rgbIndex + 3]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 4]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 5]);

    assertEquals(0xFE, 0xFF & bytes[rgbIndex + 6]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 7]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 8]);

    assertEquals(0xFE, 0xFF & bytes[rgbIndex + 9]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 10]);
    assertEquals(0xFF, 0xFF & bytes[rgbIndex + 11]);
  }

  /*
   * 4ポイント
   */
  @Test
  public void getBytes3x3AndAlpha() {
    int width = 10;
    int height = 10;
    int[] orgImage = new int[width * height];
    orgImage[width + 1] = 0xFFFFFF;
    orgImage[width + 2] = 0xFFFFFF;
    orgImage[width + 3] = 0xFFFFFF;
    orgImage[width * 2 + 1] = 0xFFFFFF;
    orgImage[width * 2 + 2] = 0xFFFFFF;
    orgImage[width * 2 + 3] = 0xFFFFFF;
    orgImage[width * 3 + 1] = 0xFFFFFF;
    orgImage[width * 3 + 2] = 0xFFFFFF;
    orgImage[width * 3 + 3] = 0xFFFFFF;
    int[] newImage = new int[width * height];
    newImage[width + 1] = 0xFFFFFE;
    newImage[width + 2] = 0xFFFFFE;
    newImage[width + 3] = 0xFFFFFE;
    newImage[width * 2 + 1] = 0xFFFFFE;
    newImage[width * 2 + 2] = 0xFFFFFF; // 1つだけ
    newImage[width * 2 + 3] = 0xFFFFFE;
    newImage[width * 3 + 1] = 0xFFFFFE;
    newImage[width * 3 + 2] = 0xFFFFFE;
    newImage[width * 3 + 3] = 0xFFFFFE;
    int rgbIndex = 11 + (3 * 3 + 7) / 8;
    int diffCount = 8;
    byte[] bytes = new byte[rgbIndex + width * height * 3];

    int result = ImageSender.getByte(bytes, width, height, orgImage, newImage);
    assertEquals(rgbIndex + diffCount * 3, result);
    assertEquals(0x00, bytes[0]); // x
    assertEquals(0x01, bytes[1]); // x
    assertEquals(0x00, bytes[2]); // y
    assertEquals(0x01, bytes[3]); // y
    assertEquals(0x00, bytes[4]); // width
    assertEquals(0x03, bytes[5]); // width
    assertEquals(0x00, bytes[6]); // height
    assertEquals(0x03, bytes[7]); // height
    for (int i = rgbIndex; i < rgbIndex + diffCount; i += 3) {
      assertEquals(0xFE, 0xFF & bytes[rgbIndex]); // B
      assertEquals(0xFF, 0xFF & bytes[rgbIndex + 1]); // G
      assertEquals(0xFF, 0xFF & bytes[rgbIndex + 2]); // R
    }
  }
}
