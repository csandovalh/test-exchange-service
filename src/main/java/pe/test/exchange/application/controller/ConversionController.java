package pe.test.exchange.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.application.service.ConversionApplicationService;

@RestController
@RequestMapping("/api/conversion")
public class ConversionController {

    private final ConversionApplicationService conversionService;

    public ConversionController(
            ConversionApplicationService conversionService
    ) {
        this.conversionService = conversionService;
    }

    @PostMapping
    public ResponseEntity<ConversionResponse> convertir(
            @Valid @RequestBody ConversionRequest request
    ) {
        ConversionResponse response =
                conversionService.convertir(request);

        return ResponseEntity.ok(response);
    }
}
