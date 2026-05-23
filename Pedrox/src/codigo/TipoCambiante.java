package codigo;

import java.util.ArrayList;
import java.util.HashMap;

public class TipoCambiante extends TipoDato {

    public TipoCambiante(HashMap<String, String[]> memoriaVariables) {
        super(memoriaVariables);
    }

    @Override
    protected String getTipoEsperado() {
        return "Palabra Reservada (Tipo Boolean)";
    }

    @Override
    public void procesar(ArrayList<String> tokens, String nombreVar, int linea) {
        String tipoEsperado = getTipoEsperado();

        if (esAsignacionValida(tokens)) {
            String rawValor = tokens.get(3);

            if (!obtenerTipo(rawValor).equals(tipoEsperado)) {
                throw new ExcepcionSemantica("Choque de tipos: se esperaba cambiante.", linea);
            }

            memoriaVariables.put(nombreVar, new String[]{resolverValor(rawValor, linea), tipoEsperado});

        } else {
            throw new ExcepcionSintactica("Estructura inválida para variable tipo cambiante.", linea);
        }
    }
}