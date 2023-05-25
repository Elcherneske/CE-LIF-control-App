package com.example.myapplicationforprojectversion1.view.Activities.LIF;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationforprojectversion1.R;
import com.example.myapplicationforprojectversion1.presenter.ParameterPresenter;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.CELIFParameterActivity;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;
import com.example.myapplicationforprojectversion1.view.ParameterClass.CELIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.LIFParameter;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

public class LIFParameterActivity extends AppCompatActivity implements ParameterProvider {

    private Button setButton;
    private Button backButton;
    private EditText laserDivisorText;
    private EditText laserPeriodText;
    private EditText laserDutyText;
    private EditText samplingFrequencyText;
    private EditText filteringPointText;
    private EditText samplingTimeText;
    private EditText magnifyText;
    private EditText nameText;
    private EditText IDText;
    private ParameterGenerator parameter;
    private ParameterPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lif_parameter);

        initialize();
    }

    @Override
    public ParameterGenerator getParameter()
    {
        return this.parameter;
    }

    @Override
    public void showMessage(String message){
        Toast.makeText(LIFParameterActivity.this,message,Toast.LENGTH_SHORT).show();
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

        samplingFrequencyText = (EditText) findViewById(R.id.samplingFrequencyText);
        filteringPointText = (EditText)findViewById(R.id.filteringPointText);
        samplingTimeText = (EditText)findViewById(R.id.samplingTimeText);
        magnifyText = (EditText)findViewById(R.id.magnifyText);

        nameText = (EditText) findViewById(R.id.nameText);
        IDText = (EditText) findViewById(R.id.IDText);


        laserDivisorText.setText("72");
        laserPeriodText.setText("100");
        laserDutyText.setText("100");
        samplingFrequencyText.setText("50");
        samplingFrequencyText.setFocusable(false);
        filteringPointText.setText("50");
        filteringPointText.setFocusable(false);
        magnifyText.setText("1");

    }


    private void initListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(CELIFParameterActivity.this, CELIFShowActivity.class);
                //startActivity(intent);
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
                    Toast.makeText(LIFParameterActivity.this,"Error! Please input the parameter",Toast.LENGTH_LONG).show();
                }
                else{
                    parameter = ParameterGenerator.formInstance(new LIFParameter(
                            Integer.valueOf(laserDivisorText.getText().toString().trim()),
                            Integer.valueOf(laserPeriodText.getText().toString().trim()),
                            Integer.valueOf(laserDutyText.getText().toString().trim()),
                            Integer.valueOf(samplingFrequencyText.getText().toString().trim()),
                            Integer.valueOf(filteringPointText.getText().toString().trim()),
                            Integer.valueOf(samplingTimeText.getText().toString().trim()),
                            Integer.valueOf(magnifyText.getText().toString().trim()),
                            nameText.getText().toString().trim(),
                            IDText.getText().toString().trim()));
                    presenter.setParameter();
                    //Toast.makeText(CELIFParameterActivity.this,"Successfully set",Toast.LENGTH_LONG).show();
                    //非测试情况要打开
                    setButtonAble(backButton);
                }
            }
        });
    }

    private void initPresenter()
    {
        presenter = new ParameterPresenter(LIFParameterActivity.this);
    }

    private boolean checkEmpty()
    {
        boolean flag = true;
        if(laserDivisorText.getText().toString().trim().equals("")) flag = false;
        if(laserPeriodText.getText().toString().trim().equals("")) flag = false;
        if(laserDutyText.getText().toString().trim().equals("")) flag = false;
        if(samplingFrequencyText.getText().toString().trim().equals("")) flag = false;
        if(filteringPointText.getText().toString().trim().equals("")) flag = false;
        if(samplingTimeText.getText().toString().trim().equals(""))flag = false;
        if(magnifyText.getText().toString().trim().equals("")) flag = false;
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
