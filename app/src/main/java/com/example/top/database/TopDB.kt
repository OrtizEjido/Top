package com.example.top.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.top.utils.Artista
import com.example.top.utils.ArtistaDAO


@Database (entities = [Artista::class], version = 1)
abstract class TopDB: RoomDatabase() {

    companion object{
        private var INSTANCE: TopDB? = null

        //Singleton para asegurar una sola instancia de la db
        fun getInstance(context: Context): TopDB {
            if(INSTANCE == null) {
                 INSTANCE = Room.databaseBuilder(
                    context.applicationContext, TopDB::class.java, "topdb"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return  INSTANCE!!
        }

    }

        abstract fun artistaDao(): ArtistaDAO

}