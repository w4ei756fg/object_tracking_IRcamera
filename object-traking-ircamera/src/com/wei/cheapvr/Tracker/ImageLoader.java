package com.wei.cheapvr.Tracker;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


public class ImageLoader {
	String esp_ip;
	VideoCapture cap;
	int w, h;
	public ImageLoader(String ip){
		esp_ip = ip;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String videoAddress = "http://" + esp_ip +"/capture";
		cap = new VideoCapture(videoAddress);
		openVideoFromWeb(cap, videoAddress);
	}
	
	int openVideoFromWeb(VideoCapture cap, String address) {
		
		if(!cap.open(address)) {
			show("Error opening video stream");
			return -1;
		}
		show("Opened camera successfully");
		return 0;
	}
	
	public BufferedImage capture() throws Exception{
		Mat image = new Mat();
		
		if(!cap.read(image)) {
			show("No frame");
			return null;
		}
		else {
			return Mat2BufferedImage(image);
			//displayImage(Mat2BufferedImage(image));
		}
	}
	
	/*
	public ImageLoader(String filename) throws Exception{
		try {
			image = ImageIO.read(new File(filename));
			w = image.getWidth();
			h = image.getHeight();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	*/
	
	public Vector2[] getData() throws Exception {
		BufferedImage image = capture();
		Vector2[] data;
		
		int c = 0;
		Color p;
		for(int x = 0; x < w; x++)
		for(int y = 0; y < h; y++) {
			p = new Color(image.getRGB(x, y));
			if(p.getRed() > 200 && p.getGreen() < 128 && p.getBlue() < 128)
				c++;
			
		}
		System.out.println("count: " + c);
		data = new Vector2[c];
		
		c = 0;
		for(int x = 0; x < w; x++)
		for(int y = 0; y < h; y++) {
			p = new Color(image.getRGB(x, y));
			if(p.getRed() > 200 && p.getGreen() < 128 && p.getBlue() < 128)
				data[c++] = new Vector2(x, y);
		}
		
		return data;
	}
	
	
	public void displayImage(Image img2) {   
		ImageIcon icon = new ImageIcon(img2);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {        
		MatOfByte mob=new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[]=mob.toArray();

		BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}
	
	public static void show(String str) { System.out.println(str); }
}
