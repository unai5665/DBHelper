package org.iesharia.myapplication

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import org.iesharia.myapplication.ui.theme.MyApplicationTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ) { innerPadding ->
                    MainActivity (
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)

    var lName:String by remember { mutableStateOf("Nombre") }
    var lAge:String by remember { mutableStateOf("Edad") }

    var nameValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf(-1) }
    var dataList by remember { mutableStateOf(listOf<Triple<Int, String, String>>()) }

    fun loadData() {
        val cursor: Cursor? = db.getName()
        val list = mutableListOf<Triple<Int, String, String>>()
        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndex(DBHelper.ID_COL))
                    val name = it.getString(it.getColumnIndex(DBHelper.NAME_COl))
                    val age = it.getString(it.getColumnIndex(DBHelper.AGE_COL))
                    list.add(Triple(id, name, age))
                } while (it.moveToNext())
            }
            it.close()
        }
        dataList = list
    }

    Column (
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp

        )

        OutlinedTextField(
            value = nameValue,
            onValueChange = {
                nameValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
            value = ageValue,
            onValueChange = {
                ageValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        var bModifier:Modifier = Modifier.padding(20.dp)
        Row {
            Button(
                modifier = bModifier,
                onClick = {
                    val name = nameValue
                    val age = ageValue
                    db.addName(name, age)

                    Toast.makeText(
                        context,
                        name + " adjuntado a la base de datos",
                        Toast.LENGTH_LONG)
                        .show()

                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Añadir")
            }
            Button(
                modifier = bModifier,
                onClick = {
                    val cursor = db.getName()
                    if (cursor != null && cursor.moveToFirst()) {
                        lName = "Nombre"
                        lAge = "Edad"
                        do {
                            val id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL))
                            val name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
                            val age = cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL))
                            lName += "\n$id - $name"
                            lAge += "\n$age"
                        } while (cursor.moveToNext())
                        cursor.close()
                    }
                }
            ) {
                Text(text = "Mostrar")
            }
        }
        Row { Button(
            modifier = bModifier,
            onClick = {
                if (selectedId != -1) {
                    db.updateName(selectedId, nameValue, ageValue)
                    Toast.makeText(
                        context,
                        "Registro actualizado",
                        Toast.LENGTH_LONG
                    ).show()
                    selectedId = -1
                    nameValue = ""
                    ageValue = ""
                } else {
                    Toast.makeText(
                        context,
                        "Selecciona un registro para actualizar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text(text = "Actualizar")
        }
            Button(
                modifier = bModifier,
                onClick = {
                    if (selectedId != -1) {
                        db.deleteName(selectedId)
                        Toast.makeText(
                            context,
                            "Registro eliminado",
                            Toast.LENGTH_LONG
                        ).show()
                        selectedId = -1
                        nameValue = ""
                        ageValue = ""
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona un registro para eliminar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text(text = "Eliminar")
            }
        }

        Row {
            Text(
                modifier = bModifier,
                text = lName
            )
            Text(
                modifier = bModifier,
                text = lAge
            )

        }
    }
}

