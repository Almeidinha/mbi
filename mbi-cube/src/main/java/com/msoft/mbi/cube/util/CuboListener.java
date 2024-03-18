package com.msoft.mbi.cube.util;

public interface CuboListener {
  
  public void start();
  public void finish();
  public boolean stopProcess();
  public void setTemDados(boolean temDados);
    

}
