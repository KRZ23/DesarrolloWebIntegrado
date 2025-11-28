package com.devintegrado.impuestoscal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devintegrado.impuestoscal.dto.TrabajadorDtos;
import com.devintegrado.impuestoscal.model.TipoRegimenPensionario;
import com.devintegrado.impuestoscal.model.Trabajador;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.TrabajadorRepository;
import com.devintegrado.impuestoscal.service.TrabajadorService;

@Service
public class TrabajadorServiceImpl implements TrabajadorService {
    
    private final TrabajadorRepository repository;
    
    // Tasas de aporte (simuladas - en Perú varían)
    private static final BigDecimal TASA_ONP = new BigDecimal("0.13"); // 13% ONP
    private static final BigDecimal TASA_AFP_PROMEDIO = new BigDecimal("0.11"); // 11% AFP (promedio)
    private static final BigDecimal TASA_ESSALUD = new BigDecimal("0.09"); // 9% EsSalud (a cargo del empleador)
    private static final BigDecimal UIT = new BigDecimal("5150"); // UIT 2025 (simulado)
    
    public TrabajadorServiceImpl(TrabajadorRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDtos.Response> listarPorEmpresa(Usuario empresa) {
        return repository.findByEmpresaAndActivoTrue(empresa).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrabajadorDtos.Response buscarPorId(Long id, Usuario empresa) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        if (!trabajador.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para ver este trabajador");
        }
        
        return toDto(trabajador);
    }
    
    @Override
    @Transactional
    public TrabajadorDtos.Response crear(TrabajadorDtos.CreateRequest request, Usuario empresa) {
        // Validar que no exista otro trabajador con el mismo DNI en la empresa
        repository.findByDniAndEmpresaAndActivoTrue(request.getDni(), empresa)
                .ifPresent(t -> {
                    throw new IllegalArgumentException("Ya existe un trabajador con ese DNI");
                });
        
        Trabajador trabajador = Trabajador.builder()
                .dni(request.getDni())
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .fechaIngreso(request.getFechaIngreso())
                .sueldoBruto(request.getSueldoBruto())
                .regimenPensionario(request.getRegimenPensionario())
                .afpNombre(request.getAfpNombre())
                .empresa(empresa)
                .activo(true)
                .build();
        
        // Calcular descuentos
        calcularDescuentos(trabajador);
        
        trabajador = repository.save(trabajador);
        return toDto(trabajador);
    }
    
    @Override
    @Transactional
    public TrabajadorDtos.Response actualizar(Long id, TrabajadorDtos.UpdateRequest request, Usuario empresa) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        if (!trabajador.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para modificar este trabajador");
        }
        
        if (!trabajador.getActivo()) {
            throw new IllegalArgumentException("No se puede modificar un trabajador inactivo");
        }
        
        // Actualizar campos
        if (request.getNombres() != null) {
            trabajador.setNombres(request.getNombres());
        }
        if (request.getApellidoPaterno() != null) {
            trabajador.setApellidoPaterno(request.getApellidoPaterno());
        }
        if (request.getApellidoMaterno() != null) {
            trabajador.setApellidoMaterno(request.getApellidoMaterno());
        }
        if (request.getEmail() != null) {
            trabajador.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            trabajador.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            trabajador.setDireccion(request.getDireccion());
        }
        if (request.getFechaCese() != null) {
            trabajador.setFechaCese(request.getFechaCese());
        }
        if (request.getSueldoBruto() != null) {
            trabajador.setSueldoBruto(request.getSueldoBruto());
        }
        if (request.getRegimenPensionario() != null) {
            trabajador.setRegimenPensionario(request.getRegimenPensionario());
        }
        if (request.getAfpNombre() != null) {
            trabajador.setAfpNombre(request.getAfpNombre());
        }
        
        // Recalcular descuentos
        calcularDescuentos(trabajador);
        
        trabajador = repository.save(trabajador);
        return toDto(trabajador);
    }
    
