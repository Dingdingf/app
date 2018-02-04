package com.ding.app;

/**
 * Created by 丁丁 on 2018/2/3.
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

//import cn.waps.AppConnect;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private EditText editText;
    private EditText editText2;
    private Button button;
    private Button button1;
    private InputMethodManager inputMethodManager;
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "Android/data/jp.co.hit_point.tabikaeru/files/Tabikaeru.sav";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button1);



        /*
           检查储存权限
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 只要有一个权限没有被授予, 则直接返回 false
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText2.getText().toString().equals("")) {
                    return;
                }
                String couponHex = String.format("%06X",  Integer.valueOf(editText2.getText().toString()));
                //byte[] byteArray = hexStringToByte(cloverHex);
                //for (int i = 0; i < byteArray.length; i++) {
                //    Log.d("123", " byte array : " + byteArray[i]);
                //}
                Log.d("123", " " + couponHex);
                writeToFile(couponHex,1);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("") ) {
                    return;
                }
                String cloverHex = String.format("%06X",  Integer.valueOf(editText.getText().toString()));
                //byte[] byteArray = hexStringToByte(cloverHex);
                //for (int i = 0; i < byteArray.length; i++) {
                //    Log.d("123", " byte array : " + byteArray[i]);
                //}
                Log.d("123", " " + cloverHex);
                writeToFile(cloverHex,0);
            }
        });
    }

    public void writeToFile(String str, int x) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = new File(FILE_PATH);
        File newFile = new File(FILE_PATH);
        byte[] cloverByteArray=null;
        byte[] couponByteArray=null;

        switch (x){
            case 0:cloverByteArray = hexStringToByte(str);
                break;
            case 1:couponByteArray = hexStringToByte(str);
                break;
        }

        if (!file.exists()) {
            Log.d("123", "未找到文件Tabikaeru.sav");
            return;
        }
        try {
            fileInputStream = new FileInputStream(file);
            byte[] arrayOfByte = new byte[fileInputStream.available()];
            Log.d("123", "文件大小" + arrayOfByte.length);
            fileInputStream.read(arrayOfByte);
            if (arrayOfByte.length > 29) {
                file.delete();
                Log.d("123", "删除旧文件");
                createFile(newFile);
                //三叶草
                if(x==0) {
                    arrayOfByte[23] = cloverByteArray[0];//Byte.valueOf(cloverHex.substring(0, 2));
                    arrayOfByte[24] = cloverByteArray[1];//Byte.valueOf(cloverHex.substring(2, 4));
                    arrayOfByte[25] = cloverByteArray[2];//Byte.valueOf(cloverHex.substring(4, 6));
                }
                //抽奖券
                if(x==1) {
                    arrayOfByte[27] = couponByteArray[0];//Byte.valueOf(couponHex.substring(0, 2));
                    arrayOfByte[28] = couponByteArray[1];//Byte.valueOf(couponHex.substring(2, 4));
                    arrayOfByte[29] = couponByteArray[2];//Byte.valueOf(couponHex.substring(4, 6));
                }
                Log.d("123", " " + arrayOfByte.length);
                for (int i = 0; i <arrayOfByte.length; i++) {
                    Log.d("123", " " + arrayOfByte[i]);
                }
                fileOutputStream = new FileOutputStream(newFile);
                fileOutputStream.write(arrayOfByte);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            hideSoftInput();
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createFile(File file){
        try{
            file.getParentFile().mkdirs();
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void hideSoftInput(){
        if(inputMethodManager == null) {
            inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();

        inputMethodManager.hideSoftInputFromWindow(editText2.getWindowToken(), 0);
        editText2.clearFocus();
    }

    /**
     * 把16进制字符串转换成字节数组
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
            if (result[i] == 0) {
                result[i] = 00;
            }
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

}
