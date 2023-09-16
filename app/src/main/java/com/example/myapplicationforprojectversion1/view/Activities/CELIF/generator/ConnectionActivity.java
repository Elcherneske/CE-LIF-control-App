package com.example.myapplicationforprojectversion1.view.Activities.CELIF.generator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.server.ServerConnection;

public class ConnectionActivity extends AppCompatActivity {
    private EditText deviceName;
    private EditText serviceIP;
    private EditText carName;
    private Button connect;
    private Button back;
    private String deviceInformation;
    private String IP;
    private String carInformation;
    private int deviceConnect = 0;

    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(ConnectionActivity.this,"cannot find car",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ConnectionActivity.this,"connect success",Toast.LENGTH_SHORT).show();
                    break;
                //接下来的都是debug用
                case 3:
                    Toast.makeText(ConnectionActivity.this,"listen",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(ConnectionActivity.this,"process",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(ConnectionActivity.this,message,Toast.LENGTH_SHORT).show();

            }
        }
    };

    private String message;
    public void show_Debug_Message(String message){
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initialize();
    }






    //private function
    private void initialize()
    {
        initView();
        initListener();
    }
    private void initView(){
        this.deviceName = (EditText) findViewById(R.id.deviceName);
        this.serviceIP = (EditText) findViewById(R.id.ServiceIP);
        this.carName = (EditText) findViewById(R.id.carName);
        this.connect = (Button) findViewById(R.id.Connection_ConnectButton);
        this.back = (Button) findViewById(R.id.Connection_BackButton);
    }
    private void getInformation()
    {
        this.deviceInformation = deviceName.getText().toString().trim();
        this.IP = serviceIP.getText().toString().trim();
        this.carInformation = carName.getText().toString().trim();
    }
    private void initListener()
    {
        this.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInformation();
                if(deviceInformation.equals("")/*||carInformation.equals("")*/){ //check内容是否正确填写
                    Toast.makeText(ConnectionActivity.this,"Error! Please input the device name",Toast.LENGTH_SHORT).show();
                }
                else{
                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);//这里与标准蓝牙略有不同
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                    if(!bluetoothAdapter.isEnabled()){
                        try{
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
                        }
                        catch (SecurityException e){
                            Toast.makeText(ConnectionActivity.this,"get adapter error",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //创建两个蓝牙连接的全局对象，同时开启连接监听
                    BlueToothServiceConnection.formInstance(BluetoothAdapter.getDefaultAdapter(),deviceInformation);//连接stm32芯片
                    //BlueToothCarConnection.formInstance(bluetoothAdapter,carInformation,ConnectionActivity.this);//连接小车，被淘汰采用其他方案
                    ServerConnection.formInstance(IP);//连接服务端
                    beginListen();


                }


            }
        });

        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BlueToothCarConnection.getInstance().activate(1,1);//有点不懂当时有car的时候为什么加这句，感觉应该是没用的
                Intent intent = getIntent();
                Bundle bd = new Bundle();
                bd.putInt("whether",deviceConnect);
                intent.putExtras(bd);
                setResult(0x100,intent);
                finish();
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
                        if(/*BlueToothCarConnection.getInstance().isConnected &&*/ BlueToothServiceConnection.getInstance().isConnect()){
                            deviceConnect = 1;
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

}
