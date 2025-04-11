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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp



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
    val opcionesCliente = listOf("Comfandi") // puedes agregar más luego
    var expanded by remember { mutableStateOf(false) }
    val cliente = remember { mutableStateOf("") }
    val ciudad = remember { mutableStateOf("") }
    val sucursal = remember { mutableStateOf("") }
    val caso = remember { mutableStateOf("") }
    val horaInicio = remember { mutableStateOf("") }
    val horaFin = remember { mutableStateOf("") }


    val nombreTecnico = remember { mutableStateOf("") }
    var ccTecnico = remember { mutableStateOf("") }

    val tipoDispositivo = remember { mutableStateOf("") }
    val marca = remember { mutableStateOf("") }
    val modelo = remember { mutableStateOf("") }
    val serial = remember { mutableStateOf("") }
    val ubicacion = remember { mutableStateOf("") }
    val contador = remember { mutableStateOf("") }
    val ip = remember { mutableStateOf("") }

    var estado = remember { mutableStateOf("Operativo") } // valor por defecto
    val tipoServicio = remember { mutableStateOf("Preventivo") }

    val observaciones = remember { mutableStateOf("") }

    val nombreUsuario = remember { mutableStateOf("") }
    val firmaUsuario = remember { mutableStateOf("") }
    val funcionario = remember { mutableStateOf("") }


    Text("ACTA DE MANTENIMIENTO", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Campos del formulario

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


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = cliente.value,
                onValueChange = {},
                label = { Text("Cliente") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesCliente.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            cliente.value = opcion
                            expanded = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(value = ciudad.value, onValueChange = { ciudad.value = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sucursal.value, onValueChange = { sucursal.value = it }, label = { Text("Sucursal o Sede") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = caso.value, onValueChange = { caso.value = it }, label = { Text("Caso #") }, modifier = Modifier.fillMaxWidth())
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
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = horaFin.value,
                onValueChange = { horaFin.value = it },
                label = { Text("Hora Fin") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Datos del Técnico", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = nombreTecnico.value, onValueChange = { nombreTecnico.value = it }, label = { Text("Nombre Técnico") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ccTecnico.value, onValueChange = { ccTecnico.value = it }, label = { Text("CC Técnico") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))
        Text("Datos del Dispositivo", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = tipoDispositivo.value, onValueChange = { tipoDispositivo.value = it }, label = { Text("Tipo de Dispositivo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = marca.value, onValueChange = { marca.value = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = modelo.value, onValueChange = { modelo.value = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = serial.value, onValueChange = { serial.value = it }, label = { Text("Serial") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ubicacion.value, onValueChange = { ubicacion.value = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contador.value, onValueChange = { contador.value = it }, label = { Text("Contador") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ip.value, onValueChange = { ip.value = it }, label = { Text("Dirección IP") }, modifier = Modifier.fillMaxWidth())


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

        Spacer(modifier = Modifier.height(16.dp))
        Text("Observaciones", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = observaciones.value, onValueChange = { observaciones.value = it }, label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))
        Text("Recibe a satisfacción", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = nombreUsuario.value, onValueChange = { nombreUsuario.value = it }, label = { Text("Nombre Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = firmaUsuario.value, onValueChange = { firmaUsuario.value = it }, label = { Text("Firma Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = funcionario.value, onValueChange = { funcionario.value = it }, label = { Text("Funcionario") }, modifier = Modifier.fillMaxWidth())

        // Botón de Guardar
        Button(
            onClick = {
                val form = FormularioMantenimiento(
                    fecha.value, cliente.value, ciudad.value, sucursal.value, caso.value,
                    horaInicio.value, horaFin.value, nombreTecnico.value, ccTecnico.value,
                    tipoDispositivo.value, marca.value, modelo.value, serial.value, ubicacion.value,
                    contador.value, ip.value, estado.value, tipoServicio.value, observaciones.value,
                    nombreUsuario.value, firmaUsuario.value, funcionario.value
                )
                Toast.makeText(context, "Guardado para ${form.cliente}", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Guardar Formulario")
        }
    }
}
data class FormularioMantenimiento(
    val fecha: String,
    val cliente: String,
    val ciudad: String,
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
    val nombreUsuario: String,
    val firmaUsuario: String,
    val funcionario: String
)

