package com.msoft.mbi.data.api.data;

public interface Permission {

    public BIInterface getInterface();

    public void setInterface(BIInterface anBIInterface);

    public boolean hasAction(int action);

    public void setActions(int actions);

    public int getActions();

    public boolean temMaxWeight();

}
