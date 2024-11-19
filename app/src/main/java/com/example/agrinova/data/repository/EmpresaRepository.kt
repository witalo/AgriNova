package com.example.agrinova.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Transaction
import androidx.room.withTransaction
import com.apollographql.apollo3.ApolloClient
import com.example.agrinova.CreateMuestraVGMutation
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
import com.example.agrinova.data.dto.DatoWithDetalleDto
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
import com.example.agrinova.data.local.entity.CultivoEntity
import com.example.agrinova.data.local.entity.DatoDetalleEntity
import com.example.agrinova.data.local.entity.DatoEntity
import com.example.agrinova.data.local.entity.EmpresaEntity
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
import com.example.agrinova.type.MuestraVGInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmpresaRepository(
    private val database: AppDatabase,
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
    private val graphQLClient: ApolloClient
) {
//    suspend fun syncEmpresaData(empresaId: Int) {
//        val response = graphQLClient.query(
//            GetEmpresaDataQuery(empresaId.toString())
//        ).execute()
//
//        response.data?.empresaById?.let { empresaData ->
//            val existingEmpresa = empresaDao.getEmpresaById(empresaData.id!!.toInt())
//            val empresaEntity = EmpresaDataModel(id = empresaData.id,
//                ruc = empresaData.ruc ?: "",
//                razonSocial = empresaData.razonSocial ?: "",
//                correo = empresaData.correo ?: "",
//                telefono = empresaData.telefono ?: "",
//                direccion = empresaData.direccion ?: "",
//                userSet = empresaData.userSet?.map {
//                    UsuarioDataModel(
//                        id = it?.id ?: 0,
//                        firstName = it?.firstName ?: "",
//                        lastName = it?.lastName ?: "",
//                        document = it?.document ?: "",
//                        email = it?.email ?: "",
//                        phone = it?.phone ?: "",
//                        isActive = it?.isActive ?: false
//                    )
//                },
//                zonaSet = empresaData.zonaSet?.map { zona ->
//                    ZonaDataModel(id = zona?.id ?: 0,
//                        codigo = zona?.codigo ?: "",
//                        nombre = zona?.nombre ?: "",
//                        activo = zona?.activo ?: false,
//                        empresaId = zona?.empresaId ?: 0,
//                        fundoSet = zona?.fundoSet?.map { fundo ->
//                            FundoDataModel(id = fundo?.id ?: 0,
//                                codigo = fundo?.codigo ?: "",
//                                nombre = fundo?.nombre ?: "",
//                                activo = fundo?.activo ?: false,
//                                zonaId = fundo?.zonaId ?: 0,
//                                userFundoSet = fundo?.userFundoSet?.map { userFundo ->
//                                    UsuarioFundoDataModel(
//                                        userId = userFundo?.userId ?: 0,
//                                        fundoId = userFundo?.fundoId ?: 0
//                                    )
//                                } ?: emptyList(),
//                                moduloSet = fundo?.moduloSet?.map { modulo ->
//                                    ModuloDataModel(id = modulo?.id ?: 0,
//                                        codigo = modulo?.codigo ?: "",
//                                        nombre = modulo?.nombre ?: "",
//                                        activo = modulo?.activo ?: false,
//                                        fundoId = modulo?.fundoId ?: 0,
//                                        loteSet = modulo?.loteSet?.map { lote ->
//                                            LoteDataModel(id = lote?.id ?: 0,
//                                                codigo = lote?.codigo ?: "",
//                                                nombre = lote?.nombre ?: "",
//                                                activo = lote?.activo ?: false,
//                                                moduloId = lote?.moduloId ?: 0,
//                                                campaniaSet = lote?.campaniaSet?.map { campania ->
//                                                    CampaniaDataModel(id = campania?.id ?: 0,
//                                                        numero = campania?.numero ?: 0,
//                                                        centroCosto = campania?.centroCosto ?: "",
//                                                        activo = campania?.activo ?: false,
//                                                        loteId = campania?.loteId ?: 0,
//                                                        cultivoId = campania?.cultivoId ?: 0,
//                                                        valvulaSet = campania?.valvulaSet?.map { valvula ->
//                                                            ValvulaDataModel(id = valvula?.id ?: 0,
//                                                                codigo = valvula?.codigo ?: "",
//                                                                nombre = valvula?.nombre ?: "",
//                                                                activo = valvula?.activo ?: false,
//                                                                campaniaId = valvula?.campaniaId
//                                                                    ?: 0,
//                                                                poligonoSet = valvula?.poligonoSet?.map { poligono ->
//                                                                    PoligonoDataModel(
//                                                                        id = poligono?.id ?: 0,
//                                                                        latitud = poligono?.latitud?.toFloat()
//                                                                            ?: 0.0f,
//                                                                        longitud = poligono?.longitud?.toFloat()
//                                                                            ?: 0.0f,
//                                                                        valvulaId = poligono?.valvulaId
//                                                                            ?: 0
//                                                                    )
//                                                                } ?: emptyList())
//                                                        } ?: emptyList())
//                                                } ?: emptyList())
//                                        } ?: emptyList())
//                                } ?: emptyList())
//                        } ?: emptyList())
//                },
//                cartillaEvaluacionSet = empresaData.cartillaEvaluacionSet?.map { cartilla ->
//                    CartillaEvaluacionDataModel(id = cartilla?.id ?: 0,
//                        codigo = cartilla?.codigo ?: "",
//                        nombre = cartilla?.nombre ?: "",
//                        activo = cartilla?.activo ?: false,
//                        cultivoId = cartilla?.cultivoId ?: 0,
//                        userCartillaSet = cartilla?.userCartillaSet?.map { userCartilla ->
//                            UsuarioCartillaDataModel(
//                                usuarioId = userCartilla?.userId ?: 0,
//                                cartillaId = userCartilla?.cartillaId ?: 0
//                            )
//                        } ?: emptyList(),
//                        grupoVariableSet = cartilla?.grupovariableSet?.map { grupo ->
//                            GrupoVariableDataModel(id = grupo.id ?: 0,
//                                calculado = grupo.calculado,
//                                grupoCodigo = grupo.grupoCodigo ?: "",
//                                grupoNombre = grupo.grupoNombre ?: "",
//                                grupoId = grupo.grupoId ?: 0,
//                                cartillaEvaluacionId = grupo.cartillaEvaluacionId ?: 0,
//                                variableGrupoSet = grupo.variableGrupoSet?.map { variable ->
//                                    VariableGrupoDataModel(
//                                        id = variable?.id ?: 0,
//                                        minimo = variable?.minimo ?: 0,
//                                        maximo = variable?.maximo ?: 0,
//                                        calculado = variable?.calculado ?: false,
//                                        variableEvaluacionNombre = variable?.variableEvaluacionNombre
//                                            ?: "",
//                                        grupoVariableId = variable?.grupoVariableId ?: 0
//                                    )
//                                } ?: emptyList())
//                        } ?: emptyList())
//                },
//                cultivoSet = empresaData.cultivoSet?.map { cultivo ->
//                    CultivoDataModel(
//                        id = cultivo?.id ?: 0,
//                        codigo = cultivo?.codigo ?: "",
//                        nombre = cultivo?.nombre ?: "",
//                        activo = cultivo?.activo ?: false
//                    )
//                } ?: emptyList())
//            try {
//                // Guardar o actualizar la empresa y sus datos relacionados
//                if (existingEmpresa != null) {
//                    empresaDao.updateEmpresa(empresaEntity.toEntity())
//                } else {
//                    Log.d("Italo Empresa->Insert", empresaData.toString())
//                    empresaDao.insertEmpresa(empresaEntity.toEntity())
//                }
//                // Sincronizar cultivos
//                Log.d("Italo Cultivos 2024", "Cultivos")
//                empresaEntity.cultivoSet?.forEach { cultivo ->
//                    val existingCultivo = cultivoDao.getCultivoById(cultivo.id.toInt())
//                    if (existingCultivo != null) {
//                        cultivoDao.updateCultivo(cultivo.toEntity())
//                    } else {
//                        Log.d("Italo Cultivo->Insert", cultivo.toString())
//                        cultivoDao.insertCultivo(cultivo.toEntity())
//                    }
//                }
//                // Sincronizar usuarios
//                Log.d("Italo Usuarios 2024", "Usuarios")
//                empresaEntity.userSet?.forEach { usuario ->
//                    val existingUsuario = usuarioDao.getUsuarioById(usuario.id.toInt())
//                    if (existingUsuario != null) {
//                        usuarioDao.updateUsuario(usuario.toEntity())
//                    } else {
//                        Log.d("Italo Usuario->Insert", usuario.toString())
//                        usuarioDao.insertUsuario(usuario.toEntity())
//                    }
//                }
//                // Sincronizar zonas y otros
//                Log.d("Italo Zonas 2024", "Zonas")
//                empresaEntity.zonaSet?.forEach { zona ->
//                    val existingZona = zonaDao.getZonaById(zona.id)
//                    if (existingZona != null) {
//                        zonaDao.updateZona(zona.toEntity())
//                    } else {
//                        Log.d("Italo Zona->Insert", zona.toString())
//                        zonaDao.insertZona(zona.toEntity())
//                    }
//
//                    zona.fundoSet?.forEach { fundo ->
//                        val existingFundo = fundoDao.getFundoById(fundo.id)
//                        if (existingFundo != null) {
//                            fundoDao.updateFundo(fundo.toEntity())
//                        } else {
//                            Log.d("Italo Fundo->Insert", fundo.toString())
//                            fundoDao.insertFundo(fundo.toEntity())
//                        }
//                        // Almacenar relaciones usuario-fundo
//                        fundo.userFundoSet?.forEach { userFundo ->
//                            val userId = userFundo.userId
//                            val fundoId = userFundo.fundoId
//
//                            // Verifica si la relación ya existe
//                            val exists = usuarioDao.checkUsuarioFundoExists(userId, fundoId) > 0
//
//                            if (!exists) {
//                                // Inserta solo si la relación no existe
//                                Log.d("Italo UsuarioFundo->Insert", userFundo.toString())
//                                usuarioDao.insertUsuarioFundoCrossRef(userFundo.toEntity())
//                            }
//                        }
//                        // Almacenar modulo y otros
//                        fundo.moduloSet?.forEach { modulo ->
//                            val existingModulo = moduloDao.getModuloById(modulo.id)
//                            if (existingModulo != null) {
//                                moduloDao.updateModulo(modulo.toEntity())
//                            } else {
//                                Log.d("Italo Modulo->Insert", modulo.toString())
//                                moduloDao.insertModulo(modulo.toEntity())
//                            }
//                            modulo.loteSet?.forEach { lote ->
//                                val existingLote = loteDao.getLoteById(lote.id)
//                                if (existingLote != null) {
//                                    loteDao.updateLote(lote.toEntity())
//                                } else {
//                                    Log.d("Italo Lote->Insert", lote.toString())
//                                    loteDao.insertLote(lote.toEntity())
//                                }
//                                lote.campaniaSet?.forEach { campania ->
//                                    val existingCampania = campaniaDao.getCampaniaById(campania.id)
//                                    if (existingCampania != null) {
//                                        campaniaDao.updateCampania(campania.toEntity())
//                                    } else {
//                                        Log.d("Italo Campaña->Insert", campania.toString())
//                                        campaniaDao.insertCampania(campania.toEntity())
//                                    }
//                                    campania.valvulaSet?.forEach { valvula ->
//                                        val existingValvula = valvulaDao.getValvulaById(valvula.id)
//                                        if (existingValvula != null) {
//                                            valvulaDao.updateValvula(valvula.toEntity())
//                                        } else {
//                                            Log.d("Italo Valvula->Insert", valvula.toString())
//                                            valvulaDao.insertValvula(valvula.toEntity())
//                                        }
//                                        valvula.poligonoSet?.forEach { poligono ->
//                                            val existingPoligono =
//                                                poligonoDao.getPoligonoById(poligono.id)
//                                            if (existingPoligono != null) {
//                                                poligonoDao.updatePoligono(poligono.toEntity())
//                                            } else {
//                                                Log.d("Italo Poligono->Insert", poligono.toString())
//                                                poligonoDao.insertPoligono(poligono.toEntity())
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                // Sincronizar cartilla
//                Log.d("Italo Cartilla Evaluacion 2024", "Cartillas")
//                empresaEntity.cartillaEvaluacionSet?.forEach { cartilla ->
//                    val existingCartilla =
//                        cartillaDao.getCartillaEvaluacionById(cartilla.id.toInt())
//                    if (existingCartilla != null) {
//                        cartillaDao.updateCartillaEvaluacion(cartilla.toEntity())
//                    } else {
//                        Log.d("Italo Cartilla->Insert", cartilla.toString())
//                        cartillaDao.insertCartillaEvaluacion(cartilla.toEntity())
//                    }
//                    // Almacenar relaciones usuario-fundo
//                    cartilla.userCartillaSet?.forEach { userCartilla ->
//                        val userId = userCartilla.usuarioId
//                        val cartillaId = userCartilla.cartillaId
//
//                        // Verifica si la relación ya existe
//                        val exists = cartillaDao.checkUsuarioCartillaExists(userId, cartillaId) > 0
//
//                        if (!exists) {
//                            // Inserta solo si la relación no existe
//                            Log.d("Italo UsuarioCartilla->Insert", userCartilla.toString())
//                            cartillaDao.insertUsuarioCartillaCrossRef(userCartilla.toEntity())
//                        }
//                    }
//                    cartilla.grupoVariableSet?.forEach { grupoVariable ->
//                        val existingGrupoVariable =
//                            grupoVariableDao.getGrupoVariableById(grupoVariable.id.toInt())
//                        if (existingGrupoVariable != null) {
//                            grupoVariableDao.updateGrupoVariable(grupoVariable.toEntity())
//                        } else {
//                            Log.d("Italo Grupo Variable->Insert", grupoVariable.toString())
//                            grupoVariableDao.insertGrupoVariable(grupoVariable.toEntity())
//                        }
//                        grupoVariable.variableGrupoSet?.forEach { variableGrupo ->
//                            val existingVariableGrupo =
//                                variableGrupoDao.getVariableGrupoById(variableGrupo.id.toInt())
//                            if (existingVariableGrupo != null) {
//                                variableGrupoDao.updateVariableGrupo(variableGrupo.toEntity())
//                            } else {
//                                Log.d("Italo Variable Grupo->Insert", variableGrupo.toString())
//                                variableGrupoDao.insertVariableGrupo(variableGrupo.toEntity())
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("Sync Error", "Error durante la sincronización: ${e.message}", e)
//                throw e // Lanza la excepción para evitar que se cometan datos parciales
//            }
//        } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
//    }

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
        variableValues: Map<Int, String>
    ): Result<Unit> = runCatching {
        val fechaActual = LocalDateTime.now()
        // Formatear fecha y hora
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Ajusta el formato si es necesario
        val fechaFormateada = fechaActual.format(formato)
        // Crear la entidad DatoEntity
        val datoEntity = DatoEntity(
            valvulaId = valvulaId,
            cartillaId = cartillaId,
            fecha = fechaFormateada
        )

        // Mapear los valores válidos a DatoDetalleEntity
        val detalles = variableValues.mapNotNull { (variableId, valor) ->
            valor.toFloatOrNull()?.let { floatValue ->
                DatoDetalleEntity(
                    muestra = floatValue,       // Ejemplo: usando el valor como "muestra"
                    latitud = 0f,              // Placeholder (reemplaza con valor real si es necesario)
                    longitud = 0f,             // Placeholder (reemplaza con valor real si es necesario)
                    datoId = 0,                // Temporal, se asignará después
                    variableGrupoId = variableId
                )
            }
        }
        // Verifica si hay detalles válidos para insertar
        if (detalles.isEmpty()) {
            throw IllegalStateException("No hay valores válidos para guardar")
        }
        // Llama al método DAO para insertar el dato y sus detalles
        datoDao.insertDatoWithDetalles(datoEntity, detalles)
    }
    suspend fun uploadMuestraData(fecha: String, cartillaId: Int): Result<Boolean> {
        return try {
            val datosLocales = datoDao.getDatoWithDetalleByDateAndCartillaId(fecha, cartillaId)

            // Convertir datos a la estructura generada por Apollo
            val muestrasVGInputList = datosLocales.map {
                MuestraVGInput(
                    valvulaId = it.valvulaId,
                    fechaHora = it.fecha,
                    latitud = it.latitud.toDouble(),
                    longitud = it.longitud.toDouble(),
                    muestra = it.muestra.toDouble(),
                    variableGrupoId = it.variableGrupoId
                )
            }
            // Enviar la lista al servidor mediante Apollo Client
            val mutation = CreateMuestraVGMutation(muestrasVGInputList)
            val response = graphQLClient.mutation(mutation).execute()

            // Validar la respuesta
            if (response.hasErrors() || response.data?.createMuestra?.success == false) {
                val errorMsg = response.errors?.joinToString(", ") { it.message }
                    ?: response.data?.createMuestra?.message ?: "Error desconocido"
                throw Exception(errorMsg)
            }

            Result.success(true)
        } catch (e: Exception) {
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

            response.data?.empresaById?.let { empresaData ->
                // Procesar empresa principal
                syncEmpresa(empresaData)

                // Procesar datos relacionados
                syncCultivos(empresaData.cultivoSet)
                syncUsuarios(empresaData.userSet)
                syncZonas(empresaData.zonaSet)
                syncCartillas(empresaData.cartillaEvaluacionSet)
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

    private suspend fun syncUsuarios(userSet: List<GetEmpresaDataQuery.UserSet?>?) {
        userSet?.forEach { usuario ->
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
                // Convertir cada `FundoSet` en `FundoDataModel` antes de pasarlo a syncFundos
                val fundoDataModels = zona?.fundoSet?.map { fundo ->
                    FundoDataModel(
                        id = fundo?.id ?: 0,
                        codigo = fundo?.codigo ?: "",
                        nombre = fundo?.nombre ?: "",
                        activo = fundo?.activo ?: false,
                        zonaId = zonaEntity.id
                    )
                }
                syncFundos(fundoDataModels)
            } catch (e: Exception) {
                Log.e("SyncZona", "Error al sincronizar zona: ${e.message}", e)
            }
        }
        println("Sincronización de zonas completada")
    }

    private suspend fun syncFundos(fundoSet: List<FundoDataModel>?) {
        // Verificamos si fundoSet no es nulo ni vacío
        if (fundoSet.isNullOrEmpty()) {
            println("No hay fondos para sincronizar")
            return
        }

        fundoSet.forEach { fundo ->
            try {
                val fundoEntity = fundo.toEntity()
                val existingFundo = fundoDao.getFundoById(fundoEntity.id)
                if (existingFundo != null) {
                    fundoDao.updateFundo(fundoEntity)
                } else {
                    fundoDao.insertFundo(fundoEntity)
                }
                val moduloDataModels = fundo.moduloSet?.map { modulo ->
                    ModuloDataModel(
                        id = modulo.id,
                        codigo = modulo.codigo,
                        nombre = modulo.nombre,
                        activo = modulo.activo,
                        fundoId = modulo.fundoId
                    )
                }
                syncModulos(moduloDataModels)
            } catch (e: Exception) {
                Log.e("SyncFundos", "Error al sincronizar fundo: ${e.message}", e)
            }
        }
        println("Sincronización de fundos completada")
    }

    private suspend fun syncModulos(moduloSet: List<ModuloDataModel>?) {
        moduloSet?.forEach { modulo ->
            try {
                val moduloEntity = modulo.toEntity()
                val existingModulo = moduloDao.getModuloById(moduloEntity.id)
                if (existingModulo != null) {
                    moduloDao.updateModulo(moduloEntity)
                } else {
                    moduloDao.insertModulo(moduloEntity)
                }
                val loteDataModels = modulo.loteSet?.map { lote ->
                    LoteDataModel(
                        id = lote.id ,
                        codigo = lote.codigo,
                        nombre = lote.nombre,
                        activo = lote.activo,
                        moduloId = moduloEntity.id
                    )
                }
                syncLotes(loteDataModels)
            } catch (e: Exception) {
                Log.e("SyncModulo", "Error al sincronizar módulo: ${e.message}", e)
            }
        }
        println("Sincronización de modulos completada")
    }

    private suspend fun syncLotes(loteSet: List<LoteDataModel>?) {
        loteSet?.forEach { lote ->
            try {
                val loteEntity = LoteDataModel(
                    id = lote.id ?: 0,
                    codigo = lote.codigo ?: "",
                    nombre = lote.nombre ?: "",
                    activo = lote.activo ?: false,
                    moduloId = lote.moduloId ?: 0
                ).toEntity()

                val existingLote = loteDao.getLoteById(loteEntity.id)
                if (existingLote != null) {
                    loteDao.updateLote(loteEntity)
                } else {
                    loteDao.insertLote(loteEntity)
                }
            } catch (e: Exception) {
                Log.e("SyncLote", "Error al sincronizar lote: ${e.message}", e)
            }
        }
    }


}
