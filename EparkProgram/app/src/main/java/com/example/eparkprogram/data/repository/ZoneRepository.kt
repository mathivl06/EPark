package com.example.eparkprogram.data.repository

import com.example.eparkprogram.data.model.ParkingZone
import com.example.eparkprogram.data.remote.MunicipalityDto
import com.example.eparkprogram.data.remote.RetrofitClient

class ZoneRepository(
    private val api: com.example.eparkprogram.data.remote.ApiService = RetrofitClient.api
) {
    suspend fun getMunicipalities(): List<MunicipalityDto> = api.getMunicipalities()

    suspend fun getZones(municipalityId: Int? = null): List<ParkingZone> =
        api.getZones(municipalityId)
}
