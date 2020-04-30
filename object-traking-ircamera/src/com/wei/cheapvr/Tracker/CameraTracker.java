package com.wei.cheapvr.Tracker;

import java.util.ArrayList;
import java.util.Iterator;

import static com.wei.cheapvr.Utils.*;

/**
 * CameraTracker
 * Find tracking points from images that IR cameras captured
 * , and save to 'points(ArrayList<Vector3>)'
 * CameraTracker can have camera objects. Cameras stored to 'cam(ArrayList<Camera>)'
 * <p>
 * 정확한 의미 전달을 위해 메소드 주석은 주로 한국어로 작성됨
 * For expressing exactly, this class's javadoc is written in Korean. Sorry for my bad English..
 *
 * @author wei756
 * @see Camera
 * @see ImageLoader
 */
public class CameraTracker {

    /**
     * CameraTracker에 등록된 카메라
     */
    private ArrayList<Camera> cam = new ArrayList<Camera>();

    /**
     * 카메라로부터 추적한 Point
     */
    private ArrayList<Vector3> points = new ArrayList<Vector3>();

    /**
     * CameraTracker에 새로운 카메라를 추가합니다
     *
     * @param x          카메라 X좌표
     * @param y          카메라 Y좌표
     * @param z          카메라 Z좌표
     * @param pan        카메라 yaw방향
     * @param tilt       카메라 pitch방향
     * @param camProfile 카메라 정보
     * @param ip         카메라 source ip
     * @see Camera
     */
    public void addCamera(float x, float y, float z, float pan, float tilt, float[] camProfile, String ip) {
        Camera c = new Camera(x, y, z, pan, tilt, ip, cam.size());
        c.setCameraParameter(camProfile);

        cam.add(c);
    }

    /**
     * 모든 카메라로부터 traceLine을 새로 로드합니다
     *
     * @see #findPoint()
     * @see Camera#updateTraceLine()
     */
    public void updateCamera() {
        ArrayList<CameraRunnable> runs = new ArrayList<>();
        ArrayList<Thread> ths = new ArrayList<>();
        for (Camera camera : cam) {
            Thread th = new Thread(new CameraRunnable(camera));
            ths.add(th);
            th.start();
        }
        for (Thread th : ths) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CameraRunnable implements Runnable {
        Camera camera;

        CameraRunnable(Camera camera) {
            this.camera = camera;
        }

        @Override
        public void run() {
            camera.updateTraceLine();
            //camera.showInfo();
        }
    }

    /**
     * traceLine으로부터 point를 찾아 points에 저장합니다
     *
     * @see #updateCamera()
     * @see #points
     */
    public void findPoint() {
        updateCamera(); // Capture camera

        float th = 1f; // 이 값보다 먼 거리의 traceLine은 서로 다른 tracking point를 지나는 것으로 간주함
        points.clear();
        ArrayList<Vector3>[] tl = new ArrayList[cam.size()]; // i번 카메라에서 캡처된 traceLine들을 저장함
        for (int i = 0; i < tl.length; i++)
            tl[i] = cam.get(i).getTraceLine(); // camera에서 traceLine을 불러옵니다

        float nearDist = th;
        Vector3 near = new Vector3();

        //traceLine으로부터 교차점 찾기
        for (int i = 0; i < tl.length - 1; i++)
            for (int j = 0; j < tl[i].size(); j++)
                for (int k = i + 1; k < tl.length; k++) {
                    nearDist = th;
                    for (int l = 0; l < tl[k].size(); l++)
                        if (i != k) { // 비교하는 traceLine이 자신이 아닐 때만
                            Vector3 p1 = cam.get(i).getPos(), v1 = tl[i].get(j), p2 = cam.get(k).getPos(), v2 = tl[k].get(l);
                            float dist = distLineToLine(p1, v1, p2, v2);

                            show("");

                            if (dist < th) {
                                show("I:점 감지됨");
                                Vector3 v3 = v1.cross(v2).cross(v1); // 최단거리 벡터와 v1이 이루는 평면의 법선벡터  v3 = (v1×v2)×v1
                                float u = v3.dot(p1.minus(p2)) / v2.dot(v3); // u = (v3·(p1-p2)) / (v2·v3)
                                Vector3 p3 = p2.plus(v2.mul(u)); // l2측 최근점   p3 = p2 + u·v2
                                Vector3 p4 = p3.plus(v1.cross(v2).mul(-dist / v1.cross(v2).abs())); // l1측 최근점  p4 = p3 + ( -dist / abs(v1×v2) )·(v1×v2)
                                Vector3 p = p3.plus(p4).mul(0.5f); // (p3 + p4) / 2
                                //show("직선 (p1,v1)측 근접점: " + p3.getX() + ", " + p3.getY() + ", " + p3.getZ());
                                //show("직선 (p2,v2)측 근접점: " + p4.getX() + ", " + p4.getY() + ", " + p4.getZ());
                                if (dist < nearDist) { // 같은 카메라 안에서 하나만 인식(고스트 현상 방지)
                                    if (nearDist == th) {
                                        near = p;
                                        nearDist = dist;
                                    } else {
                                        near = null;
                                        nearDist = -1;
                                    }
                                }
                                show("중간점: " + p.toString());
                            }
                            show("dist: " + dist + "(" + i + "번 카메라의 " + j + "번째 광선과 " + k + "번 카메라의 " + l + "번째 광선)");
                        }
                    if (nearDist != th && near != null) {
                        points.add(near);

                    }
                }

        showPoints();
    }

    /**
     * 두 직선 사이의 거리를 반환합니다.
     *
     * @param p1
     * @param v1
     * @param p2
     * @param v2
     * @return
     */
    float distLineToLine(Vector3 p1, Vector3 v1, Vector3 p2, Vector3 v2) {
        return Math.abs(p2.minus(p1).dot(v1.cross(v2))) / v1.cross(v2).abs(); // |(p2 - p1)·(v1×v2)| / abs(v1×v2)
    }

    /**
     * 추적된 Point를 출력합니다.
     */
    public void showPoints() {
        for (Vector3 point : points) {
            show(point.toString());
        }
    }

    /**
     * 추적된 Point를 반환합니다.
     */
    public Vector3[] getPoints() {
        Vector3[] point = new Vector3[points.size()];
        for (int i = 0; i < point.length; i++)
            point[i] = points.get(i);

        return point;
    }

    /**
     * 카메라 오브젝트를 반환합니다.
     */
    public Camera[] getCameras() {
        Camera[] cam = new Camera[this.cam.size()];
        for (int i = 0; i < cam.length; i++)
            cam[i] = this.cam.get(i);

        return cam;
    }
}


