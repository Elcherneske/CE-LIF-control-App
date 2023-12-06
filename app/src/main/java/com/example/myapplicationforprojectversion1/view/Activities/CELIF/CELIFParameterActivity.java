package com.example.myapplicationforprojectversion1.view.Activities.CELIF;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.model.CSVFileUtil;
import com.example.myapplicationforprojectversion1.presenter.ParameterPresenter;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;
import com.example.myapplicationforprojectversion1.view.ParameterClass.CELIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.Parameter;

import java.io.File;
import java.util.Calendar;

public class CELIFParameterActivity extends AppCompatActivity implements ParameterProvider {

    private Button setButton;
    private Button backButton;
    private EditText samplingFrequency_PSR_Text;
    private EditText samplingFrequency_ARR_Text;
    private EditText magnify_1_Text;
    private EditText magnify_2_Text;
    private EditText voltage_1_Text;
    private EditText voltage_2_Text;
    private SeekBar voltage_1_seekbar;
    private SeekBar voltage_2_seekbar;
    private EditText nameText;//输出数据的文件名
    private TextView infoText;
    private String message;
    private CELIFParameter parameter;



    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(CELIFParameterActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    voltage_1_Text.setText(voltage_1_seekbar.getProgress() + "");
                    voltage_2_Text.setText(voltage_2_seekbar.getProgress() + "");
                    break;
                case 3:
                    Toast.makeText(CELIFParameterActivity.this, "1", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(CELIFParameterActivity.this, "2", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);
        initialize();
    }

    @Override
    public Parameter getParameter()
    {
        return this.parameter;
    }

    @Override
    public void showMessage(String message){
        this.message = message;
        myHandler.sendEmptyMessage(1);
    }


    //private function


    private void initialize(){
        initView();
        initListener();
        Views.getInstance().setParameterActivity(this);
    }
    private void initView(){
        initButton();
        initEditText();
        this.voltage_1_seekbar = (SeekBar) findViewById(R.id.voltage_1_seekBar);
        this.voltage_2_seekbar = (SeekBar) findViewById(R.id.voltage_2_seekBar);
        this.infoText = (TextView) findViewById(R.id.ParameterInfoText);
    }
    private void initButton(){
        setButton = (Button) findViewById(R.id.setButton);
        backButton = (Button) findViewById(R.id.backButton);
        setButtonUnable(backButton);
        setButtonAble(setButton);
    }
    private void initEditText(){

        samplingFrequency_PSR_Text = (EditText) findViewById(R.id.samplingFrequency_PSR_Text);
        samplingFrequency_ARR_Text = (EditText)findViewById(R.id.samplingFrequency_ARR_Text);

        magnify_1_Text = (EditText)findViewById(R.id.magnify_1_Text);
        magnify_2_Text = (EditText)findViewById(R.id.magnify_2_Text);

        voltage_1_Text = (EditText)findViewById(R.id.voltage_1_Text);
        voltage_2_Text = (EditText) findViewById(R.id.voltage_2_Text);

        nameText = (EditText) findViewById(R.id.nameText);

        samplingFrequency_PSR_Text.setText("2400");
        //samplingFrequency_PSR_Text.setFocusable(false);
        samplingFrequency_ARR_Text.setText("1000");
        //samplingFrequency_ARR_Text.setFocusable(false);

        magnify_1_Text.setText("1");
        magnify_2_Text.setText("1");
        //测试用，实际使用删去
        voltage_1_Text.setText("0");
        voltage_2_Text.setText("0");



        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        nameText.setText(calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DAY_OF_MONTH)
        + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND));
    }


    private void initListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                Bundle bd = new Bundle();
                bd.putInt("whether",1);
                intent.putExtras(bd);
                setResult(0x123,intent);
                finish();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmpty()==false){
                    showMessage("Error! Please input the parameter");
                }
                else{
                    File appDir = getFilesDir();//这个需要在activity中使用，因此需要特地生成这个函数
                    File currentFile = new File(appDir,nameText.getText().toString().trim()+".csv");
                    CSVFileUtil.formInstance(currentFile);

                    parameter = new CELIFParameter(
                            Integer.valueOf(voltage_1_Text.getText().toString().trim()),
                            Integer.valueOf(voltage_2_Text.getText().toString().trim()),
                            Integer.valueOf(magnify_1_Text.getText().toString().trim()),
                            Integer.valueOf(magnify_2_Text.getText().toString().trim()),
                            Integer.valueOf(samplingFrequency_PSR_Text.getText().toString().trim()),
                            Integer.valueOf(samplingFrequency_ARR_Text.getText().toString().trim()),
                            nameText.getText().toString().trim());

//                    ParameterPresenter presenter = new ParameterPresenter(CELIFParameterActivity.this);
//
//                    presenter.setParameter();

                    //不知道为啥paramtercontainer会导致这个VIew闪退，所以干脆直接砍掉，直接操作蓝牙

                    BlueToothServiceConnection.getInstance().sendParameter(parameter);
                    new Thread(){
                        @Override
                        public void run(){
                            BlueToothServiceConnection.getInstance().sendParameter(parameter);

                            CSVFileUtil.getInstance().write(parameter.getInformation());

                            try {
                                sleep(20000);
                            }
                            catch (Exception e){}
                            CELIFParameter copyParameter = new CELIFParameter(parameter);
                            copyParameter.setVoltage_1(parameter.getVoltage_2());
                            BlueToothServiceConnection.getInstance().sendParameter(copyParameter);

                            showMessage("set parameter success");
                        }
                    }.start();

                    setButtonAble(backButton);
                }
            }
        });
        setButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setButton.setBackgroundColor(Color.BLUE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    setButton.setBackgroundColor(Color.GREEN);
                }
                return false;
            }
        });
        voltage_1_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.sendEmptyMessage(2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.sendEmptyMessage(2);
            }
        });
        voltage_2_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.sendEmptyMessage(2);
            }
        });
    }

    private boolean checkEmpty()
    {
        boolean flag = true;
        if(voltage_1_Text.getText().toString().trim().equals("")) flag = false;
        if(voltage_2_Text.getText().toString().trim().equals("")) flag = false;
        if(magnify_1_Text.getText().toString().trim().equals(""))flag = false;
        if(magnify_2_Text.getText().toString().trim().equals("")) flag = false;
        if(samplingFrequency_PSR_Text.getText().toString().trim().equals("")) flag = false;
        if(samplingFrequency_ARR_Text.getText().toString().trim().equals("")) flag = false;
        if(nameText.getText().toString().trim().equals("")) flag = false;
        return flag;
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
