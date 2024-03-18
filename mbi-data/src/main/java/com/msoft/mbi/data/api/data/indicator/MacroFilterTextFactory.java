package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIMacro;
import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.data.services.BIUserService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class MacroFilterTextFactory extends MacroFilterFactory {

    BIUserService biUserService;

    private final BIMacro macro;

    public MacroFilterTextFactory(Field field, BIMacro macro) throws BIException {
        this.macro = macro;
        super.operators = new ArrayList<>();
        super.values = new ArrayList<>();
        super.setField(field);
        this.unravelMacro();
    }


    @Override
    public void unravelMacro() throws BIException {
        // TODO get usuário logado
        BIUserDTO biUserDTO = biUserService.findByEmail("");
        if (biUserDTO != null) {
            if (macro.getId().equals(BIMacro.LOGIN_USUARIO_LOGADO)) {
                super.operators.add(new Operator("="));
                super.values.add(biUserDTO.getEmail());
            } else if (macro.getId().equals(BIMacro.NOME_USUARIO_LOGADO)) {
                super.operators.add(new Operator("="));
                super.values.add(biUserDTO.getFirstName());
            } else if (macro.getId().equals(BIMacro.EMAIL_USUARIO_LOGADO)) {
                super.operators.add(new Operator("="));
                super.values.add(biUserDTO.getEmail());
            }
        }
    }
}
