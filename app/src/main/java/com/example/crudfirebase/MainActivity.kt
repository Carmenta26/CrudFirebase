package com.example.crudfirebase

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var txtid: EditText
    private lateinit var txtnom: EditText
    private lateinit var btnbus: Button
    private lateinit var btnmod: Button
    private lateinit var btnreg: Button
    private lateinit var btneli: Button
    private lateinit var lvDatos: ListView
    private lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseRef = FirebaseDatabase.getInstance().getReference("Luchador")

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

        listarLuchadores()
    }



    private fun listarLuchadores() {
        val db = FirebaseDatabase.getInstance()
        val dbRef = db.getReference(Luchador::class.java.simpleName)

        val listaLuchadores = ArrayList<Luchador>()
        val adapter = ArrayAdapter<Luchador>(this, android.R.layout.simple_list_item_1, listaLuchadores)
        lvDatos.adapter = adapter

        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val luchador = snapshot.getValue(Luchador::class.java)
                luchador?.let {
                    listaLuchadores.add(it)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Implementar acción tras eliminación de un elemento si es necesario
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Implementar acción tras mover un elemento si es necesario
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores potenciales de Firebase
            }
        })

        lvDatos.setOnItemClickListener { _, _, position, _ ->
            val luchador = listaLuchadores[position]
            val alerta = AlertDialog.Builder(this@MainActivity)
            alerta.setCancelable(true)
            alerta.setTitle("Luchador Seleccionado")

            val mensaje = "ID: ${luchador.id}\n\nNOMBRE: ${luchador.nombre}"
            alerta.setMessage(mensaje)
            alerta.show()
        }
    }


    private fun botonBuscar() {
        btnbus.setOnClickListener {
            val id = txtid.text.toString().trim()
            if (id.isEmpty()) {
                ocultarTeclado()
                Toast.makeText(this, "Digite el ID del luchador a buscar!", Toast.LENGTH_SHORT).show()
            } else {
                val db = FirebaseDatabase.getInstance()
                val dbRef = db.getReference(Luchador::class.java.simpleName)

                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var encontrado = false
                        for (x in snapshot.children) {
                            if (x.child("id").value.toString() == id) {
                                encontrado = true
                                ocultarTeclado()
                                txtnom.setText(x.child("nombre").value.toString())
                                break
                            }
                        }
                        if (!encontrado) {
                            ocultarTeclado()
                            Toast.makeText(this@MainActivity, "ID ($id) no encontrado!!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar posible error de Firebase
                    }
                })
            }
        }
    }



    private fun botonModificar() {
        btnmod.setOnClickListener {
            val id = txtid.text.toString().trim()
            val nombre = txtnom.text.toString().trim()

            if (id.isEmpty() || nombre.isEmpty()) {
                ocultarTeclado()
                Toast.makeText(this, "Complete los campos faltantes para actualizar!!", Toast.LENGTH_SHORT).show()
            } else {
                val db = FirebaseDatabase.getInstance()
                val dbRef = db.getReference(Luchador::class.java.simpleName)

                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var nombreExiste = false

                        // Verificar si el nombre ya existe
                        for (x in snapshot.children) {
                            if (x.child("nombre").value.toString().equals(nombre, ignoreCase = true)) {
                                nombreExiste = true
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "El nombre ($nombre) ya existe. Imposible modificar!!", Toast.LENGTH_LONG).show()
                                break
                            }
                        }

                        if (!nombreExiste) {
                            var idEncontrado = false
                            for (x in snapshot.children) {
                                if (x.child("id").value.toString() == id) {
                                    idEncontrado = true
                                    ocultarTeclado()
                                    x.ref.child("nombre").setValue(nombre)
                                    txtid.text.clear()
                                    txtnom.text.clear()
                                    listarLuchadores()
                                    break
                                }
                            }

                            if (!idEncontrado) {
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "ID ($id) no encontrado. Imposible modificar!!", Toast.LENGTH_LONG).show()
                                txtid.text.clear()
                                txtnom.text.clear()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar posible error de Firebase
                    }
                })
            }
        }
    }




    private fun botonRegistrar() {
        val id = txtid.text.toString().trim()
        val nombre = txtnom.text.toString().trim()

        if (id.isEmpty() || nombre.isEmpty()) {
            ocultarTeclado()
            Toast.makeText(this, "Complete los campos faltantes!", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseDatabase.getInstance().getReference(Luchador::class.java.simpleName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var idExists = false
                        var nameExists = false

                        for (x in snapshot.children) {
                            if (x.child("id").value.toString() == id) {
                                idExists = true
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "Error. El ID ($id) ya existe!!", Toast.LENGTH_SHORT).show()
                                break
                            }
                            if (x.child("nombre").value.toString().equals(nombre, ignoreCase = true)) {
                                nameExists = true
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "Error. El nombre ($nombre) ya existe!!", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }

                        if (!idExists && !nameExists) {
                            val luchador = Luchador(id.toInt(), nombre)
                            FirebaseDatabase.getInstance().getReference(Luchador::class.java.simpleName)
                                .child(id).setValue(luchador)
                                .addOnSuccessListener {
                                    ocultarTeclado()
                                    Toast.makeText(this@MainActivity, "Luchador registrado correctamente!!", Toast.LENGTH_SHORT).show()
                                    txtid.text.clear()
                                    txtnom.text.clear()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle possible errors
                    }
                })
        }
    }



    private fun botonEliminar() {
        btneli.setOnClickListener {
            val id = txtid.text.toString().trim()

            if (id.isEmpty()) {
                ocultarTeclado()
                Toast.makeText(this, "Digite el ID del luchador a eliminar!!", Toast.LENGTH_SHORT).show()
            } else {
                val db = FirebaseDatabase.getInstance()
                val dbRef = db.getReference(Luchador::class.java.simpleName)

                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var encontrado = false
                        for (x in snapshot.children) {
                            if (x.child("id").value.toString() == id) {
                                val alertDialog = AlertDialog.Builder(this@MainActivity)
                                alertDialog.setCancelable(false)
                                alertDialog.setTitle("Pregunta")
                                alertDialog.setMessage("¿Está seguro(a) de querer eliminar el registro?")

                                alertDialog.setNegativeButton("Cancelar", null)

                                alertDialog.setPositiveButton("Aceptar") { _, _ ->
                                    encontrado = true
                                    ocultarTeclado()
                                    x.ref.removeValue().addOnSuccessListener {
                                        Toast.makeText(this@MainActivity, "Registro eliminado correctamente.", Toast.LENGTH_SHORT).show()
                                        listarLuchadores()
                                    }.addOnFailureListener {
                                        Toast.makeText(this@MainActivity, "Error al eliminar el registro.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                alertDialog.show()
                                break
                            }
                        }

                        if (!encontrado) {
                            ocultarTeclado()
                            Toast.makeText(this@MainActivity, "ID ($id) no encontrado. Imposible eliminar!!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar posible error de Firebase
                    }
                })
            }
        }
    }



    private fun ocultarTeclado() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


}