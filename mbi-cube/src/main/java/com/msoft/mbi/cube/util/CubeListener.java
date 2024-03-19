package com.msoft.mbi.cube.util;

public interface CubeListener {
  
  void start();
  void finish();
  boolean stopProcess();
  void setHasData(boolean hasData);
    

}
