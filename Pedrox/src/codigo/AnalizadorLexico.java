package codigo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class AnalizadorLexico {

    private final HashMap<String, String[]> memoriaVariables = new HashMap<>();

    // ── 1. MOTOR PRINCIPAL ──────────────────────────────────────────────────
    public String analizar(String codigoFuente) {
        StringBuilder consola = new StringBuilder();
        memoriaVariables.clear();

        String[] lineas = codigoFuente.split("\n");
        Pattern patronSeparador = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"|\\S+");

        for (int i = 0; i < lineas.length; i++) {
            int lineaActual = i + 1;
            String linea = lineas[i].trim();

            if (linea.isEmpty()) {
                continue;
            }

            Matcher matcher = patronSeparador.matcher(linea);
            ArrayList<String> tokens = new ArrayList<>();

            try {
                while (matcher.find()) {
                    String palabra = matcher.group();

                    if (palabra.equals("01")) {
                        break;
                    }

                    validarToken(palabra, lineaActual);
                    tokens.add(palabra);
                }

                if (tokens.isEmpty()) {
                    continue;
                }

                String ultimoToken = tokens.get(tokens.size() - 1);

                if (!Diccionario.clasificarToken(ultimoToken).equals("Delimitador de Fin")) {
                    throw new ExcepcionSintactica("Falta 'fin' al final de la instrucción", lineaActual);
                }

                for (int t = 0; t < tokens.size() - 1; t++) {
                    if (Diccionario.clasificarToken(tokens.get(t)).equals("Delimitador de Fin")) {
                        throw new ExcepcionSintactica("Solo se permite una instrucción por línea.", lineaActual);
                    }
                }

                enrutarInstruccion(tokens, lineaActual, consola);

            } catch (ExcepcionLexica | ExcepcionSintactica | ExcepcionSemantica e) {
                consola.append(e.getMessage()).append("\n");

            } catch (NumberFormatException e) {
                consola.append("ERROR SEMÁNTICO: Valor numérico inválido")
                        .append(" (Línea ")
                        .append(lineaActual)
                        .append(")")
                        .append("\n");
            }
        }

        return consola.length() == 0
                ? "Ejecución finalizada sin salidas."
                : consola.toString().trim();
    }

    // ── 2. ENRUTADOR CENTRAL ───────────────────────────────────────────────
    private void enrutarInstruccion(ArrayList<String> tokens, int linea, StringBuilder consola) {
        String primerToken = tokens.get(0);
        String tipoPrimerToken = Diccionario.clasificarToken(primerToken);

        if (tipoPrimerToken.equals("Función de Salida")) {
            procesarSalida(tokens, linea, consola);
            return;
        }

        if (tipoPrimerToken.startsWith("Palabra Reservada")) {
            procesarDeclaracion(tokens, tipoPrimerToken, linea);
            return;
        }

        throw new ExcepcionSintactica("Instrucción no reconocida.", linea);
    }

    private void procesarSalida(ArrayList<String> tokens, int linea, StringBuilder consola) {
        if (tokens.size() != 3) {
            throw new ExcepcionSintactica("Estructura: sacar [Variable o Texto] fin", linea);
        }

        String aImprimir = tokens.get(1);

        if (aImprimir.startsWith("\"")) {
            consola.append(procesarCadena(aImprimir)).append("\n");

        } else if (memoriaVariables.containsKey(aImprimir)) {
            consola.append(memoriaVariables.get(aImprimir)[0]).append("\n");

        } else {
            throw new ExcepcionSemantica("Variable '" + aImprimir + "' no declarada.", linea);
        }
    }

    private void procesarDeclaracion(ArrayList<String> tokens, String tipoPrimerToken, int linea) {
        if (tokens.size() < 5 || !Diccionario.clasificarToken(tokens.get(2)).equals("Operador de Asignación")) {
            throw new ExcepcionSintactica("Estructura: [Tipo] [Variable] igual [Valor] fin", linea);
        }

        String nombreVar = tokens.get(1);

        validarNombreVariable(nombreVar, linea);
        validarRedeclaracion(nombreVar, tipoPrimerToken, linea);

        TipoDato tipoDato = fabricarTipoDato(tipoPrimerToken, linea);
        tipoDato.procesar(tokens, nombreVar, linea);
    }

    private TipoDato fabricarTipoDato(String tipoPrimerToken, int linea) {
        if (tipoPrimerToken.contains("Int")) {
            return new TipoEntierro(memoriaVariables);
        }

        if (tipoPrimerToken.contains("Double")) {
            return new TipoEsquimales(memoriaVariables);
        }

        if (tipoPrimerToken.contains("String")) {
            return new TipoCenas(memoriaVariables);
        }

        if (tipoPrimerToken.contains("Boolean")) {
            return new TipoCambiante(memoriaVariables);
        }

        throw new ExcepcionSintactica("Tipo de dato no reconocido.", linea);
    }

    // ── 3. VALIDADORES GENERALES ───────────────────────────────────────────
    private void validarNombreVariable(String nombreVar, int linea) {
        if (!nombreVar.matches("[a-z][a-zA-Z0-9_]*")) {
            throw new ExcepcionLexica("Variable inválida.", linea);
        }

        if (!Diccionario.clasificarToken(nombreVar).equals("Desconocido")
                || nombreVar.equals("verdadero")
                || nombreVar.equals("falso")) {
            throw new ExcepcionLexica("No puedes usar una palabra reservada como nombre de variable.", linea);
        }
    }

    private void validarRedeclaracion(String nombreVar, String tipoNuevo, int linea) {
        if (memoriaVariables.containsKey(nombreVar)
                && !memoriaVariables.get(nombreVar)[1].equals(tipoNuevo)) {
            throw new ExcepcionSemantica("Variable redeclarada con tipo distinto.", linea);
        }
    }

    private void validarToken(String palabra, int linea) {
        if (!Diccionario.clasificarToken(palabra).equals("Desconocido")
                || palabra.matches("[a-z][a-zA-Z0-9_]*")
                || palabra.matches("-?[0-9]{1,10}(\\.[0-9]{1,8})?")
                || palabra.matches("\"(?:[^\"\\\\]|\\\\.)*\"")) {
            return;
        }

        if (palabra.matches("-?[0-9]+\\.")) {
            throw new ExcepcionLexica(
                    "Número decimal mal escrito: '" + palabra + "'. Usa un formato como 8.0",
                    linea
            );
        }

        if (palabra.matches("-?\\.[0-9]+")) {
            throw new ExcepcionLexica(
                    "Número decimal mal escrito: '" + palabra + "'. Usa un formato como 0.8",
                    linea
            );
        }

        if (palabra.matches("-?[0-9]{11,}(\\.[0-9]+)?")) {
            throw new ExcepcionLexica(
                    "Número fuera de límite: máximo 10 dígitos enteros.",
                    linea
            );
        }

        if (palabra.matches("-?[0-9]+\\.[0-9]{9,}")) {
            throw new ExcepcionLexica(
                    "Número fuera de límite: máximo 8 dígitos decimales.",
                    linea
            );
        }

        throw new ExcepcionLexica("Token desconocido: " + palabra, linea);
    }

    private String procesarCadena(String raw) {
        return raw.startsWith("\"") ? raw.replace("\"", "") : raw;
    }
}