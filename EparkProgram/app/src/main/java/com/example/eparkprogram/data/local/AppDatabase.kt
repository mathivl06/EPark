package com.example.eparkprogram.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room de la aplicación e-park.
 *
 * Contiene la tabla "sessions" (definida por SessionEntity) y expone
 * el SessionDao para que los repositorios puedan leer/escribir
 * el historial local sin depender de internet.
 *
 * Se implementa con el patrón Singleton: una sola instancia por proceso
 * usando doble verificación con @Volatile para seguridad en multihilo.
 *
 * Cómo usarlo desde un repositorio o Composable:
 *   val db = AppDatabase.getInstance(context)
 *   val dao = db.sessionDao()
 */
@Database(
    entities = [SessionEntity::class],
    version = 1,
    exportSchema = false  // no genera JSON de esquema; activar si se quiere migraciones documentadas
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    companion object {
        /** Nombre del archivo SQLite en el dispositivo */
        private const val DATABASE_NAME = "epark_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Devuelve la instancia única de la base de datos.
         * La crea la primera vez que se llama (lazy + thread-safe).
         *
         * @param context Se recomienda pasar applicationContext para
         *                evitar memory leaks.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // fallbackToDestructiveMigration: si el esquema cambia y no hay
                    // migración definida, Room borra y recrea la BD.
                    // Aceptable en desarrollo; reemplazar por migraciones en producción.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}