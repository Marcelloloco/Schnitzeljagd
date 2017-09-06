package de.htwberlin.f4.ai.ma.fingerprint.accesspointsample;

/**
 * Created by Johann Winter
 *
 * A AccessPointSampleImpl consists of a MAC-Address and a signal strength (RSSI) in dBm.
 */

class AccessPointSampleImpl implements AccessPointSample {

    private String macAddress;
    private int RSSI;

    AccessPointSampleImpl(String macAddress, int RSSI) {
        this.macAddress = macAddress;
        this.RSSI = RSSI;
    }



    public int getRSSI() {
        return this.RSSI;
    }

    public String getMacAddress() {
        return this.macAddress;
    }


    // Convert the RSSI (dBm) to milliwatt
    public double getMilliwatt() {
        return Math.pow(10, this.getRSSI()/10);
    }

}