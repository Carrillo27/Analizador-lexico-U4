package codigo;

public class Token {
    
    public String tipo;     // Para guardar si es un "Tipo de Dato", "Operador", etc.
    public String lexema;   // Para guardar la palabra exacta, ej: "entierro", "mas", "25"
    public int linea;       // Para saber en qué línea de código ocurrió (útil para errores)

    // Constructor para armar nuestro Token
    public Token(String tipo, String lexema, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
    }

    // Este método nos permitirá imprimir en consola de forma bonita para hacer pruebas
    @Override
    public String toString() {
        return "Linea: " + linea + " | Tipo: " + tipo + " | Lexema: " + lexema;
    }
}