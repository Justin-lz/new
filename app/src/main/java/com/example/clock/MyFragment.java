package com.example.clock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.clock.database.DatabaseDao;
import com.example.clock.database.DatabaseThread;
import com.example.clock.util.BitmapUtil;

import java.util.HashMap;

public class MyFragment extends Fragment {
    private EditText location;
    private EditText name;
    private EditText sex;
    private EditText age;
    private EditText time;
    private EditText resume;
    private ImageView avatar;
    String userId = "10037";

    private class myHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == DatabaseDao.getUserInformationFlag) {
               HashMap<String,String> infromation =(HashMap<String, String>) msg.getData().getSerializable("value");
               name.setText(infromation.get("userName"));
               sex.setText(infromation.get("userSex"));
               age.setText(infromation.get("userAge"));
               time.setText(infromation.get("userTime"));
               resume.setText(infromation.get("userResume"));
                try {
                    startlocation();
                }catch (Exception e){
                    System.out.println(e);
                }
                String avatarString = infromation.get("userAvatar");
                Bitmap img = BitmapUtil.Base642Bitmap(avatarString);
                avatar.setImageBitmap(img);
            }
            if (msg.what == DatabaseDao.updateUserInformationFlag) {
                String message;
                if (msg.getData().getBoolean("value")){
                    message = new String("修改成功");
                }else {
                    message = new String("修改失败");
                }
                Toast.makeText(MyFragment.this.getActivity(),message,Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_mine, container, false);
        FragmentActivity activity = getActivity();
        TextView textView =view.findViewById(R.id.user_name);
        Button change_btn = view.findViewById(R.id.user_change_btn);
        Button locate_btn = view.findViewById(R.id.user_locate_btn);
        location =(EditText) view.findViewById(R.id.user_location_value);
        name = (EditText) view.findViewById(R.id.user_name);
        sex = (EditText) view.findViewById(R.id.user_sex_value);
        age =(EditText) view.findViewById(R.id.user_age_value);
        time =(EditText) view.findViewById(R.id.user_time_value);
        resume=(EditText) view.findViewById(R.id.user_resume);
        avatar=(ImageView) view.findViewById(R.id.user_img);
        locate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startlocation();
                }catch (Exception e){
                    System.out.println(e.toString());
                }

            }
        });
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DatabaseThread.getUserInformation(new myHandler(),userId);


        return view;
    }

    public void startlocation() throws Exception{
        AMapLocationClient.updatePrivacyShow(getActivity(), true, true);
        AMapLocationClient.updatePrivacyAgree(getActivity(),true);
        AMapLocationClient mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);

        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation !=null ) {
                if (amapLocation.getErrorCode() == 0) {
                    location.setText(amapLocation.getCity());
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    Toast.makeText(MyFragment.this.getActivity(),"无法获取定位信息",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}

