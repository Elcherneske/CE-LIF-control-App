package com.example.myapplicationforprojectversion1.model.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CSVFileUtil {

    public static CSVFileUtil instance;

    private File file;

    private FileOutputStream out;


    public static void formInstance(String dir){
        synchronized (CSVFileUtil.class){
            instance = new CSVFileUtil(dir);
        }
    }

    public static void formInstance(File file){
        synchronized (CSVFileUtil.class){
            instance = new CSVFileUtil(file);
        }
    }

    public static CSVFileUtil getInstance(){
        return instance;
    }



    public File getFile(){
        return this.file;
    }


    private CSVFileUtil(String dir){
        this.file = new File(dir);
        try{
            this.out = new FileOutputStream(this.file);
        }catch (IOException e){e.printStackTrace();}
    }

    private CSVFileUtil(File file){
        this.file = file;
        try{
            this.out = new FileOutputStream(this.file);
        }catch (IOException e){e.printStackTrace();}
    }


    public void write(String str){
        try {
            out.write(str.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



}
