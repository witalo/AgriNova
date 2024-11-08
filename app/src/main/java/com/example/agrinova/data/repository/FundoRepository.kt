//package com.example.agrinova.data.repository
//import com.example.agrinova.di.models.FundoDomainModel
//import com.example.agrinova.data.local.dao.FundoDao
//import com.example.agrinova.data.local.entity.FundoEntity
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//class FundoRepository (
//    private val fundoDao: FundoDao
//) {
//    // Mapeo de FundoEntity a FundoDomainModel
//    private fun FundoEntity.toDomainModel(): FundoDomainModel {
//        return FundoDomainModel(
//            id = this.id,
//            codigo = this.codigo,
//            nombre = this.nombre,
//            activo = this.activo,
//            zonaId = this.zonaId
//        )
//    }
//
//    // Función para obtener todos los fundos
//    fun getFundos(): Flow<List<FundoDomainModel>> {
//        return fundoDao.getAllFundos().map { fundos ->
//            fundos.map { it.toDomainModel() } // Mapea cada FundoEntity a FundoDomainModel
//        }
//    }
//}
