package com.desafiojava.desafioLibreria.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}

