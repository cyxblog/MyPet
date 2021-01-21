package com.example.mypet;


import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String TAG = "XING";

    // 创建宠物的控件
    private Spinner petMode;
    private EditText editName;
    private Button bn_createPet;

    // 展示宠物信息的控件
    private Spinner petNameList;
    private TextView showPetName;
    private TextView showPetMode;
    private TextView petAgeView;
    private ImageView showPetImg;
    private Button bn_showPet;
    private Button bn_backPet;
    private Button bn_delete;

    // 宠物年龄(秒）
    long petAgeSeconds = 0;
    // 宠物名字
    String petName = "wangzai";
    // 宠物类型
    String mode = "旺仔";

    // 暂时的名字
    String tmp_name;

    //List<String> pet_name_list = new ArrayList<>();
    List<Pet> pet_list;

    long openTime = 0;
    long closeTime = 0;

    // 宠物收回标志
    boolean back_flag = false;
    // 第一只宠物释放标志
    int first_open_flag = 0;
    // 记录上一只宠物名
    String last_name = "";

    UsePetDB usePetDB;

    ArrayAdapter<String> petNameAdapter;

    final String[] modeArray = {"狐狸", "山竹", "海狮", "妙蛙", "旺仔"};

    // 宠物分类
    final int[] foxSet = {R.drawable.fox1, R.drawable.fox2, R.drawable.fox3};
    final int[] hsSet = {R.drawable.haishi1, R.drawable.haishi2, R.drawable.haishi3};
    final int[] mwSet = {R.drawable.miaowa1, R.drawable.miaowa2, R.drawable.miaowa3};
    final int[] szSet = {R.drawable.shanzhu1, R.drawable.shanzhu2, R.drawable.shanzhu3};
    final int[] wzSet = { R.drawable.wz1, R.drawable.wz2, R.drawable.wz3, R.drawable.wz4, R.drawable.wz5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建宠物数据库
        usePetDB = new UsePetDB(this);
        usePetDB.open();

        // 初始化界面
        initialView();

        // 事件监听
        exeListen();

        Log.d(TAG, "onCreate: " + petName + " " + mode);


    }

    // 初始化界面
    public void initialView() {
        bn_showPet = findViewById(R.id.bn_show);
        bn_backPet = findViewById(R.id.bn_back);
        bn_createPet = findViewById(R.id.bn_createPet);
        bn_delete = findViewById(R.id.bn_delete);
        petAgeView = findViewById(R.id.petAgeView);
        petMode = findViewById(R.id.modeList);
        editName = findViewById(R.id.editName);
        petNameList = findViewById(R.id.petNameList);
        showPetMode = findViewById(R.id.showPetMode);
        showPetName = findViewById(R.id.showPetName);
        showPetImg = findViewById(R.id.showPetImg);

        // 提取数据库加载到petNameList
        pet_list = getAllPetInfo();

        petNameAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, R.id.petNameShow);
        petNameAdapter.setDropDownViewResource(R.layout.pet_name_list);
        for (int i = 0; i < pet_list.size(); i++) {
            petNameAdapter.add(pet_list.get(i).getPetName());
        }
        petNameList.setAdapter(petNameAdapter);
    }


    // 执行监听事件
    public void exeListen() {

        // 选择宠物类型
        petMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = modeArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 创建宠物
        bn_createPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 取名
                petName = editName.getText().toString();

                boolean sameName = false;

                for (int i = 0; i < petNameAdapter.getCount(); i++) {
                    if (petName.equals(petNameAdapter.getItem(i))) {
                        sameName = true;
                        break;
                    }
                }

                if (!editName.getText().toString().equals("") && !sameName){

                    Log.d(TAG, "onCreate: " + petName + " " + mode);

                    // 创建宠物
                    createPet(mode, petName, petAgeSeconds);
                    Toast.makeText(MainActivity.this,
                            "恭喜你拥有了一只" + mode + "类宠物，" + "它的名字叫" + petName,
                            Toast.LENGTH_LONG).show();
                    petNameAdapter.add(petName);

                } else {
                    Toast.makeText(MainActivity.this, "名字不能为空或重复，请再次输入！", Toast.LENGTH_LONG).show();
                }


            }
        });

        // 从数据库中选择一只宠物
        petNameList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tmp_name = (String) petNameList.getSelectedItem();
                Log.d(TAG, "onItemSelected: name:"+tmp_name);
                // 上一个宠物名字
                if (!last_name.equals("")){
                    last_name = showPetName.getText().toString();
                } else {
                    last_name = tmp_name;
                }

                showPetName.setText(tmp_name);
                showPetMode.setText(getPetMode(tmp_name));
                // 显示宠物图像
                showPetImg.setImageResource(changePet(showPetMode.getText().toString()));
                // 显示宠物年龄
                petAgeView.setText(showPetAge(getPetAge(tmp_name)));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 显示宠物年龄
        petAgeView.setText(showPetAge(getPetAge(editName.getText().toString())));
        
        // 释放宠物
        bn_showPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (first_open_flag == 0) {
                    // 第一只宠物释放时间
                    openTime = System.currentTimeMillis();
                }

                // 第二次起释放不同宠物，收回标志改变
                if (back_flag) {
                    openTime = System.currentTimeMillis();
                    back_flag = false;
                } else {
                    if (first_open_flag > 0 && !last_name.equals(showPetName.getText().toString())){
                        closeTime = System.currentTimeMillis();
                        petAgeSeconds += (closeTime - openTime) / 1000;
                        long new_age;
                        new_age = getPetAge(last_name) + petAgeSeconds;
                        // 更新宠物年龄到数据库
                        if (petAgeSeconds > 0) {
                            updatePetAge(last_name, new_age);
                        }
                        last_name = showPetName.getText().toString();
                        petAgeSeconds = 0;
                        back_flag = true;
                    }
                }
                first_open_flag++;

                // 传宠物参数到Service
                Intent intent = new Intent(MainActivity.this, PetWindowService.class);
                intent.putExtra("pet_name", showPetName.getText().toString())
                        .putExtra("pet_mode", showPetMode.getText().toString())
                        .putExtra("pet_age", String.valueOf(getPetAge(showPetName.getText().toString())));
                startService(intent);
               // finish();
                Log.d(TAG, "onClick: 成功释放");
            }
        });

        // 收回宠物
        bn_backPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 收回时间
                if (!back_flag){
                    closeTime = System.currentTimeMillis();
                    petAgeSeconds += (closeTime - openTime) / 1000;
                    long new_age;
                    new_age = getPetAge(showPetName.getText().toString()) + petAgeSeconds;
                    // 更新宠物年龄到数据库
                    updatePetAge(showPetName.getText().toString(), new_age);
                    Log.d(TAG, "onClick: db_name = " + showPetName.getText().toString());
                    Log.d(TAG, "onClick: time = " + petAgeSeconds);
                    Log.d(TAG, "onClick: all_time = " + new_age);
                    petAgeView.setText(showPetAge(getPetAge(showPetName.getText().toString())));
                    petAgeSeconds = 0;
                    back_flag = true;
                }
                Log.d(TAG, "onClick: close time=" + closeTime);
                Intent intent = new Intent(MainActivity.this, PetWindowService.class);
                stopService(intent);
            }
        });

        bn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePet(tmp_name);
            }
        });
        
    }

    // 根据宠物设置变换状态
    public int changePet(String mode) {
        int pet_img;
        long pet_age = getPetAge(showPetName.getText().toString());
        switch (mode) {
            case "狐狸" :
                if (pet_age < 60) {
                    pet_img = foxSet[0];
                } else if (pet_age < 3600) {
                    pet_img = foxSet[1];
                } else {
                    pet_img = foxSet[2];
                }
                break;
            case "山竹" :
                if (pet_age < 60) {
                    pet_img = szSet[0];
                } else if (pet_age < 3600) {
                    pet_img = szSet[1];
                } else {
                    pet_img = szSet[2];
                }
                break;
            case "妙蛙" :
                if (pet_age < 60) {
                    pet_img = mwSet[0];
                } else if (pet_age < 3600) {
                    pet_img = mwSet[1];
                } else {
                    pet_img = mwSet[2];
                }
                break;
            case "海狮" :
                if (pet_age < 60) {
                    pet_img = hsSet[0];
                } else if (pet_age < 3600) {
                    pet_img = hsSet[1];
                } else {
                    pet_img = hsSet[2];
                }
                break;
            case "旺仔" :
                if (pet_age < 60) {
                    pet_img = wzSet[0];
                } else if (pet_age < 3600) {
                    pet_img = wzSet[1];
                } else if (pet_age < 7200) {
                    pet_img = wzSet[2];
                } else if (pet_age < 14400) {
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

    // 将宠物年龄由秒数转化为年月日时分秒
    private String showPetAge(long petAgeSeconds) {
        String petAgeStr = "";

        if (petAgeSeconds > 31536000) {
            petAgeStr += (petAgeSeconds / 31536000) + "年";
            petAgeSeconds = petAgeSeconds % 31536000;
        }
        if(petAgeSeconds > 2626560) {
            petAgeStr += (petAgeSeconds / 2626560) + "月";
            petAgeSeconds = petAgeSeconds % 2626560;
        }
        if(petAgeSeconds > 2626560) {
            petAgeStr += (petAgeSeconds / 86400) + "天";
            petAgeSeconds = petAgeSeconds % 86400;
        }
        if(petAgeSeconds > 3600) {
            petAgeStr += (petAgeSeconds / 3600) + "小时";
            petAgeSeconds = petAgeSeconds % 3600;
        }
        if(petAgeSeconds > 60) {
            petAgeStr += (petAgeSeconds / 60) + "分钟";
            petAgeSeconds = petAgeSeconds % 60;
        }
        petAgeStr += petAgeSeconds + "秒";

        return petAgeStr;
    }

    // 创建一只宠物
    public void createPet(String mode, String name, long age) {
        usePetDB.addPet(mode, name, age);
    }

    public void removePet(String name) {

        petNameAdapter.remove(name);
        
        usePetDB.removePet(name);

        if (petNameAdapter.getCount() == 0) {
            showPetName.setText("");
            showPetMode.setText(getPetMode(""));
            // 显示宠物图像
            showPetImg.setImageResource(R.drawable.wz1);
            // 显示宠物年龄
            petAgeView.setText("");
        }
    }

    // 获取宠物年龄
    private long getPetAge(String name) {
        return usePetDB.getAge(name);
    }

    // 获取宠物类型
    private String getPetMode(String name) {
        return usePetDB.getMode(name);
    }

    // 更改宠物年龄
    public void updatePetAge(String name, long age) {
        usePetDB.updatePet(name, age);
    }

    // 获取数据库中所有宠物信息
    public List<Pet> getAllPetInfo () {
        Log.d(TAG, "getAllPetInfo()： OK" );
        return usePetDB.getAllPet();
    }

    @Override
    protected void onDestroy() {
        if (!back_flag){
            closeTime = System.currentTimeMillis();
            back_flag = true;
        }
        petAgeSeconds += (closeTime - openTime) / 1000;
        long new_age;
        new_age = getPetAge(showPetName.getText().toString()) + petAgeSeconds;
        // 更新宠物年龄到数据库
        updatePetAge(showPetName.getText().toString(), new_age);
        petAgeSeconds = 0;
        usePetDB.close();
        super.onDestroy();
    }
}