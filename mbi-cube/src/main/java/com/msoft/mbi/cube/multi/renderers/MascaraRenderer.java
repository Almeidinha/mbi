package com.msoft.mbi.cube.multi.renderers;

import java.io.Serializable;

public interface MascaraRenderer extends Serializable {

    public abstract Object aplica(Object valor);
}
