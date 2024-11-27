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
//    suspend fun insertDatoWithDetalles(
//        valvulaId: Int,
//        cartillaId: Int,
//        usuarioId: Int,
//        variableValues: Map<Int, String>
//    ): Result<Unit> = runCatching {
//        val fechaActual = LocalDateTime.now()
//        // Formatear fecha y hora
//        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Ajusta el formato si es necesario
//        val fechaFormateada = fechaActual.format(formato)
//        // Crear la entidad DatoEntity
//        val datoEntity = DatoEntity(
//            valvulaId = valvulaId,
//            cartillaId = cartillaId,
//            usuarioId = usuarioId,
//            fecha = fechaFormateada
//        )
//
//        // Mapear los valores válidos a DatoDetalleEntity
//        val detalles = variableValues.mapNotNull { (variableId, valor) ->
//            valor.toFloatOrNull()?.let { floatValue ->
//                DatoDetalleEntity(
//                    muestra = floatValue,       // Ejemplo: usando el valor como "muestra"
//                    latitud = 0f,              // Placeholder (reemplaza con valor real si es necesario)
//                    longitud = 0f,             // Placeholder (reemplaza con valor real si es necesario)
//                    datoId = 0,                // Temporal, se asignará después
//                    variableGrupoId = variableId
//                )
//            }
//        }
//        // Verifica si hay detalles válidos para insertar
//        if (detalles.isEmpty()) {
//            throw IllegalStateException("No hay valores válidos para guardar")
//        }
//        // Llama al método DAO para insertar el dato y sus detalles
//        datoDao.insertDatoWithDetalles(datoEntity, detalles)
//    }
//    suspend fun uploadMuestraData(fecha: String, cartillaId: Int): Result<Boolean> {
//        return try {
//            val datosLocales = datoDao.getDatoWithDetalleByDateAndCartillaId(fecha, cartillaId)
//
//            // Convertir datos a la estructura generada por Apollo
//            val muestrasVGInputList = datosLocales.map {
//                MuestraVGInput(
//                    valvulaId = it.valvulaId,
//                    fechaHora = it.fecha,
//                    latitud = it.latitud.toDouble(),
//                    longitud = it.longitud.toDouble(),
//                    muestra = it.muestra.toDouble(),
//                    variableGrupoId = it.variableGrupoId
//                )
//            }
//            // Enviar la lista al servidor mediante Apollo Client
//            val mutation = CreateMuestraVGMutation(muestrasVGInputList)
//            val response = graphQLClient.mutation(mutation).execute()
//
//            // Validar la respuesta
//            if (response.hasErrors() || response.data?.createMuestra?.success == false) {
//                val errorMsg = response.errors?.joinToString(", ") { it.message }
//                    ?: response.data?.createMuestra?.message ?: "Error desconocido"
//                throw Exception(errorMsg)
//            }
//
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
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











