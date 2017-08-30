package de.htwberlin.f4.ai.ma.android.record;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.BaseActivity;

/**
 * Created by benni on 22.07.2017.
 */

public class RecordViewImpl extends BaseActivity implements RecordView {

    private RecordController controller;

    private TextView accelerationX;
    private TextView accelerationY;
    private TextView accelerationZ;

    private TextView accelerationLinearX;
    private TextView accelerationLinearY;
    private TextView accelerationLinearZ;

    private TextView gravityX;
    private TextView gravityY;
    private TextView gravityZ;

    private TextView gyroscopeX;
    private TextView gyroscopeY;
    private TextView gyroscopeZ;

    private TextView gyroscopeUncalibratedX;
    private TextView gyroscopeUncalibratedY;
    private TextView gyroscopeUncalibratedZ;
    private TextView gyroscopeUncalibratedDriftX;
    private TextView gyroscopeUncalibratedDriftY;
    private TextView gyroscopeUncalibratedDriftZ;

    private TextView magneticFieldX;
    private TextView magneticFieldY;
    private TextView magneticFieldZ;

    private TextView compassFusion;
    private TextView compassSimple;

    private TextView barometer;

    private TextView periodValue;

    private SeekBar periodSeekbar;


    public RecordViewImpl() {
        controller = new RecordControllerImpl();
        controller.setView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_sensor_record, contentFrameLayout);

        setTitle("Aufnehmen");

        accelerationX = (TextView) findViewById(R.id.fragment_record_acc_x);
        accelerationY = (TextView) findViewById(R.id.fragment_record_acc_y);
        accelerationZ = (TextView) findViewById(R.id.fragment_record_acc_z);

        accelerationLinearX = (TextView) findViewById(R.id.fragment_record_acc_linear_x);
        accelerationLinearY = (TextView) findViewById(R.id.fragment_record_acc_linear_y);
        accelerationLinearZ = (TextView) findViewById(R.id.fragment_record_acc_linear_z);

        gravityX = (TextView) findViewById(R.id.fragment_record_gravity_x);
        gravityY = (TextView) findViewById(R.id.fragment_record_gravity_y);
        gravityZ = (TextView) findViewById(R.id.fragment_record_gravity_z);

        gyroscopeX = (TextView) findViewById(R.id.fragment_record_gyroscope_x);
        gyroscopeY = (TextView) findViewById(R.id.fragment_record_gyroscope_y);
        gyroscopeZ = (TextView) findViewById(R.id.fragment_record_gyroscope_z);

        gyroscopeUncalibratedX = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_x);
        gyroscopeUncalibratedY = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_y);
        gyroscopeUncalibratedZ = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_z);
        gyroscopeUncalibratedDriftX = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_x);
        gyroscopeUncalibratedDriftY = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_y);
        gyroscopeUncalibratedDriftZ = (TextView) findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_z);

        magneticFieldX = (TextView) findViewById(R.id.fragment_record_magneticfield_x);
        magneticFieldY = (TextView) findViewById(R.id.fragment_record_magneticfield_y);
        magneticFieldZ = (TextView) findViewById(R.id.fragment_record_magneticfield_z);

        compassFusion = (TextView) findViewById(R.id.fragment_record_compass_fusion);
        compassSimple = (TextView) findViewById(R.id.fragment_record_compass_simple);

        barometer = (TextView) findViewById(R.id.fragment_record_barometer);

        periodValue = (TextView) findViewById(R.id.fragment_record_period);

        periodSeekbar = (SeekBar) findViewById(R.id.fragment_record_period_seekbar);
        periodSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                periodValue.setText(String.valueOf(i));
                if (controller != null) {
                    controller.onSavePeriodChanged(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        periodSeekbar.setProgress(250);


        Button btnStart = (Button) findViewById(R.id.fragment_record_start_btn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
            }
        });

        Button btnStop = (Button) findViewById(R.id.fragment_record_stop_btn);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopClicked();
                }
            }
        });
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_sensor_record, container, false);

        accelerationX = (TextView) root.findViewById(R.id.fragment_record_acc_x);
        accelerationY = (TextView) root.findViewById(R.id.fragment_record_acc_y);
        accelerationZ = (TextView) root.findViewById(R.id.fragment_record_acc_z);

        accelerationLinearX = (TextView) root.findViewById(R.id.fragment_record_acc_linear_x);
        accelerationLinearY = (TextView) root.findViewById(R.id.fragment_record_acc_linear_y);
        accelerationLinearZ = (TextView) root.findViewById(R.id.fragment_record_acc_linear_z);

        gravityX = (TextView) root.findViewById(R.id.fragment_record_gravity_x);
        gravityY = (TextView) root.findViewById(R.id.fragment_record_gravity_y);
        gravityZ = (TextView) root.findViewById(R.id.fragment_record_gravity_z);

        gyroscopeX = (TextView) root.findViewById(R.id.fragment_record_gyroscope_x);
        gyroscopeY = (TextView) root.findViewById(R.id.fragment_record_gyroscope_y);
        gyroscopeZ = (TextView) root.findViewById(R.id.fragment_record_gyroscope_z);

        gyroscopeUncalibratedX = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_x);
        gyroscopeUncalibratedY = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_y);
        gyroscopeUncalibratedZ = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_z);
        gyroscopeUncalibratedDriftX = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_x);
        gyroscopeUncalibratedDriftY = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_y);
        gyroscopeUncalibratedDriftZ = (TextView) root.findViewById(R.id.fragment_record_gyroscope_uncalibrated_drift_z);

        magneticFieldX = (TextView) root.findViewById(R.id.fragment_record_magneticfield_x);
        magneticFieldY = (TextView) root.findViewById(R.id.fragment_record_magneticfield_y);
        magneticFieldZ = (TextView) root.findViewById(R.id.fragment_record_magneticfield_z);

        compassFusion = (TextView) root.findViewById(R.id.fragment_record_compass_fusion);
        compassSimple = (TextView) root.findViewById(R.id.fragment_record_compass_simple);

        barometer = (TextView) root.findViewById(R.id.fragment_record_barometer);

        periodValue = (TextView) root.findViewById(R.id.fragment_record_period);

        periodSeekbar = (SeekBar) root.findViewById(R.id.fragment_record_period_seekbar);
        periodSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                periodValue.setText(String.valueOf(i));
                if (controller != null) {
                    controller.onSavePeriodChanged(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        periodSeekbar.setProgress(250);


        Button btnStart = (Button) root.findViewById(R.id.fragment_record_start_btn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
            }
        });

        Button btnStop = (Button) root.findViewById(R.id.fragment_record_stop_btn);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopClicked();
                }
            }
        });



        return root;
    }
