
package com.administrador.app.util;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MensajeError implements Serializable {
    
    private String mensaje;
    
    private boolean error;
    
}