    @Override
    @Transactional
    public void eliminar(Long id, Usuario empresa) {
        Trabajador trabajador = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        
        if (!trabajador.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para eliminar este trabajador");
        }
        
        // Eliminación lógica: cambiar estado a inactivo
        trabajador.setActivo(false);
        repository.save(trabajador);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrabajadorDtos.ResumenPlanilla calcularResumenPlanilla(Usuario empresa, int mes, int anio) {
        long totalTrabajadores = repository.countByEmpresaAndActivoTrue(empresa);
        BigDecimal totalSueldosBrutos = repository.calcularTotalSueldosBrutos(empresa);
        BigDecimal totalAportes = repository.calcularTotalAportesPensionarios(empresa);
        BigDecimal totalRetenciones = repository.calcularTotalRetenciones(empresa);
        BigDecimal totalSueldosNetos = repository.calcularTotalSueldosNetos(empresa);
        
        totalSueldosBrutos = totalSueldosBrutos != null ? totalSueldosBrutos : BigDecimal.ZERO;
        totalAportes = totalAportes != null ? totalAportes : BigDecimal.ZERO;
        totalRetenciones = totalRetenciones != null ? totalRetenciones : BigDecimal.ZERO;
        totalSueldosNetos = totalSueldosNetos != null ? totalSueldosNetos : BigDecimal.ZERO;
        
        // Calcular EsSalud (9% a cargo del empleador)
        BigDecimal essalud = totalSueldosBrutos.multiply(TASA_ESSALUD).setScale(2, RoundingMode.HALF_UP);
        
        return TrabajadorDtos.ResumenPlanilla.builder()
                .mes(mes)
                .anio(anio)
                .totalTrabajadores((int) totalTrabajadores)
                .totalSueldosBrutos(totalSueldosBrutos)
                .totalAportesPensionarios(totalAportes)
                .totalRetenciones(totalRetenciones)
                .totalSueldosNetos(totalSueldosNetos)
                .essaludEmpleador(essalud)
                .build();
    }
    
    /**
     * Calcula los descuentos y sueldo neto del trabajador
     */
    private void calcularDescuentos(Trabajador trabajador) {
        BigDecimal sueldoBruto = trabajador.getSueldoBruto();
        
        // 1. Calcular aporte pensionario
        BigDecimal aportePensionario;
        if (trabajador.getRegimenPensionario() == TipoRegimenPensionario.ONP) {
            aportePensionario = sueldoBruto.multiply(TASA_ONP);
        } else {
            aportePensionario = sueldoBruto.multiply(TASA_AFP_PROMEDIO);
        }
        aportePensionario = aportePensionario.setScale(2, RoundingMode.HALF_UP);
        trabajador.setAportePensionario(aportePensionario);
        
        // 2. Calcular retención de 5ta categoría (simplificado)
        // En Perú: si el ingreso anual supera 7 UIT, se aplica impuesto progresivo
        BigDecimal ingresoAnual = sueldoBruto.multiply(new BigDecimal("12"));
        BigDecimal uitMinima = UIT.multiply(new BigDecimal("7")); // 7 UIT exoneradas
        
        BigDecimal retencion = BigDecimal.ZERO;
        if (ingresoAnual.compareTo(uitMinima) > 0) {
            BigDecimal baseImponible = ingresoAnual.subtract(uitMinima);
            // Tasa simplificada: 8% para los primeros 5 UIT adicionales, 14% hasta 20 UIT, etc.
            // Aquí usamos una tasa promedio del 10% para simplificar
            retencion = baseImponible.multiply(new BigDecimal("0.10"))
                    .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP); // Mensual
        }
        trabajador.setRetencionQuintaCategoria(retencion);
        
        // 3. Calcular sueldo neto
        BigDecimal sueldoNeto = sueldoBruto
                .subtract(aportePensionario)
                .subtract(retencion);
        trabajador.setSueldoNeto(sueldoNeto);
    }
    
    private TrabajadorDtos.Response toDto(Trabajador trabajador) {
        return TrabajadorDtos.Response.builder()
                .id(trabajador.getId())
                .dni(trabajador.getDni())
                .nombreCompleto(trabajador.getNombreCompleto())
                .nombres(trabajador.getNombres())
                .apellidoPaterno(trabajador.getApellidoPaterno())
                .apellidoMaterno(trabajador.getApellidoMaterno())
                .email(trabajador.getEmail())
                .telefono(trabajador.getTelefono())
                .direccion(trabajador.getDireccion())
                .fechaIngreso(trabajador.getFechaIngreso())
                .fechaCese(trabajador.getFechaCese())
                .sueldoBruto(trabajador.getSueldoBruto())
                .regimenPensionario(trabajador.getRegimenPensionario())
                .afpNombre(trabajador.getAfpNombre())
                .aportePensionario(trabajador.getAportePensionario())
                .retencionQuintaCategoria(trabajador.getRetencionQuintaCategoria())
                .sueldoNeto(trabajador.getSueldoNeto())
                .activo(trabajador.getActivo())
                .fechaCreacion(trabajador.getFechaCreacion())
                .fechaActualizacion(trabajador.getFechaActualizacion())
                .build();
    }
}
