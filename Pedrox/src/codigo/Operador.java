package codigo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Operador {

    public interface OperacionMatematica {
        String evaluar(BigDecimal num1, BigDecimal num2, int linea);
    }

    public static class Suma implements OperacionMatematica {
        @Override
        public String evaluar(BigDecimal num1, BigDecimal num2, int linea) {
            return num1.add(num2).toPlainString();
        }
    }

    public static class Resta implements OperacionMatematica {
        @Override
        public String evaluar(BigDecimal num1, BigDecimal num2, int linea) {
            return num1.subtract(num2).toPlainString();
        }
    }

    public static class Multiplicacion implements OperacionMatematica {
        @Override
        public String evaluar(BigDecimal num1, BigDecimal num2, int linea) {
            return num1.multiply(num2).toPlainString();
        }
    }

    public static class Division implements OperacionMatematica {
        @Override
        public String evaluar(BigDecimal num1, BigDecimal num2, int linea) {
            if (num2.compareTo(BigDecimal.ZERO) == 0) {
                throw new ExcepcionSemantica("Indeterminación matemática: No se puede realizar una división por cero", linea);
            }
            return num1.divide(num2, 8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        }
    }

    public static OperacionMatematica fabricar(String operador, int linea) {
        return switch (operador) {
            case "mas" -> new Suma();
            case "menos" -> new Resta();
            case "por" -> new Multiplicacion();
            case "divide" -> new Division();
            default -> throw new ExcepcionSintactica("Operador matemático no soportado: " + operador, linea);
        };
    }
}