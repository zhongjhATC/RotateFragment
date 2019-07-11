package com.zhongjh.rotatefragment.ui.home;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zhongjh.rotatefragment.R;
import com.zhongjh.rotatefragment.base.BaseMainFragment;


public class HomeFragment extends BaseMainFragment{
    private static final String TAG = "Fragmentation";

    private Toolbar mToolbar;

    private Button mButton;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mButton = view.findViewById(R.id.btnTo);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(ContentFragment.newInstance("1"));
            }
        });

        mToolbar.setTitle(R.string.home);
        initToolbarNav(mToolbar, true);



    }

    /**
     * 类似于 Activity的 onNewIntent()
     */
    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);

        Toast.makeText(_mActivity, args.getString("from"), Toast.LENGTH_SHORT).show();
    }
}
