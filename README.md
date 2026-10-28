# Desafío Técnico – API de Tipo de Cambio

Microservicio REST desarrollado con Java 17 y Spring Boot para convertir montos entre distintas monedas, utilizando tipos de cambio reales obtenidos desde ExchangeRate-API mediante OpenFeign.

Cada conversión exitosa se registra en una base de datos H2 en memoria.

## Tecnologías

- Java 17
- Spring Boot 4.0.6
- Spring Cloud OpenFeign 2025.1.1
- Spring Data JPA
- H2 Database
- Jakarta Bean Validation
- JUnit 5 y Mockito
- Maven Wrapper

## Arquitectura

El proyecto utiliza una arquitectura en capas basada en principios de Domain-Driven Design (DDD), con separación de responsabilidades y dependencia de interfaces.

### Domain

Contiene el modelo de negocio, los contratos y las reglas de conversión:

- `Conversion`: modelo de dominio de una conversión.
- `ConversionRepository`: contrato para registrar conversiones.
- `ExchangeRateProvider`: contrato para obtener tipos de cambio.
- `CurrencyConversionDomainService`: cálculo monetario y redondeo según la moneda destino.
- Excepciones específicas del dominio y del proveedor.

### Application

Coordina los casos de uso y expone la API REST:

- `ConversionController`: endpoint HTTP.
- `ConversionApplicationService`: validación, obtención de tasa, conversión y persistencia.
- `ConversionRequest` y `ConversionResponse`: contratos de entrada y salida.
- `GlobalExceptionHandler`: manejo centralizado de errores HTTP.

### Infrastructure

Implementa las integraciones técnicas:

- `ExchangeRateApiClient`: cliente OpenFeign para ExchangeRate-API.
- `ExchangeRateFeignAdapter`: implementación de `ExchangeRateProvider`.
- `ConversionJpaEntity`: entidad de persistencia.
- `SpringDataConversionRepository`: repositorio Spring Data JPA.
- `ConversionPersistenceAdapter`: implementación de `ConversionRepository`.

El dominio no depende directamente de OpenFeign ni de Spring Data JPA.

## Requisitos previos

- JDK 17 o superior.
- Acceso a Internet para consultar ExchangeRate-API.
- API key válida de ExchangeRate-API.

No es necesario instalar Maven, porque el proyecto incluye Maven Wrapper.

## Configuración de la API key

La clave se obtiene del documento del desafío técnico.

La aplicación utiliza la variable de entorno `EXCHANGE_RATE_API_KEY`:

```yaml
exchange-rate:
  api:
    base-url: https://v6.exchangerate-api.com/v6
    key: ${EXCHANGE_RATE_API_KEY}
```

Por seguridad, la clave no debe almacenarse directamente en el código fuente ni publicarse en GitHub.

### Windows PowerShell

```powershell
$env:EXCHANGE_RATE_API_KEY = "TU_API_KEY"
```

### Linux / macOS

```bash
export EXCHANGE_RATE_API_KEY="TU_API_KEY"
```

## Ejecutar la aplicación

Desde la raíz del proyecto:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en:

`http://localhost:8080`

También se puede generar y ejecutar el JAR:

### Windows

```powershell
.\mvnw.cmd clean package
java -jar target/exchange-0.0.1-SNAPSHOT.jar
```

### Linux / macOS

```bash
./mvnw clean package
java -jar target/exchange-0.0.1-SNAPSHOT.jar
```

## API REST

### Convertir monedas

**Endpoint**

```http
POST /api/conversion
Content-Type: application/json
```

**URL local**

`http://localhost:8080/api/conversion`

**Request**

```json
{
  "monto": 100,
  "monedaOrigen": "USD",
  "monedaDestino": "PEN"
}
```

**Response de ejemplo**

```json
{
  "monto": 100,
  "montoConvertido": 344.86,
  "monedaOrigen": "USD",
  "monedaDestino": "PEN",
  "tipoCambio": 3.4486
}
```

