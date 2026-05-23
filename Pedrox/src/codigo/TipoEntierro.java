package codigo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class TipoEntierro extends TipoDato {

    public TipoEntierro(HashMap<String, String[]> memoriaVariables) {
        super(memoriaVariables);
    }

    @Override
    protected String getTipoEsperado() {
        return "Palabra Reservada (Tipo Int)";
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

            validarResultadoEntero(resultado, linea);

            memoriaVariables.put(
                    nombreVar,
                    new String[]{new BigDecimal(resultado).toBigInteger().toString(), tipoEsperado}
            );

        } else if (esAsignacionValida(tokens)) {
            String rawValor = tokens.get(3);

            if (!obtenerTipo(rawValor).equals(tipoEsperado)) {
                throw new ExcepcionSemantica("Choque de tipos: se esperaba entierro.", linea);
            }

            memoriaVariables.put(nombreVar, new String[]{resolverValor(rawValor, linea), tipoEsperado});

        } else {
            throw new ExcepcionSintactica("Estructura inválida para variable tipo entierro.", linea);
        }
    }
}