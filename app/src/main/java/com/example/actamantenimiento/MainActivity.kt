package com.example.actamantenimiento

import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
//import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.*
//import androidx.compose.material.icons.filled.AddCircle
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import java.util.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.Canvas
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Done
//import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import android.net.Uri
//import androidx.compose.material.icons.filled.AccountBox
import java.io.File
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
//import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.*
import android.graphics.Color.BLACK
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.FileOutputStream
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.content.ContextCompat
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaFormulario()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    // Estados de los campos
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = remember { mutableStateOf(dateFormat.format(calendar.time)) }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            fecha.value = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val todosLosClientes = listOf("Comfandi", "Virrey Solis", "Ingredium")
    var expandedCliente by remember { mutableStateOf(false) }
    var clienteSeleccionado by remember { mutableStateOf("") }
    // Filtrar según lo que el usuario escribe
    val clientesFiltrados = todosLosClientes.filter {
        it.contains(clienteSeleccionado, ignoreCase = true)
    }

    val todasLasCiudades = listOf("Cali", "Palmira", "Buenaventura", "Cartago", "Tuluá", "Jamundí")
    var expandedciudad by remember { mutableStateOf(false) }
    var ciudadSeleccionada by remember { mutableStateOf("") }
    // Filtrar según lo que el usuario escribe
    val ciudadesFiltradas = todasLasCiudades.filter {
        it.contains(ciudadSeleccionada, ignoreCase = true)
    }


    val caso = remember { mutableStateOf("") }


    val horaInicio = remember { mutableStateOf("") }
    val horaFin = remember { mutableStateOf("") }
    val calendarHora = Calendar.getInstance()
    val regexHora = Regex("^([0][1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$", RegexOption.IGNORE_CASE)

    val horaInicioValida = remember(horaInicio.value) {
        horaInicio.value.trim().matches(regexHora)
    }
    val horaFinValida = remember(horaFin.value) {
        horaFin.value.trim().matches(regexHora)
    }
    val timePickerInicio = TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val horaFormateada =
                String.format("%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, amPm)
            horaInicio.value = horaFormateada
        },
        calendarHora.get(Calendar.HOUR_OF_DAY),
        calendarHora.get(Calendar.MINUTE),
        false // formato AM/PM
    )

    val timePickerFin = TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val horaFormateada =
                String.format("%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, amPm)
            horaFin.value = horaFormateada
        },
        calendarHora.get(Calendar.HOUR_OF_DAY),
        calendarHora.get(Calendar.MINUTE),
        false
    )

    var ccTecnico by remember { mutableStateOf("") }
    var nombreTecnico by remember { mutableStateOf("") }
    LaunchedEffect(ccTecnico) {
        if (ccTecnico.length >= 6) { // puedes ajustar este valor
            db.collection("usuario")
                .whereEqualTo("cedula", ccTecnico)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val tecnico = documents.first()
                        nombreTecnico = tecnico.getString("nombre") ?: ""
                    } else {
                        nombreTecnico = "No encontrado"
                    }
                }
                .addOnFailureListener { exception ->
                    nombreTecnico = "Error: ${exception.message}"
                    Log.e("FirestoreError", "Fallo al consultar: ", exception)
                }
        }
    }
    var serial by remember { mutableStateOf("") }
    var tipoDispositivo by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var sucursal by remember { mutableStateOf("") }
    var contador by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    // Lanzar búsqueda cuando cambia el serial


    var estado = remember { mutableStateOf("Operativo") } // valor por defecto
    val tipoServicio = remember { mutableStateOf("Preventivo") }

    val observaciones = remember { mutableStateOf("") }

    val nombreUsuario = remember { mutableStateOf("") }

    val firmaUsuario = remember { mutableStateOf("") }
    var showFirma by remember { mutableStateOf(false) }
    val path = remember { mutableStateOf(Path()) }
    val canvasSize = remember { mutableStateOf(Size.Zero) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val funcionario = remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Campos del formulario
        Spacer(modifier = Modifier.height(24.dp))
        Text("ACTA DE MANTENIMIENTO", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = fecha.value,
            onValueChange = {},
            label = { Text("Fecha") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha"
                    )
                }
            }
        )


        OutlinedTextField(
            value = caso.value,
            onValueChange = { caso.value = it },
            label = { Text("Caso #") },
            modifier = Modifier.fillMaxWidth()
        )

        //Variables Horas
        Text("Horario", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = horaInicio.value,
                onValueChange = { horaInicio.value = it },
                label = { Text("Hora Inicio") },
                placeholder = { Text("Ej: 08:30 AM") },
                isError = horaInicio.value.isNotEmpty() && !horaInicioValida,
                trailingIcon = {
                    IconButton(onClick = { timePickerInicio.show() }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Abrir reloj")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        timePickerInicio.show()
                    },
                singleLine = true
            )

            OutlinedTextField(
                value = horaFin.value,
                onValueChange = { horaFin.value = it },
                label = { Text("Hora Fin") },
                placeholder = { Text("Ej: 10:45 AM") },
                isError = horaFin.value.isNotEmpty() && !horaFinValida,
                trailingIcon = {
                    IconButton(onClick = { timePickerFin.show() }) {
                        Icon(Icons.Default.AccessTimeFilled, contentDescription = "Abrir reloj")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        timePickerFin.show()
                    },
                singleLine = true
            )
        }

        //Variables Cedula y Nombre del Tecnico
        Spacer(modifier = Modifier.height(24.dp))
        Text("Datos del Técnico", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = ccTecnico,
            onValueChange = { ccTecnico = it },
            label = { Text("Cédula Técnico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombreTecnico,
            onValueChange = {},
            label = { Text("Nombre Técnico") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        //Variables Datos del Dispositivo
        Spacer(modifier = Modifier.height(24.dp))
        Text("Datos del Dispositivo", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = serial,
            onValueChange = { serial = it.replace(" ", "") },
            label = { Text("Serial") },
            trailingIcon = {
                IconButton(onClick = {
                    val serialBusqueda = serial.uppercase()
                    Log.d("SerialDebug", "Serial limpio: $serialBusqueda")
                    db.collection("impresora")
                        .whereEqualTo("serial", serialBusqueda)
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                val document = result.documents[0]
                                tipoDispositivo = document.getString("tipoImp") ?: "No encontrado"
                                marca = document.getString("marca") ?: "No encontrado"
                                modelo = document.getString("modelo") ?: "No encontrado"
                                ubicacion = document.getString("ubicacion") ?: "No encontrado"
                                ip = document.getString("ip") ?: "No encontrado"
                                ciudadSeleccionada = document.getString("ciudad") ?: "No encontrado"
                                clienteSeleccionado =
                                    document.getString("cliente") ?: "No encontrado"
                                sucursal = document.getString("sucursal") ?: "No encontrado"
                            } else {
                                tipoDispositivo = "No encontrado"
                                marca = ""
                                modelo = ""
                                ubicacion = ""
                                sucursal = ""
                                ip = ""
                                ciudadSeleccionada = ""
                                clienteSeleccionado = ""
                                sucursal = ""
                            }
                        }
                        .addOnFailureListener { exception ->
                            tipoDispositivo = "Error: ${exception.message}"
                            Log.e("FirestoreError", "Fallo al consultar: ", exception)
                        }
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = tipoDispositivo,
            onValueChange = { tipoDispositivo = it },
            label = { Text("Tipo de Dispositivo") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ip,
            onValueChange = { ip = it },
            label = { Text("Dirección IP") },
            modifier = Modifier.fillMaxWidth()
        )
        //Variable Cliente
        ExposedDropdownMenuBox(
            expanded = expandedCliente,
            onExpandedChange = { expandedCliente = !expandedCliente }
        ) {
            OutlinedTextField(
                value = clienteSeleccionado,
                onValueChange = {
                    clienteSeleccionado = it
                    expandedCliente = true // se despliega al escribir
                },
                label = { Text("Cliente") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCliente,
                onDismissRequest = { expandedCliente = false }
            ) {
                clientesFiltrados.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text(cliente) },
                        onClick = {
                            clienteSeleccionado = cliente
                            expandedCliente = false
                        }
                    )
                }
            }
        }


        //Variable Ciudad
        ExposedDropdownMenuBox(
            expanded = expandedciudad,
            onExpandedChange = { expandedciudad = !expandedciudad }
        ) {
            OutlinedTextField(
                value = ciudadSeleccionada,
                onValueChange = {
                    ciudadSeleccionada = it
                    expandedciudad = true // se despliega al escribir
                },
                label = { Text("Ciudad") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedciudad)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedciudad,
                onDismissRequest = { expandedciudad = false }
            ) {
                ciudadesFiltradas.forEach { ciudad ->
                    DropdownMenuItem(
                        text = { Text(ciudad) },
                        onClick = {
                            ciudadSeleccionada = ciudad
                            expandedciudad = false
                        }
                    )
                }
            }
        }



        OutlinedTextField(
            value = sucursal,
            onValueChange = { sucursal = it },
            label = { Text("Sucursal o Sede") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = contador,
            onValueChange = { contador = it },
            label = { Text("Contador") },
            modifier = Modifier.fillMaxWidth()
        )
        //Variable Estado del dispositivo
        Spacer(modifier = Modifier.height(24.dp))
        Text("Estado del Dispositivo", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = estado.value == "Operativo",
                onClick = { estado.value = "Operativo" }
            )
            Text("Operativo", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = estado.value == "No Operativo",
                onClick = { estado.value = "No Operativo" }
            )
            Text("No Operativo")
        }

        //Variable Tipos de Servicio
        Spacer(modifier = Modifier.height(24.dp))
        Text("Tipo de Servicio", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = tipoServicio.value == "Preventivo",
                onClick = { tipoServicio.value = "Preventivo" }
            )
            Text("Preventivo", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = tipoServicio.value == "Correctivo",
                onClick = { tipoServicio.value = "Correctivo" }
            )
            Text("Correctivo")
        }

        //Variable Observaciones
        Spacer(modifier = Modifier.height(16.dp))
        Text("Observaciones", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = observaciones.value,
            onValueChange = { observaciones.value = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth()
        )

        //Variables Finales
        Spacer(modifier = Modifier.height(24.dp))
        Text("Recibe a satisfacción", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = nombreUsuario.value,
            onValueChange = { nombreUsuario.value = it },
            label = { Text("Nombre Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        //OutlinedTextField(value = firmaUsuario.value, onValueChange = { firmaUsuario.value = it }, label = { Text("Firma Usuario") }, modifier = Modifier.fillMaxWidth())
        var mostrarModalUsuario by remember { mutableStateOf(false) }
        var firmaUsuarioBitmap by remember { mutableStateOf<Bitmap?>(null) }

        OutlinedButton(
            onClick = { mostrarModalUsuario = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Firmar Usuario")
        }

        if (mostrarModalUsuario) {
            ModalFirmaUsuario(
                onCerrar = { mostrarModalUsuario = false },
                onGuardar = {
                    firmaUsuarioBitmap = it
                    mostrarModalUsuario = false
                }
            )
        }


        //Variable funcionario
        var mostrarModalFuncionario by remember { mutableStateOf(false) }
        var firmaFuncionarioBitmap by remember { mutableStateOf<Bitmap?>(null) }

        OutlinedButton(
            onClick = { mostrarModalFuncionario = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Firmar Funcionario")
        }

        if (mostrarModalFuncionario) {
            ModalFirmaFuncionario(
                onCerrar = { mostrarModalFuncionario = false },
                onGuardar = {
                    firmaFuncionarioBitmap = it
                    mostrarModalFuncionario = false
                }
            )
        }




        // Botón de Guardar
        val formImpresora = FormularioMantenimiento(
            cliente = clienteSeleccionado,
            ciudad = ciudadSeleccionada,
            sucursal = sucursal,
            tipoDispositivo = tipoDispositivo,
            marca = marca,
            modelo = modelo,
            serial = serial.uppercase(),
            ubicacion = ubicacion,
            ip = ip,

        )
        //Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    // Paso 1: Buscar el documento por un campo único
                    val serialBusqueda = serial.uppercase()
                    db.collection("impresora")
                        .whereEqualTo(
                            "serial",
                            serialBusqueda
                        )  // Cambia "caso" por el campo que estés usando como clave única
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val document =
                                    querySnapshot.documents[0]  // Tomamos el primer documento encontrado
                                val documentId = document.id


                                // Paso 3: Actualizar el documento
                                db.collection("impresora")
                                    .document(documentId)
                                    .set(formImpresora, SetOptions.merge())
                                    .addOnSuccessListener {
//                                        guardarFormularioEnPDF(
//                                            fecha.value, clienteSeleccionado, ciudadSeleccionada, sucursal, caso.value,
//                                            horaInicio.value, horaFin.value, nombreTecnico, ccTecnico, tipoDispositivo,
//                                            marca, modelo, serial, ubicacion, contador, ip, estado.value,
//                                            tipoServicio.value, observaciones.value, nombreUsuario.value,
//                                            firmaUsuarioBitmap, firmaFuncionarioBitmap, context
//                                        )
                                        Toast.makeText(
                                            context,
                                            "Formulario actualizado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val formulario = Formulario1(
                                            fecha = fecha.value,
                                            clienteSeleccionado = clienteSeleccionado,
                                            ciudadSeleccionada = ciudadSeleccionada,
                                            sucursal = sucursal,
                                            caso = caso.value,
                                            horaInicio = horaInicio.value,
                                            horaFin = horaFin.value,
                                            nombreTecnico = nombreTecnico,
                                            ccTecnico = ccTecnico,
                                            tipoDispositivo = tipoDispositivo,
                                            marca = marca,
                                            modelo = modelo,
                                            serial = serial,
                                            ubicacion = ubicacion,
                                            contador = contador,
                                            ip = ip,
                                            estado = estado.value,
                                            tipoServicio = tipoServicio.value,
                                            observaciones = observaciones.value,
                                            nombreUsuario = nombreUsuario.value
                                        )
                                        val archivoPDF = llenarPlantillaPDF(
                                            context = context,
                                            formulario = formulario,
                                            firmaUsuario = firmaUsuarioBitmap,
                                            firmaFuncionario = firmaFuncionarioBitmap
                                        )

                                        abrirPDF(context, archivoPDF)
                                        enviarPDFPorCorreo(context, archivoPDF, "eiman.males@carvajal.com")
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error al actualizar: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "No se encontró el documento con ese serial",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error al buscar el documento: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Guardar Formulario")
            }

            var mostrarVistaPrevia by remember { mutableStateOf(false) }
            Button(onClick = { mostrarVistaPrevia = true }) {
                Icon(Icons.Filled.Assignment, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Vista Previa")
            }

            if (mostrarVistaPrevia) {
                VistaPreviaFormularioCompleta(
                    fecha.value, clienteSeleccionado, ciudadSeleccionada, sucursal, caso.value,
                    horaInicio.value, horaFin.value, nombreTecnico, ccTecnico, tipoDispositivo,
                    marca, modelo, serial, ubicacion, contador, ip, estado.value,
                    tipoServicio.value, observaciones.value, nombreUsuario.value,
                    firmaUsuarioBitmap, firmaFuncionarioBitmap
                )
            }
       // }
    }
}
data class FormularioMantenimiento(
    val cliente: String,
    val ciudad: String,
    val sucursal: String,
    val tipoDispositivo: String,
    val marca: String,
    val modelo: String,
    val serial: String,
    val ubicacion: String,
    val ip: String,

)
data class Formulario1(
    val fecha: String,
    val clienteSeleccionado: String,
    val ciudadSeleccionada: String,
    val sucursal: String,
    val caso: String,
    val horaInicio: String,
    val horaFin: String,
    val nombreTecnico: String,
    val ccTecnico: String,
    val tipoDispositivo: String,
    val marca: String,
    val modelo: String,
    val serial: String,
    val ubicacion: String,
    val contador: String,
    val ip: String,
    val estado: String,
    val tipoServicio: String,
    val observaciones: String,
    val nombreUsuario: String
)
@Composable
fun ModalFirmaUsuario(
    onCerrar: () -> Unit,
    onGuardar: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var imagenFirmaFoto by remember { mutableStateOf<Bitmap?>(null) }

    val paths = remember { mutableStateListOf<android.graphics.Path>() }
    var currentPath = remember { mutableStateOf<android.graphics.Path?>(null) }

    val uri = remember {
        val archivo = File(context.cacheDir, "firma_usuario.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val original = BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(uri)
                )
                imagenFirmaFoto = recortarFirmaAutomatica(original)
                paths.clear()
                currentPath.value = null
            }
        }
    )

    Dialog(onDismissRequest = onCerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Firma del Usuario", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val newPath = android.graphics.Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                        currentPath.value = newPath
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPath.value?.lineTo(change.position.x, change.position.y)
                                    },
                                    onDragEnd = {
                                        currentPath.value?.let { paths.add(it) }
                                        currentPath.value = null
                                    }
                                )
                            }
                    ) {
                        if (imagenFirmaFoto != null) {
                            Image(
                                bitmap = imagenFirmaFoto!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    strokeWidth = 4f
                                    style = android.graphics.Paint.Style.STROKE
                                    isAntiAlias = true
                                }

                                drawIntoCanvas { canvas ->
                                    paths.forEach { canvas.nativeCanvas.drawPath(it, paint) }
                                    currentPath.value?.let { canvas.nativeCanvas.drawPath(it, paint) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = {
                        imagenFirmaFoto = null
                        currentPath.value = null
                        paths.clear()
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Limpiar")
                    }

                    OutlinedButton(onClick = onCerrar) {
                        Text("Cancelar")
                    }

                    OutlinedButton(onClick = {
                        cameraLauncher.launch(uri)
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Foto Firma")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val bitmap = if (imagenFirmaFoto != null) {
                            imagenFirmaFoto!!
                        } else {
                            capturarListaPathComoBitmap(paths)
                        }
                        onGuardar(bitmap)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Guardar")
                }
            }
        }
    }
}




@Composable
fun ModalFirmaFuncionario(
    onCerrar: () -> Unit,
    onGuardar: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var imagenFirmaFoto by remember { mutableStateOf<Bitmap?>(null) }

    // Lista de trazos independientes
    val paths = remember { mutableStateListOf<android.graphics.Path>() }
    val currentPath = remember { mutableStateOf<android.graphics.Path?>(null) }

    val uri = remember {
        val archivo = File(context.cacheDir, "firma_funcionario.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val original = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                imagenFirmaFoto = recortarFirmaAutomatica(original)
                paths.clear()
                currentPath.value = null
            }
        }
    )

    Dialog(onDismissRequest = onCerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Firma del Funcionario", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val nuevoPath = android.graphics.Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                        currentPath.value = nuevoPath
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPath.value?.lineTo(change.position.x, change.position.y)
                                    },
                                    onDragEnd = {
                                        currentPath.value?.let { paths.add(it) }
                                        currentPath.value = null
                                    }
                                )
                            }
                    ) {
                        if (imagenFirmaFoto != null) {
                            Image(
                                bitmap = imagenFirmaFoto!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = 4f
                                    isAntiAlias = true
                                }

                                drawIntoCanvas { canvas ->
                                    paths.forEach { canvas.nativeCanvas.drawPath(it, paint) }
                                    currentPath.value?.let { canvas.nativeCanvas.drawPath(it, paint) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = {
                        imagenFirmaFoto = null
                        currentPath.value = null
                        paths.clear()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Limpiar")
                    }

                    OutlinedButton(onClick = onCerrar) {
                        Icon(Icons.Filled.Cancel, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cancelar")
                    }

                    OutlinedButton(onClick = {
                        cameraLauncher.launch(uri)
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Foto Firma")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val bitmap = if (imagenFirmaFoto != null) {
                            imagenFirmaFoto!!
                        } else {
                            capturarListaPathComoBitmap(paths)
                        }
                        onGuardar(bitmap)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Guardar")
                }
            }
        }
    }
}

fun capturarListaPathComoBitmap(paths: List<android.graphics.Path>): Bitmap {
    val bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 4f
        style = android.graphics.Paint.Style.STROKE
        isAntiAlias = true
    }
    paths.forEach { canvas.drawPath(it, paint) }
    return bitmap
}



fun recortarFirmaAutomatica(original: Bitmap): Bitmap {
    val width = original.width
    val height = original.height

    var top = height
    var bottom = 0
    var left = width
    var right = 0

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = original.getPixel(x, y)
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff

            val isDark = (r + g + b) / 3 < 200

            if (isDark) {
                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }
    }

    return if (top >= bottom || left >= right) {
        original
    } else {
        Bitmap.createBitmap(original, left, top, right - left, bottom - top)
    }
}
fun capturarFirmaComoBitmap(path: android.graphics.Path): Bitmap {
    val bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 4f
        style = android.graphics.Paint.Style.STROKE
        isAntiAlias = true
    }
    canvas.drawPath(path, paint)
    return bitmap
}

