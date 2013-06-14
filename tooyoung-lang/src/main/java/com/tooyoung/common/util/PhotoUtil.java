package com.tooyoung.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * 图片转换的工具类
 *
 */
public class PhotoUtil {

	public static void scaleImage(String srcFile, String destPath ,String imageFormat) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		// 读取源图片
		try {
			fin =  new FileInputStream(srcFile);
			byte[] result = scaleImage(fin, imageFormat);
			fout = new FileOutputStream(destPath);
			fout.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 图片裁剪的方法
	 * @param srcImage 原始图片
	 * @param imageFormat 图片格式（eg：JPG、GIF等）
	 * @return
	 */
	public static byte[] scaleImage(InputStream srcImage, String imageFormat){
		try {
			BufferedImage image = ImageIO.read(srcImage);
			int width = image.getWidth();// 图片宽度
			int height = image.getHeight();// 图片高度
			int edge = 0;
			int reducew = 0, reduceh = 0; 
			BufferedImage subImage = null;
			if(width == height){
				subImage = image;
			}else if(width > height){
				edge = height;
				reducew = (width - height)/2; 
				reduceh = 0;
				subImage =  image.getSubimage(reducew, reduceh, edge, edge);
			}else {
				edge = width;
				reducew = 0;
				reduceh = (height - width)/2; 
				subImage =  image.getSubimage(reducew, reduceh, edge, edge);
			}
			// 写图片
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			ImageIO.write(subImage, imageFormat, outStream);
			return outStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		PhotoUtil.scaleImage("tt3.jpg", "test3.jpg", "JPG");
		System.out.println("scale image ok");
	}
}
