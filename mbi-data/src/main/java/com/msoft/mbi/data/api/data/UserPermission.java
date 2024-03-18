package com.msoft.mbi.data.api.data;

import com.msoft.mbi.data.helpers.ComposeNumber;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPermission implements Permission {

    private int actions;
    private int userId;
    private BIInterface anBIInterface;

    @Override
    public BIInterface getInterface() {
        return this.anBIInterface;
    }

    @Override
    public void setInterface(BIInterface anBIInterface) {
        this.anBIInterface = anBIInterface;
    }

    @Override
    public boolean hasAction(int action) {
        return ComposeNumber.composeCount(this.actions, action);
    }

    @Override
    public boolean temMaxWeight() {
        return this.anBIInterface.getMaxWeight() == this.actions;
    }
}
