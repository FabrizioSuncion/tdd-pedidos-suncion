package pe.edu.upn.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraPedidoTest {

    private final CalculadoraPedido calc = new CalculadoraPedido();

    private static BigDecimal soles(String monto) {
        return new BigDecimal(monto);
    }

    private static Producto producto(String nombre, String precio, int cantidad) {
        return new Producto(nombre, soles(precio), cantidad);
    }

    @Test
    void subtotalDeDosProductosSumaSusPrecios() {
        List<Producto> productos = List.of(
                producto("Mouse", "50.00", 1),
                producto("Teclado", "30.00", 1));
        assertEquals(soles("80.00"), calc.calcularSubtotal(productos));
    }

    @Test
    void subtotalMultiplicaPrecioPorCantidad() {
        List<Producto> productos = List.of(
                producto("Cuaderno", "25.50", 2),
                producto("Lapicero", "10.00", 3));
        assertEquals(soles("81.00"), calc.calcularSubtotal(productos));
    }

    @Test
    void subtotalDeListaVaciaEsCero() {
        assertEquals(soles("0.00"), calc.calcularSubtotal(List.of()));
    }

    @ParameterizedTest(name = "{0} con {1}% -> {2}")
    @CsvSource({ "100.00, 10, 90.00", "100.00, 0, 100.00", "100.00, 100, 0.00" })
    void aplicarDescuentoRestaElPorcentaje(BigDecimal subtotal, BigDecimal porcentaje,
                                           BigDecimal esperado) {
        assertEquals(esperado, calc.aplicarDescuento(subtotal, porcentaje));
    }

    @ParameterizedTest(name = "base {0} -> IGV {1}")
    @CsvSource({ "100.00, 18.00", "90.00, 16.20" })
    void calcularImpuestoAplica18PorCiento(BigDecimal baseImponible, BigDecimal esperado) {
        assertEquals(esperado, calc.calcularImpuesto(baseImponible));
    }

    @Test
    void totalConCuponDel10PorCientoEs106_20() {
        List<Producto> productos = List.of(producto("Monitor", "100.00", 1));
        assertEquals(soles("106.20"), calc.calcularTotal(productos, soles("10")));
    }

    @Test
    void totalRedondeaElIgvADosDecimales() {
        List<Producto> productos = List.of(producto("Lapiz", "11.11", 3));
        assertEquals(soles("39.33"), calc.calcularTotal(productos, BigDecimal.ZERO));
    }
}