package com.devintegrado.impuestoscal.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devintegrado.impuestoscal.dto.PagoDto;
import com.devintegrado.impuestoscal.service.DashboardService;
import com.devintegrado.impuestoscal.service.PdfService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final PdfService pdfService;

    public DashboardController(DashboardService dashboardService, PdfService pdfService) {
        this.dashboardService = dashboardService;
        this.pdfService = pdfService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> resumen(Authentication auth) {
        return ResponseEntity.ok(dashboardService.obtenerResumen(auth.getName()));
    }

    @GetMapping("/declaracion-pdf")
    public ResponseEntity<byte[]> generarDeclaracion(
            @RequestParam int mes,
            @RequestParam int anio,
            Authentication auth) {
        byte[] pdf = pdfService.generarDeclaracionMensual(auth.getName(), mes, anio);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=declaracion.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @org.springframework.web.bind.annotation.PostMapping("/pagar")
    public ResponseEntity<Void> realizarPago(@RequestBody PagoDto request, Authentication auth) {
        dashboardService.realizarPagoSimulado(auth.getName(), request.getRegistroIds(), request.isPagarIgvMes());
        return ResponseEntity.ok().build();
    }
}


