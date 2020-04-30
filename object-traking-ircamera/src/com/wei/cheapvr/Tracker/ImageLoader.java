package com.wei.cheapvr.Tracker;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;


public class ImageLoader {
    String esp_ip, videoAddress;
    VideoCapture cap;
    int w, h;
    int id;

    JFrame frame;
    JLabel lbl;

    Mat rawImage, grayImage, image;

    public ImageLoader(String ip, int id) {
        esp_ip = ip;
        this.id = id;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        videoAddress = "http://" + esp_ip + "/capture";
        cap = new VideoCapture(videoAddress);
        openVideoFromWeb(cap, videoAddress);
    }

    int openVideoFromWeb(VideoCapture cap, String address) {
        if (!cap.open(address)) {
            show("Error opening video stream");
            return -1;
        }
        show("Opened camera successfully(ip: " + esp_ip + ")");
        return 0;
    }

    public Mat capture() throws Exception {
        Mat image = new Mat();
        if (cap.open(videoAddress))
            if (!cap.read(image)) {
                show("No frame");
                return null;
            } else {
                return image;
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

    /**
     * 실제 이미지로부터 point를 추출합니다.
     *
     * @return
     * @throws Exception
     * @see Camera#findPoint()
     */
    public Vector2[] getData() throws Exception {
        Mat blobs = new Mat();
        List<MatOfPoint> listBlobs = new ArrayList<MatOfPoint>();
        rawImage = capture();
        grayImage = new Mat();
        image = new Mat();

        Vector2[] points;
        if (rawImage == null) {
            return new Vector2[0];
        }

        //bainarization
        Imgproc.cvtColor(rawImage, grayImage, Imgproc.COLOR_RGB2GRAY);
        //Imgproc.adaptiveThreshold(grayImage, image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 51, 4);
        Imgproc.threshold(grayImage, image, 58, 255, Imgproc.THRESH_BINARY);
        Imgproc.findContours(image, listBlobs, blobs, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        points = new Vector2[listBlobs.size()];


        // 원 그리기
        double x, y;
        int r;
        for (int i = 0; i < points.length; i++) {
            MatOfPoint data = listBlobs.get(i);
            Moments moments = Imgproc.moments(data);

            x = moments.get_m10() / moments.get_m00();
            y = moments.get_m01() / moments.get_m00();
            r = 10;

            Point center = new Point(x, y);
            Imgproc.circle(rawImage,
                    center, // 원 중심
                    r,  // 원 반지름
                    new Scalar(255), // 컬러
                    1);    // 두께
            points[i] = new Vector2((float) x, (float) y);
        }

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

    Image getRawImage() {
        try {
            return Mat2BufferedImage(rawImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Image getGrayImage() {
        try {
            return Mat2BufferedImage(grayImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Image getImage() {
        try {
            return Mat2BufferedImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    public static void show(String str) {
        System.out.println(str);
    }
}
