package com.example.agrinova.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Transaction
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
//    private val database: AppDatabase,
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
    suspend fun syncEmpresaData(empresaId: Int) {
        val response = graphQLClient.query(
            GetEmpresaDataQuery(empresaId.toString())
        ).execute()

        response.data?.empresaById?.let { empresaData ->
            val existingEmpresa = empresaDao.getEmpresaById(empresaData.id!!.toInt())
            val empresaEntity = EmpresaDataModel(id = empresaData.id,
                ruc = empresaData.ruc ?: "",
                razonSocial = empresaData.razonSocial ?: "",
                correo = empresaData.correo ?: "",
                telefono = empresaData.telefono ?: "",
                direccion = empresaData.direccion ?: "",
                userSet = empresaData.userSet?.map {
                    UsuarioDataModel(
                        id = it?.id ?: 0,
                        firstName = it?.firstName ?: "",
                        lastName = it?.lastName ?: "",
                        document = it?.document ?: "",
                        email = it?.email ?: "",
                        phone = it?.phone ?: "",
                        isActive = it?.isActive ?: false
                    )
                },
                zonaSet = empresaData.zonaSet?.map { zona ->
                    ZonaDataModel(id = zona?.id ?: 0,
                        codigo = zona?.codigo ?: "",
                        nombre = zona?.nombre ?: "",
                        activo = zona?.activo ?: false,
                        empresaId = zona?.empresaId ?: 0,
                        fundoSet = zona?.fundoSet?.map { fundo ->
                            FundoDataModel(id = fundo?.id ?: 0,
                                codigo = fundo?.codigo ?: "",
                                nombre = fundo?.nombre ?: "",
                                activo = fundo?.activo ?: false,
                                zonaId = fundo?.zonaId ?: 0,
                                userFundoSet = fundo?.userFundoSet?.map { userFundo ->
                                    UsuarioFundoDataModel(
                                        userId = userFundo?.userId ?: 0,
                                        fundoId = userFundo?.fundoId ?: 0
                                    )
                                } ?: emptyList(),
                                moduloSet = fundo?.moduloSet?.map { modulo ->
                                    ModuloDataModel(id = modulo?.id ?: 0,
                                        codigo = modulo?.codigo ?: "",
                                        nombre = modulo?.nombre ?: "",
                                        activo = modulo?.activo ?: false,
                                        fundoId = modulo?.fundoId ?: 0,
                                        loteSet = modulo?.loteSet?.map { lote ->
                                            LoteDataModel(id = lote?.id ?: 0,
                                                codigo = lote?.codigo ?: "",
                                                nombre = lote?.nombre ?: "",
                                                activo = lote?.activo ?: false,
                                                moduloId = lote?.moduloId ?: 0,
                                                campaniaSet = lote?.campaniaSet?.map { campania ->
                                                    CampaniaDataModel(id = campania?.id ?: 0,
                                                        numero = campania?.numero ?: 0,
                                                        centroCosto = campania?.centroCosto ?: "",
                                                        activo = campania?.activo ?: false,
                                                        loteId = campania?.loteId ?: 0,
                                                        cultivoId = campania?.cultivoId ?: 0,
                                                        valvulaSet = campania?.valvulaSet?.map { valvula ->
                                                            ValvulaDataModel(id = valvula?.id ?: 0,
                                                                codigo = valvula?.codigo ?: "",
                                                                nombre = valvula?.nombre ?: "",
                                                                activo = valvula?.activo ?: false,
                                                                campaniaId = valvula?.campaniaId
                                                                    ?: 0,
                                                                poligonoSet = valvula?.poligonoSet?.map { poligono ->
                                                                    PoligonoDataModel(
                                                                        id = poligono?.id ?: 0,
                                                                        latitud = poligono?.latitud?.toFloat()
                                                                            ?: 0.0f,
                                                                        longitud = poligono?.longitud?.toFloat()
                                                                            ?: 0.0f,
                                                                        valvulaId = poligono?.valvulaId
                                                                            ?: 0
                                                                    )
                                                                } ?: emptyList())
                                                        } ?: emptyList())
                                                } ?: emptyList())
                                        } ?: emptyList())
                                } ?: emptyList())
                        } ?: emptyList())
                },
                cartillaEvaluacionSet = empresaData.cartillaEvaluacionSet?.map { cartilla ->
                    CartillaEvaluacionDataModel(id = cartilla?.id ?: 0,
                        codigo = cartilla?.codigo ?: "",
                        nombre = cartilla?.nombre ?: "",
                        activo = cartilla?.activo ?: false,
                        cultivoId = cartilla?.cultivoId ?: 0,
                        userCartillaSet = cartilla?.userCartillaSet?.map { userCartilla ->
                            UsuarioCartillaDataModel(
                                usuarioId = userCartilla?.userId ?: 0,
                                cartillaId = userCartilla?.cartillaId ?: 0
                            )
                        } ?: emptyList(),
                        grupoVariableSet = cartilla?.grupovariableSet?.map { grupo ->
                            GrupoVariableDataModel(id = grupo.id ?: 0,
                                calculado = grupo.calculado,
                                grupoCodigo = grupo.grupoCodigo ?: "",
                                grupoNombre = grupo.grupoNombre ?: "",
                                grupoId = grupo.grupoId ?: 0,
                                cartillaEvaluacionId = grupo.cartillaEvaluacionId ?: 0,
                                variableGrupoSet = grupo.variableGrupoSet?.map { variable ->
                                    VariableGrupoDataModel(
                                        id = variable?.id ?: 0,
                                        minimo = variable?.minimo ?: 0,
                                        maximo = variable?.maximo ?: 0,
                                        calculado = variable?.calculado ?: false,
                                        variableEvaluacionNombre = variable?.variableEvaluacionNombre
                                            ?: "",
                                        grupoVariableId = variable?.grupoVariableId ?: 0
                                    )
                                } ?: emptyList())
                        } ?: emptyList())
                },
                cultivoSet = empresaData.cultivoSet?.map { cultivo ->
                    CultivoDataModel(
                        id = cultivo?.id ?: 0,
                        codigo = cultivo?.codigo ?: "",
                        nombre = cultivo?.nombre ?: "",
                        activo = cultivo?.activo ?: false
                    )
                } ?: emptyList())
            try {
                // Guardar o actualizar la empresa y sus datos relacionados
                if (existingEmpresa != null) {
                    empresaDao.updateEmpresa(empresaEntity.toEntity())
                } else {
                    Log.d("Italo Empresa->Insert", empresaData.toString())
                    empresaDao.insertEmpresa(empresaEntity.toEntity())
                }
                // Sincronizar cultivos
                Log.d("Italo Cultivos 2024", "Cultivos")
                empresaEntity.cultivoSet?.forEach { cultivo ->
                    val existingCultivo = cultivoDao.getCultivoById(cultivo.id.toInt())
                    if (existingCultivo != null) {
                        cultivoDao.updateCultivo(cultivo.toEntity())
                    } else {
                        Log.d("Italo Cultivo->Insert", cultivo.toString())
                        cultivoDao.insertCultivo(cultivo.toEntity())
                    }
                }
                // Sincronizar usuarios
                Log.d("Italo Usuarios 2024", "Usuarios")
                empresaEntity.userSet?.forEach { usuario ->
                    val existingUsuario = usuarioDao.getUsuarioById(usuario.id.toInt())
                    if (existingUsuario != null) {
                        usuarioDao.updateUsuario(usuario.toEntity())
                    } else {
                        Log.d("Italo Usuario->Insert", usuario.toString())
                        usuarioDao.insertUsuario(usuario.toEntity())
                    }
                }
                // Sincronizar zonas y otros
                Log.d("Italo Zonas 2024", "Zonas")
                empresaEntity.zonaSet?.forEach { zona ->
                    val existingZona = zonaDao.getZonaById(zona.id)
                    if (existingZona != null) {
                        zonaDao.updateZona(zona.toEntity())
                    } else {
                        Log.d("Italo Zona->Insert", zona.toString())
                        zonaDao.insertZona(zona.toEntity())
                    }

                    zona.fundoSet?.forEach { fundo ->
                        val existingFundo = fundoDao.getFundoById(fundo.id)
                        if (existingFundo != null) {
                            fundoDao.updateFundo(fundo.toEntity())
                        } else {
                            Log.d("Italo Fundo->Insert", fundo.toString())
                            fundoDao.insertFundo(fundo.toEntity())
                        }
                        // Almacenar relaciones usuario-fundo
                        fundo.userFundoSet?.forEach { userFundo ->
                            val userId = userFundo.userId
                            val fundoId = userFundo.fundoId

                            // Verifica si la relación ya existe
                            val exists = usuarioDao.checkUsuarioFundoExists(userId, fundoId) > 0

                            if (!exists) {
                                // Inserta solo si la relación no existe
                                Log.d("Italo UsuarioFundo->Insert", userFundo.toString())
                                usuarioDao.insertUsuarioFundoCrossRef(userFundo.toEntity())
                            }
                        }
                        // Almacenar modulo y otros
                        fundo.moduloSet?.forEach { modulo ->
                            val existingModulo = moduloDao.getModuloById(modulo.id)
                            if (existingModulo != null) {
                                moduloDao.updateModulo(modulo.toEntity())
                            } else {
                                Log.d("Italo Modulo->Insert", modulo.toString())
                                moduloDao.insertModulo(modulo.toEntity())
                            }
                            modulo.loteSet?.forEach { lote ->
                                val existingLote = loteDao.getLoteById(lote.id)
                                if (existingLote != null) {
                                    loteDao.updateLote(lote.toEntity())
                                } else {
                                    Log.d("Italo Lote->Insert", lote.toString())
                                    loteDao.insertLote(lote.toEntity())
                                }
                                lote.campaniaSet?.forEach { campania ->
                                    val existingCampania = campaniaDao.getCampaniaById(campania.id)
                                    if (existingCampania != null) {
                                        campaniaDao.updateCampania(campania.toEntity())
                                    } else {
                                        Log.d("Italo Campaña->Insert", campania.toString())
                                        campaniaDao.insertCampania(campania.toEntity())
                                    }
                                    campania.valvulaSet?.forEach { valvula ->
                                        val existingValvula = valvulaDao.getValvulaById(valvula.id)
                                        if (existingValvula != null) {
                                            valvulaDao.updateValvula(valvula.toEntity())
                                        } else {
                                            Log.d("Italo Valvula->Insert", valvula.toString())
                                            valvulaDao.insertValvula(valvula.toEntity())
                                        }
                                        valvula.poligonoSet?.forEach { poligono ->
                                            val existingPoligono =
                                                poligonoDao.getPoligonoById(poligono.id)
                                            if (existingPoligono != null) {
                                                poligonoDao.updatePoligono(poligono.toEntity())
                                            } else {
                                                Log.d("Italo Poligono->Insert", poligono.toString())
                                                poligonoDao.insertPoligono(poligono.toEntity())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Sincronizar cartilla
                Log.d("Italo Cartilla Evaluacion 2024", "Cartillas")
                empresaEntity.cartillaEvaluacionSet?.forEach { cartilla ->
                    val existingCartilla =
                        cartillaDao.getCartillaEvaluacionById(cartilla.id.toInt())
                    if (existingCartilla != null) {
                        cartillaDao.updateCartillaEvaluacion(cartilla.toEntity())
                    } else {
                        Log.d("Italo Cartilla->Insert", cartilla.toString())
                        cartillaDao.insertCartillaEvaluacion(cartilla.toEntity())
                    }
                    // Almacenar relaciones usuario-fundo
                    cartilla.userCartillaSet?.forEach { userCartilla ->
                        val userId = userCartilla.usuarioId
                        val cartillaId = userCartilla.cartillaId

                        // Verifica si la relación ya existe
                        val exists = cartillaDao.checkUsuarioCartillaExists(userId, cartillaId) > 0

                        if (!exists) {
                            // Inserta solo si la relación no existe
                            Log.d("Italo UsuarioCartilla->Insert", userCartilla.toString())
                            cartillaDao.insertUsuarioCartillaCrossRef(userCartilla.toEntity())
                        }
                    }
                    cartilla.grupoVariableSet?.forEach { grupoVariable ->
                        val existingGrupoVariable =
                            grupoVariableDao.getGrupoVariableById(grupoVariable.id.toInt())
                        if (existingGrupoVariable != null) {
                            grupoVariableDao.updateGrupoVariable(grupoVariable.toEntity())
                        } else {
                            Log.d("Italo Grupo Variable->Insert", grupoVariable.toString())
                            grupoVariableDao.insertGrupoVariable(grupoVariable.toEntity())
                        }
                        grupoVariable.variableGrupoSet?.forEach { variableGrupo ->
                            val existingVariableGrupo =
                                variableGrupoDao.getVariableGrupoById(variableGrupo.id.toInt())
                            if (existingVariableGrupo != null) {
                                variableGrupoDao.updateVariableGrupo(variableGrupo.toEntity())
                            } else {
                                Log.d("Italo Variable Grupo->Insert", variableGrupo.toString())
                                variableGrupoDao.insertVariableGrupo(variableGrupo.toEntity())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync Error", "Error durante la sincronización: ${e.message}", e)
                throw e // Lanza la excepción para evitar que se cometan datos parciales
            }
        } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
    }

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
//    suspend fun syncCompanyData(empresaId: Int) {
//        try {
//            val response = graphQLClient.query(
//                GetEmpresaDataQuery(empresaId.toString())
//            ).execute()
//
//            response.data?.empresaById?.let { empresaData ->
//                // Usa withContext para manejar la transacción
//                withContext(Dispatchers.IO) {
//                    // Iniciar bloque de sincronización
//                    database.runInTransaction {
//                        // Sincronizar Empresa
//                        empresaData.let { data ->
//                            val empresaDataModel = EmpresaDataModel(
//                                id = data.id ?: return@let,
//                                ruc = data.ruc ?: "",
//                                razonSocial = data.razonSocial ?: "",
//                                correo = data.correo ?: "",
//                                telefono = data.telefono ?: "",
//                                direccion = data.direccion ?: ""
//                            )
//                            upsertEmpresa(empresaDataModel.toEntity())
//                        }
//
//                        // Sincronizar Cultivos
//                        empresaData.cultivoSet?.forEach { cultivo ->
//                            cultivo?.let {
//                                val cultivoEntity = CultivoDataModel(
//                                    id = it.id ?: return@let,
//                                    codigo = it.codigo ?: "",
//                                    nombre = it.nombre ?: "",
//                                    activo = it.activo ?: false
//                                )
//                                database.cultivoDao().upsertCultivo(cultivoEntity.toEntity())
//                            }
//                        }
//
//                        // Sincronizar Usuarios
//                        empresaData.userSet?.forEach { usuario ->
//                            usuario?.let {
//                                val usuarioEntity = UsuarioDataModel(
//                                    id = it.id ?: return@let,
//                                    firstName = it.firstName ?: "",
//                                    lastName = it.lastName ?: "",
//                                    document = it.document ?: "",
//                                    email = it.email ?: "",
//                                    phone = it.phone ?: "",
//                                    isActive = it.isActive ?: false
//                                )
//                                database.usuarioDao().upsertUsuario(usuarioEntity.toEntity())
//                            }
//                        }
//
//                        // Sincronizar Zonas
//                        empresaData.zonaSet?.forEach { zonaData ->
//                            zonaData?.let { zona ->
//                                val zonaEntity = ZonaDataModel(
//                                    id = zona.id ?: return@let,
//                                    codigo = zona.codigo ?: "",
//                                    nombre = zona.nombre ?: "",
//                                    activo = zona.activo ?: false,
//                                    empresaId = zona.empresaId ?: 0
//                                )
//                                database.zonaDao().upsertZona(zonaEntity.toEntity())
//
//                                // Sincronizar Fundos de cada Zona
//                                zona.fundoSet?.forEach { fundoData ->
//                                    fundoData?.let { fundo ->
//                                        val fundoEntity = FundoDataModel(
//                                            id = fundo.id ?: return@let,
//                                            codigo = fundo.codigo ?: "",
//                                            nombre = fundo.nombre ?: "",
//                                            activo = fundo.activo ?: false,
//                                            zonaId = fundo.zonaId ?: 0
//                                        )
//                                        database.fundoDao().upsertFundo(fundoEntity.toEntity())
//
//                                        // Sincronizar Relaciones Usuario-Fundo
//                                        fundo.userFundoSet?.forEach { userFundo ->
//                                            userFundo?.let {
//                                                val usuarioFundoEntity = UsuarioFundoDataModel(
//                                                    userId = it.userId ?: return@let,
//                                                    fundoId = it.fundoId ?: 0
//                                                )
//                                                database.usuarioDao()
//                                                    .insertOrIgnoreUsuarioFundoCrossRef(
//                                                        usuarioFundoEntity.toEntity()
//                                                    )
//                                            }
//                                        }
//
//                                        // Sincronizar Módulos
//                                        fundo.moduloSet?.forEach { moduloData ->
//                                            moduloData?.let { modulo ->
//                                                val moduloEntity = ModuloDataModel(
//                                                    id = modulo.id ?: return@let,
//                                                    codigo = modulo.codigo ?: "",
//                                                    nombre = modulo.nombre ?: "",
//                                                    activo = modulo.activo ?: false,
//                                                    fundoId = modulo.fundoId ?: 0
//                                                )
//                                                database.moduloDao()
//                                                    .upsertModulo(moduloEntity.toEntity())
//
//                                                // Sincronizar Lotes
//                                                modulo.loteSet?.forEach { loteData ->
//                                                    loteData?.let { lote ->
//                                                        val loteEntity = LoteDataModel(
//                                                            id = lote.id ?: return@let,
//                                                            codigo = lote.codigo ?: "",
//                                                            nombre = lote.nombre ?: "",
//                                                            activo = lote.activo ?: false,
//                                                            moduloId = lote.moduloId ?: 0
//                                                        )
//                                                        database.loteDao()
//                                                            .upsertLote(loteEntity.toEntity())
//
//                                                        // Sincronizar Campañas
//                                                        lote.campaniaSet?.forEach { campaniaData ->
//                                                            campaniaData?.let { campania ->
//                                                                val campaniaEntity =
//                                                                    CampaniaDataModel(
//                                                                        id = campania.id
//                                                                            ?: return@let,
//                                                                        numero = campania.numero
//                                                                            ?: 0,
//                                                                        centroCosto = campania.centroCosto
//                                                                            ?: "",
//                                                                        activo = campania.activo
//                                                                            ?: false,
//                                                                        loteId = campania.loteId
//                                                                            ?: 0,
//                                                                        cultivoId = campania.cultivoId
//                                                                            ?: 0
//                                                                    )
//                                                                database.campaniaDao()
//                                                                    .upsertCampania(
//                                                                        campaniaEntity.toEntity()
//                                                                    )
//
//                                                                // Sincronizar Válvulas
//                                                                campania.valvulaSet?.forEach { valvulaData ->
//                                                                    valvulaData?.let { valvula ->
//                                                                        val valvulaEntity =
//                                                                            ValvulaDataModel(
//                                                                                id = valvula.id
//                                                                                    ?: return@let,
//                                                                                codigo = valvula.codigo
//                                                                                    ?: "",
//                                                                                nombre = valvula.nombre
//                                                                                    ?: "",
//                                                                                activo = valvula.activo
//                                                                                    ?: false,
//                                                                                campaniaId = valvula.campaniaId
//                                                                                    ?: 0
//                                                                            )
//                                                                        database.valvulaDao()
//                                                                            .upsertValvula(
//                                                                                valvulaEntity.toEntity()
//                                                                            )
//
//                                                                        // Sincronizar Polígonos
//                                                                        valvula.poligonoSet?.forEach { poligonoData ->
//                                                                            poligonoData?.let { poligono ->
//                                                                                val poligonoEntity =
//                                                                                    PoligonoDataModel(
//                                                                                        id = poligono.id
//                                                                                            ?: return@let,
//                                                                                        latitud = poligono.latitud?.toFloat()
//                                                                                            ?: 0.0f,
//                                                                                        longitud = poligono.longitud?.toFloat()
//                                                                                            ?: 0.0f,
//                                                                                        valvulaId = poligono.valvulaId
//                                                                                            ?: 0
//                                                                                    )
//                                                                                database.poligonoDao()
//                                                                                    .upsertPoligono(
//                                                                                        poligonoEntity.toEntity()
//                                                                                    )
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        // Sincronizar Cartillas de Evaluación
//                        empresaData.cartillaEvaluacionSet?.forEach { cartillaData ->
//                            cartillaData?.let { cartilla ->
//                                val cartillaEntity = CartillaEvaluacionDataModel(
//                                    id = cartilla.id ?: return@let,
//                                    codigo = cartilla.codigo ?: "",
//                                    nombre = cartilla.nombre ?: "",
//                                    activo = cartilla.activo ?: false,
//                                    cultivoId = cartilla.cultivoId ?: 0
//                                )
//                                database.cartillaEvaluacionDao().upsertCartillaEvaluacion(
//                                    cartillaEntity.toEntity()
//                                )
//
//                                // Sincronizar Relaciones Usuario-Cartilla
//                                cartilla.userCartillaSet?.forEach { userCartillaData ->
//                                    userCartillaData?.let { userCartilla ->
//                                        val usuarioCartillaEntity = UsuarioCartillaDataModel(
//                                            usuarioId = userCartilla.userId ?: return@let,
//                                            cartillaId = userCartilla.cartillaId ?: 0
//                                        )
//                                        database.cartillaEvaluacionDao()
//                                            .insertOrIgnoreUsuarioCartillaCrossRef(
//                                                usuarioCartillaEntity.toEntity()
//                                            )
//                                    }
//                                }
//
//                                // Sincronizar Grupos Variables
//                                cartilla.grupovariableSet?.forEach { grupoVariableData ->
//                                    grupoVariableData?.let { grupoVariable ->
//                                        val grupoVariableEntity = GrupoVariableDataModel(
//                                            id = grupoVariable.id ?: return@let,
//                                            calculado = grupoVariable.calculado ?: false,
//                                            grupoCodigo = grupoVariable.grupoCodigo ?: "",
//                                            grupoNombre = grupoVariable.grupoNombre ?: "",
//                                            grupoId = grupoVariable.grupoId ?: 0,
//                                            cartillaEvaluacionId = grupoVariable.cartillaEvaluacionId
//                                                ?: 0
//                                        )
//                                        database.grupoVariableDao().upsertGrupoVariable(
//                                            grupoVariableEntity.toEntity()
//                                        )
//
//                                        // Sincronizar Variables Grupo
//                                        grupoVariable.variableGrupoSet?.forEach { variableGrupoData ->
//                                            variableGrupoData?.let { variableGrupo ->
//                                                val variableGrupoEntity = VariableGrupoDataModel(
//                                                    id = variableGrupo.id ?: return@let,
//                                                    minimo = variableGrupo.minimo ?: 0,
//                                                    maximo = variableGrupo.maximo ?: 0,
//                                                    calculado = variableGrupo.calculado ?: false,
//                                                    variableEvaluacionNombre = variableGrupo.variableEvaluacionNombre
//                                                        ?: "",
//                                                    grupoVariableId = variableGrupo.grupoVariableId
//                                                        ?: 0
//                                                )
//                                                database.variableGrupoDao().upsertVariableGrupo(
//                                                    variableGrupoEntity.toEntity()
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
//        } catch (e: Exception) {
//            Log.e("SyncEmpresa", "Error en sincronización completa: ${e.message}", e)
//            throw e
//        }
//    }
//    // Extensiones de DAO para upsert
//    @Transaction
//    suspend fun upsertEmpresa(empresa: EmpresaEntity) {
//        val existingEmpresa = getEmpresaById(empresa.id)
//        if (existingEmpresa != null) {
//            updateEmpresa(empresa)
//        } else {
//            insertEmpresa(empresa)
//        }
//    }
//
//    // Implementar métodos upsert similares para cada DAO
//    @Transaction
//    suspend fun upsertCultivo(cultivo: CultivoEntity) {
//        val existingCultivo = getCultivoById(cultivo.id)
//        if (existingCultivo != null) {
//            updateCultivo(cultivo)
//        } else {
//            insertCultivo(cultivo)
//        }
//    }
}
