package com.example.agrinova.data.repository
import com.example.agrinova.data.dto.PoligonoWithValvula
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.entity.UsuarioEntity
import com.example.agrinova.di.models.UsuarioDomainModel
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject


class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun validateUser(dni: String, fundoId: Int): UsuarioDomainModel {
        val usuarioEntity = usuarioDao.isUserAssociatedWithFundo(dni, fundoId)
        return usuarioEntity?.let {
            UsuarioDomainModel(
                id = it.id,
                document = it.document,
                firstName = it.firstName,
                lastName = it.lastName,
                phone = it.phone,
                email = it.email,
                isActive = it.isActive
            )
        } ?: throw IllegalArgumentException("Usuario no encontrado")
    }
//    suspend fun validateUser(dni: String, fundoId: Int): Boolean {
//        return usuarioDao.isUserAssociatedWithFundo(dni, fundoId)
//    }
    suspend fun getCurrentUserId(): Int {
        val usuario = usuarioDao.getCurrentUser()
        return usuario?.id ?: throw IllegalStateException("No se ha iniciado sesión")
    }

    suspend fun getUserById(userId: Int): UsuarioDomainModel {
        val usuarioEntity = usuarioDao.getUsuarioById(userId)
        return usuarioEntity?.let {
            UsuarioDomainModel(
                id = it.id,
                document = it.document,
                firstName = it.firstName.split(" ").firstOrNull() ?: "",
                lastName = it.lastName.split(" ").lastOrNull() ?: "",
                phone = "",
                email = "",
                isActive = true
            )
        } ?: throw IllegalArgumentException("Usuario no encontrado")
    }

    fun getUserStatusText(active: Boolean?): String {
        return if (active == true) {
            "Activo"
        } else {
            "Inactivo"
        }
    }
    fun findClosestValvula(
        userLatLng: LatLng,
        poligonos: List<PoligonoWithValvula>
    ): PoligonoWithValvula? {
        return poligonos.minByOrNull { poligono ->
            haversineDistance(
                userLatLng.latitude,
                userLatLng.longitude,
                poligono.latitud.toDouble(),
                poligono.longitud.toDouble()
            )
        }
    }
    // Fórmula Haversine
    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Radio de la Tierra en metros
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lon2 - lon1)

        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }
}