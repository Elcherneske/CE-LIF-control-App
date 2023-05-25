package com.example.myapplicationforprojectversion1.view.ParameterClass;

public class ParameterGenerator {
    private CELIFParameter CELIF_parameter = null;
    private LIFParameter LIF_parameter = null;
    private int parameter_type;

    private static ParameterGenerator parameter;

    public static ParameterGenerator formInstance(CELIFParameter CELIF_parameter){
        if(parameter == null || parameter.parameter_type!=0){
            parameter = new ParameterGenerator(0,CELIF_parameter);
        }
        else{
            parameter.clearParameter();
            parameter.setParameter(CELIF_parameter);
        }
        return parameter;
    }

    public static ParameterGenerator formInstance(LIFParameter LIF_parameter){
        if(parameter == null || parameter.parameter_type!=1){
            parameter = new ParameterGenerator(1,LIF_parameter);
        }
        else{
            parameter.clearParameter();
            parameter.setParameter(LIF_parameter);
        }
        return parameter;
    }

    public static ParameterGenerator getInstance(){
        if(parameter == null){
            parameter = new ParameterGenerator(0,new CELIFParameter(100,100,240,500,250, 1,1,2400,500,"default","default"));
        }
        return parameter;
    }


    public int getKind(){return this.parameter_type;}

    // possible to return null
    public Parameter getParameter()
    {
        Parameter parameter = null;
        switch (parameter_type)
        {
            case 0:
                if(CELIF_parameter != null){
                    parameter = CELIF_parameter;
                }
                break;
            case 1:
                if(LIF_parameter != null)
                {
                    parameter = LIF_parameter;
                }
                break;
        }
        return parameter;
    }



    //private function



    //not allowed to form the object outside
    private ParameterGenerator(int kind, CELIFParameter CELIF_parameter)
    {
        this.parameter_type = kind;
        this.CELIF_parameter = CELIF_parameter;
    }
    private ParameterGenerator(int kind, LIFParameter LIF_parameter)
    {
        this.parameter_type = kind;
        this.LIF_parameter = LIF_parameter;
    }

    private void setParameter(CELIFParameter CELIF_parameter) {
        this.CELIF_parameter = CELIF_parameter;
    }

    private void setParameter(LIFParameter LIF_parameter) {
        this.LIF_parameter = LIF_parameter;
    }

    private void clearParameter()
    {
        this.CELIF_parameter = null;
        this.LIF_parameter = null;
    }





}
