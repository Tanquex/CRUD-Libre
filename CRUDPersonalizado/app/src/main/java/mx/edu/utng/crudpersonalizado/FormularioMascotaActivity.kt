package mx.edu.utng.crudpersonalizado

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FormularioMascotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEspecie: EditText
    private lateinit var etRaza: EditText
    private lateinit var etEdad: EditText
    private lateinit var cbVacunada: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var databaseHelper: DatabaseHelper

    private var mascotaId: Int? = null  // Para saber si estamos editando una mascota existente

    private lateinit var ivMascota: ImageView
    private lateinit var etUrlImagen: EditText
    private lateinit var btnSeleccionarImagen: Button
    private var imagenSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_mascota)

        etNombre = findViewById(R.id.etNombre)
        etEspecie = findViewById(R.id.etEspecie)
        etRaza = findViewById(R.id.etRaza)
        etEdad = findViewById(R.id.etEdad)
        cbVacunada = findViewById(R.id.cbVacunada)
        btnGuardar = findViewById(R.id.btnGuardar)

        etUrlImagen = findViewById(R.id.etUrlImagen)
        ivMascota = findViewById(R.id.ivMascota)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)

        databaseHelper = DatabaseHelper(this)

        // Escuchar cambios en el campo de URL para cargar la imagen automáticamente
        etUrlImagen.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val url = s.toString()
                if (url.isNotEmpty()) {
                    Glide.with(this@FormularioMascotaActivity)
                        .load(url)
                        .into(ivMascota)
                    imagenSeleccionada = url
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // Verificamos si venimos a EDITAR una mascota
        mascotaId = intent.getIntExtra("MASCOTA_ID", -1)
        if (mascotaId != -1) {
            val mascota = databaseHelper.obtenerMascotaPorId(mascotaId!!)
            mascota?.let {
                etNombre.setText(it.nombre)
                etEspecie.setText(it.especie)
                etRaza.setText(it.raza)
                etEdad.setText(it.edad.toString())
                cbVacunada.isChecked = it.vacunada
            }
        }

        btnGuardar.setOnClickListener {
            guardarMascota()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            ivMascota.setImageURI(selectedImageUri)
            imagenSeleccionada = selectedImageUri.toString() // Guardar la URI
        }
    }

    private fun guardarMascota() {
        val nombre = etNombre.text.toString()
        val especie = etEspecie.text.toString()
        val raza = etRaza.text.toString()
        val edad = etEdad.text.toString().toIntOrNull() ?: 0
        val vacunada = cbVacunada.isChecked
        val imagen = imagenSeleccionada // Obtener la URL de la imagen seleccionada

        if (nombre.isEmpty() || especie.isEmpty() || raza.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (mascotaId == -1) {
            databaseHelper.insertarMascota(nombre, especie, raza, edad, vacunada, imagen)
            Toast.makeText(this, "Mascota agregada", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.actualizarMascota(mascotaId!!, nombre, especie, raza, edad, vacunada, imagen)
            Toast.makeText(this, "Mascota actualizada", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
