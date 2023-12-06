package com.example.myapplicationforprojectversion1.view.Activities.CELIF;

import android.view.View;

public class Views {

    private static Views instance = new Views();

    private GeneratorActivity generatorActivity;

    private CELIFShowActivity showActivity;

    private CELIFParameterActivity parameterActivity;

    public static Views getInstance(){
        return instance;
    }

    public void setGeneratorActivity(GeneratorActivity activity){
        this.generatorActivity = activity;
    }

    public void setShowActivity(CELIFShowActivity activity){
        this.showActivity = activity;
    }

    public void setParameterActivity(CELIFParameterActivity activity){
        this.parameterActivity = activity;
    }

    public GeneratorActivity getGeneratorActivity(){
        return this.generatorActivity;
    }

    public CELIFShowActivity getShowActivity(){
        return this.showActivity;
    }

    public CELIFParameterActivity getParameterActivity(){
        return this.parameterActivity;
    }


}
