package com.example.top.utils

import androidx.room.*

@Dao
interface ArtistaDAO {

    @Query ("SELECT * FROM artista")
    fun getAll(): MutableList<Artista>

    @Query ("SELECT COUNT(*) FROM artista")
    fun getCount(): Int

    @Query ("SELECT * FROM artista WHERE nombre LIKE :nombre")
    fun findByNombre(nombre: String): MutableList<Artista>

    @Query("DELETE FROM artista")
    fun clearAll()

    @Query("SELECT * FROM artista WHERE id LIKE :id")
    fun getArtista(id:Long): Artista

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artista: Artista)

    @Update
    fun update(artista: Artista)

    @Delete
    fun delete(artista: Artista)

}