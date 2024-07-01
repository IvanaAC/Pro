package com.desafiojava.desafioLibreria.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);/*Se lee el valor y transforma el json en la clase
             y luego se añade la excepcion con un try chatch */
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

