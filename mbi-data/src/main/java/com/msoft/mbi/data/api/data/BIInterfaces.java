package com.msoft.mbi.data.api.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BIInterfaces {

    private List<BIInterface> biInterfaceList;

    public BIInterfaces() {
        loadInterfaces();
    }

    private void loadInterfaces() {

    }
}
