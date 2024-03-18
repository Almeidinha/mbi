package com.msoft.mbi.cube.util;

public interface CubeListener {
  
  public void start();
  public void finish();
  public boolean stopProcess();
  public void setHasData(boolean hasData);
    

}
