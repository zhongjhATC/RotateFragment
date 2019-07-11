package com.zhongjh.rotatefragment;

import android.os.Bundle;

import com.zhongjh.rotatefragment.base.MySupportActivity;
import com.zhongjh.rotatefragment.base.MySupportFragment;
import com.zhongjh.rotatefragment.ui.home.HomeFragment;

import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * 流程式demo  tip: 多使用右上角的"查看栈视图"
 * Created by YoKeyword on 16/1/29.
 */
public class MainActivity extends MySupportActivity{

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySupportFragment fragment = findFragment(HomeFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, HomeFragment.newInstance());
        }
        initView();
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
    }

    private void initView() {
    }


}
