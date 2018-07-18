package com.jimlp.util.web.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class CaptchaUtils {

	// 背景宽度
	private static final int WIDTH = 90;
	// 背景高度
	private static final int HEIGHT = 40;
	// 字符基础大小
	private static final int FONT_SIZE = HEIGHT;
	// 字符基础宽度
	private static final int FONT_WIDTH = HEIGHT >>> 1;
	// 输出字符数量
	private static final int LENGTH = 4;
	// 字符间距调整
	private static final int INTERVAL = -(FONT_WIDTH >>> 3);
	// 字符起始 x 坐标
	private static final int X = ((WIDTH - LENGTH * FONT_WIDTH - (LENGTH - 1) * INTERVAL) >>> 1) - 5;
	// 字符集
	private static final String[] CODES = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	// 字符集（数字）
	private static final String[] CODES_NUM = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	// 字体
	private static final String[] FONT_FAMILYS = { "Consolas", "Monospaced", "Serif", "SansSerif", "DialogInput", "Dialog" };
	private static final int FONT_FAMILYS_LENGTH = FONT_FAMILYS.length;

	private static Random random = new Random();

	private CaptchaUtils() {
		super();
	}

	/**
	 * 生成随机验证码及图片
	 * 
	 * @return {String 字符, BufferedImage 图片}
	 */
	public static Object[] getSimpleCaptcha() {
		return getSimpleCaptcha(18, 45, false);
	}
	
	/**
	 * 生成随机验证码及图片
	 * 
	 * @param lineNum 干扰线数量
	 * @param xuanzhuan 字符最大旋转角度
	 * @param onlyNum 是否只使用纯数字
	 * @return {String 字符, BufferedImage 图片}
	 */
	public static Object[] getSimpleCaptcha(int lineNum ,int xuanzhuan, boolean onlyNum) {
	    String[] codes = CODES;
	    if(onlyNum){
	        codes = CODES_NUM;
	    }
	    
		StringBuffer imgCode = new StringBuffer();
		// 1.创建背景图片
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		// 设置图片背景透明
		image = g.getDeviceConfiguration().createCompatibleImage(WIDTH, HEIGHT, Transparency.TRANSLUCENT);
		g.dispose();

		g = image.createGraphics();
		//g.setColor(new Color(255, 87, 34));
		// 渐变色
		GradientPaint paint = new GradientPaint(0, 0, Color.RED, WIDTH, HEIGHT, Color.BLUE, true);
		g.setPaint(paint);
		// 画笔透明度 ，值从0-1.0
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.0f));
		// 字符起始x
		int charX = X;
		int charY = (HEIGHT >>> 1) + (HEIGHT >>> 2);
		for (int i = 0; i < LENGTH; i++) {
			int index = random.nextInt(codes.length);
			// 角度正负45度
			int jiaodu = random.nextInt(xuanzhuan << 1) - xuanzhuan;
			// 弧度
			double hudu = jiaodu * Math.PI / 180;
			g.setFont(new Font(FONT_FAMILYS[random.nextInt(FONT_FAMILYS_LENGTH)], Font.ITALIC, FONT_SIZE));
			g.rotate(hudu, charX, charY);
			g.drawString(codes[index], charX, charY);
			g.rotate(-hudu, charX, charY);
			imgCode.append(codes[index]);
			charX += FONT_WIDTH + INTERVAL;
		}
		// 6.画干扰线
		g.setColor(Color.WHITE);
		int lineHeight = HEIGHT/(lineNum+1);
		int lineY = random.nextInt(lineHeight);
		for (int i = 0; i < lineNum; i++) {
		    g.drawLine(0, lineY, WIDTH, lineY);
		    lineY += lineHeight;
        }

		g.dispose();

		// 7.返回验证码和图片
		return new Object[] { imgCode.toString(), image };
	}
}