*/

    @Override
    public void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
    }


    @Override
    public void updateAcceleration(float[] values) {
        accelerationX.setText(getString(R.string.x_caption) + " " + values[0]);
        accelerationY.setText(getString(R.string.y_caption) + " " + values[1]);
        accelerationZ.setText(getString(R.string.z_caption) + " " + values[2]);
    }

    @Override
    public void updateAccelerationLinear(float[] values) {
        accelerationLinearX.setText(getString(R.string.x_caption) + " " + values[0]);
        accelerationLinearY.setText(getString(R.string.y_caption) + " " + values[1]);
        accelerationLinearZ.setText(getString(R.string.z_caption) + " " + values[2]);
    }

    @Override
    public void updateGravity(float[] values) {
        gravityX.setText(getString(R.string.x_caption) + " " + values[0]);
        gravityY.setText(getString(R.string.y_caption) + " " + values[1]);
        gravityZ.setText(getString(R.string.z_caption) + " " + values[2]);
    }

    @Override
    public void updateGyroscope(float[] values) {
        gyroscopeX.setText(getString(R.string.x_caption) + " " + values[0]);
        gyroscopeY.setText(getString(R.string.y_caption) + " " + values[1]);
        gyroscopeZ.setText(getString(R.string.z_caption) + " " + values[2]);
    }

    @Override
    public void updateGyroscopeUncalibrated(float[] values) {
        gyroscopeUncalibratedX.setText(getString(R.string.x_caption) + " " + values[0]);
        gyroscopeUncalibratedY.setText(getString(R.string.y_caption) + " " + values[1]);
        gyroscopeUncalibratedZ.setText(getString(R.string.z_caption) + " " + values[2]);
        gyroscopeUncalibratedDriftX.setText(getString(R.string.x_caption) + " " + values[3]);
        gyroscopeUncalibratedDriftY.setText(getString(R.string.x_caption) + " " + values[4]);
        gyroscopeUncalibratedDriftZ.setText(getString(R.string.x_caption) + " " + values[5]);
    }

    @Override
    public void updateMagneticField(float[] values) {
        magneticFieldX.setText(getString(R.string.x_caption) + " " + values[0]);
        magneticFieldY.setText(getString(R.string.y_caption) + " " + values[1]);
        magneticFieldZ.setText(getString(R.string.z_caption) + " " + values[2]);
    }

    @Override
    public void updateCompassFusion(float value) {
        compassFusion.setText(getString(R.string.compass_fusion) + ": " + value);
    }

    @Override
    public void updateCompassSimple(float value) {
        compassSimple.setText(getString(R.string.compass_simple) + ": " + value);
    }

    @Override
    public void updatePressure(float value) {
        barometer.setText(getString(R.string.measure_pressure) + " " + value);
    }

    @Override
    public Context getContext() {
        return this;
    }

}