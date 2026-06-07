package com.example.eparkprogram.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa una sesión de parqueo guardada localmente.
 * Permite mostrar el historial aunque el dispositivo no tenga conexión a internet.
 *
 * Campos espejo de SessionHistoryDto (API) + isSynced para saber si ya
 * fue confirmada contra el servidor.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val sessionId: Long,

    val zoneName: String,
    val spaceCode: String,
    val plateNumber: String,

    /** ISO-8601, ej: "2025-06-05T14:32:00.000Z" */
    val startedAt: String,

    /** null si la sesión todavía no terminó */
    val endedAt: String? = null,

    val elapsedMinutes: Int? = null,
    val hourlyRateApplied: Double,
    val totalAmount: Double? = null,

    /** "ACTIVE", "FINISHED", "CANCELLED" */
    val status: String,

    val paymentId: Long? = null,
    val paymentStatus: String? = null,
    val receiptNumber: String? = null,

    /**
     * true  → ya existe en el servidor (se descargó del endpoint /driver/sessions/history).
     * false → se guardó localmente (offline) y aún no se confirmó contra la API.
     */
    val isSynced: Boolean = false
)