package com.zhongjh.rotatefragment.ui.home;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhongjh.rotatefragment.R;
import com.zhongjh.rotatefragment.base.MySupportFragment;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * 这是可控制旋转的fragment
 */
public class ContentRotateFragment extends MySupportFragment {
    private static final String ARG_MENU = "arg_menu";

    private TextView mTvContent;
    private Button mBtnNext;

    private String mMenu;

    // region 重力感应

    protected boolean isLandscape = false;      // 默认是竖屏

    protected SensorManager sm;
    protected OrientationSensorListener listener; // 重力感应监听
    protected Sensor sensor;

    protected ContentObserver mSettingsContentObserver;    // 监控方向锁定

    // endregion

    public static ContentRotateFragment newInstance(String menu) {

        Bundle args = new Bundle();
        args.putString(ARG_MENU, menu);

        ContentRotateFragment fragment = new ContentRotateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mMenu = args.getString(ARG_MENU);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTvContent = view.findViewById(R.id.tv_content);
        mBtnNext = view.findViewById(R.id.btn_next);

        mTvContent.setText("这是一个可以旋转的fragment");

        // region 重力感应

        // 初始化重力感应器
        sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        listener = new OrientationSensorListener(mHandler, _mActivity);

        // 设置事件
        mSettingsContentObserver = new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // 启动重力感应
                if (Settings.System.getInt(_mActivity.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                    // 1为自动旋转模式，0为锁定竖屏模式
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                } else {
                    sm.unregisterListener(listener);
                }
            }

        };
        _mActivity.getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);

        // endregion
    }

    @Override
    public boolean onBackPressedSupport() {
        // ContentFragment是ShopFragment的栈顶子Fragment,可以在此处理返回按键事件
        return super.onBackPressedSupport();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        // 启动重力感应
        if (Settings.System.getInt(_mActivity.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            // 1为自动旋转模式，0为锁定竖屏模式
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        // 重新开启重力感应
        if (Settings.System.getInt(_mActivity.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        // 重新开启重力感应
        if (Settings.System.getInt(_mActivity.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        super.onFragmentResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 关闭重力感应
        sm.unregisterListener(listener);
        // 关闭监控手机方向锁定
        _mActivity.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    // region 重力感应相关方法

    /**
     * 接收重力感应监听的结果，来改变屏幕朝向
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            if (msg.what == 888) {
                int orientation = msg.arg1;

                /**
                 * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                 */
                if (orientation > 45 && orientation < 135) {
                    _mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    isLandscape = true;
                } else if (orientation > 135 && orientation < 225) {
                    _mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    isLandscape = false;
                } else if (orientation > 225 && orientation < 315) {
                    _mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isLandscape = true;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    _mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isLandscape = false;
                }
            }
        }
    };

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {

        private boolean isClickFullScreen;        // 记录全屏按钮的状态，默认false
        private boolean isEffetSysSetting = false;   // 手机系统的重力感应设置是否生效，默认无效，想要生效改成true就好了
        private boolean isOpenSensor = true;      // 是否打开传输，默认打开
        private boolean isLandscape = false;      // 默认是竖屏
        private boolean isChangeOrientation = true;  // 记录点击全屏后屏幕朝向是否改变，默认会自动切换

        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;
        private Activity activity;

        public OrientationSensorListener(Handler handler, Activity activity) {
            rotateHandler = handler;
            this.activity = activity;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }


            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if (isEffetSysSetting) {
                try {
                    int isRotate = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) return;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // 只有点了按钮时才需要根据当前的状态来更新状态
            if (isClickFullScreen) {
                if (isLandscape && screenIsPortrait(orientation)) {           // 之前是横屏，并且当前是竖屏的状态
                    updateState(false, false, true, true);
                } else if (!isLandscape && screenIsLandscape(orientation)) {  // 之前是竖屏，并且当前是横屏的状态
                    updateState(true, false, true, true);
                } else if (isLandscape && screenIsLandscape(orientation)) {    // 之前是横屏，现在还是横屏的状态
                    isChangeOrientation = false;
                } else if (!isLandscape && screenIsPortrait(orientation)) {  // 之前是竖屏，现在还是竖屏的状态
                    isChangeOrientation = false;
                }
            }

            // 判断是否要进行中断信息传递
            if (!isOpenSensor) {
                return;
            }

            if (rotateHandler != null) {
                rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }

        /**
         * 当前屏幕朝向是否竖屏
         *
         * @param orientation
         * @return
         */
        private boolean screenIsPortrait(int orientation) {
            return (((orientation > 315 && orientation <= 360) || (orientation >= 0 && orientation <= 45))
                    || (orientation > 135 && orientation <= 225));
        }

        /**
         * 当前屏幕朝向是否横屏
         *
         * @param orientation
         * @return
         */
        private boolean screenIsLandscape(int orientation) {
            return ((orientation > 45 && orientation <= 135) || (orientation > 225 && orientation <= 315));
        }


        /**
         * 更新状态
         *
         * @param isLandscape         横屏
         * @param isClickFullScreen   全屏点击
         * @param isOpenSensor        打开传输
         * @param isChangeOrientation 朝向改变
         */
        private void updateState(boolean isLandscape, boolean isClickFullScreen, boolean isOpenSensor, boolean isChangeOrientation) {
            this.isLandscape = isLandscape;
            this.isClickFullScreen = isClickFullScreen;
            this.isOpenSensor = isOpenSensor;
            this.isChangeOrientation = isChangeOrientation;
        }

    }

    // endregion

}
