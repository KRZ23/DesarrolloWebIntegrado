package com.devintegrado.impuestoscal.controller;

import com.devintegrado.impuestoscal.service.DashboardService;
import com.devintegrado.impuestoscal.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
}


