package codigo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TipoCenas extends TipoDato {

    public TipoCenas(HashMap<String, String[]> memoriaVariables) {
        super(memoriaVariables);
    }

    @Override
    protected String getTipoEsperado() {
        return "Palabra Reservada (Tipo String)";
    }

    @Override
    public void procesar(ArrayList<String> tokens, String nombreVar, int linea) {
        String tipoEsperado = getTipoEsperado();

        if (esOperacionValida(tokens)) {
            List<String> expresion = tokens.subList(3, tokens.size() - 1);

            validarOperandosOperacion(expresion, tipoEsperado, linea);
            validarOperadoresConcatenacion(expresion, linea);

            StringBuilder concatenacion = new StringBuilder();
            concatenacion.append(resolverValor(expresion.get(0), linea));

            for (int i = 1; i < expresion.size(); i += 2) {
                concatenacion.append(resolverValor(expresion.get(i + 1), linea));
            }

            memoriaVariables.put(nombreVar, new String[]{concatenacion.toString(), tipoEsperado});

        } else if (esAsignacionValida(tokens)) {
            String rawValor = tokens.get(3);

            if (!obtenerTipo(rawValor).equals(tipoEsperado)) {
                throw new ExcepcionSemantica("Choque de tipos: se esperaba cenas.", linea);
            }

            memoriaVariables.put(nombreVar, new String[]{resolverValor(rawValor, linea), tipoEsperado});

        } else {
            throw new ExcepcionSintactica("Estructura inválida para variable tipo cenas.", linea);
        }
    }
}