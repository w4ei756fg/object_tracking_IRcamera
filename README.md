# IR Object Tracking

IR Object Tracking은 물체의 위치와 방향을 추적하는 기능을 합니다.

IR Object Tracking은 '흙수저 VR 프로젝트'의 일부입니다.

IR Object Tracking detects object and gives you its position and direction.

It is a part of my 'Cheap VR under $200' project.

![Screenshot](./pictures/1.png)

## Require
- OpenCV v3.4.6

## Default tracker setting

- 0: \[13.6, 0, 0\], 172.30.1.91
- 1: \[0,    0, 0\], 172.30.1.92

## Parts
- ESP32-CAM (OV2640)
- IR Filter
- IR LED (SFH485P)

## Credit
- IR Object Tracking: Apache License 2.0 Copyright 2019 - 2020, Wei756([@wei756](http://github.com/wei756)) (kjhoon1122@naver.com)
- [Httpd](./esp32-cam/CameraWebServer/app_httpd.cpp): Apache License 2.0 Copyright 2015 - 2016, Espressif Systems (Shanghai) PTE LTD