package com.devintegrado.impuestoscal.service;

public interface PdfService {
    byte[] generarDeclaracionMensual(String rut10, int mes, int anio);
    byte[] generarPlanillaTrabajadores(String rut10);
}
