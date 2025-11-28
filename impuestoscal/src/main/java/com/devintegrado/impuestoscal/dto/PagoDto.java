package com.devintegrado.impuestoscal.dto;

import java.util.List;

import lombok.Data;

@Data
public class PagoDto {
    private List<Long> registroIds;
    private boolean pagarIgvMes;
}
