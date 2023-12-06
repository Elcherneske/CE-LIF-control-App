package com.example.myapplicationforprojectversion1.model.model;

import android.widget.Toast;

import com.example.myapplicationforprojectversion1.model.device.BlueToothServiceConnection;
import com.example.myapplicationforprojectversion1.model.server.ServerConnection;
import com.example.myapplicationforprojectversion1.view.Activities.CELIF.Views;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.MessageShower;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.UIHolder;

import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.LUDecomposition;
import org.apache.commons.math4.legacy.linear.RealMatrix;

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
    private CSVFileUtil fileUtil;

    private FileOutputStream output;
    private ChartData empty = new ChartData(1,0);
    private final int pulse_threshold = 20;//抗脉冲
    private UIHolder activity;



    //cont
    public DataContainer()
    {
        initialization();
    }

    public DataContainer(UIHolder activity)
    {
        //debug
        this.activity = activity;
        this.fileUtil = CSVFileUtil.getInstance();
        fileUtil.write("Time/ms" + "," + "Data" + "\n");
        initialization();
    }


    @Override
    public void stopAll()
    {
        fileUtil.close();
        deviceConnection.sendStopMessage();//传入device，表示结束数据接收,用于关闭电源
        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendStopMessage();
        }
    }
    @Override
    public void beginAll()
    {
        deviceConnection.sendBeginMessage();
        if(serverConnection!=null && serverConnection.isConnected()){
            serverConnection.sendBeginMessage();
        }
        Toast.makeText(Views.getInstance().getShowActivity(), "999", Toast.LENGTH_SHORT).show();
    }


    //每次get之后要写入文件中
    @Override
    public List<ChartData> getData(int size)
    {
        //fetch items from connection
        List<ChartData> newData = anti_pulse(deviceConnection.fetchAllData());
        int windowSize = (newData.size() - 1)/2;
        if(windowSize % 2 == 0){
            windowSize--;
        }
        if(windowSize <= 0){
            windowSize = 1;
        }
        newData = filtering(newData, windowSize, 3);
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
        List<ChartData> newData = anti_pulse(deviceConnection.fetchAllData());
        int windowSize = (newData.size() - 1)/2;
        if(windowSize % 2 == 0){
            windowSize--;
        }
        if(windowSize <= 0){
            windowSize = 1;
        }
        newData = filtering(newData, windowSize, 3);
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
    }



    private void storeFile(ChartData data)
    {
        try {
            String str = data.getTime()+","+data.getData() + "\n";
            this.fileUtil.write(str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //抗脉冲
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

    //简单滤波
//    private List<ChartData> filtering(List<ChartData> input){
//        if(input.size()==0) return input;
//        int previous;
//        double ratio = 0.5;
//        if(this.data.isEmpty()){
//            previous = 0;
//        }
//        else{
//            previous = this.data.get(this.data.size() - 1).getData();
//        }
//        for(int i = 0; i<input.size(); i++){
//            int temp = input.get(i).getData();
//            input.get(i).setData((int)(ratio * previous + (1-ratio) * temp));
//            previous = temp;
//        }
//        return input;
//    }

    // 计算系数矩阵（滤波操作之一）
    private double[][] computeCoefficients(int windowSize, int polynomialOrder) {
        double[][] coefficients = new double[windowSize][polynomialOrder + 1];
        int halfWindowSize = windowSize / 2;
        for (int i = -halfWindowSize; i <= halfWindowSize; i++) {
            for (int j = 0; j <= polynomialOrder; j++) {
                coefficients[i + halfWindowSize][j] = Math.pow(i, j);
            }
        }

        RealMatrix coeffMatrix = new Array2DRowRealMatrix(coefficients);
        RealMatrix coeffTransMatrix = coeffMatrix.transpose();
        RealMatrix coeffTransMultCoeff = coeffTransMatrix.multiply(coeffMatrix);
        RealMatrix inverse = new LUDecomposition(coeffTransMultCoeff).getSolver().getInverse();
        RealMatrix result = coeffMatrix.multiply(inverse.multiply(coeffTransMatrix));
        return result.getData();
        //return MatrixUtils.inverse(new Array2DRowRealMatrix(coefficients)).getData();
    }

    // 对数据进行滤波
    private List<ChartData> filtering(List<ChartData> input, int windowSize, int polynomialOrder) {

        int len = input.size();
        double[] data = new double[len];
        for(int i = 0; i<len; i++){
            data[i] = (double)(input.get(i).getData());
        }

        int n = data.length;
        if(n <= windowSize || n < 3 || windowSize < 3) return input;

        double[][] coefficients = computeCoefficients(windowSize, polynomialOrder);

        int halfWindowSize = windowSize / 2;



        double[] filteredData = new double[n];

        for(int i = 0; i < halfWindowSize; i++){
            double sum = 0.0;
            for (int j = 0; j < 2 * halfWindowSize + 1; j++) {
                sum += coefficients[i][j] * data[j];
            }
            filteredData[i] = sum;
        }


        for (int i = halfWindowSize; i < n - halfWindowSize; i++) {
            double sum = 0.0;
            for (int j = -halfWindowSize; j <= halfWindowSize; j++) {
                sum += coefficients[halfWindowSize + 1][j + halfWindowSize] * data[i + j];
            }
            filteredData[i] = sum;
        }


        for(int i = n - halfWindowSize; i < n; i++){
            double sum = 0.0;
            for (int j = 0; j < 2 * halfWindowSize + 1; j++) {
                sum += coefficients[i - (n-windowSize)][j] * data[ n - windowSize + j];
            }
            filteredData[i] = sum;
        }

        List<ChartData> output = new ArrayList<>();
        for(int i = 0; i < input.size(); i++){
            ChartData item = input.get(i);
            item.setData((int)(filteredData[i]));
            output.add(item);
        }

        return output;
    }

}
