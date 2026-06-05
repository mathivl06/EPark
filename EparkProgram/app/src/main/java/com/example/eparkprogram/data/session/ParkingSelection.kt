package com.example.eparkprogram.data.session

import com.example.eparkprogram.data.model.FinishedSession
import com.example.eparkprogram.data.model.ParkingSession
import com.example.eparkprogram.data.model.ParkingZone

object ParkingSelection {
    var selectedZone: ParkingZone? = null
    var lastActiveSession: ParkingSession? = null    // se guarda al terminar la sesión
    var lastFinishedSession: FinishedSession? = null // contiene minutos y monto real del servidor
}