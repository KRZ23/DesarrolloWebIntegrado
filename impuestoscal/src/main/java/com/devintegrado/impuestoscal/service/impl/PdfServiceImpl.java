package com.devintegrado.impuestoscal.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.Trabajador;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.TrabajadorRepository;
import com.devintegrado.impuestoscal.service.OperacionIGVService;
import com.devintegrado.impuestoscal.service.PdfService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfServiceImpl implements PdfService {

    private final OperacionIGVService operacionIGVService;
    private final com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;

    public PdfServiceImpl(OperacionIGVService operacionIGVService, 
                          com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository,
                          TrabajadorRepository trabajadorRepository) {
        this.operacionIGVService = operacionIGVService;
        this.usuarioRepository = usuarioRepository;
        this.trabajadorRepository = trabajadorRepository;
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
            addCell(table, String.format("%.2f", resumen.getBaseVentas()), false);

            addCell(table, "IGV Ventas (Débito Fiscal)", false);
            addCell(table, String.format("%.2f", resumen.getIgvVentas()), false);

            addCell(table, "Compras Netas", false);
            addCell(table, String.format("%.2f", resumen.getBaseCompras()), false);

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

    @Override
    public byte[] generarPlanillaTrabajadores(String rut10) {
        Usuario usuario = usuarioRepository.findByRut10(rut10).orElseThrow();
        List<Trabajador> trabajadores = trabajadorRepository.findByEmpresaAndActivoTrue(usuario);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Planilla de Trabajadores", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Datos de la Empresa
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Empresa: " + usuario.getNombre(), fontBold));
            document.add(new Paragraph("RUC: " + usuario.getRut10()));
            document.add(new Paragraph("Fecha de Emisión: " + java.time.LocalDate.now()));
            document.add(Chunk.NEWLINE);

            // Tabla de Trabajadores
            PdfPTable table = new PdfPTable(5); // DNI, Nombre, Sueldo Bruto, Descuentos, Neto
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2f, 4f, 2f, 2f, 2f});

            addCell(table, "DNI", true);
            addCell(table, "Trabajador", true);
            addCell(table, "Sueldo Bruto", true);
            addCell(table, "Descuentos", true);
            addCell(table, "Sueldo Neto", true);

            java.math.BigDecimal totalBruto = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalNeto = java.math.BigDecimal.ZERO;

            for (Trabajador t : trabajadores) {
                addCell(table, t.getDni(), false);
                addCell(table, t.getNombreCompleto(), false);
                addCell(table, "S/ " + t.getSueldoBruto(), false);
                
                java.math.BigDecimal descuentos = (t.getAportePensionario() != null ? t.getAportePensionario() : java.math.BigDecimal.ZERO)
                        .add(t.getRetencionQuintaCategoria() != null ? t.getRetencionQuintaCategoria() : java.math.BigDecimal.ZERO);
                
                addCell(table, "S/ " + descuentos, false);
                addCell(table, "S/ " + t.getSueldoNeto(), false);

                totalBruto = totalBruto.add(t.getSueldoBruto());
                if (t.getSueldoNeto() != null) {
                    totalNeto = totalNeto.add(t.getSueldoNeto());
                }
            }

            document.add(table);

            // Totales
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Planilla Bruta: S/ " + totalBruto, fontBold));
            document.add(new Paragraph("Total Planilla Neta: S/ " + totalNeto, fontBold));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar planilla", e);
        }
    }
}
