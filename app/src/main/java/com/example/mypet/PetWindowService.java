package com.example.mypet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;


public class PetWindowService extends Service {

    // 打开APP主界面
    private ImageView imgHome;
    // 打开淘宝
    private ImageView imgCart;
    // 打开相机
    private ImageView imgCamera;
    // 宠物名
    private TextView petNameView;

    // 手机窗体布局的管理
    private WindowManager wm;
    // 手机窗体布局参数
    private WindowManager.LayoutParams params;
    // 手机窗体宽度
    private int winWidth;
    // 手机窗体高度
    private int winHeight;
    // 宠物的View
    View petView;

    private int startX;
    private int startY;

    ImageView imageView;

    private int count = 0;

    private String pet_name;
    private String pet_mode;
    private long pet_age;

    // 释放时间
    long showTime;
    // 收回时间
    long backTime;

    final String TAG = "XING";

    //String petName = "山竹";

    // 存放宠物的集合
    Map<String, int[]> petSet = new HashMap<>();

    // 宠物分类
    final int[] foxSet = {R.drawable.fox1, R.drawable.fox2, R.drawable.fox3};
    final int[] hsSet = {R.drawable.haishi1, R.drawable.haishi2, R.drawable.haishi3};
    final int[] mwSet = {R.drawable.miaowa1, R.drawable.miaowa2, R.drawable.miaowa3};
    final int[] szSet = {R.drawable.shanzhu1, R.drawable.shanzhu2, R.drawable.shanzhu3};
    final int[] wzSet = { R.drawable.wz1, R.drawable.wz2, R.drawable.wz3, R.drawable.wz4, R.drawable.wz5};

    @Override
    public void onCreate() {

        setPetSet();

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 服务启动，打开
        showPetView();

        // 实现全部监听事件
        exeListener();

        super.onCreate();
    }

    // 存放宠物进化集合
    private void setPetSet() {
        petSet.put("狐狸", foxSet);
        petSet.put("海狮", hsSet);
        petSet.put("山竹", szSet);
        petSet.put("妙蛙", mwSet);
        petSet.put("旺仔", wzSet);
    }

    // 菜单里的功能实现
    private void exeListener() {
        // 实现单击打开菜单,双击关闭菜单
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count % 2 == 1){
                    imgHome.setVisibility(View.VISIBLE);
                    imgCamera.setVisibility(View.VISIBLE);
                    imgCart.setVisibility(View.VISIBLE);
                }
                else {
                    imgHome.setVisibility(View.GONE);
                    imgCamera.setVisibility(View.GONE);
                    imgCart.setVisibility(View.GONE);
                    count = 0;
                }


                Log.d(TAG, "click ok");
            }
        });

        // 点击imgHome打开主页面
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetWindowService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // 点击imgCamera打开相机
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intent);
            }
        });

        // 点击imgCart打开淘宝
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("taobao://shop.m.taobao.com"));
                Intent intent = new Intent();
                intent.setAction("Android.intent.action.VIEW");
                intent.setClassName("com.taobao.taobao", "com.taobao.tao.welcome.Welcome");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void showPetView() {

        //获取屏幕宽度和高度
        winWidth = wm.getDefaultDisplay().getWidth();
        winHeight = wm.getDefaultDisplay().getHeight();
        params = new WindowManager.LayoutParams();

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 自定义宠物窗体
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // 左上角对齐
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = LAYOUT_FLAG;

        // 加入宠物窗体
        petView = View.inflate(this, R.layout.activity_pet, null);
        imageView = petView.findViewById(R.id.petView);
        imgHome = petView.findViewById(R.id.imgHome);
        imgCart = petView.findViewById(R.id.imgCart);
        imgCamera = petView.findViewById(R.id.imgCamera);
        petNameView = petView.findViewById(R.id.petName);


        // 实现拖拽宠物
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.x > winWidth - petView.getWidth()) {
                            params.x = winWidth - petView.getWidth();
                        }

                        if (params.y > winHeight - petView.getHeight()) {
                            params.y = winHeight - petView.getHeight();
                        }

                        wm.updateViewLayout(petView, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        wm.addView(petView, params);

    }

    // 根据宠物设置变换状态
    public int changePet(String mode, long age) {
        int pet_img;
        switch (mode) {
            case "狐狸" :
                if (age < 60) {
                    pet_img = foxSet[0];
                } else if (age < 3600) {
                    pet_img = foxSet[1];
                } else {
                    pet_img = foxSet[2];
                }
                break;
            case "山竹" :
                if (age < 60) {
                    pet_img = szSet[0];
                } else if (age < 3600) {
                    pet_img = szSet[1];
                } else {
                    pet_img = szSet[2];
                }
                break;
            case "妙蛙" :
                if (age < 60) {
                    pet_img = mwSet[0];
                } else if (age < 3600) {
                    pet_img = mwSet[1];
                } else {
                    pet_img = mwSet[2];
                }
                break;
            case "海狮" :
                if (age < 60) {
                    pet_img = hsSet[0];
                } else if (age < 3600) {
                    pet_img = hsSet[1];
                } else {
                    pet_img = hsSet[2];
                }
                break;
            case "旺仔" :
                if (age < 60) {
                    pet_img = wzSet[0];
                } else if (age < 3600) {
                    pet_img = wzSet[1];
                } else if (age < 7200) {
                    pet_img = wzSet[2];
                } else if (age < 14400) {
                    pet_img = wzSet[3];
                } else {
                    pet_img = wzSet[4];
                }
                break;
            default:
                pet_img = wzSet[0];
        }
        return pet_img;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pet_name = intent.getStringExtra("pet_name");
        pet_mode = intent.getStringExtra("pet_mode");
        if (intent.getStringExtra("pet_age") != null)
            pet_age = Integer.parseInt(intent.getStringExtra("pet_age"));
        else
            pet_age = 0;

        // 设置宠物名字、形态
        petNameView.setText(pet_name);
        imageView.setImageResource(changePet(pet_mode, pet_age));

        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (wm != null && petView != null) {
            wm.removeView(petView);
        }
        super.onDestroy();
    }
}
