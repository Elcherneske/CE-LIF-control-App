package com.example.myapplicationforprojectversion1.view.Activities.CELIF;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.presenter.ParameterPresenter;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;
import com.example.myapplicationforprojectversion1.view.ParameterClass.CELIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;
public class CELIFParameterActivity extends AppCompatActivity implements ParameterProvider {

    private Button setButton;
    private Button backButton;
    private EditText laserDivisorText;
    private EditText laserPeriodText;
    private EditText laserDutyText;
    private EditText samplingFrequency_PSR_Text;
    private EditText samplingFrequency_ARR_Text;
    private EditText magnify_1_Text;
    private EditText magnify_2_Text;
    private EditText voltage_1_Text;
    private EditText voltage_2_Text;
    private EditText nameText;//输出数据的文件名
    private EditText IDText;//仪器ID
    private ParameterGenerator parameter;
    private ParameterPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);
        initialize();
    }

    @Override
    public ParameterGenerator getParameter()
    {
        return this.parameter;
    }

    @Override
    public void showMessage(String message){
        Toast.makeText(CELIFParameterActivity.this,message,Toast.LENGTH_SHORT).show();
    }




    //private function


    private void initialize(){
        initView();
        initListener();
        initPresenter();
    }



    private void initView(){
        initButton();
        initEditText();
    }

    private void initButton(){
        setButton = (Button) findViewById(R.id.setButton);
        backButton = (Button) findViewById(R.id.backButton);
        setButtonUnable(backButton);
        setButtonAble(setButton);
    }
    private void initEditText(){
        laserDivisorText = (EditText) findViewById(R.id.laserDivisorText);
        laserPeriodText = (EditText)findViewById(R.id.laserPeriodText);
        laserDutyText = (EditText) findViewById(R.id.laserDutyText);

        samplingFrequency_PSR_Text = (EditText) findViewById(R.id.samplingFrequency_PSR_Text);
        samplingFrequency_ARR_Text = (EditText)findViewById(R.id.samplingFrequency_ARR_Text);

        magnify_1_Text = (EditText)findViewById(R.id.magnify_1_Text);
        magnify_2_Text = (EditText)findViewById(R.id.magnify_2_Text);

        voltage_1_Text = (EditText)findViewById(R.id.voltage_1_Text);
        voltage_2_Text = (EditText) findViewById(R.id.voltage_2_Text);

        nameText = (EditText) findViewById(R.id.nameText);
        IDText = (EditText) findViewById(R.id.IDText);


        laserDivisorText.setText("240");
        laserPeriodText.setText("500");
        laserDutyText.setText("250");
        samplingFrequency_PSR_Text.setText("2400");
        //samplingFrequency_PSR_Text.setFocusable(false);
        samplingFrequency_ARR_Text.setText("1000");
        //samplingFrequency_ARR_Text.setFocusable(false);


        magnify_1_Text.setText("1");
        magnify_2_Text.setText("1");


        //测试用，实际使用删去
        voltage_1_Text.setText("100");
        voltage_2_Text.setText("100");

        nameText.setText("www");
        IDText.setText("1");
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
                    Toast.makeText(CELIFParameterActivity.this,"Error! Please input the parameter",Toast.LENGTH_LONG).show();
                }
                else{
                    parameter = ParameterGenerator.formInstance(new CELIFParameter(
                            Integer.valueOf(voltage_1_Text.getText().toString().trim()),
                            Integer.valueOf(voltage_2_Text.getText().toString().trim()),
                            Integer.valueOf(laserDivisorText.getText().toString().trim()),
                            Integer.valueOf(laserPeriodText.getText().toString().trim()),
                            Integer.valueOf(laserDutyText.getText().toString().trim()),
                            Integer.valueOf(magnify_1_Text.getText().toString().trim()),
                            Integer.valueOf(magnify_2_Text.getText().toString().trim()),
                            Integer.valueOf(samplingFrequency_PSR_Text.getText().toString().trim()),
                            Integer.valueOf(samplingFrequency_ARR_Text.getText().toString().trim()),
                            nameText.getText().toString().trim(),
                            IDText.getText().toString().trim()));
                    presenter.setParameter();
                    Toast.makeText(CELIFParameterActivity.this,"Successfully set",Toast.LENGTH_LONG).show();
                    setButtonAble(backButton);
                }
            }
        });

        setButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 在这里编写按钮按下时的操作
                    // 例如改变按钮的背景颜色、播放音效等
                    setButton.setBackgroundColor(Color.BLUE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 在这里编写按钮抬起时的操作
                    // 例如恢复按钮的背景颜色、执行某个动作等
                    setButton.setBackgroundColor(Color.GREEN);
                }
                return false;
            }
        });
    }

    private void initPresenter()
    {
        presenter = new ParameterPresenter(CELIFParameterActivity.this);
    }

    private boolean checkEmpty()
    {
        boolean flag = true;
        if(voltage_1_Text.getText().toString().trim().equals("")) flag = false;
        if(voltage_2_Text.getText().toString().trim().equals("")) flag = false;
        if(laserDivisorText.getText().toString().trim().equals("")) flag = false;
        if(laserPeriodText.getText().toString().trim().equals("")) flag = false;
        if(laserDutyText.getText().toString().trim().equals("")) flag = false;
        if(magnify_1_Text.getText().toString().trim().equals(""))flag = false;
        if(magnify_2_Text.getText().toString().trim().equals("")) flag = false;
        if(samplingFrequency_PSR_Text.getText().toString().trim().equals("")) flag = false;
        if(samplingFrequency_ARR_Text.getText().toString().trim().equals("")) flag = false;
        if(nameText.getText().toString().trim().equals("")) flag = false;
        if(IDText.getText().toString().trim().equals("")) flag = false;
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