//    private suspend fun syncUsuarios(userSet: List<GetEmpresaDataQuery.UserSet?>?) {
//        userSet?.forEach { usuario ->
//            try {
//                val usuarioDataModel = UsuarioDataModel(
//                    id = usuario?.id ?: 0,
//                    firstName = usuario?.firstName ?: "",
//                    lastName = usuario?.lastName ?: "",
//                    document = usuario?.document ?: "",
//                    email = usuario?.email ?: "",
//                    phone = usuario?.phone ?: "",
//                    isActive = usuario?.isActive?: false
//                )
//                val usuarioEntity = usuarioDataModel.toEntity()
//                val existingUsuario = usuarioDao.getUsuarioById(usuarioEntity.id)
//                if (existingUsuario != null) {
//                    usuarioDao.updateUsuario(usuarioEntity)
//                } else {
//                    usuarioDao.insertUsuario(usuarioEntity)
//                }
//            } catch (e: Exception) {
//                Log.e("SyncUsuario", "Error al sincronizar usuario: ${e.message}", e)
//            }
//        }
//        println("Sincronización de usuarios completada")
//    }
//    private suspend fun syncCartillas(cartillaSet: List<GetEmpresaDataQuery.CartillaEvaluacionSet?>?) {
//        cartillaSet?.forEach { cartilla ->
//            try {
//                val cartillaDataModel = CartillaEvaluacionDataModel(
//                    id = cartilla?.id ?: 0,
//                    codigo = cartilla?.codigo ?: "",
//                    nombre = cartilla?.nombre ?: "",
//                    activo = cartilla?.activo ?: false,
//                    cultivoId = cartilla?.cultivoId ?: 0
//                )
//                val cartillaEntity = cartillaDataModel.toEntity()
//                val existingCartilla = cartillaDao.getCartillaEvaluacionById(cartillaEntity.id)
//                if (existingCartilla != null) {
//                    cartillaDao.updateCartillaEvaluacion(cartillaEntity)
//                } else {
//                    cartillaDao.insertCartillaEvaluacion(cartillaEntity)
//                }
//                val grupoDataModels = cartilla?.grupovariableSet?.map { grupo ->
//                    GrupoVariableDataModel(
//                        id = grupo.id ?: 0,
//                        calculado = grupo.calculado,
//                        grupoCodigo = grupo.grupoCodigo?:"",
//                        grupoNombre = grupo.grupoNombre?:"",
//                        grupoId = grupo.grupoId?:0,
//                        cartillaEvaluacionId = cartillaEntity.id
//                    )
//                }
//                syncGrupoVariable(grupoDataModels)
//                val usuarioCartillaDataModels = cartilla?.userCartillaSet?.map { userCartilla ->
//                    UsuarioCartillaDataModel(
//                        usuarioId = userCartilla?.userId ?: 0,
//                        cartillaId = userCartilla?.cartillaId ?: 0
//                    )
//                }
//                syncUsuarioCartillaCrossRef(usuarioCartillaDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncCartilla", "Error al sincronizar cartilla: ${e.message}", e)
//            }
//        }
//        println("Sincronización de cartillas completada")
//    }
//    private suspend fun syncUsuarioCartillaCrossRef(userCartillaSet: List<UsuarioCartillaDataModel>?) {
//        userCartillaSet?.forEach { userCartilla ->
//            try {
//                val userCartillaEntity = userCartilla.toEntity()
//                val exists = cartillaDao.checkUsuarioCartillaExists(userCartillaEntity.usuarioId, userCartillaEntity.cartillaId) > 0
//
//                if (!exists) {
//                    cartillaDao.insertUsuarioCartillaCrossRef(userCartillaEntity)
//                }
//            } catch (e: Exception) {
//                Log.e("SyncUsuarioCartilla", "Error al sincronizar usuario con cartilla: ${e.message}", e)
//            }
//        }
//        println("Sincronización de usuario con cartilla completada")
//    }
//    private suspend fun syncGrupoVariable(grupoSet: List<GrupoVariableDataModel>?) {
//        grupoSet?.forEach { grupo ->
//            try {
//                val grupoEntity = grupo.toEntity()
//                val existingGrupo = grupoVariableDao.getGrupoVariableById(grupoEntity.id)
//                if (existingGrupo != null) {
//                    grupoVariableDao.updateGrupoVariable(grupoEntity)
//                } else {
//                    grupoVariableDao.insertGrupoVariable(grupoEntity)
//                }
//                val variableDataModels = grupo.variableGrupoSet?.map { variable ->
//                    VariableGrupoDataModel(
//                        id = variable.id,
//                        minimo = variable.minimo,
//                        maximo = variable.maximo,
//                        calculado = variable.calculado,
//                        variableEvaluacionNombre = variable.variableEvaluacionNombre,
//                        grupoVariableId  = grupoEntity.id
//                    )
//                }
//                syncVariableGrupo(variableDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncGrupoVariable", "Error al sincronizar grupo variable: ${e.message}", e)
//            }
//        }
//        println("Sincronización de grupos variable completada")
//    }
//    private suspend fun syncVariableGrupo(variableSet: List<VariableGrupoDataModel>?) {
//        variableSet?.forEach { variable ->
//            try {
//                val variableEntity = variable.toEntity()
//                val existingVariable = variableGrupoDao.getVariableGrupoById(variableEntity.id)
//                if (existingVariable != null) {
//                    variableGrupoDao.updateVariableGrupo(variableEntity)
//                } else {
//                    variableGrupoDao.insertVariableGrupo(variableEntity)
//                }
//            } catch (e: Exception) {
//                Log.e("SyncVariableGrupo", "Error al sincronizar viariable grupo: ${e.message}", e)
//            }
//        }
//        println("Sincronización de variables grupo completada")
//    }
//    private suspend fun syncFundos(fundoSet: List<FundoDataModel>?) {
//        // Verificamos si fundoSet no es nulo ni vacío
//        if (fundoSet.isNullOrEmpty()) {
//            println("No hay fondos para sincronizar")
//            return
//        }
//        fundoSet.forEach { fundo ->
//            try {
//                val fundoEntity = fundo.toEntity()
//                val existingFundo = fundoDao.getFundoById(fundoEntity.id)
//                if (existingFundo != null) {
//                    fundoDao.updateFundo(fundoEntity)
//                } else {
//                    fundoDao.insertFundo(fundoEntity)
//                }
//                val moduloDataModels = fundo.moduloSet?.map { modulo ->
//                    ModuloDataModel(
//                        id = modulo.id,
//                        codigo = modulo.codigo,
//                        nombre = modulo.nombre,
//                        activo = modulo.activo,
//                        fundoId = modulo.fundoId
//                    )
//                }
//                syncModulos(moduloDataModels)
//                val usuarioFundoDataModels = fundo.userFundoSet?.map { userFundo ->
//                    UsuarioFundoDataModel(
//                        userId = userFundo.userId,
//                        fundoId = userFundo.fundoId
//                    )
//                }
//                Log.d("AgriSort Modulo:", usuarioFundoDataModels.toString())
//                syncUsuarioFundoCrossRef(usuarioFundoDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncFundos", "Error al sincronizar fundo: ${e.message}", e)
//            }
//        }
//        println("Sincronización de fundos completada")
//    }
//    private suspend fun syncUsuarioFundoCrossRef(userFundoSet: List<UsuarioFundoDataModel>?) {
//        userFundoSet?.forEach { userFundo ->
//            try {
//                val userFundoEntity = userFundo.toEntity()
//                val exists = usuarioDao.checkUsuarioFundoExists(userFundoEntity.usuarioId, userFundoEntity.fundoId) > 0
//                if (!exists) {
//                    usuarioDao.insertUsuarioFundoCrossRef(userFundoEntity)
//                }
//            } catch (e: Exception) {
//                Log.e("SyncUsuarioFundo", "Error al sincronizar usuario con fundo: ${e.message}", e)
//            }
//        }
//        println("Sincronización de usuario con fundo completada")
//    }
//    private suspend fun syncModulos(moduloSet: List<ModuloDataModel>?) {
//        moduloSet?.forEach { modulo ->
//            try {
//                val moduloEntity = modulo.toEntity()
//                val existingModulo = moduloDao.getModuloById(moduloEntity.id)
//                if (existingModulo != null) {
//                    moduloDao.updateModulo(moduloEntity)
//                } else {
//                    moduloDao.insertModulo(moduloEntity)
//                }
//                val loteDataModels = modulo.loteSet?.map { lote ->
//                    LoteDataModel(
//                        id = lote.id ,
//                        codigo = lote.codigo,
//                        nombre = lote.nombre,
//                        activo = lote.activo,
//                        moduloId = moduloEntity.id
//                    )
//                }
//                Log.d("AgriSort Lote:", loteDataModels.toString())
//                syncLotes(loteDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncModulo", "Error al sincronizar módulo: ${e.message}", e)
//            }
//        }
//        println("Sincronización de modulos completada")
//    }
//    private suspend fun syncLotes(loteSet: List<LoteDataModel>?) {
//        loteSet?.forEach { lote ->
//            try {
//                val loteEntity = lote.toEntity()
//                val existingLote = loteDao.getLoteById(loteEntity.id)
//                if (existingLote != null) {
//                    loteDao.updateLote(loteEntity)
//                } else {
//                    loteDao.insertLote(loteEntity)
//                }
//                val campaniaDataModels = lote.campaniaSet?.map { campania ->
//                    CampaniaDataModel(
//                        id = campania.id,
//                        numero = campania.numero,
//                        centroCosto = campania.centroCosto,
//                        loteId = campania.loteId,
//                        cultivoId = campania.cultivoId,
//                        activo = campania.activo
//                    )
//                }
//                syncCampania(campaniaDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncLote", "Error al sincronizar lote: ${e.message}", e)
//            }
//        }
//        println("Sincronización de lotes completada")
//    }
//    private suspend fun syncCampania(campaniaSet: List<CampaniaDataModel>?) {
//        campaniaSet?.forEach { campania ->
//            try {
//                val campaniaEntity = campania.toEntity()
//                val existingCampania = campaniaDao.getCampaniaById(campaniaEntity.id)
//                if (existingCampania != null) {
//                    campaniaDao.updateCampania(campaniaEntity)
//                } else {
//                    campaniaDao.insertCampania(campaniaEntity)
//                }
//                val valvulaDataModels = campania.valvulaSet?.map { valvula ->
//                    ValvulaDataModel(
//                        id = valvula.id,
//                        codigo = valvula.codigo,
//                        nombre = valvula.nombre,
//                        campaniaId = campaniaEntity.id,
//                        activo = valvula.activo
//                    )
//                }
//                syncValvula(valvulaDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncCampaña", "Error al sincronizar campaña: ${e.message}", e)
//            }
//        }
//        println("Sincronización de campaña completada")
//    }
//    private suspend fun syncValvula(valvulaSet: List<ValvulaDataModel>?) {
//        valvulaSet?.forEach { valvula ->
//            try {
//                val valvulaEntity = valvula.toEntity()
//                val existingValvula = valvulaDao.getValvulaById(valvulaEntity.id)
//                if (existingValvula != null) {
//                    valvulaDao.updateValvula(valvulaEntity)
//                } else {
//                    valvulaDao.insertValvula(valvulaEntity)
//                }
//                val poligonoDataModels = valvula.poligonoSet?.map { poligono ->
//                    PoligonoDataModel(
//                        id = poligono.id,
//                        latitud = poligono.latitud,
//                        longitud = poligono.longitud,
//                        valvulaId = valvulaEntity.id
//                    )
//                }
//                syncPoligono(poligonoDataModels)
//            } catch (e: Exception) {
//                Log.e("SyncValvula", "Error al sincronizar valvula: ${e.message}", e)
//            }
//        }
//        println("Sincronización de valvula completada")
//    }
//    private suspend fun syncPoligono(poligonoSet: List<PoligonoDataModel>?) {
//        poligonoSet?.forEach { poligono ->
//            try {
//                val poligonoEntity = poligono.toEntity()
//                val existingPoligono = poligonoDao.getPoligonoById(poligonoEntity.id)
//                if (existingPoligono != null) {
//                    poligonoDao.updatePoligono(poligonoEntity)
//                } else {
//                    poligonoDao.insertPoligono(poligonoEntity)
//                }
//            } catch (e: Exception) {
//                Log.e("SyncPoligono", "Error al sincronizar poligono: ${e.message}", e)
//            }
//        }
//        println("Sincronización de poligono completada")
//    }
}
