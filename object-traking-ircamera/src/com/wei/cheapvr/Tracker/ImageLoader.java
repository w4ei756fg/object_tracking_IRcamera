package com.wei.cheapvr.Tracker;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


public class ImageLoader {
	String esp_ip, videoAddress;
	VideoCapture cap;
	int w, h;

	JFrame frame;
	JLabel lbl;

	public ImageLoader(String ip){
		esp_ip = ip;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		videoAddress = "http://" + esp_ip +"/capture";
		cap = new VideoCapture(videoAddress);
		openVideoFromWeb(cap, videoAddress);
	}
	
	int openVideoFromWeb(VideoCapture cap, String address) {
		String var = "framesize", val = "5";
		cap.open("http://" + esp_ip +"/control?var=" + var + "&val=" + val);
		if(!cap.open(address)) {
			show("Error opening video stream");
			return -1;
		}
		show("Opened camera successfully(ip: " + esp_ip + ")");
		return 0;
	}
	
	public Mat capture() throws Exception{
		Mat image = new Mat();
		if(cap.open(videoAddress))
		if(!cap.read(image)) {
			show("No frame");
			return null;
		}
		else {
			return image;
			//displayImage(Mat2BufferedImage(image));
		}
		return null;
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
		Mat image = new Mat(), grayImage = new Mat(), circles = new Mat();
		image = capture();
		
		Vector2[] points;
		if(image == null) { return new Vector2[0]; }
		
		//bainarization
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
		//Imgproc.adaptiveThreshold(grayImage, image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 51, 4);
		//Imgproc.threshold(grayImage, image, 20, 255, Imgproc.THRESH_BINARY);
		
		/*
		Imgproc.HoughCircles(image, circles, Imgproc.HOUGH_GRADIENT,
  2,   // 누적기 해상도(영상크기/2)
  10,  // 두 원 간의 최소 거리
  200, // 캐니 최대 경계값
  35, // 투표 최소 개수
  2, 20); // 최소와 최대 반지름
		show("Circles count: " + circles.size());
		show("Circles: " + circles.dump());
		 */
		points = new Vector2[circles.cols()];
		
		
		// 원 그리기
		double x, y;
		int r;
		for(int i = 0; i < circles.cols(); i++) {
			double[] data = circles.get(0, i);
			show("data:" + data.length);
			/*for(int j = 0; j < data.length; j++)*/ {
				x = data[0];
				y = data[1];
				r = (int)Math.round(data[2]);
			}
			Point center = new Point(x, y);
			Imgproc.circle(image, 
			     center, // 원 중심
			     r,  // 원 반지름
			     new Scalar(255), // 컬러 
			     1);    // 두께
			points[i] = new Vector2((float)x, (float)y);
		}
		displayImage(Mat2BufferedImage(image));
		return points;
	}
	
	
	public void displayImage(Image img2) {
		if (frame == null) {
			frame = new JFrame();
			frame.setLayout(new FlowLayout());
			frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
			lbl = new JLabel();
			frame.add(lbl);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		ImageIcon icon = new ImageIcon(img2);
	    lbl.setIcon(icon);
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
