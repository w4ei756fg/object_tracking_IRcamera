package com.wei.cheapvr.Tracker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TrackingViewer extends JFrame {
    CameraTracker ct;
    TrackingViewPanel trackingViewPanel;
    ArrayList<CameraPanel> cameraPanels = new ArrayList<>();

    public TrackingViewer(CameraTracker ct) {
        this.ct = ct;
        initUI();
        initCameraPanel();
    }

    private void initUI() {

        trackingViewPanel = new TrackingViewPanel();
        trackingViewPanel.setCameraTracker(ct);
        trackingViewPanel.setSize(720, 854);
        trackingViewPanel.setLocation(560, 0);
        add(trackingViewPanel);

        setSize(1280, 854);
        setTitle("Tracking Viewer");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
    }

    private void initCameraPanel() {
        for (Camera camera : ct.getCameras()) {
            CameraPanel cp = new CameraPanel(camera);
            cp.setLocation(0, cp.getHeight() * cameraPanels.size());
            add(cp);
            cameraPanels.add(cp);
        }
    }

    public void updateTrackingPanel() {
        trackingViewPanel.update();
    }

    public void updateCameraPanel(int id) {
        for (CameraPanel cameraPanel : cameraPanels) {
            if (cameraPanel.id == id)
                cameraPanel.update();
        }
    }
}


/**
 * 트래킹 모니터링 패널
 */
class TrackingViewPanel extends JPanel {
    CameraTracker ct;
    Graphics2D g2d;
    int width, height;
    int cx, cy;
    float scale = 4;

    private void doDrawing(Graphics g) {
        g2d = (Graphics2D) g;

        Dimension size = getSize();
        Insets insets = getInsets();

        width = size.width - insets.left - insets.right;
        height = size.height - insets.top - insets.bottom;

        cx = width / 2;
        cy = height / 2;

        drawCenter();

        Camera[] cameras = ct.getCameras();
        Vector3[] points = ct.getPoints();
        for (Camera cam : cameras) {
            Vector3 pos = cam.getPos();
            drawCamera(pos.x, pos.y, pos.z, cam.getPan(), cam.getTilt());
        }
        for (Vector3 point : points) {
            drawPoint(point.x, point.y, point.z);
        }
    }

    private void drawCenter() {
        g2d.setColor(Color.green);
        g2d.drawLine(cx, 0, cx, height);
        g2d.drawLine(0, cy / 2, width, cy / 2);
        g2d.drawLine(0, cy / 2 * 3, width, cy / 2 * 3);
        g2d.setColor(Color.black);
        g2d.drawLine(0, cy, width, cy);
        g2d.drawLine(0, 0, 0, height);
    }

    public void drawPoint(float x, float y, float z) {
        int _x, _y;
        g2d.setColor(Color.red);
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // xy 평면
        _x = cx + (int) (x * scale);
        _y = cy / 2 - (int) (y * scale);
        g2d.drawOval(_x - 3, _y - 3, 5, 5);
        g2d.drawString("[" + x + "," + y + "," + z + "]", _x + 5, _y + 5);

        // xz 평면
        _y = cy / 2 * 3 - (int) (z * scale);
        g2d.drawOval(_x - 3, _y - 3, 5, 5);
        g2d.drawString("[" + x + "," + y + "," + z + "]", _x + 5, _y + 5);
    }

    public void drawCamera(float x, float y, float z, float pan, float tilt) {
        int _x, _y;
        g2d.setColor(Color.blue);
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // xy 평면
        _x = cx + (int) (x * scale);
        _y = cy / 2 - (int) (y * scale);
        g2d.drawOval(_x - 5, _y - 5, 9, 9);
        double x1 = 150 * Math.cos(-pan - Math.PI * 0.5 + 0.349344f),
                y1 = 150 * Math.sin(-pan - Math.PI * 0.5 + 0.349344f),
                x2 = 150 * Math.cos(-pan - Math.PI * 0.5 - 0.349344f),
                y2 = 150 * Math.sin(-pan - Math.PI * 0.5 - 0.349344f);
        g2d.drawLine(_x, _y,
                (int) (_x + x1),
                (int) (_y + y1));
        g2d.drawLine(_x, _y,
                (int) (_x + x2),
                (int) (_y + y2));

        g2d.drawString("[" + x + "," + y + "," + z + "]", _x + 10, _y + 10);
        g2d.drawString("pan: " + pan, _x + 10, _y + 10 + 20);
        g2d.drawString("tilt: " + tilt, _x + 10, _y + 10 + 40);

        // xz 평면
        _x = cx + (int) (x * scale);
        _y = cy / 2 * 3 - (int) (z * scale);
        g2d.drawOval(_x - 5, _y - 5, 9, 9);

        g2d.drawString("[" + x + "," + y + "," + z + "]", _x + 10, _y + 10);
        g2d.drawString("pan: " + pan, _x + 10, _y + 10 + 20);
        g2d.drawString("tilt: " + tilt, _x + 10, _y + 10 + 40);
    }

    public void setCameraTracker(CameraTracker ct) {
        this.ct = ct;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    public void update() {
        revalidate();
        repaint();
    }
}

/**
 * 카메라 모니터링 패널
 */
class CameraPanel extends JPanel {
    final int id;
    Camera camera;
    JLabel lbl;

    CameraPanel(Camera camera) {
        this.camera = camera;
        this.id = camera.id;

        setSize(400, 320);
        JLabel idLabel = new JLabel("id: " + id);
        add(idLabel);
    }

    public void update() {
        Image img = camera.getRawImage();
        if (img != null) {
            ImageIcon icon = new ImageIcon(img);

            if (lbl == null) {
                lbl = new JLabel();
                lbl.setSize(400, 296);
                lbl.setLocation(0, 0);
                add(lbl);
            }

            lbl.setIcon(icon);
        }
    }
}