@Composable
fun VistaPreviaFormularioCompleta(
    fecha: String,
    clienteSeleccionado: String,
    ciudadSeleccionada: String,
    sucursal: String,
    caso: String,
    horaInicio: String,
    horaFin: String,
    nombreTecnico: String,
    ccTecnico: String,
    tipoDispositivo: String,
    marca: String,
    modelo: String,
    serial: String,
    ubicacion: String,
    contador: String,
    ip: String,
    estado: String,
    tipoServicio: String,
    observaciones: String,
    nombreUsuario: String,
    firmaUsuarioBitmap: Bitmap?,
    firmaFuncionarioBitmap: Bitmap?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Vista Previa del Formulario", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        InfoCampo("Fecha", fecha)
        InfoCampo("Cliente", clienteSeleccionado)
        InfoCampo("Ciudad", ciudadSeleccionada)
        InfoCampo("Sucursal", sucursal)
        InfoCampo("Caso", caso)

        InfoCampo("Hora de Inicio", horaInicio)
        InfoCampo("Hora de Fin", horaFin)

        InfoCampo("Nombre Técnico", nombreTecnico)
        InfoCampo("Cédula Técnico", ccTecnico)

        InfoCampo("Tipo de Dispositivo", tipoDispositivo)
        InfoCampo("Marca", marca)
        InfoCampo("Modelo", modelo)
        InfoCampo("Serial", serial)
        InfoCampo("Ubicación", ubicacion)
        InfoCampo("Contador", contador)
        InfoCampo("IP", ip)

        InfoCampo("Estado", estado)
        InfoCampo("Tipo de Servicio", tipoServicio)
        InfoCampo("Observaciones", observaciones)

        InfoCampo("Nombre Usuario", nombreUsuario)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Firma del Usuario", fontWeight = FontWeight.Bold)
        FirmaPreview(firmaUsuarioBitmap)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Firma del Funcionario", fontWeight = FontWeight.Bold)
        FirmaPreview(firmaFuncionarioBitmap)
    }
}