Los valores son ilustrativos. El tipo de cambio real depende de la respuesta de ExchangeRate-API en el momento de la consulta.

### Ejemplo con cURL

```bash
curl -X POST http://localhost:8080/api/conversion \
  -H "Content-Type: application/json" \
  -d '{"monto":100,"monedaOrigen":"USD","monedaDestino":"PEN"}'
```

## Reglas de negocio

- El monto debe ser mayor que cero.
- Las monedas se normalizan a mayúsculas.
- Los códigos de moneda se validan mediante `java.util.Currency`.
- Si la moneda de origen y destino son iguales, se aplica un tipo de cambio de `1`.
- Los cálculos utilizan `BigDecimal`.
- El resultado se redondea con `RoundingMode.HALF_UP`, según los decimales definidos para la moneda destino.
- Solo se registran conversiones completadas exitosamente.

## Manejo de errores

Se utiliza `GlobalExceptionHandler` para devolver respuestas HTTP consistentes.

| Situación | Código HTTP |
|---|---|
| Monto inválido o datos obligatorios ausentes | 400 Bad Request |
| Código de moneda inválido | 400 Bad Request |
| Respuesta inválida del proveedor | 502 Bad Gateway |
| Tipo de cambio no disponible | 502 Bad Gateway |
| Error HTTP del proveedor externo | 502 Bad Gateway |
| Error de conexión con el proveedor | 503 Service Unavailable |
| Error interno inesperado | 500 Internal Server Error |

**Ejemplo de error de validación**

```json
{
  "timestamp": "2026-10-28T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El monto debe ser mayor a cero"
}
```

## Persistencia H2

Cada conversión exitosa se almacena en la tabla `conversions`, incluyendo:

- Identificador autogenerado.
- Monto original.
- Moneda de origen.
- Moneda de destino.
- Tipo de cambio aplicado.
- Monto convertido.
- Fecha y hora de la operación.

La base de datos es temporal y sus datos se pierden al detener la aplicación.

### Acceder a la consola H2

URL:

`http://localhost:8080/h2-console`

Configuración:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `sa` |
| Password | `password` |

Consulta para visualizar las conversiones:

```sql
SELECT *
FROM conversions
ORDER BY id DESC;
```

## Pruebas

El proyecto incluye pruebas unitarias con JUnit 5 y Mockito, además de una prueba de arranque de Spring Boot.

Los escenarios cubiertos incluyen:

- Conversión correcta y registro en repositorio.
- Rechazo de montos negativos.
- Rechazo de códigos de moneda inválidos.
- Ausencia de persistencia cuando falla el proveedor.
- Conversión entre monedas iguales.
- Redondeo según la moneda destino.
- Obtención correcta del tipo de cambio mediante el adaptador.
- Respuestas nulas o fallidas del proveedor.
- Tipo de cambio inexistente o no positivo.
- Arranque del contexto Spring Boot.

### Ejecutar pruebas en Windows

```powershell
.\mvnw.cmd clean test
```

### Ejecutar pruebas en Linux / macOS

```bash
./mvnw clean test
```

### Verificación completa

```bash
./mvnw clean verify
```

Última ejecución local: 12 pruebas exitosas, sin fallos ni errores.

## Decisiones técnicas

**Separación por capas:** permite mantener la lógica de negocio independiente de la infraestructura.

**Interfaces de dominio:** facilitan sustituir integraciones externas y probar los casos de uso con mocks.

**BigDecimal:** evita los problemas de precisión asociados al uso de tipos de punto flotante para importes monetarios.

**H2 en memoria:** permite ejecutar y verificar el microservicio sin instalar una base de datos externa.

**OpenFeign:** simplifica la integración HTTP con el proveedor de tipos de cambio.

**Manejo centralizado de excepciones:** mantiene respuestas HTTP consistentes y evita exponer detalles internos innecesarios.
