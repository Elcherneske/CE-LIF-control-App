package com.example.myapplicationforprojectversion1.view.ParameterClass;

import com.example.myapplicationforprojectversion1.model.Modbus.ModbusRtuMaster;

import java.util.ArrayList;
import java.util.List;

public class LIFParameter implements Parameter{
    //激光器分频数
    private int laser_divisor;
    //激光器周期
    private int laser_period;
    //激光器占空比
    private int laser_duty;
    //采样频率
    private int sampling_frequency;
    //滤波点数
    private int filtering_point;
    //采样时间
    private int sampling_time;
    //放大倍数
    private int magnify;
    //文件名字
    private String name;
    //仪器编号
    private String id;

    public LIFParameter(int laser_divisor,int laser_period,int laser_duty,int sampling_frequency,int filtering_point,int sampling_time,int magnify,String name,String id)
    {
        this.laser_divisor = laser_divisor;
        this.laser_duty = laser_duty;
        this.laser_period = laser_period;
        this.sampling_frequency = sampling_frequency;
        this.filtering_point = filtering_point;
        this.sampling_time = sampling_time;
        this.magnify = magnify;
        this.name = name;
        this.id = id;
    }

    public int getSampling_frequency()
    {
        return this.sampling_frequency;
    }
    public int getFiltering_point()
    {
        return this.filtering_point;
    }
    public int getLaser_divisor()
    {
        return this.laser_divisor;
    }
    public int getLaser_period()
    {
        return this.laser_period;
    }
    public int getLaser_duty()
    {
        return this.laser_duty;
    }
    public int getSampling_time()
    {
        return this.sampling_time;
    }

    public int getMagnify() {
        return magnify;
    }

    public String getId() {
        return id;
    }

    public String getName()
    {
        return this.name;
    }
    public String getID(){return this.id;}



    @Override
    public String formString()
    {
        String output = "Parameter" + " " + "LIF" + " " +
                this.laser_divisor + " " +
                this.laser_period + " " +
                this.laser_duty + " " +
                this.sampling_frequency + " " +
                this.filtering_point + " " +
                this.sampling_time + " " +
                this.magnify + " " +
                this.name + " " +
                this.id;
        return output;
    }
    @Override
    public String getInformation()
    {
        String output = "Working mode:" + "," + "LIF" + "," +
                "laser_divisor:" + "," +this.laser_divisor + "," +
                "laser_period:" + "," +this.laser_period + "," +
                "laser_duty:" + "," +this.laser_duty + "," +
                "sampling_frequency:" + "," + this.sampling_frequency + "," +
                "filtering_point:" + "," +this.filtering_point + "," +
                "sampling_time:" + "," + this.sampling_time + "," +
                "magnify:" + "," + this.magnify + "," +
                "filename:" + "," +this.name + "," +
                "device ID:" + "," +this.id + "\n";
        return output;
    }

    @Override
    public String getFileName()
    {
        return this.name;
    }

    @Override
    public List<byte[]> formByteParameter(ModbusRtuMaster modbusRtuMaster)
    {
        List<byte[]> result = new ArrayList<>();

        return result;
    }
}
