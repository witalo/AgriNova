package com.example.agrinova.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Transaction
import androidx.room.withTransaction
import com.apollographql.apollo3.ApolloClient
import com.example.agrinova.CreateDatosMutation
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.dao.ZonaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.remote.model.EmpresaDataModel
import com.example.agrinova.data.remote.model.UsuarioDataModel
import com.example.agrinova.data.remote.model.UsuarioFundoDataModel
import com.example.agrinova.data.remote.model.ZonaDataModel
import com.example.agrinova.data.remote.model.FundoDataModel
import com.example.agrinova.GetEmpresaDataQuery
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.data.dto.LocationModel
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.local.AppDatabase
import com.example.agrinova.data.local.dao.CampaniaDao
import com.example.agrinova.data.local.dao.CartillaEvaluacionDao
import com.example.agrinova.data.local.dao.CultivoDao
import com.example.agrinova.data.local.dao.DatoDao
import com.example.agrinova.data.local.dao.DatoDetalleDao
import com.example.agrinova.data.local.dao.GrupoVariableDao
import com.example.agrinova.data.local.dao.LoteDao
import com.example.agrinova.data.local.dao.ModuloDao
import com.example.agrinova.data.local.dao.PoligonoDao
import com.example.agrinova.data.local.dao.ValvulaDao
import com.example.agrinova.data.local.dao.VariableGrupoDao
import com.example.agrinova.data.local.entity.DatoDetalleEntity
import com.example.agrinova.data.local.entity.DatoEntity
import com.example.agrinova.data.remote.model.CampaniaDataModel
import com.example.agrinova.data.remote.model.CartillaEvaluacionDataModel
import com.example.agrinova.data.remote.model.CultivoDataModel
import com.example.agrinova.data.remote.model.GrupoVariableDataModel
import com.example.agrinova.data.remote.model.LoteDataModel
import com.example.agrinova.data.remote.model.ModuloDataModel
import com.example.agrinova.data.remote.model.PoligonoDataModel
import com.example.agrinova.data.remote.model.ValvulaDataModel
import com.example.agrinova.data.remote.model.VariableGrupoDataModel
import com.example.agrinova.data.remote.model.UsuarioCartillaDataModel
import com.example.agrinova.di.models.CartillaEvaluacionDomainModel
import com.example.agrinova.di.models.FundoDomainModel
import com.example.agrinova.di.models.GrupoVariableDomainModel
import com.example.agrinova.di.models.ValvulaDomainModel
import com.example.agrinova.di.models.VariableGrupoDomainModel
import com.example.agrinova.type.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmpresaRepository(
    private val empresaDao: EmpresaDao,
    private val cultivoDao: CultivoDao,
    private val usuarioDao: UsuarioDao,
    private val cartillaDao: CartillaEvaluacionDao,
    private val grupoVariableDao: GrupoVariableDao,
    private val variableGrupoDao: VariableGrupoDao,
    private val zonaDao: ZonaDao,
    private val fundoDao: FundoDao,
    private val moduloDao: ModuloDao,
    private val loteDao: LoteDao,
    private val campaniaDao: CampaniaDao,
    private val valvulaDao: ValvulaDao,
    private val poligonoDao: PoligonoDao,
    private val datoDao: DatoDao,
    private val datoDetalleDao: DatoDetalleDao,
    private val graphQLClient: ApolloClient,
    private val database: AppDatabase
) {
    fun getFundos(): Flow<List<FundoDomainModel>> {
        return fundoDao.getAllFundos().map { fundos ->
            fundos.map { it.toDomainModel() } // Mapea cada FundoEntity a FundoDomainModel
        }
    }
    fun getCartillas(usuarioId: Int): Flow<List<CartillaEvaluacionDomainModel>> {
        return cartillaDao.getCartillasByUsuarioId(usuarioId).map { cartillas ->
            cartillas.map { it.toDomainModel() }
        }
    }
    fun getLotesByFundo(fundoId: Int): Flow<List<LoteModuloDto>> {
        return loteDao.getAllLotesByFundo(fundoId).map { lotes ->
            lotes.map { it }
        }
    }
    fun getValvulasByLoteId(loteId: Int): Flow<List<ValvulaDomainModel>> {
        return valvulaDao.getValvulasByLoteId(loteId).map { valvulas ->
            valvulas.map { it.toDomainModel() }
        }
    }
    fun getGruposVariableByCartillaId(cartillaId: Int): Flow<List<GrupoVariableDomainModel>> {
        return grupoVariableDao.getGruposVariableByCartillaId(cartillaId).map { grupos ->
            grupos.map { it.toDomainModel() }
        }
    }
    fun getVariablesGrupoByGrupoVariableId(grupoVariableId: Int): Flow<List<VariableGrupoDomainModel>> {
        return variableGrupoDao.getVariablesGrupoByGrupoVariableId(grupoVariableId).map { variables ->
            variables.map { it.toDomainModel() }
        }
    }
    fun getDatosByDateAndCartillaId(date: String, cartillaId: Int): Flow<List<DatoValvulaDto>> {
        return datoDao.getDatosByDateAndCartillaId(date, cartillaId).map { datos ->
            datos.map { it }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertDatoWithDetalles(
        valvulaId: Int,
        cartillaId: Int,
        usuarioId: Int,
        variableValues: Map<Int, String>,
        locationDetails: Map<Int, LocationModel>
    ): Result<Unit> = runCatching {
        Log.d("GPS:", locationDetails.toString())
        val fechaActual = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fechaFormateada = fechaActual.format(formato)

        val datoEntity = DatoEntity(
            valvulaId = valvulaId,
            cartillaId = cartillaId,
            usuarioId = usuarioId,
            fecha = fechaFormateada
        )

//        // Procesar todos los valores, convirtiendo vacíos a 0
//        val detalles = variableValues.map { (variableId, valor) ->
//            DatoDetalleEntity(
//                muestra = when {
//                    valor.isBlank() -> 0f
//                    valor.toFloatOrNull() != null -> valor.toFloat()
//                    else -> 0f
//                },
//                latitud = 0f,
//                longitud = 0f,
//                datoId = 0,
//                variableGrupoId = variableId
//            )
//        }
        // Procesar todos los valores, convirtiendo vacíos a 0
        val detalles = variableValues.map { (variableId, valor) ->
            val location = locationDetails[variableId] ?: LocationModel(0.0, 0.0)
            DatoDetalleEntity(
                muestra = when {
                    valor.isBlank() -> 0f
                    valor.toFloatOrNull() != null -> valor.toFloat()
                    else -> 0f
                },
                latitud = location.latitude.toFloat(),
                longitud = location.longitude.toFloat(),
                datoId = 0,
                variableGrupoId = variableId
            )
        }
        datoDao.insertDatoWithDetalles(datoEntity, detalles)
    }
    suspend fun uploadDatos(fecha: String, cartillaId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener datos locales de manera eficiente
            val datos = datoDao.getDatosByCartilla(cartillaId, fecha)
            if (datos.isEmpty()) {
                return@withContext Result.success(true) // No hay datos para enviar
            }
            // 2. Obtener detalles en batch
            val detalles = datoDao.getDatoDetallesByDatoIds(datos.map { it.id })
            // 3. Agrupar detalles por datoId eficientemente
            val detallesPorDato = detalles.groupBy { it.datoId }
            // 4. Construir los inputs para la mutación
            val datosInput = datos.map { dato ->
                DatoInput(
                    usuarioId = dato.usuarioId,
                    valvulaId = dato.valvulaId,
                    cartillaId = dato.cartillaId,
                    fechaHora = dato.fecha,
                    datoDetalle = detallesPorDato[dato.id]?.map { detalle ->
                        DatoDetalleInput(
                            variableGrupoId = detalle.variableGrupoId,
                            muestra = detalle.muestra.toDouble(),
                            latitud = detalle.latitud.toDouble(),
                            longitud = detalle.longitud.toDouble()
                        )
                    } ?: emptyList()
                )
            }
            Log.d("Upload 3:", "${datosInput}")
            // 5. Ejecutar la mutación GraphQL en chunks para manejar grandes cantidades de datos
            val chunkSize = 100 // Ajustar según necesidades
            datosInput.chunked(chunkSize).forEach { chunk ->
                val mutation = CreateDatosMutation(datos = chunk)
                val response = graphQLClient.mutation(mutation).execute()
                Log.d("Upload 4:", "${response}")
                if (!response.data?.createDatos?.success!!) {
                    return@withContext Result.failure(
                        Exception(response.data?.createDatos?.message ?: "Error al enviar datos")
                    )
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.d("Upload error:", "${e}")
            Result.failure(e)
        }
    }

    suspend fun clearDatosAndDetallesByDateAndCartillaId(fecha: String, cartillaId: Int) {
        withContext(Dispatchers.IO) {
            // Obtiene los IDs de Dato correspondientes
            val datoIds = datoDao.getIdsDatoByDateAndCartillaId(fecha, cartillaId)

            // Borra los detalles en DatoDetalle basándose en los datoIds
            datoDetalleDao.clearDatoDetalleByDatoIds(datoIds)

            // Finalmente borra los datos en Dato
            datoDao.clearDatoByDateAndCartillaId(fecha, cartillaId)
        }
    }

    /*Sincronizacion de la data*/
    @Transaction
    suspend fun clearAllLocalData() {
        // Limpiar tablas en orden inverso a las dependencias de claves foráneas
        datoDetalleDao.clearAll()
        datoDao.clearAll()
        poligonoDao.clearAll()
        valvulaDao.clearAll()
        campaniaDao.clearAll()
        loteDao.clearAll()
        moduloDao.clearAll()
        fundoDao.clearAll()
        zonaDao.clearAll()
        usuarioDao.clearAll()
        cultivoDao.clearAll()
        variableGrupoDao.clearAll()
        grupoVariableDao.clearAll()
        cartillaDao.clearAll()
        empresaDao.clearAll()
    }
    suspend fun syncCompanyData(empresaId: Int) {
        try {
            val response = graphQLClient.query(
                GetEmpresaDataQuery(empresaId.toString())
            ).execute()
            database.withTransaction {
                // Limpiar datos locales antes de sincronizar (opcional pero recomendado)
//                clearAllLocalData()
                response.data?.empresaById?.let { empresaData ->
                    // Sincronización de datos en orden jerárquico
                    syncEmpresa(empresaData)
                    syncCultivos(empresaData.cultivoSet)
                    syncUsuarios(empresaData.userSet)
                    syncZonas(empresaData.zonaSet)
                    syncCartillas(empresaData.cartillaEvaluacionSet)
                }
            }
        } catch (e: Exception) {
            Log.e("SyncEmpresaData", "Error al sincronizar datos: ${e.message}", e)
        }
    }
    private suspend fun syncEmpresa(empresaData: GetEmpresaDataQuery.EmpresaById) {
        try {
            val empresaEntity = EmpresaDataModel(
                id = empresaData.id?:0,
                ruc = empresaData.ruc ?: "",
                razonSocial = empresaData.razonSocial ?: "",
                correo = empresaData.correo ?: "",
                telefono = empresaData.telefono ?: "",
                direccion = empresaData.direccion ?: ""
            ).toEntity()

            val existingEmpresa = empresaDao.getEmpresaById(empresaEntity.id)
            if (existingEmpresa != null) {
                empresaDao.updateEmpresa(empresaEntity)
            } else {
                empresaDao.insertEmpresa(empresaEntity)
            }
        } catch (e: Exception) {
            Log.e("SyncEmpresa", "Error al sincronizar empresa: ${e.message}", e)
        }
    }
    private suspend fun syncCultivos(cultivoSet: List<GetEmpresaDataQuery.CultivoSet?>?) {
        if (cultivoSet.isNullOrEmpty()) {
            println("No hay cultivos para sincronizar")
            return
        }
        cultivoSet.filterNotNull().forEach { cultivo ->
            // Convierte el dato de GraphQL a CultivoDataModel
            val cultivoDataModel = CultivoDataModel(
                        id = cultivo.id ?: 0,
                        codigo = cultivo.codigo ?: "",
                        nombre = cultivo.nombre ?: "",
                        activo = cultivo.activo
                    )
            val cultivoEntity = cultivoDataModel.toEntity()
            val existingCultivo = cultivoDao.getCultivoById(cultivoEntity.id)
            if (existingCultivo != null) {
                cultivoDao.updateCultivo(cultivoEntity)
            } else {
                cultivoDao.insertCultivo(cultivoEntity)
            }
        }
        println("Sincronización de cultivos completada")
    }
    private suspend fun syncUsuarios(usuarioSet: List<GetEmpresaDataQuery.UserSet?>?) {
        usuarioSet?.forEach { usuario ->
            try {
                val usuarioDataModel = UsuarioDataModel(
                    id = usuario?.id ?: 0,
                    firstName = usuario?.firstName ?: "",
                    lastName = usuario?.lastName ?: "",
                    document = usuario?.document ?: "",
                    email = usuario?.email ?: "",
                    phone = usuario?.phone ?: "",
                    isActive = usuario?.isActive?: false
                )
                val usuarioEntity = usuarioDataModel.toEntity()
                val existingUsuario = usuarioDao.getUsuarioById(usuarioEntity.id)
                if (existingUsuario != null) {
                    usuarioDao.updateUsuario(usuarioEntity)
                } else {
                    usuarioDao.insertUsuario(usuarioEntity)
                }
            } catch (e: Exception) {
                Log.e("SyncUsuario", "Error al sincronizar usuario: ${e.message}", e)
            }
        }
        println("Sincronización de usuarios completada")
    }
    private suspend fun syncCartillas(cartillaSet: List<GetEmpresaDataQuery.CartillaEvaluacionSet?>?) {
        cartillaSet?.forEach { cartilla ->
            try {
                val cartillaDataModel = CartillaEvaluacionDataModel(
                    id = cartilla?.id ?: 0,
                    codigo = cartilla?.codigo ?: "",
                    nombre = cartilla?.nombre ?: "",
                    activo = cartilla?.activo ?: false,
                    cultivoId = cartilla?.cultivoId ?: 0
                )
                val cartillaEntity = cartillaDataModel.toEntity()
                val existingCartilla = cartillaDao.getCartillaEvaluacionById(cartillaEntity.id)
                if (existingCartilla != null) {
                    cartillaDao.updateCartillaEvaluacion(cartillaEntity)
                } else {
                    cartillaDao.insertCartillaEvaluacion(cartillaEntity)
                }
                // Sincronizar módulos de este fundo
                cartilla?.grupovariableSet?.filterNotNull()?.forEach { grupo ->
                    syncGrupoVariableCompleto(grupo, cartillaEntity.id)
                }

                // Sincronizar usuarios de este fundo
                cartilla?.userCartillaSet?.filterNotNull()?.forEach { userCartilla ->
                    syncUsuarioCartillaCompleto(userCartilla, cartillaEntity.id)
                }
            } catch (e: Exception) {
                Log.e("SyncCartilla", "Error al sincronizar cartilla: ${e.message}", e)
            }
        }
        println("Sincronización de cartillas completada")
    }
    private suspend fun syncUsuarioCartillaCompleto(
        userCartilla: GetEmpresaDataQuery.UserCartillaSet,
        cartillaId: Int
    ) {
        val userCartillaEntity = UsuarioCartillaDataModel(
            usuarioId = userCartilla.userId ?: 0,
            cartillaId = cartillaId
        ).toEntity()
        val exists = cartillaDao.checkUsuarioCartillaExists(userCartillaEntity.usuarioId, userCartillaEntity.cartillaId) > 0
        if (!exists) {
            cartillaDao.insertUsuarioCartillaCrossRef(userCartillaEntity)
        }
    }
    private suspend fun syncGrupoVariableCompleto(
        grupo: GetEmpresaDataQuery.GrupovariableSet,
        cartillaId: Int
    )
    {
        val grupoEntity = GrupoVariableDataModel(
            id = grupo.id ?: 0,
            calculado = grupo.calculado,
            grupoCodigo = grupo.grupoCodigo?:"",
            grupoNombre = grupo.grupoNombre?:"",
            grupoId = grupo.grupoId?:0,
            cartillaEvaluacionId = cartillaId
        ).toEntity()
        val existingGrupo = grupoVariableDao.getGrupoVariableById(grupoEntity.id)
        if (existingGrupo != null) {
            grupoVariableDao.updateGrupoVariable(grupoEntity)
        } else {
            grupoVariableDao.insertGrupoVariable(grupoEntity)
        }
        // Sincronizar variables de este grupo
        grupo.variableGrupoSet?.filterNotNull()?.forEach { variable ->
            syncVariableGrupoCompleto(variable, grupoEntity.id)
        }
    }
    private suspend fun syncVariableGrupoCompleto(
        variable: GetEmpresaDataQuery.VariableGrupoSet,
        grupoId: Int
    )
    {
        val variableEntity = VariableGrupoDataModel(
            id = variable.id?:0,
            minimo = variable.minimo?:0,
            maximo = variable.maximo?:0,
            calculado = variable.calculado,
            variableEvaluacionNombre = variable.variableEvaluacionNombre?:"",
            grupoVariableId  = grupoId
        ).toEntity()
        val existingVariable = variableGrupoDao.getVariableGrupoById(variableEntity.id)
        if (existingVariable != null) {
            variableGrupoDao.updateVariableGrupo(variableEntity)
        } else {
            variableGrupoDao.insertVariableGrupo(variableEntity)
        }
    }

    private suspend fun syncZonas(zonaSet: List<GetEmpresaDataQuery.ZonaSet?>?) {
        zonaSet?.forEach { zona ->
            try {
                val zonaDataModel = ZonaDataModel(
                    id = zona?.id ?: 0,
                    codigo = zona?.codigo ?: "",
                    nombre = zona?.nombre ?: "",
                    activo = zona?.activo ?: false,
                    empresaId = zona?.empresaId ?: 0
                )
                val zonaEntity = zonaDataModel.toEntity()
                val existingZona = zonaDao.getZonaById(zonaEntity.id)
                if (existingZona != null) {
                    zonaDao.updateZona(zonaEntity)
                } else {
                    zonaDao.insertZona(zonaEntity)
                }
                // Sincronizar fundos de esta zona
                zona?.fundoSet?.filterNotNull()?.forEach { fundo ->
                    syncFundoCompleto(fundo, zonaEntity.id)
                }
            } catch (e: Exception) {
                Log.e("SyncZona", "Error al sincronizar zona: ${e.message}", e)
            }
        }
        println("Sincronización de zonas completada")
    }
    private suspend fun syncFundoCompleto(
        fundo: GetEmpresaDataQuery.FundoSet,
        zonaId: Int
    ) {
        val fundoEntity = FundoDataModel(
            id = fundo.id ?: 0,
            codigo = fundo.codigo ?: "",
            nombre = fundo.nombre ?: "",
            activo = fundo.activo ?: false,
            zonaId = zonaId
        ).toEntity()

        // Guardar o actualizar fundo
        val existingFundo = fundoDao.getFundoById(fundoEntity.id)
        if (existingFundo != null) {
            fundoDao.updateFundo(fundoEntity)
        } else {
            fundoDao.insertFundo(fundoEntity)
        }

        // Sincronizar módulos de este fundo
        fundo.moduloSet?.filterNotNull()?.forEach { modulo ->
            syncModuloCompleto(modulo, fundoEntity.id)
        }

        // Sincronizar usuarios de este fundo
        fundo.userFundoSet?.filterNotNull()?.forEach { userFundo ->
            syncUsuarioFundoCompleto(userFundo, fundoEntity.id)
        }
    }
    private suspend fun syncUsuarioFundoCompleto(
        userFundo: GetEmpresaDataQuery.UserFundoSet,
        fundoId: Int
    ) {
        val userFundoEntity = UsuarioFundoDataModel(
            userId = userFundo.userId ?: 0,
            fundoId = fundoId
        ).toEntity()
        val exists = usuarioDao.checkUsuarioFundoExists(userFundoEntity.usuarioId, userFundoEntity.fundoId) > 0
        if (!exists) {
            usuarioDao.insertUsuarioFundoCrossRef(userFundoEntity)
        }
    }
    private suspend fun syncModuloCompleto(
        modulo: GetEmpresaDataQuery.ModuloSet,
        fundoId: Int
    ) {
        val moduloEntity = ModuloDataModel(
            id = modulo.id ?: 0,
            codigo = modulo.codigo ?: "",
            nombre = modulo.nombre ?: "",
            activo = modulo.activo ?: false,
            fundoId = fundoId
        ).toEntity()

        // Guardar o actualizar módulo
        val existingModulo = moduloDao.getModuloById(moduloEntity.id)
        if (existingModulo != null) {
            moduloDao.updateModulo(moduloEntity)
        } else {
            moduloDao.insertModulo(moduloEntity)
        }

        // Sincronizar lotes de este módulo
        modulo.loteSet?.filterNotNull()?.forEach { lote ->
            syncLoteCompleto(lote, moduloEntity.id)
        }
    }

    private suspend fun syncLoteCompleto(
        lote: GetEmpresaDataQuery.LoteSet,
        moduloId: Int
    ) {
        val loteEntity = LoteDataModel(
            id = lote.id ?: 0,
            codigo = lote.codigo ?: "",
            nombre = lote.nombre ?: "",
            activo = lote.activo,
            moduloId = moduloId
        ).toEntity()

        // Guardar o actualizar lote
        val existingLote = loteDao.getLoteById(loteEntity.id)
        if (existingLote != null) {
            loteDao.updateLote(loteEntity)
        } else {
            loteDao.insertLote(loteEntity)
        }

        // Sincronizar campañas de este lote
        lote.campaniaSet?.filterNotNull()?.forEach { campania ->
            syncCampaniaCompleto(campania, loteEntity.id)
        }
    }

    private suspend fun syncCampaniaCompleto(
        campania: GetEmpresaDataQuery.CampaniaSet,
        loteId: Int
    ) {
        val campaniaEntity = CampaniaDataModel(
            id = campania.id ?: 0,
            numero = campania.numero,
            centroCosto = campania.centroCosto ?: "",
            loteId = loteId,
            cultivoId = campania.cultivoId ?: 0,
            activo = campania.activo
        ).toEntity()

        // Guardar o actualizar campaña
        val existingCampania = campaniaDao.getCampaniaById(campaniaEntity.id)
        if (existingCampania != null) {
            campaniaDao.updateCampania(campaniaEntity)
        } else {
            campaniaDao.insertCampania(campaniaEntity)
        }

        // Sincronizar válvulas de esta campaña
        campania.valvulaSet?.filterNotNull()?.forEach { valvula ->
            syncValvulaCompleto(valvula, campaniaEntity.id)
        }
    }

    private suspend fun syncValvulaCompleto(
        valvula: GetEmpresaDataQuery.ValvulaSet,
        campaniaId: Int
    ) {
        val valvulaEntity = ValvulaDataModel(
            id = valvula.id ?: 0,
            codigo = valvula.codigo ?: "",
            nombre = valvula.nombre ?: "",
            campaniaId = campaniaId,
            activo = valvula.activo
        ).toEntity()

        // Guardar o actualizar válvula
        val existingValvula = valvulaDao.getValvulaById(valvulaEntity.id)
        if (existingValvula != null) {
            valvulaDao.updateValvula(valvulaEntity)
        } else {
            valvulaDao.insertValvula(valvulaEntity)
        }

        // Sincronizar polígonos de esta válvula
        valvula.poligonoSet?.filterNotNull()?.forEach { poligono ->
            syncPoligonoCompleto(poligono, valvulaEntity.id)
        }
    }

    private suspend fun syncPoligonoCompleto(
        poligono: GetEmpresaDataQuery.PoligonoSet,
        valvulaId: Int
    ) {
        val poligonoEntity = PoligonoDataModel(
            id = poligono.id ?: 0,
            latitud = poligono.latitud.toFloat(),
            longitud = poligono.longitud.toFloat(),
            valvulaId = valvulaId
        ).toEntity()

        // Guardar o actualizar polígono
        val existingPoligono = poligonoDao.getPoligonoById(poligonoEntity.id)
        if (existingPoligono != null) {
            poligonoDao.updatePoligono(poligonoEntity)
        } else {
            poligonoDao.insertPoligono(poligonoEntity)
        }
    }
}