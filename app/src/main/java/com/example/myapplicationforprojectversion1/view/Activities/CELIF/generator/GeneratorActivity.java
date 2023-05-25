package com.example.myapplicationforprojectversion1.view.Activities.CELIF.generator;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.CELIFShowActivity;
import com.example.myapplicationforprojectversion1.view.Activities.LIF.LIFShowActivity;

public class GeneratorActivity extends AppCompatActivity {
    private Button CE_LIF_Button;
    private Button LIF_Button;
    private Button Connect_Button;


    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setButtonAble(CE_LIF_Button);
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_generator);

        initialize();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x100 && resultCode == 0x100)
        {
            Bundle bd = data.getExtras();
            int flag = bd.getInt("whether");
            if(flag==1){
                myHandler.sendEmptyMessage(1);
            }
        }
    }









    //private function
    private void initialize()
    {
        checkBluetoothAndLocationPermission();

        initView();

        initListener();

    }

    private void initView()
    {
        this.CE_LIF_Button = (Button) findViewById(R.id.CELIF_Button);
        this.LIF_Button = (Button) findViewById(R.id.LIF_Button);
        this.Connect_Button = (Button) findViewById(R.id.ConnectionButton);

        setButtonAble(Connect_Button);
        setButtonUnable(CE_LIF_Button);
        setButtonUnable(LIF_Button);
    }

    private void initListener()
    {
        this.CE_LIF_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeneratorActivity.this, CELIFShowActivity.class);
                startActivity(intent);
            }
        });
        this.LIF_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeneratorActivity.this, LIFShowActivity.class);
                startActivity(intent);
            }
        });
        this.Connect_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeneratorActivity.this, ConnectionActivity.class);
                startActivityForResult(intent,0x100);
            }
        });
    }










    /*
    @TargetApi(Build.VERSION_CODES.M)
    private void checkBluetoothAndLocationPermission(){
        //判断是否有访问位置的权限，没有权限，直接申请位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedLocation = true;
        if(requestCode == 1){
            for(int i : grantResults){
                if(i != PERMISSION_GRANTED){
                    grantedLocation = false;
                }
            }
        }
        if(!grantedLocation){
            Toast.makeText(this,"Permission error !!!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkBluetoothAndLocationPermission(){
        //判断是否有访问位置的权限，没有权限，直接申请位置权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }

        else{
            //Toast.makeText(MainActivity.this,"permission checked",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedLocation = true;
        if(requestCode == 1){
            for(int i : grantResults){
                if(i != PERMISSION_GRANTED){
                    grantedLocation = false;
                }
            }
        }
        if(!grantedLocation){
            //Toast.makeText(this,"Permission error !!!",Toast.LENGTH_SHORT).show();
            //finish();
        }
        else{
            //Toast.makeText(this,"success",Toast.LENGTH_SHORT).show();
        }
    }




    private void setButtonUnable(Button button){
        button.setEnabled(false);
        button.setBackgroundColor(Color.RED);
        button.setTextColor(Color.WHITE);
    }
    private void setButtonAble(Button button)
    {
        button.setEnabled(true);
        button.setBackgroundColor(Color.GREEN);
        button.setTextColor(Color.WHITE);
    }

}
