package pe.test.exchange.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;

@Repository
public class ConversionPersistenceAdapter implements ConversionRepository {

    private final SpringDataConversionRepository repository;

    public ConversionPersistenceAdapter(
            SpringDataConversionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Conversion guardar(Conversion conversion) {

        ConversionJpaEntity entity = new ConversionJpaEntity(
                conversion.monto(),
                conversion.monedaOrigen(),
                conversion.monedaDestino(),
                conversion.tipoCambio(),
                conversion.montoConvertido(),
                conversion.fechaConversion()
        );

        repository.save(entity);

        return conversion;
    }
}