@Composable
fun InfoCampo(label: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = if (valor.isNotBlank()) valor else "—", color = Color.Gray)
    }
}

@Composable
fun FirmaPreview(bitmap: Bitmap?) {
    if (bitmap != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        Text("Sin firma", color = Color.Red)
    }
}

//fun guardarFormularioEnPDF(
//    fecha: String,
//    clienteSeleccionado: String,
//    ciudadSeleccionada: String,
//    sucursal: String,
//    caso: String,
//    horaInicio: String,
//    horaFin: String,
//    nombreTecnico: String,
//    ccTecnico: String,
//    tipoDispositivo: String,
//    marca: String,
//    modelo: String,
//    serial: String,
//    ubicacion: String,
//    contador: String,
//    ip: String,
//    estado: String,
//    tipoServicio: String,
//    observaciones: String,
//    nombreUsuario: String,
//    firmaUsuarioBitmap: Bitmap?,
//    firmaFuncionarioBitmap: Bitmap?,
//    context: Context
//) {
//    val pdfDocument = PdfDocument()
//    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamaño A4
//    val page = pdfDocument.startPage(pageInfo)
//    val canvas = page.canvas
//
//    val paint = Paint()
//    paint.color = android.graphics.Color.BLACK
//    paint.textSize = 12f
//
//    var y = 100f // Posición inicial vertical
//
//    // Encabezado centrado
//    val headerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.encabezado)
//    val scaledHeader = Bitmap.createScaledBitmap(headerBitmap, 500, 80, true)
//    canvas.drawBitmap(scaledHeader, (pageInfo.pageWidth - scaledHeader.width) / 2f, 10f, null)
//
//    // Datos del técnico
//    canvas.drawText("Datos del Técnico:", 30f, y, paint)
//    y += 20
//    canvas.drawText("Nombre: $nombreTecnico", 30f, y, paint)
//    y += 20
//    canvas.drawText("CC: $ccTecnico", 30f, y, paint)
//    y += 30
//
//    // Datos del dispositivo
//    canvas.drawText("Datos del dispositivo:", 30f, y, paint)
//    y += 20
//    canvas.drawText("Tipo: $tipoDispositivo", 30f, y, paint)
//    canvas.drawText("Marca: $marca", 250f, y, paint)
//    y += 20
//    canvas.drawText("Modelo: $modelo", 30f, y, paint)
//    canvas.drawText("Serial: $serial", 250f, y, paint)
//    y += 20
//    canvas.drawText("Ubicación: $ubicacion", 30f, y, paint)
//    canvas.drawText("Contador: $contador", 250f, y, paint)
//    y += 20
//    canvas.drawText("Estado del dispositivo: $estado", 30f, y, paint)
//    y += 20
//    canvas.drawText("Dirección IP: $ip", 30f, y, paint)
//    y += 30
//
//    // Tipo de servicio
//    canvas.drawText("Tipo de Servicio: $tipoServicio", 30f, y, paint)
//    y += 20
//
//    // Observaciones
//    canvas.drawText("Observaciones:", 30f, y, paint)
//    y += 20
//    val obsLines = observaciones.chunked(90) // Dividir texto largo
//    obsLines.forEach {
//        canvas.drawText(it, 30f, y, paint)
//        y += 20
//    }
//    y += 20
//
//    // Firma Usuario
//    canvas.drawText("Recibe a satisfacción:", 30f, y, paint)
//    y += 30
//    firmaUsuarioBitmap?.let {
//        val scaledFirmaUsuario = Bitmap.createScaledBitmap(it, 120, 60, true)
//        canvas.drawBitmap(scaledFirmaUsuario, 30f, y, null)
//    }
//    firmaFuncionarioBitmap?.let {
//        val scaledFirmaFuncionario = Bitmap.createScaledBitmap(it, 120, 60, true)
//        canvas.drawBitmap(scaledFirmaFuncionario, 200f, y, null)
//    }
//    canvas.drawText("$nombreUsuario", 30f, y + 70, paint)
//    canvas.drawText("Funcionario Carvajal S.A.", 200f, y + 70, paint)
//
//    // Pie de página
//    val footerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pie_pagina)
//    val scaledFooter = Bitmap.createScaledBitmap(footerBitmap, 500, 80, true)
//    canvas.drawBitmap(scaledFooter, (pageInfo.pageWidth - scaledFooter.width) / 2f, pageInfo.pageHeight - 90f, null)
//
//    pdfDocument.finishPage(page)
//
//    // Guardar archivo
//    val file = File(context.cacheDir, "formulario_mantenimiento.pdf")
//    try {
//        pdfDocument.writeTo(FileOutputStream(file))
//        Toast.makeText(context, "PDF guardado en ${file.absolutePath}", Toast.LENGTH_LONG).show()
//        abrirPDF(context, file.name)
//    } catch (e: IOException) {
//        e.printStackTrace()
//        Toast.makeText(context, "Error al guardar el PDF", Toast.LENGTH_SHORT).show()
//    }
//
//    pdfDocument.close()
//}
//
//
//
//fun abrirPDF(context: Context, fileName: String) {
//    try {
//        val file = File(context.cacheDir, fileName)
//        if (!file.exists()) {
//            Toast.makeText(context, "El archivo PDF no se encuentra", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val uri: Uri = FileProvider.getUriForFile(
//            context,
//            "${context.packageName}.provider",
//            file
//        )
//
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(uri, "application/pdf")
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
//        }
//
//        context.startActivity(Intent.createChooser(intent, "Abrir PDF con..."))
//
//    } catch (e: Exception) {
//        e.printStackTrace()
//        Toast.makeText(context, "No se pudo abrir el PDF", Toast.LENGTH_SHORT).show()
//    }
//}

