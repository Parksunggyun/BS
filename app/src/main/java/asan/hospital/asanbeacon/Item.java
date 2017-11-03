package asan.hospital.asanbeacon;

/*
 * Created by 15U560 on 2017-10-26.
 */

public class Item {

    private String address;
    private double rssi;
    private int txPower;
    private double distance;
    private int major;
    private int minor;

    public Item(String address, double rssi, int txPower, double distance, int major, int minor) {
        this.address = address;
        this.rssi = rssi;
        this.txPower = txPower;
        this.distance = distance;
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getAddress() {
        return address;
    }

    public double getRssi() {
        return rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public double getDistance() {
        return distance;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
