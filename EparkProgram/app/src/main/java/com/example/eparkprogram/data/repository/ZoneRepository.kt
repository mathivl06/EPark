package com.example.eparkprogram.data.repository

import com.example.eparkprogram.data.model.ParkingZone
import com.example.eparkprogram.data.remote.AdminZoneRequest
import com.example.eparkprogram.data.remote.MunicipalityDto
import com.example.eparkprogram.data.remote.RetrofitClient
import com.example.eparkprogram.data.remote.ZoneTariffRequest

class ZoneRepository(
    private val api: com.example.eparkprogram.data.remote.ApiService = RetrofitClient.api
) {
    suspend fun getMunicipalities(): List<MunicipalityDto> = api.getMunicipalities()

    suspend fun getZones(municipalityId: Int? = null): List<ParkingZone> =
        api.getZones(municipalityId)

    suspend fun getAdminZones() = api.getAdminZones()

    suspend fun createAdminZone(request: AdminZoneRequest) = api.createAdminZone(request)

    suspend fun updateAdminZone(zoneId: Int, request: AdminZoneRequest) =
        api.updateAdminZone(zoneId, request)

    suspend fun getZoneTariff(zoneId: Int) = api.getZoneTariff(zoneId)

    suspend fun updateZoneTariff(zoneId: Int, hourlyRate: Double, currencyCode: String = "CRC") =
        api.updateZoneTariff(zoneId, ZoneTariffRequest(hourlyRate, currencyCode))
}
