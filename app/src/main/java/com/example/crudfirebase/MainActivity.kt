package com.example.crudfirebase

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var txtid: EditText
    private lateinit var txtnom: EditText
    private lateinit var btnbus: Button
    private lateinit var btnmod: Button
    private lateinit var btnreg: Button
    private lateinit var btneli: Button
    private lateinit var lvDatos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Inicialización de las variables usando findViewById
        txtid = findViewById(R.id.txtid)
        txtnom = findViewById(R.id.txtnom)
        btnbus = findViewById(R.id.btnbus)
        btnmod = findViewById(R.id.btnmod)
        btnreg = findViewById(R.id.btnreg)
        btneli = findViewById(R.id.btneli)
        lvDatos = findViewById(R.id.lvDatos)

        // Configuración de los listeners para los botones
        btnbus.setOnClickListener { botonBuscar() }
        btnmod.setOnClickListener { botonModificar() }
        btnreg.setOnClickListener { botonRegistrar() }
        btneli.setOnClickListener { botonEliminar() }
    }

    private fun botonBuscar() {
        // Aquí la lógica para buscar
    }

    private fun botonModificar() {
        // Aquí la lógica para modificar
    }

    private fun botonRegistrar() {
        // Aquí la lógica para registrar
    }

    private fun botonEliminar() {
        // Aquí la lógica para eliminar
    }

    private fun ocultarTeclado() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


}