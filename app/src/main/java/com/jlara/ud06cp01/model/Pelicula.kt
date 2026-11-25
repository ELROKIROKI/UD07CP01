package com.jlara.ud06cp01.model

import java.io.Serializable

data class Pelicula(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val duracion: Int,
    val anio: Int,
    val nacionalidad: String
) : Serializable
