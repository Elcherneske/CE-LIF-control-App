package com.example.myapplicationforprojectversion1.view.Activities.CELIF;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.model.CSVFileUtil;
import com.example.myapplicationforprojectversion1.model.model.ChartData;
import com.example.myapplicationforprojectversion1.model.model.ParameterContainer;
import com.example.myapplicationforprojectversion1.presenter.DataPresenter;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.UIHolder;
import com.example.myapplicationforprojectversion1.view.customview.ChartView;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CELIFShowActivity extends AppCompatActivity implements UIHolder {
    private Button parameterButton;
    private Button beginButton;
    private Button stopButton;
    private Button shareButton;
    private Button Y_magnifyButton;
    private Button X_magnifyButton;
    private Button Y_clearButton;
    private Button X_clearButton;
    private Button backButton;
    private ChartView chartView;
    private DataPresenter dataSource;
    private Timer timer;
    private GestureDetector chartViewGestureDetector;


    //debug
    private String message ;
    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(CELIFShowActivity.this,message,Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(CELIFShowActivity.this,message,Toast.LENGTH_SHORT).show();
                    break;
                //接下来的都是debug用
                case 3:
                    Toast.makeText(CELIFShowActivity.this,message,Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(CELIFShowActivity.this,message,Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(CELIFShowActivity.this,message,Toast.LENGTH_SHORT).show();

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


    @Override
    public void showMessage(String message)
    {
        this.message = message;
        this.myHandler.sendEmptyMessage(1);
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

        initListener();

        Views.getInstance().setShowActivity(this);

    }



    private void initView() {
        parameterButton = (Button) findViewById(R.id.parameterButton);
        beginButton = (Button) findViewById(R.id.beginButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        chartView = (ChartView) findViewById(R.id.chartView);
        shareButton = (Button) findViewById(R.id.shareButton);
        Y_magnifyButton = (Button)findViewById(R.id.Y_magnifyButton);
        X_magnifyButton = (Button) findViewById(R.id.X_magnifyButton);
        Y_clearButton = (Button) findViewById(R.id.YC);
        X_clearButton = (Button) findViewById(R.id.XC);
        backButton = (Button) findViewById(R.id.MainBackButton);

        setButtonAble(parameterButton);
        setButtonUnable(beginButton);
        setButtonUnable(stopButton);
        setButtonUnable(shareButton);
        setButtonUnable(Y_magnifyButton);
        setButtonUnable(X_magnifyButton);
        setButtonUnable(Y_clearButton);
        setButtonUnable(X_clearButton);
    }

    private void initListener() {

        parameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CELIFShowActivity.this, CELIFParameterActivity.class);
                startActivityForResult(intent,0x123);
            }
        });

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonUnable(parameterButton);
                setButtonUnable(beginButton);
                setButtonAble(stopButton);
                setButtonAble(Y_magnifyButton);
                setButtonAble(X_magnifyButton);
                setButtonAble(Y_clearButton);
                setButtonAble(X_clearButton);

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


        this.stopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 在这里编写按钮按下时的操作
                    // 例如改变按钮的背景颜色、播放音效等
                    stopButton.setBackgroundResource(R.drawable.button2_down);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 在这里编写按钮抬起时的操作
                    // 例如恢复按钮的背景颜色、执行某个动作等
                    stopButton.setBackgroundResource(R.drawable.button2_up);
                }
                return false;
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = FileProvider.getUriForFile(CELIFShowActivity.this,
                        "com.example.myapplicationforprojectversion1.fileprovider", CSVFileUtil.getInstance().getFile());

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share,"Share Text"));
            }
        });


        X_magnifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSource.shrinkSize();
            }
        });

        Y_magnifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartView.rangeShrink();
            }
        });

        X_clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSource.setDefaultSize();
            }
        });

        Y_clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartView.setDefaultRange();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setChartViewGestureDetector();

        chartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return chartViewGestureDetector.onTouchEvent(motionEvent);
            }
        });


    }

    private void initPresenter()
    {
        this.dataSource = new DataPresenter(this);

        this.dataSource.beginAll();

        this.timer = new Timer();
    }

    private void beginGetData()
    {
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                dataSource.getData();
            }
        };
        timer.schedule(task,0,1000);
    }


    private void setButtonUnable(Button button){
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.button2_invalid);
        button.setTextColor(Color.WHITE);
    }
    private void setButtonAble(Button button)
    {
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.button2_up);
        button.setTextColor(Color.WHITE);
    }

    private void setChartViewGestureDetector()
    {
        GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }
            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }
            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return true;
            }
            @Override
            public void onLongPress(MotionEvent motionEvent) {
                chartView.setDefaultRange();
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                if(Math.abs(vx)>Math.abs(vy)){
                    if(vx>0) dataSource.shrinkSwift();
                    else dataSource.magnifySwift();
                }
                else{
                    if(vy>0) chartView.downSwift();
                    else chartView.upSwift();
                }
                return true;
            }
        };

        this.chartViewGestureDetector = new GestureDetector(listener);

        chartViewGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return true;
            }
            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
               dataSource.setDefaultSize();
                return true;
            }
            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return true;
            }
        });

    }

}