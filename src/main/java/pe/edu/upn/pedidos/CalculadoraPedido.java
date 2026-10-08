package pe.edu.upn.pedidos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CalculadoraPedido {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal TASA_IGV = new BigDecimal("0.18");

    public BigDecimal calcularSubtotal(List<Producto> productos) {
        productos.forEach(this::validarProducto);
        return productos.stream()
                .map(this::calcularImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal aplicarDescuento(BigDecimal subtotal, BigDecimal porcentaje) {
        validarPorcentaje(porcentaje);
        BigDecimal descuento = subtotal.multiply(porcentaje).divide(CIEN);
        return subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularImpuesto(BigDecimal baseImponible) {
        return baseImponible.multiply(TASA_IGV).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(List<Producto> productos, BigDecimal porcentaje) {
        BigDecimal subtotal = calcularSubtotal(productos);
        BigDecimal baseImponible = aplicarDescuento(subtotal, porcentaje);
        BigDecimal igv = calcularImpuesto(baseImponible);
        return baseImponible.add(igv).setScale(2, RoundingMode.HALF_UP);
    }

    private void validarProducto(Producto producto) {
        if (producto.precio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (producto.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    private void validarPorcentaje(BigDecimal porcentaje) {
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
    }

    private BigDecimal calcularImporte(Producto producto) {
        return producto.precio().multiply(BigDecimal.valueOf(producto.cantidad()));
    }
}