package com.example.myapplicationforprojectversion1.presenter;

import com.example.myapplicationforprojectversion1.model.model.ParameterContainer;
import com.example.myapplicationforprojectversion1.model.model.ParameterHolder;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;

public class ParameterPresenter {
    private ParameterProvider provider;
    private ParameterHolder holder;

    public ParameterPresenter(ParameterProvider provider)
    {
        this.provider= provider;
        this.holder = new ParameterContainer(this);
    }


    public void setParameter()
    {
        holder.sendParameter(provider.getParameter());
    }

    public void sendMessage(String message){this.provider.showMessage(message);}

}
