package com.wei.cheapvr.Tracker;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

import static com.wei.cheapvr.Utils.*;

/**
 * 이미지나 웹캠으로부터 Point를 추적하는 역할을 합니다.
 *
 * @author wei756
 */
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

    /**
     * 웹 스트리밍으로부터 이미지를 가져온 뒤 Mat로 반환합니다.
     * @return
     * @throws Exception
     * @see #getData()
     */
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

        // Binarization
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
            Imgproc.circle(rawImage, center, r, new Scalar(255), 1);
            points[i] = new Vector2((float) x, (float) y);
        }

        return points;
    }

    /**
     * 카메라의 현재 원본 이미지를 반환합니다.
     * @return
     */
    Image getRawImage() {
        try {
            return Mat2BufferedImage(rawImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 카메라의 현재 그레이스케일 이미지를 반환합니다.
     * @return
     */
    Image getGrayImage() {
        try {
            return Mat2BufferedImage(grayImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 카메라의 현재 흑백 이미지를 반환합니다.
     * @return
     */
    Image getImage() {
        try {
            return Mat2BufferedImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mat 타입의 이미지를 Image 로 변환하여 반환합니다
     * @return
     */
    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
}
