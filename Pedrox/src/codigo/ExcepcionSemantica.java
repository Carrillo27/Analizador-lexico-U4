package codigo;

public class ExcepcionSemantica extends RuntimeException {
    public ExcepcionSemantica(String mensaje, int linea) {
        super("ERROR SEMÁNTICO: " + mensaje + " (Línea " + linea + ")");
    }
}