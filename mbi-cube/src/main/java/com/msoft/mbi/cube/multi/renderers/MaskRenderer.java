package com.msoft.mbi.cube.multi.renderers;

import java.io.Serializable;

public interface MaskRenderer extends Serializable {

    public abstract Object apply(Object valor);
}
