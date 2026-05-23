package codigo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class TipoDato {

    protected final HashMap<String, String[]> memoriaVariables;

    public TipoDato(HashMap<String, String[]> memoriaVariables) {
        this.memoriaVariables = memoriaVariables;
    }

    public abstract void procesar(ArrayList<String> tokens, String nombreVar, int linea);

    protected abstract String getTipoEsperado();

    protected boolean esOperacionValida(ArrayList<String> tokens) {
        if (tokens.size() < 7) {
            return false;
        }

        int inicioExpresion = 3;
        int finExpresion = tokens.size() - 2;
        int cantidadElementosExpresion = finExpresion - inicioExpresion + 1;

        if (cantidadElementosExpresion % 2 == 0) {
            return false;
        }

        for (int i = 4; i < tokens.size() - 1; i += 2) {
            if (!Diccionario.clasificarToken(tokens.get(i)).startsWith("Operador")) {
                return false;
            }
        }

        return true;
    }

    protected boolean esAsignacionValida(ArrayList<String> tokens) {
        return tokens.size() == 5;
    }

    protected String ejecutarMatematicaEstricta(List<String> expresion, int linea, String tipoEsperado) {
        validarOperandosOperacion(expresion, tipoEsperado, linea);

        BigDecimal acumulador = new BigDecimal(resolverValor(expresion.get(0), linea));

        for (int i = 1; i < expresion.size(); i += 2) {
            BigDecimal siguiente = new BigDecimal(resolverValor(expresion.get(i + 1), linea));

            String resultado = Operador
                    .fabricar(expresion.get(i), linea)
                    .evaluar(acumulador, siguiente, linea);

            validarLimitesMemoria(resultado, linea);

            acumulador = new BigDecimal(resultado);
        }

        return acumulador.toPlainString();
    }

    protected void validarOperandosOperacion(List<String> expresion, String tipoEsperado, int linea) {
        for (int i = 0; i < expresion.size(); i += 2) {
            String operando = expresion.get(i);
            String tipoReal = obtenerTipo(operando);

            if (tipoReal.equals("Desconocido")) {
                throw new ExcepcionSemantica(
                        "Variable o valor '" + operando + "' no declarado o inválido.",
                        linea
                );
            }

            if (!tipoReal.equals(tipoEsperado)) {
                throw new ExcepcionSemantica(
                        "Choque de tipos: no puedes operar "
                        + nombreTipo(tipoReal)
                        + " con "
                        + nombreTipo(tipoEsperado)
                        + ".",
                        linea
                );
            }
        }
    }

    protected void validarOperadoresConcatenacion(List<String> expresion, int linea) {
        for (int i = 1; i < expresion.size(); i += 2) {
            if (!expresion.get(i).equals("mas")) {
                throw new ExcepcionSemantica(
                        "Las cadenas tipo cenas solo pueden concatenarse con 'mas'.",
                        linea
                );
            }
        }
    }

    protected void validarResultadoEntero(String resultado, int linea) {
        BigDecimal numero = new BigDecimal(resultado).stripTrailingZeros();

        if (numero.scale() > 0) {
            throw new ExcepcionSemantica(
                    "Choque de tipos: el resultado tiene decimales y no puede guardarse en entierro.",
                    linea
            );
        }
    }

    protected void validarLimitesMemoria(String valorStr, int linea) {
        String limpio = new BigDecimal(valorStr)
                .stripTrailingZeros()
                .toPlainString()
                .replace("-", "");

        if (limpio.contains(".")) {
            String[] partes = limpio.split("\\.");

            String enteros = partes[0];
            String decimales = partes.length > 1 ? partes[1] : "";

            if (enteros.length() > 10 || decimales.length() > 8) {
                throw new ExcepcionSemantica(
                        "Overflow: El resultado de la operación excedió 10 enteros o 8 decimales.",
                        linea
                );
            }

        } else if (limpio.length() > 10) {
            throw new ExcepcionSemantica(
                    "Overflow: El resultado de la operación excedió 10 enteros.",
                    linea
            );
        }
    }

    protected String obtenerTipo(String token) {
        if (memoriaVariables.containsKey(token)) {
            return memoriaVariables.get(token)[1];
        }

        if (token.matches("-?[0-9]{1,10}")) {
            return "Palabra Reservada (Tipo Int)";
        }

        if (token.matches("-?[0-9]{1,10}\\.[0-9]{1,8}")) {
            return "Palabra Reservada (Tipo Double)";
        }

        if (token.matches("\"(?:[^\"\\\\]|\\\\.)*\"")) {
            return "Palabra Reservada (Tipo String)";
        }

        if (token.equals("verdadero") || token.equals("falso")) {
            return "Palabra Reservada (Tipo Boolean)";
        }

        return "Desconocido";
    }

    protected String resolverValor(String token, int linea) {
        if (memoriaVariables.containsKey(token)) {
            return memoriaVariables.get(token)[0];
        }

        if (token.matches("-?[0-9]{1,10}(\\.[0-9]{1,8})?")
                || token.matches("\"(?:[^\"\\\\]|\\\\.)*\"")
                || token.equals("verdadero")
                || token.equals("falso")) {
            return procesarCadena(token);
        }

        throw new ExcepcionSemantica(
                "Variable o valor '" + token + "' no declarado o inválido.",
                linea
        );
    }

    protected String procesarCadena(String raw) {
        return raw.startsWith("\"") ? raw.replace("\"", "") : raw;
    }

    protected String nombreTipo(String tipo) {
        if (tipo.contains("Int")) {
            return "entierro";
        }

        if (tipo.contains("Double")) {
            return "esquimales";
        }

        if (tipo.contains("String")) {
            return "cenas";
        }

        if (tipo.contains("Boolean")) {
            return "cambiante";
        }

        return "desconocido";
    }
}