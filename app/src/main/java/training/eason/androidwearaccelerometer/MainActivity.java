package training.eason.androidwearaccelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends WearableActivity {

    @BindView(R.id.accEventButton)
    Button mAccEventButton;
    @BindView(R.id.accSamplingRateTextView)
    TextView mAccSamplingRate;
    @BindView(R.id.accXTextView)
    TextView mAccXTextView;
    @BindView(R.id.accYTextView)
    TextView mAccYTextView;
    @BindView(R.id.accZTextView)
    TextView mAccZTextView;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;
    private Sensor mSensor;
    private Unbinder mUnbinder;
    private int mCurrentStatus = 0;
    private long mPrevMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Enables Always-on
        setAmbientEnabled();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
//    }

    @Override
    protected void onPause() {
        mAccEventButton.setText("開始");
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.accEventButton)
    protected void onAccEventButtonClick(View view) {
        if (mCurrentStatus == 0) {
            mAccEventButton.setText("停止");
            mCurrentStatus = 1;
            mSensorEventListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(final SensorEvent sensorEvent) {
                    if (sensorEvent.accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE) {
                        final long nowMillis = TimeUnit.MILLISECONDS.convert(sensorEvent.timestamp, TimeUnit.NANOSECONDS);
                        final long diffMillis = (nowMillis - mPrevMillis);
                        mPrevMillis = nowMillis;

                        mAccSamplingRate.setText(String.format("Sampling rate per second: %s", (1000 / diffMillis)));
                        mAccXTextView.setText(String.format(Locale.getDefault(), "X: %s", sensorEvent.values[0]));
                        mAccYTextView.setText(String.format(Locale.getDefault(), "Y: %s", sensorEvent.values[1]));
                        mAccZTextView.setText(String.format(Locale.getDefault(), "Z: %s", sensorEvent.values[2]));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }

            };

            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorManager.unregisterListener(mSensorEventListener);
            mAccEventButton.setText("開始");
            mCurrentStatus = 0;
        }
    }
}
