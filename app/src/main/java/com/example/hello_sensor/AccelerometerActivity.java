package com.example.hello_sensor;

import androidx.appcompat.app.AppCompatActivity;

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
    private static final float ALPHA = 0.1f; // might change to 0.5f
    private TextView x;
    private TextView y;
    private TextView z;
    private TextView tilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        x = findViewById(R.id.xTextView);
        y = findViewById(R.id.yTextView);
        z = findViewById(R.id.zTextView);
        tilt = findViewById(R.id.tiltTextView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastAccelerometer = applyLowPassFilter(sensorEvent.values, lastAccelerometer);
        x.setText("X:   " + String.format("%.2f", lastAccelerometer[0]));
        y.setText("Y:   " + String.format("%.2f", lastAccelerometer[1]));
        z.setText("Z:   " + String.format("%.2f", lastAccelerometer[2]));
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

    // https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    private float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null ) {
            return input;
        }
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}