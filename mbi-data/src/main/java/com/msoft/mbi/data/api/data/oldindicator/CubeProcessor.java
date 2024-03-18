package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.cube.util.CuboListener;

public class CubeProcessor implements CuboListener {

    private final Indicator indicator;

    public CubeProcessor(Indicator indicator) {
        this.indicator = indicator;
    }

    public void finish() {
    }

    public void start() {
    }

    public boolean stopProcess() {
        try {
            return this.indicator.stopProcess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setTemDados(boolean hasData){
        this.indicator.setHasData(hasData);
    }
}
