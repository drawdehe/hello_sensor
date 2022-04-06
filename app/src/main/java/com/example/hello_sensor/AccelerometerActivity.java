package com.example.hello_sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private float[] lastAccelerometer = new float[3];
    private static final float ALPHA = 0.1f;
    private TextView x;
    private TextView y;
    private TextView z;
    private TextView direction;
    private ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        x = findViewById(R.id.xTextView);
        y = findViewById(R.id.yTextView);
        z = findViewById(R.id.zTextView);
        direction = findViewById(R.id.tiltTextView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        background = (ConstraintLayout) findViewById(R.id.background);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastAccelerometer = filter(sensorEvent.values, lastAccelerometer);
        x.setText("X:   " + String.format("%.2f", lastAccelerometer[0]));
        y.setText("Y:   " + String.format("%.2f", lastAccelerometer[1]));
        z.setText("Z:   " + String.format("%.2f", lastAccelerometer[2]));
        setDirection(lastAccelerometer);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL); //maybe change to game
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometerSensor);
    }

    // https://github.com/phishman3579/android-compass/blob/master/src/com/jwetherell/compass/common/LowPassFilter.java
    private float[] filter(float[] input, float[] prev) {
        if (input == null || prev == null)
            throw new NullPointerException("input and prev float arrays must be non-NULL");
        if (input.length != prev.length)
            throw new IllegalArgumentException("input and prev must be the same length");
        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
        }
        return prev;
    }

    private void setDirection(float[] lastAccelerometer) {
        if (lastAccelerometer[0] > 3) {
            direction.setText("LEFT");
            background.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (lastAccelerometer[1] > 3) {
            direction.setText("UP");
            background.setBackgroundColor(getResources().getColor(R.color.black));
        }
        if (lastAccelerometer[2] > 3) {
            direction.setText("FORWARD");
            background.setBackgroundColor(getResources().getColor(R.color.green));
        }
        if (lastAccelerometer[0] < -3) {
            direction.setText("RIGHT");
            background.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        if (lastAccelerometer[1] < -3) {
            direction.setText("UPSIDE DOWN");
            background.setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
        if (lastAccelerometer[2]< -3) {
            direction.setText("BACKSIDE UP");
            background.setBackgroundColor(getResources().getColor(R.color.yellow));
        }
    }
}