package pe.test.exchange.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversions")
public class ConversionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal monto;

    @Column(nullable = false, length = 3)
    private String monedaOrigen;

    @Column(nullable = false, length = 3)
    private String monedaDestino;

    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal tipoCambio;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal montoConvertido;

    @Column(nullable = false)
    private LocalDateTime fechaConversion;

    protected ConversionJpaEntity() {
    }

    public ConversionJpaEntity(
            BigDecimal monto,
            String monedaOrigen,
            String monedaDestino,
            BigDecimal tipoCambio,
            BigDecimal montoConvertido,
            LocalDateTime fechaConversion
    ) {
        this.monto = monto;
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
        this.tipoCambio = tipoCambio;
        this.montoConvertido = montoConvertido;
        this.fechaConversion = fechaConversion;
    }

    public Long getId() {
        return id;
    }
}
