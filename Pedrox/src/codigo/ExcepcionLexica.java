package codigo;

public class ExcepcionLexica extends RuntimeException {
    public ExcepcionLexica(String mensaje, int linea) {
        super("ERROR LÉXICO: " + mensaje + " (Línea " + linea + ")");
    }
}