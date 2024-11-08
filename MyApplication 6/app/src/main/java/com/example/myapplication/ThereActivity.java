package com.example.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.example.myapplication.R;
import com.example.myapplication.ImageAdapter;
import com.example.myapplication.Image;



public class ThereActivity extends Activity{
    Button btn1,btn2,btn3,btn4;
    private List<Image> imageList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        btn1 = this.findViewById(R.id.navigation_one);         //登录按钮
        btn2 = this.findViewById(R.id.navigation_two);
        btn3 = this.findViewById(R.id.navigation_there);
        btn4 = this.findViewById(R.id.navigation_four);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ThereActivity.this, TwoActivity.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ThereActivity.this, ThereActivity.class);
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ThereActivity.this, FourActivity.class);
                startActivity(intent);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ThereActivity.this, FiveActivity.class);
                startActivity(intent);
            }
        });
        initImages();   //初始化图片数据
        ImageAdapter adapter = new ImageAdapter(ThereActivity.this,
                R.layout.activity_img, imageList);
        ListView listView = findViewById(R.id.img_list);
        listView.setAdapter(adapter);
        ///list的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView < ? > parent, View view,int position, long id){
                Image image = imageList.get(position);
                Toast.makeText(ThereActivity.this, "你点击了图片" + image.getName(), Toast.LENGTH_LONG).show();
            }
        });
    }
    //查询图片信息
    private void initImages() {
        /* 因为手机本地图片过多，若将其全部查询出来并显示，需要耗费过多时间，
           这里定义一个count变量用于控制显示出的图片数目 */
        int count = 0;
        imageList = new ArrayList();
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null,  null, null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            Log.d("ImgActivity: ", "initImages: " + "imageName: " + name);

            //获取图片的路径
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String location = new String(data, 0, data.length - 1);
            Log.d("ImgActivity: ", "initImages: " + "imageLocation: " + location);
            //根据路径获取图片
            Bitmap bm = getImgFromDesc(location);

            //获取图片的详细信息
            String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
            Log.d("ImgActivity", "initImages: " + "ImageDesc: " + desc);

            Image image = new Image(bm, name, location);
            imageList.add(image);

            count++;
            //显示出3张图片，可改变该数字，控制显示出的图片数目
            if(count >= 3) break;
        }
        Log.d("ImgActivity: ", "initImage: " + "imageList.size: " + imageList.size());
    }

    //根据路径获取图片
    private Bitmap getImgFromDesc(String path) {
        Bitmap bm = null;
        File file = new File(path);
        // 动态申请权限
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        final int REQUEST_CODE = 10001;

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取

            for (String permission : permissions) {
                //  GRANTED---授权  DINIED---拒绝
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
                }
            }
        }

        boolean permission_readStorage = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean permission_camera = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        Log.d("ImgActivity:", "getImageFromDesc: \n");
        Log.d("ImgActivity: ", "readPermission: " + permission_readStorage + "\n");
        Log.d("ImgActivity： ", "cameraPermission: " + permission_camera + "\n");

        if(file.exists()) {
            bm = BitmapFactory.decodeFile(path);
        } else {
            Toast.makeText(ThereActivity.this, " 该图片不存在！", Toast.LENGTH_LONG).show();
            Log.d("ImgActivity ", "getImgFromDesc: 该图片不存在！");
        }
        return bm;
    }

}


