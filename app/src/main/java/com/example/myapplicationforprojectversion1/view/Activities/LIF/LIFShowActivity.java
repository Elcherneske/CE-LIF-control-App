package com.example.myapplicationforprojectversion1.view.Activities.LIF;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.presenter.DataPresenter;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.UIHolder;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;
import com.example.myapplicationforprojectversion1.view.customview.ChartView;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LIFShowActivity extends AppCompatActivity implements UIHolder{
    private Button parameterButton;
    private Button beginButton;
    private Button stopButton;
    private Button shareButton;
    private ChartView chartView;
    private DataPresenter dataSource;
    private Timer timer;
    private String filename;
    private ParameterGenerator parameter;
    private BlueToothServiceConnection connection;

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setButtonAble(parameterButton);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }


    @Override
    public void showData(List<ChartData> data)
    {
        chartView.setData(data);
    }



    public String provideDir()
    {
        if(this.parameter == null) this.parameter = ParameterGenerator.getInstance();
        this.filename = this.parameter.getParameter().getFileName();
        File file = getFilesDir();
        String fileName = "LIF";
        File appDir = new File(file, fileName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File currentFile = new File(appDir, this.filename+".csv");
        return currentFile.getAbsolutePath();
    }

    @Override
    public ParameterGenerator provideParameter()
    {
        if(this.parameter == null) this.parameter = ParameterGenerator.getInstance();
        return this.parameter;
    }

    @Override
    public void showMessage(String message)
    {
        Toast.makeText(LIFShowActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x123 && resultCode == 0x123)
        {
            Bundle bd = data.getExtras();
            int flag = bd.getInt("whether");
            if(flag==1) setButtonAble(this.beginButton);
        }
    }





//private function

    private void initialize()
    {

        initView();


        initService();


        initListener();

    }



    private void initView()
    {
        parameterButton = (Button) findViewById(R.id.parameterButton);
        beginButton = (Button) findViewById(R.id.beginButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        chartView = (ChartView) findViewById(R.id.chartView);
        shareButton = (Button) findViewById(R.id.shareButton);

        setButtonUnable(parameterButton);
        setButtonUnable(beginButton);
        setButtonUnable(stopButton);
        setButtonUnable(shareButton);
    }

    private void initListener()
    {

        parameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LIFShowActivity.this, LIFParameterActivity.class);
                startActivityForResult(intent,0x123);
            }
        });

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonUnable(parameterButton);
                setButtonUnable(beginButton);
                setButtonAble(stopButton);
                initPresenter();
                beginGetData();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                dataSource.stopAll();
                //去除presenter
                setButtonAble(parameterButton);
                setButtonAble(beginButton);
                setButtonAble(shareButton);
                setButtonUnable(stopButton);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = FileProvider.getUriForFile(LIFShowActivity.this, "com.example.myapplicationforprojectversion1.fileprovider", new File(provideDir()));

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share,"Share Text"));
            }
        });

    }

    private void initPresenter()
    {
        this.parameter = ParameterGenerator.getInstance();
        this.dataSource = new DataPresenter(this);
        this.dataSource.beginAll();
        this.timer = new Timer();
    }

    private void initService()
    {
        this.connection = BlueToothServiceConnection.getInstance();

        new Thread(){
            @Override
            public void run(){
                while(true){
                    if(connection.isConnect() == true){
                        myHandler.sendEmptyMessage(1);
                        break;
                    }
                }
            }
        }.start();
    }

    private void beginGetData()
    {
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                dataSource.getData();
            }
        };
        timer.schedule(task,0,20);
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
