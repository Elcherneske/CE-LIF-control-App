package com.example.myapplicationforprojectversion1.view.Activities.CELIF;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.server.ServerConnection;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.MessageShower;

public class GeneratorActivity extends AppCompatActivity implements MessageShower{

    private EditText deviceName;

    private EditText serviceIP;

    private TextView infoText;

    private Button connectButton;

    private Button enterButton;

    private String message;


    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(GeneratorActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    setButtonAble(enterButton);
                    break;
            }
        }
    };



    public void showMessage(String message){
        this.message = message;
        myHandler.sendEmptyMessage(1);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_generator);

        initialize();
    }









    //private function
    private void initialize()
    {
        checkBluetoothAndLocationPermission();
        initView();
        initListener();
        Views.getInstance().setGeneratorActivity(this);
    }

    private void initView()
    {
        this.enterButton = (Button) findViewById(R.id.EnterButton);
        this.connectButton = (Button) findViewById(R.id.ConnectionButton);
        this.deviceName = (EditText) findViewById(R.id.deviceName);
        this.serviceIP = (EditText) findViewById(R.id.ServiceIP);
        this.infoText = (TextView) findViewById(R.id.generateInfoText);

        this.deviceName.setText("test");

        setButtonAble(this.connectButton);
        setButtonUnable(this.enterButton);
    }

    private void initListener()
    {
        this.enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeneratorActivity.this, CELIFShowActivity.class);
                startActivity(intent);
            }
        });

        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceInformation = deviceName.getText().toString().trim();
                String IP = serviceIP.getText().toString().trim();
                if(deviceInformation.equals("")){ //check内容是否正确填写
                    showMessage("Error! Please input the device name");
                }
                else{
                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);//这里与标准蓝牙略有不同
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                    if(!bluetoothAdapter.isEnabled()){
                        try{
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
                        }
                        catch (SecurityException e){
                            showMessage("get adapter error");
                        }
                    }
                    //创建连接的全局对象，同时开启连接监听
                    BlueToothServiceConnection.formInstance(BluetoothAdapter.getDefaultAdapter(),deviceInformation);//连接stm32芯片
                    ServerConnection.formInstance(IP);//连接服务端
                    beginListen();
                }
            }
        });

        this.connectButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 在这里编写按钮按下时的操作
                    // 例如改变按钮的背景颜色、播放音效等
                    connectButton.setBackgroundColor(Color.BLUE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 在这里编写按钮抬起时的操作
                    // 例如恢复按钮的背景颜色、执行某个动作等
                    connectButton.setBackgroundColor(Color.GREEN);
                }
                return false;
            }
        });
    }


    private void beginListen()
    {
        new Thread(){
            @Override
            public void run()
            {
                while(true){
                    try{
                        sleep(500);
                        if(BlueToothServiceConnection.getInstance().isConnect()){
                            GeneratorActivity.this.showMessage("connect success");
                            myHandler.sendEmptyMessage(2);
                            break;
                        }
                    }
                    catch (Exception e){

                    }

                }
            }
        }.start();
    }



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
