package com.desafiojava.desafioLibreria.principal;

import com.desafiojava.desafioLibreria.models.Datos;
import com.desafiojava.desafioLibreria.models.DatosAutor;
import com.desafiojava.desafioLibreria.models.DatosLibros;
import com.desafiojava.desafioLibreria.service.ConsumoAPI;
import com.desafiojava.desafioLibreria.service.ConvierteDatos;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);

    public void muestraElMenu(){
        while (true) {
            System.out.println("""
            **************** Menú de Busqueda ****************
            \n1. Ver el Top 10 de libros más descargados
            2. Buscar libro por nombre
            3. Buscar libros por nombre de autor
            4. Buscar libros por año de nacimiento del autor
            5. Mostrar estadísticas de descargas
            6. Salir
            \n Elije una opcion: 
            **************************************************
             """);

            int opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    mostrarTop10LibrosDescargados();
                    break;
                case 2:
                    buscarLibroPorNombre();
                    break;
                case 3:
                    buscarLibrosPorNombreAutor();
                    break;
                case 4:
                    buscarLibrosPorFechaNacimientoAutor();
                    break;
                case 5:
                    mostrarEstaditicas();
                    break;
                case 6:
                    System.out.println("Saliendo del programa...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    break;
            }
            System.out.println();
        }
    }

    private void mostrarTop10LibrosDescargados() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        System.out.println("Este es el Top 10 de los libros más descargados: ");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescarga).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);
    }

    private void buscarLibroPorNombre() {
        System.out.println("Ingresa el nombre del libro que deseas buscar: ");
        var tituloUsuario = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloUsuario.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloUsuario.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado ");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    private void buscarLibrosPorNombreAutor() {
        System.out.println("Ingresa el nombre del autor que deseas buscar: ");
        var nombreAutor = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        List<DatosLibros> librosEncontrados = datos.resultados().stream()
                .filter(libro -> libro.autor().stream()
                        .anyMatch(autor -> autor.nombre().toUpperCase().contains(nombreAutor.toUpperCase())))
                .collect(Collectors.toList());

        if (!librosEncontrados.isEmpty()) {
            System.out.println("Libros encontrados para el autor " + nombreAutor + ": ");
            librosEncontrados.forEach(libro -> System.out.println(libro.titulo()));
        } else {
            System.out.println("No se encontraron libros para el autor " + nombreAutor);
        }
    }

    private void buscarLibrosPorFechaNacimientoAutor() {
        System.out.println("Ingresa el año de nacimiento del autor que deseas buscar: ");
        int añoNacimiento = teclado.nextInt();
        teclado.nextLine();

        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        List<DatosLibros> librosEncontrados = datos.resultados().stream()
                .filter(libro -> libro.autor().stream()
                        .anyMatch(autor -> autor.fechaDeNacimiento() != null &&
                                autor.fechaDeNacimiento().startsWith(String.valueOf(añoNacimiento))))
                .collect(Collectors.toList());

        if (!librosEncontrados.isEmpty()) {
            System.out.println("Libros encontrados para el año de nacimiento " + añoNacimiento + ": ");
            librosEncontrados.forEach(libro -> System.out.println(libro.titulo()));
        } else {
            System.out.println("No se encontraron libros para el año de nacimiento " + añoNacimiento);
        }
    }

    private void mostrarEstaditicas(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescarga() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescarga));
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadísticas: " + est.getCount());
    }
}