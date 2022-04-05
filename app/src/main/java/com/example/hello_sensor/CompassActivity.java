package com.example.hello_sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// check https://www.youtube.com/watch?v=IzzGVLnZBfQ&ab_channel=SarthiTechnology

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private TextView textView;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private static final float ALPHA = 0.1f;
    private boolean entering = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //textView = findViewById(R.id.textId);
        //imageView = findViewById(R.id.imageId);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent == null) {
            return;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lastAccelerometer = applyLowPassFilter(sensorEvent.values, lastAccelerometer);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            lastMagnetometer = applyLowPassFilter(sensorEvent.values, lastMagnetometer);
        }
        updateOrientationAngles();
    }

    // https://www.raywenderlich.com/10838302-sensors-tutorial-for-android-getting-started
    private void updateOrientationAngles() {
        sensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        double degrees = (Math.toDegrees((double)orientation[0]) + 360.0) % 360.0;
        double angle = Math.round(degrees * 100) / 100;
        String direction = getDirection(degrees);
        textView.setText(angle + " degrees " + direction);
        if (direction.equals("N")) {
            if (entering) {
                vibrate();
                entering = false;
            }
        } else {
            entering = true;
        }
        imageView.setRotation((float) angle * - 1);
    }

    // https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate-with-different-frequency
    // Vibrate for 150 milliseconds
    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

    // https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    private float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null ){
            return input;
        }
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL); //maybe change to game
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL); //maybe change to game
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }

    // taken from https://www.raywenderlich.com/10838302-sensors-tutorial-for-android-getting-started
    private String getDirection(double angle) {
        String direction = "";

        if (angle >= 350 || angle <= 10){
            direction = "N";
        }
        if (angle < 350 && angle > 280) {
            direction = "NW";
        }
        if (angle <= 280 && angle > 260) {
            direction = "W";
        }
        if (angle <= 260 && angle > 190) {
            direction = "SW";
        }
        if (angle <= 190 && angle > 170) {
            direction = "S";
        }
        if (angle <= 170 && angle > 100) {
            direction = "SE";
        }
        if (angle <= 100 && angle > 80) {
            direction = "E";
        }
        if (angle <= 80 && angle > 10) {
            direction = "NE";
        }
        return direction;
    }
}
