package com.example.hello_sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private static final float ALPHA = 0.1f; // might change this value
    private boolean switchInd = false;
    private ConstraintLayout background;
    private boolean colorSwitch = false;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        textView = findViewById(R.id.textViewDegrees);
        imageView = findViewById(R.id.imageViewCompass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        background = (ConstraintLayout) findViewById(R.id.background);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_2);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent == null) {
            return;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lastAccelerometer = filter(sensorEvent.values, lastAccelerometer);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            lastMagnetometer = filter(sensorEvent.values, lastMagnetometer);
        }
        updateOrientationAngles();
    }

    // https://www.raywenderlich.com/10838302-sensors-tutorial-for-android-getting-started
    private void updateOrientationAngles() {
        sensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        double degrees = (Math.toDegrees((double) orientation[0]) + 360.0) % 360.0;
        double angle = Math.round(degrees * 100) / 100;
        String direction = getDirection(degrees);
        textView.setText(angle + " degrees " + direction);
        if (direction.equals("N")) {
            if (switchInd) {
                vibrate();
                changeColour();
                playSound();
                switchInd = false;
            }
        } else {
            switchInd = true;
        }
        imageView.setRotation((float) angle * -1);
    }

    private void playSound() {
        mediaPlayer.start();
    }

    private void changeColour() {
        if (colorSwitch) {
            background.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            background.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        colorSwitch = !colorSwitch;
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

    // https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate-with-different-frequency
    // vibrate for 250 ms
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(250);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }

    // https://www.raywenderlich.com/10838302-sensors-tutorial-for-android-getting-started
    private String getDirection(double angle) {
        String direction = "";

        if (angle >= 345 || angle <= 15){
            direction = "N";
        }
        if (angle < 345 && angle > 285) {
            direction = "NW";
        }
        if (angle <= 285 && angle > 265) {
            direction = "W";
        }
        if (angle <= 265 && angle > 195) {
            direction = "SW";
        }
        if (angle <= 195 && angle > 165) {
            direction = "S";
        }
        if (angle <= 165 && angle > 105) {
            direction = "SE";
        }
        if (angle <= 105 && angle > 75) {
            direction = "E";
        }
        if (angle <= 75 && angle > 15) {
            direction = "NE";
        }
        return direction;
    }
}