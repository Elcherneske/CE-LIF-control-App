package com.example.myapplicationforprojectversion1.presenter;

import com.example.myapplicationforprojectversion1.model.model.ParameterContainer;
import com.example.myapplicationforprojectversion1.model.model.ParameterHolder;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.MessageShower;
import com.example.myapplicationforprojectversion1.view.Activities.Interface.ParameterProvider;
import com.example.myapplicationforprojectversion1.view.ParameterClass.Parameter;

public class ParameterPresenter {
    private ParameterHolder holder;

    private ParameterProvider provider;

    public ParameterPresenter(ParameterProvider provider)
    {
        this.provider = provider;
        this.holder = new ParameterContainer(provider);
    }


    public void setParameter()
    {
        holder.sendParameter(provider.getParameter());
    }

}
