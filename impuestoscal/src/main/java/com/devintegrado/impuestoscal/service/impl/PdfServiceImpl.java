package com.devintegrado.impuestoscal.service.impl;

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.service.OperacionIGVService;
import com.devintegrado.impuestoscal.service.PdfService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class PdfServiceImpl implements PdfService {

    private final OperacionIGVService operacionIGVService;
    private final com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository;

    public PdfServiceImpl(OperacionIGVService operacionIGVService, com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository) {
        this.operacionIGVService = operacionIGVService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public byte[] generarDeclaracionMensual(String rut10, int mes, int anio) {
        Usuario usuario = usuarioRepository.findByRut10(rut10).orElseThrow();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Simulación de Declaración Mensual IGV", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Datos del Contribuyente
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("RUC: " + usuario.getRut10(), fontBold));
            document.add(new Paragraph("Razón Social: " + usuario.getNombre()));
            String mesNombre = Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            document.add(new Paragraph("Periodo: " + mesNombre.toUpperCase() + " " + anio));
            document.add(Chunk.NEWLINE);

            // Obtener datos
            OperacionIGVDtos.ResumenIGV resumen = operacionIGVService.calcularResumenMensual(usuario, mes, anio);

            // Tabla de Resumen
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addCell(table, "Concepto", true);
            addCell(table, "Monto (S/)", true);

            addCell(table, "Ventas Netas", false);
            addCell(table, String.format("%.2f", resumen.getTotalVentas()), false);

            addCell(table, "IGV Ventas (Débito Fiscal)", false);
            addCell(table, String.format("%.2f", resumen.getIgvVentas()), false);

            addCell(table, "Compras Netas", false);
            addCell(table, String.format("%.2f", resumen.getTotalCompras()), false);

            addCell(table, "IGV Compras (Crédito Fiscal)", false);
            addCell(table, String.format("%.2f", resumen.getIgvCompras()), false);

            addCell(table, "Impuesto Resultante", true);
            addCell(table, String.format("%.2f", resumen.getIgvAPagar()), true);

            document.add(table);

            // Pie de página
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Este documento es una simulación y no tiene valor legal ante la SUNAT."));
            document.add(new Paragraph("Generado por ImpuestosCal el " + java.time.LocalDate.now()));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private void addCell(PdfPTable table, String text, boolean bold) {
        Font font = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12) : FontFactory.getFont(FontFactory.HELVETICA, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