fun llenarPlantillaPDF(
    context: Context,
    formulario: Formulario1,
    firmaUsuario: Bitmap?,
    firmaFuncionario: Bitmap?
): File {
    val fileAsset = File(context.cacheDir, "plantilla_formulario.pdf")
    context.assets.open("plantilla_formulario.pdf").use { input ->
        FileOutputStream(fileAsset).use { output -> input.copyTo(output) }
    }

    val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(fileAsset, ParcelFileDescriptor.MODE_READ_ONLY))
    val page = pdfRenderer.openPage(0)

    val plantillaBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
    page.render(plantillaBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    page.close()
    pdfRenderer.close()

    // Crear nuevo PDF con la imagen de fondo y los datos encima
    val documento = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(plantillaBitmap.width, plantillaBitmap.height, 1).create()
    val nuevaPagina = documento.startPage(pageInfo)
    val canvas = nuevaPagina.canvas

    // Fondo de la plantilla
    canvas.drawBitmap(plantillaBitmap, 0f, 0f, null)

    val paint = Paint()
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 12f


    // Coordenadas de campos (¡ajústalas manualmente según la plantilla!)
    canvas.drawText(formulario.nombreTecnico, 160f, 155f, paint)
    canvas.drawText(formulario.ccTecnico, 160f, 175f, paint)

    // Hora, cliente, ciudad
    canvas.drawText(formulario.fecha, 490f, 133f, paint)
    canvas.drawText(formulario.clienteSeleccionado, 478f, 155f, paint)
    canvas.drawText(formulario.ciudadSeleccionada, 478f, 180f, paint)
    canvas.drawText(formulario.sucursal, 478f, 205f, paint)
    canvas.drawText(formulario.caso, 478f, 250f, paint)
    canvas.drawText(formulario.horaInicio, 478f, 275f, paint)
    canvas.drawText(formulario.horaFin, 478f, 300f, paint)

    canvas.drawText(formulario.tipoDispositivo, 225f, 230f, paint)
    canvas.drawText(formulario.marca, 225f, 243f, paint)
    canvas.drawText(formulario.modelo, 225f, 259f, paint)
    canvas.drawText(formulario.serial, 225f, 272f, paint)
    canvas.drawText(formulario.ubicacion, 225f, 285f, paint)
    canvas.drawText(formulario.contador, 225f, 300f, paint)
    canvas.drawText(formulario.estado, 225f, 322f, paint)
    canvas.drawText(formulario.ip, 225f, 340f, paint)


    canvas.drawText(formulario.tipoServicio, 182f, 405f, paint)
    canvas.drawText(formulario.observaciones, 88f, 488f, paint)

    canvas.drawText(formulario.nombreUsuario, 90f, 670f, paint)


    // Firmas (ajusta posición)
    firmaUsuario?.let {
        val firmaEscalada = Bitmap.createScaledBitmap(it, 150, 60, true)
        canvas.drawBitmap(firmaEscalada, 220f, 620f, null)
    }

    firmaFuncionario?.let {
        val firmaEscalada = Bitmap.createScaledBitmap(it, 150, 60, true)
        canvas.drawBitmap(firmaEscalada, 350f, 650f, null)
    }

    documento.finishPage(nuevaPagina)

    // Guardar PDF final
    val archivoFinal = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "formulario_final_${formulario.serial}_${formulario.ubicacion}.pdf")
    documento.writeTo(FileOutputStream(archivoFinal))
    documento.close()

    return archivoFinal
}

fun abrirPDF(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // ← Esto debe coincidir con tu AndroidManifest
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay aplicación para abrir PDF", Toast.LENGTH_LONG).show()
    }
}


fun enviarPDFPorCorreo(context: Context, archivoPDF: File, destinatario: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        archivoPDF
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
        putExtra(Intent.EXTRA_SUBJECT, "Formulario de Mantenimiento")
        putExtra(Intent.EXTRA_TEXT, "Adjunto encontrarás el formulario en formato PDF.")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Enviar PDF por correo"))
}





