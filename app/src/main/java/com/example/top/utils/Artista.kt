package com.example.top.utils

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artista")
class Artista(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    var nombre: String? = null,
    var apellidos: String? = null,
    var fechaDeNacimiento: Long? = null,
    var lugarDeNacimiento: String? = null,
    var estatura: Short? = null,
    var notas: String? = null,
    var orden: Int? = null,
    var fotoUrl: String? = null) {

    companion object{
        var ORDEN ="orden"
        var ID = "id"
        var NUMERO: Long = 0
    }

    constructor(
        nombre: String?,
        apellidos: String?,
        fechaNacimiento: Long,
        lugarNacimiento: String,
        estatura: Short,
        notas: String?,
        orden: Int,
        fotoUrl: String?
    ) : this() {
        this.nombre = nombre
        this.apellidos = apellidos
        fechaDeNacimiento = fechaNacimiento
        lugarDeNacimiento = lugarNacimiento
        this.estatura = estatura
        this.notas = notas
        this.orden = orden
        this.fotoUrl = fotoUrl
    }


    fun nombreCompleto(): String {
        return nombre+ " " +apellidos
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artista

        if (id != other.id) return false
        if (nombre != other.nombre) return false
        if (apellidos != other.apellidos) return false
        if (fechaDeNacimiento != other.fechaDeNacimiento) return false
        if (lugarDeNacimiento != other.lugarDeNacimiento) return false
        if (estatura != other.estatura) return false
        if (notas != other.notas) return false
        if (orden != other.orden) return false
        if (fotoUrl != other.fotoUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (nombre?.hashCode() ?: 0)
        result = 31 * result + (apellidos?.hashCode() ?: 0)
        result = 31 * result + (fechaDeNacimiento?.hashCode() ?: 0)
        result = 31 * result + (lugarDeNacimiento?.hashCode() ?: 0)
        result = 31 * result + (estatura ?: 0)
        result = 31 * result + (notas?.hashCode() ?: 0)
        result = 31 * result + (orden ?: 0)
        result = 31 * result + (fotoUrl?.hashCode() ?: 0)
        return result
    }


}