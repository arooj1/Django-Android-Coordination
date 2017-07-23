package com.example.ahmed.syncserver;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class CrowdData {
    private String uuid;
    private String acc;
    private String gyro;
    private String mag;
    private String light;
    private String wifi;
    private float lat;
    private float lon;
    private int floor;

    public CrowdData(String _uuid, String _acc, String _gyro,
                      String _mag, String _light, String _wifi,
                      float _lat, float _lon, int _floor){
        this.uuid = _uuid;
        this.acc = _acc;
        this.gyro = _gyro;
        this.mag = _mag;
        this.light = _light;
        this.wifi = _wifi;
        this.lat = _lat;
        this.lon = _lon;
        this.floor = _floor;
    }
}
