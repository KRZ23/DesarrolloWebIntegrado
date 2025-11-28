package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.dto.UsuarioRegisterRequest;
import com.devintegrado.impuestoscal.model.Usuario;

public interface UsuarioService {
    Usuario crearUsuario(UsuarioRegisterRequest request);
}
