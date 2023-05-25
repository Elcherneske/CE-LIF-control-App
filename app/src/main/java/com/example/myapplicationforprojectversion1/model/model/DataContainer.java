package com.example.myapplicationforprojectversion1.model.model;

import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.service.ServerConnection;
import com.example.myapplicationforprojectversion1.view.ParameterClass.ParameterGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataContainer implements DataProvider {
    private List<ChartData> data;//所有数据的buffer
    private List<ChartData> show_data;//用于显示的数据
    private int show_size = 10000;
    private BlueToothServiceConnection deviceConnection;
    private ServerConnection serverConnection;
    private ParameterGenerator parameter;
    private String dir;
    private FileOutputStream output;
    private ChartData empty = new ChartData(1,0);
    private int kind = -1;//目前是哪种工作状态
    private final int pulse_threshold = 20;



    //cont
    public DataContainer()
    {
        initialization();
    }

    public DataContainer(String dir,ParameterGenerator parameter)
    {
        this.parameter = parameter;
        this.dir = dir;

        initialization();

        try {
            output = new FileOutputStream(new File(dir));
            String str = "Time/ms" + "," + "Data" + "," + parameter.getParameter().getInformation();
            output.write(str.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void stopAll()
    {
        try {
            output.close();//关闭文件流
            deviceConnection.sendStopMessage();//传入device，表示结束数据接收,用于关闭电源
            if(serverConnection!=null && serverConnection.isConnected()){
                serverConnection.sendStopMessage();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void beginAll()
    {
        deviceConnection.sendBeginMessage();
        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendBeginMessage();
        }
    }


    //每次get之后要写入文件中
    @Override
    public List<ChartData> getData(int size)
    {
        //fetch items from connection
        List<ChartData> newData = filtering(anti_pulse(deviceConnection.fetchAllData()));
        if(serverConnection!=null && serverConnection.isConnected()){//把数据传输到服务器
            serverConnection.sendValues(newData);
        }
        //resize the show_data
        if(this.show_data.size() != size)
        {
            this.show_data.clear();
            if(size<=this.data.size()){
                for(int i=size-1;i>=0;i--){
                    this.show_data.add(this.data.get(this.data.size()-i-1));
                }
            }
            else{
                for(int i=0;i<size - this.data.size();i++){
                    this.show_data.add(this.empty);
                }
                for(int i=0;i<this.data.size();i++){
                    this.show_data.add(this.data.get(i));
                }
            }
        }
        //renew list
        for(int i=0;i<newData.size();i++){
            ChartData temp_data = newData.get(i);
            this.data.add(temp_data);
            this.show_data.add(temp_data);
            this.show_data.remove(0);
            //这里执行将数据存入文件中的操作和上传云端的步骤
            storeFile(newData.get(i));
        }
        return this.show_data;
    }





    public List<ChartData> getData(int size,int swift)
    {
        //fetch items from connection
        List<ChartData> newData = filtering(anti_pulse(deviceConnection.fetchAllData()));
        if(serverConnection!=null && serverConnection.isConnected()){//把数据传输到服务器
            serverConnection.sendValues(newData);
        }

        for(int i=0;i<newData.size();i++){
            ChartData temp_data = newData.get(i);
            this.data.add(temp_data);
            storeFile(newData.get(i));
        }
        this.show_data.clear();
        if(size + swift<=this.data.size()){
            for(int i=size-1;i>=0;i--){
                this.show_data.add(this.data.get(this.data.size() - i - 1 - swift));
            }
        }
        else if (size<=this.data.size()){
            for(int i=0;i<size;i++){
                this.show_data.add(this.data.get(i));
            }
        }
        else{
            for(int i=0;i<size - this.data.size();i++){
                this.show_data.add(this.empty);
            }
            for(int i=0;i<this.data.size();i++){
                this.show_data.add(this.data.get(i));
            }
        }
        return this.show_data;
    }


    @Override
    public int getDataPoolSize(){
        synchronized (this.data){
            return this.data.size();
        }
    }





    //private function

    private void initialization()
    {
        this.deviceConnection = BlueToothServiceConnection.getInstance();
        this.serverConnection = ServerConnection.getInstance();
        this.data = new ArrayList<ChartData>();
        this.show_data = new ArrayList<ChartData>();
        for(int i=0;i<show_size;i++){
            this.show_data.add(empty);
        }
        checkKind();
    }

    private void checkKind()
    {
        this.kind = parameter.getKind();
    }


    private void storeFile(ChartData data)
    {
        try {
            String str = data.getTime()+","+data.getData() + "\n";
            output.write(str.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ChartData> anti_pulse(List<ChartData> input){
        if(input.size()==0) return input;
        int previous;
        if(this.data.isEmpty()){
            previous = 0;
        }
        else{
            previous = this.data.get(this.data.size() - 1).getData();
        }
        for(int i = 0; i<input.size(); i++){
            int temp = input.get(i).getData();
            if(temp - previous > this.pulse_threshold || previous - temp  > this.pulse_threshold){
                input.get(i).setData(previous);
            }
            previous = temp;
        }
        return input;
    }

    private List<ChartData> filtering(List<ChartData> input){
        if(input.size()==0) return input;
        int previous;
        double ratio = 0.5;
        if(this.data.isEmpty()){
            previous = 0;
        }
        else{
            previous = this.data.get(this.data.size() - 1).getData();
        }
        for(int i = 0; i<input.size(); i++){
            int temp = input.get(i).getData();
            input.get(i).setData((int)(ratio * previous + (1-ratio) * temp));
            previous = temp;
        }
        return input;
    }

    /*
    private List<ChartData> filtering(List<ChartData> input){
        if(input.size()==0) return input;
        int previous;
        double ratio = 0.45;
        if(this.data.isEmpty()){
            previous = 0;
        }
        else{
            previous = this.data.get(this.data.size() - 1).getData();
        }
        for(int i = 0; i<input.size(); i++){
            int temp = input.get(i).getData();
            input.get(i).setData((int)(ratio * previous + (1-ratio) * temp));
            previous = temp;
        }
        return input;
    }
    */

}
