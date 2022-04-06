package com.example.hello_sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
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
    private MediaPlayer mediaPlayer;
    private ImageView image;
    private float xFerrari;
    private float yFerrari;

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
        image = (ImageView) findViewById(R.id.ferrariImageView);
        mediaPlayer = MediaPlayer.create(this, R.raw.ferrari_sound);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastAccelerometer = filter(sensorEvent.values, lastAccelerometer);
        x.setText("X:   " + String.format("%.2f", lastAccelerometer[0]));
        y.setText("Y:   " + String.format("%.2f", lastAccelerometer[1]));
        z.setText("Z:   " + String.format("%.2f", lastAccelerometer[2]));
        setDirection(lastAccelerometer);

        xFerrari = image.getX();
        yFerrari = image.getY();
        image.setX(xFerrari);
        image.setY(yFerrari);
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
            background.setBackgroundColor(getResources().getColor(R.color.purple_200));
            vibrate();
            image.animate().scaleY(2.5F).setDuration(1500);
            image.animate().scaleX(2.5F).setDuration(1500);
        }
        if (lastAccelerometer[2] < -3) {
            direction.setText("BACKSIDE UP");
            background.setBackgroundColor(getResources().getColor(R.color.yellow));
        }
    }

    // https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate-with-different-frequency
    // vibrate for 100 ms
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(100);
        }
    }
}