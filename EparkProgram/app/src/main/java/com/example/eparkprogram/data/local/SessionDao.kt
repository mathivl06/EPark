package com.example.eparkprogram.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room para la tabla "sessions".
 *
 * Operaciones disponibles:
 *  - Insertar/reemplazar sesiones (bulk desde la API o unitarias offline)
 *  - Consultar todo el historial ordenado por fecha descendente
 *  - Consultar sólo las sesiones pendientes de sincronización
 *  - Marcar una sesión como sincronizada una vez que el servidor la confirma
 *  - Borrar todo el historial local (útil al cerrar sesión)
 */
@Dao
interface SessionDao {

    /**
     * Inserta una lista de sesiones descargadas del servidor.
     * Si ya existe una sesión con el mismo sessionId, la reemplaza
     * (REPLACE) para mantener los datos actualizados.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<SessionEntity>)

    /**
     * Inserta o reemplaza una única sesión.
     * Se usa cuando el conductor termina una sesión y queremos
     * guardarla localmente antes de confirmar con la API.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(session: SessionEntity)

    /**
     * Devuelve todas las sesiones ordenadas de la más reciente a la más antigua.
     * Retorna un Flow para que la UI se actualice automáticamente
     * cuando cambia la base de datos local.
     */
    @Query("SELECT * FROM sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    /**
     * Retorna las sesiones que aún no fueron confirmadas con el servidor
     * (isSynced = 0 en SQLite).
     * El servicio de sincronización las recorre y las sube cuando
     * recupera conexión.
     */
    @Query("SELECT * FROM sessions WHERE isSynced = 0 ORDER BY startedAt DESC")
    suspend fun getPendingSyncSessions(): List<SessionEntity>

    /**
     * Marca una sesión como ya sincronizada con el servidor.
     * Se llama después de que el endpoint del servidor responde OK.
     *
     * @param sessionId ID de la sesión confirmada.
     */
    @Query("UPDATE sessions SET isSynced = 1 WHERE sessionId = :sessionId")
    suspend fun markAsSynced(sessionId: Long)

    /**
     * Elimina todas las sesiones de la tabla local.
     * Se invoca al hacer logout para no dejar datos del usuario anterior.
     */
    @Query("DELETE FROM sessions")
    suspend fun clearAll()
}