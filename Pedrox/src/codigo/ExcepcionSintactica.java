package codigo;

public class ExcepcionSintactica extends RuntimeException {
    public ExcepcionSintactica(String mensaje, int linea) {
        super("ERROR SINTÁCTICO: " + mensaje + " (Línea " + linea + ")");
    }
}