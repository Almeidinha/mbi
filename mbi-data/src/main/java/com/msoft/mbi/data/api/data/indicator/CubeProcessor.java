package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.cube.util.CubeListener;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class CubeProcessor implements CubeListener {

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
            log.error("Error stoping cube process: " + e.getMessage());
            return false;
        }
    }

    public void setHasData(boolean hasData){
        this.indicator.setHasData(hasData);
    }
}
