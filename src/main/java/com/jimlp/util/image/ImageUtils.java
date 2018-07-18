package com.jimlp.util.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {
	/**
	 * 调整图片尺寸
	 * 
	 * @param src
	 * @param maxHeight
	 * @param maxWidth
	 * @return
	 * @throws IOException
	 */
	public static byte[] changeImageSize(byte[] src, int maxHeight, int maxWidth) throws IOException {
		// 字节流转图片对象
		Image img = ImageIO.read(new ByteArrayInputStream(src));
		// 获取图像的高度，宽度
		float height = img.getHeight(null);
		float width = img.getWidth(null);
		// 缩放比例
		float s = 1;
		if (height > maxHeight) {
			s = maxHeight / height;
		}
		if (width > maxWidth) {
			float _s = maxWidth / width;
			s = _s < s ? _s : s;
		}
		// 构建图片流
		BufferedImage newImg = new BufferedImage((int) (width * s), (int) (height * s), BufferedImage.TYPE_INT_RGB);
		// 绘制改变尺寸后的图
		newImg.getGraphics().drawImage(img, 0, 0, (int) (width * s), (int) (height * s), null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(newImg, "jpg", out);
		return out.toByteArray();
	}
}
