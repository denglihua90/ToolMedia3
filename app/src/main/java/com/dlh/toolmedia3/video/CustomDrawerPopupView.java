package com.dlh.toolmedia3.video;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dlh.toolmedia3.R;
import com.lxj.xpopup.core.DrawerPopupView;


/**
 * Description: 自定义抽屉弹窗
 * Create by dance, at 2018/12/20
 */
public class CustomDrawerPopupView extends DrawerPopupView {

    private int mW=0,mH=0;

    public CustomDrawerPopupView(@NonNull Context context,int w,int h) {
        super(context);
        mW=w;
        mH=h;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_speed_selection;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

//        CustomDrawerPopup2Binding.bind(getPopupImplView());
        Log.e("tag", "CustomDrawerPopupView onCreate");
//        text = findViewById(R.id.text);
//        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        //通过设置topMargin，可以让Drawer弹窗进行局部阴影展示
//        setPadding(0, 400, 0, 0);


    }

    @Override
    protected void onShow() {
        super.onShow();
//        text.setText(new Random().nextInt()+"");
        Log.e("tag", "CustomDrawerPopupView onShow");
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag", "CustomDrawerPopupView onDismiss");
    }

    @Override
    protected void addInnerContent() {

        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), drawerContentContainer, false);
        drawerContentContainer.addView(contentView);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        if(popupInfo!=null){
            if(mH==0){
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }else{
                params.height = mH;
            }


            if(getPopupWidth()>0) params.width = getPopupWidth();
            if(getMaxWidth() > 0) params.width = Math.min(params.width, getMaxWidth());
            contentView.setLayoutParams(params);
        }
    }

    @Override
    protected void doMeasure() {

        View contentView = drawerContentContainer.getChildAt(0);
        if(contentView==null) return;
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        if(popupInfo!=null){
            if(mH==0){
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }else{
                params.height = mH;
            }
            if(getPopupWidth()>0) params.width = getPopupWidth();
            if(getMaxWidth() > 0) params.width = Math.min(params.width, getMaxWidth());
            contentView.setLayoutParams(params);
        }
    }
}