package de.htwberlin.f4.ai.ma.indoorroutefinder.gps.node;

import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.Node;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.NodeFactory;

public class GlobalNodeImpl implements GlobalNode {

    private static final String LOG_PREFIX = "GLOBALNODE";

    private String id;
    private String description;
    private Fingerprint fingerprint;
    private String coordinates;
    private String picturePath;
    private double globalCalculationInaccuracyRating;
    private double latitude;
    private double longitude;
    private double altitude;

    GlobalNodeImpl(String id, String description, Fingerprint fingerprint, String coordinates, String picturePath, double globalCalculationInaccuracyRating, double latitude, double longitude, double altitude) {
        this.id = id;
        this.description = description;
        this.fingerprint = fingerprint;
        this.coordinates = coordinates;
        this.picturePath = picturePath;
        this.globalCalculationInaccuracyRating = globalCalculationInaccuracyRating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    GlobalNodeImpl(Node n) {
        this.id = n.getId();
        this.description = n.getDescription();
        this.fingerprint = n.getFingerprint();
        this.coordinates = n.getCoordinates();
        this.picturePath = n.getPicturePath();
        this.setAdditionalInfo(n.getAdditionalInfo());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Fingerprint getFingerprint() {
        return this.fingerprint;
    }

    @Override
    public String getCoordinates() {
        return this.coordinates;
    }

    @Override
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String getPicturePath() {
        return this.picturePath;
    }

    @Override
    public String getAdditionalInfo() {
        if (this.globalCalculationInaccuracyRating == Double.NaN) {
            return "";
        }
        else {
            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
            AdditionalInfo info = new AdditionalInfo(this.globalCalculationInaccuracyRating, this.latitude, this.longitude, this.altitude);
            return gson.toJson(info);
        }
    }

    @Override
    public void setAdditionalInfo(String additionalInfo) {
        if (additionalInfo != null && !additionalInfo.equals("")) {
            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
            try {
                this.setAdditionalInfoFromObject(gson.fromJson(additionalInfo, AdditionalInfo.class));
            } catch (JsonSyntaxException e) {
                Log.d(LOG_PREFIX, "Unable to parse additional info for '" + this.id + "'! Assuming none is set. String=" + additionalInfo, e);
                this.setAdditionalInfoFromObject(null);
            }
        }
        else {
            this.setAdditionalInfoFromObject(null);
        }
    }

    private void setAdditionalInfoFromObject(AdditionalInfo additionalInfo) {
        if (additionalInfo == null) {
            this.globalCalculationInaccuracyRating = Double.NaN;
            this.latitude = Double.NaN;
            this.longitude = Double.NaN;
            this.altitude = Double.NaN;
        }
        else {
            this.globalCalculationInaccuracyRating = additionalInfo.globalCalculationInaccuracyRating;
            this.latitude = additionalInfo.latitude;
            this.longitude = additionalInfo.longitude;
            this.altitude = additionalInfo.altitude;
        }
    }

    private AdditionalInfo getAdditionalInfoAsObject() {
        AdditionalInfo result = new AdditionalInfo();
        result.globalCalculationInaccuracyRating = this.globalCalculationInaccuracyRating;
        result.latitude = this.latitude;
        result.longitude = this.longitude;
        result.altitude = this.altitude;
        return result;
    }

    public double getGlobalCalculationInaccuracyRating() {
        return globalCalculationInaccuracyRating;
    }

    public void setGlobalCalculationInaccuracyRating(double globalCalculationInaccuracyRating) {
        this.globalCalculationInaccuracyRating = globalCalculationInaccuracyRating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Node getNode() {
        Node result = NodeFactory.createInstance(this.id, this.description, this.fingerprint, this.coordinates, this.picturePath, this.getAdditionalInfo());
        return result;
    }

    public boolean hasGlobalCoordinates() {
        return !Double.isNaN(this.globalCalculationInaccuracyRating);
    }

    public boolean hasGlobalAltitude() {
        return !Double.isNaN(this.altitude);
    }

    @Override
    public Location getLocation() {
        if (hasGlobalCoordinates()) {
            Location result = new Location("GlobalNode");
            result.setLatitude(this.latitude);
            result.setLongitude(this.longitude);
            if (hasGlobalAltitude()) {
                result.setAltitude(this.altitude);
            }
            return result;
        }
        return null;
    }

    /**
     * Helperclass for serialization.
     */
    private class AdditionalInfo implements Serializable {
        private double globalCalculationInaccuracyRating;
        private double latitude;
        private double longitude;
        private double altitude;

        private AdditionalInfo() {
            this.globalCalculationInaccuracyRating = Double.NaN;
            this.latitude = Double.NaN;
            this.longitude = Double.NaN;
            this.altitude = Double.NaN;
        }

        public AdditionalInfo(double globalCalculationInaccuracyRating, double latitude, double longitude, double altitude) {
            this.globalCalculationInaccuracyRating = globalCalculationInaccuracyRating;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        public double getGlobalCalculationInaccuracyRating() {
            return globalCalculationInaccuracyRating;
        }

        public void setGlobalCalculationInaccuracyRating(double globalCalculationInaccuracyRating) {
            this.globalCalculationInaccuracyRating = globalCalculationInaccuracyRating;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }
    }
}
