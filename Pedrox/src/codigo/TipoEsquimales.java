package codigo;

import java.util.ArrayList;
import java.util.HashMap;

public class TipoEsquimales extends TipoDato {

    public TipoEsquimales(HashMap<String, String[]> memoriaVariables) {
        super(memoriaVariables);
    }

    @Override
    protected String getTipoEsperado() {
        return "Palabra Reservada (Tipo Double)";
    }

    @Override
    public void procesar(ArrayList<String> tokens, String nombreVar, int linea) {
        String tipoEsperado = getTipoEsperado();

        if (esOperacionValida(tokens)) {
            String resultado = ejecutarMatematicaEstricta(
                    tokens.subList(3, tokens.size() - 1),
                    linea,
                    tipoEsperado
            );

            if (!resultado.contains(".")) {
                resultado += ".0";
            }

            memoriaVariables.put(nombreVar, new String[]{resultado, tipoEsperado});

        } else if (esAsignacionValida(tokens)) {
            String rawValor = tokens.get(3);

            if (!obtenerTipo(rawValor).equals(tipoEsperado)) {
                throw new ExcepcionSemantica("Choque de tipos: se esperaba esquimales.", linea);
            }

            memoriaVariables.put(nombreVar, new String[]{resolverValor(rawValor, linea), tipoEsperado});

        } else {
            throw new ExcepcionSintactica("Estructura inválida para variable tipo esquimales.", linea);
        }
    }
}