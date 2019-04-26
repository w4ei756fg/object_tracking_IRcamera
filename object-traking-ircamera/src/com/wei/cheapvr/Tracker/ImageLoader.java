package com.wei.cheapvr.Tracker;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageLoader {
	BufferedImage image;
	int w, h;
	public ImageLoader(String filename) throws Exception{
		try {
			image = ImageIO.read(new File(filename));
			w = image.getWidth();
			h = image.getHeight();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Vector2[] getData() {
		Vector2[] data;
		int c = 0;
		Color p;
		for(int x = 0; x < w; x++)
		for(int y = 0; y < h; y++) {
			p = new Color(image.getRGB(x, y));
			if(p.getRed() > 128 && p.getGreen() < 128 && p.getBlue() < 128)
				c++;
			
		}
		System.out.println("count: " + c);
		data = new Vector2[c];
		
		c = 0;
		for(int x = 0; x < w; x++)
		for(int y = 0; y < h; y++) {
			p = new Color(image.getRGB(x, y));
			if(p.getRed() > 128 && p.getGreen() < 128 && p.getBlue() < 128)
				data[c++] = new Vector2(x, y);
		}
		
		return data;
	}
}
