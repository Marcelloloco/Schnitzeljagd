package de.htwberlin.f4.ai.ma.measurement.modules;

/**
 * Created by benni on 28.07.2017.
 */

public interface DistanceModule{

    float getDistance(boolean stairs);
    void start();
    void stop();
}
