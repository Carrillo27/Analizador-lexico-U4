package codigo;

public class Diccionario {

    // Método que recibe una palabra normal (ej. "entierro") y devuelve su tipo
    public static String clasificarToken(String palabra) {
        
        // 1. Invertimos la palabra
        StringBuilder palabraAlReves = new StringBuilder();
        for (int i = palabra.length() - 1; i >= 0; i--) {
            palabraAlReves.append(palabra.charAt(i));
        }
        
        // 2. Buscamos en el diccionario invertido
        return switch (palabraAlReves.toString()) {
            case "orreitne" -> "Palabra Reservada (Tipo Int)";
            case "selamiuqse" -> "Palabra Reservada (Tipo Double)";
            case "sanec" -> "Palabra Reservada (Tipo String)";
            case "etnaibmac" -> "Palabra Reservada (Tipo Boolean)";
            case "racas" -> "Función de Salida";
            case "laugi" -> "Operador de Asignación";
            case "sam" -> "Operador Aritmético (+)";
            case "sonem" -> "Operador Aritmético (-)";
            case "rop" -> "Operador Aritmético (*)";
            case "edivid" -> "Operador Aritmético (/)";
            case "nif" -> "Delimitador de Fin";
            default -> "Desconocido";
        };
    }
}