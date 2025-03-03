package mx.edu.utng.crudpersonalizado

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewMascotas: RecyclerView
    private lateinit var adapter: MascotaAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnAgregarMascota: Button
    private lateinit var searchViewMascotas: SearchView
    private var listaCompletaMascotas: List<Mascota> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewMascotas = findViewById(R.id.recyclerViewMascotas)
        btnAgregarMascota = findViewById(R.id.btnAgregarMascota)
        searchViewMascotas = findViewById(R.id.searchViewMascotas)

        recyclerViewMascotas.layoutManager = LinearLayoutManager(this)
        databaseHelper = DatabaseHelper(this)

        cargarMascotas()

        btnAgregarMascota.setOnClickListener {
            val intent = Intent(this, FormularioMascotaActivity::class.java)
            startActivity(intent)
        }

        // Filtrar mascotas a medida que se escribe en el buscador
        searchViewMascotas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarMascotas(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarMascotas(newText ?: "")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        cargarMascotas()
    }

    private fun cargarMascotas() {
        listaCompletaMascotas = databaseHelper.obtenerMascotas()
        adapter = MascotaAdapter(listaCompletaMascotas, { mascota ->
            val intent = Intent(this, FormularioMascotaActivity::class.java)
            intent.putExtra("MASCOTA_ID", mascota.id)
            startActivity(intent)
        }, { mascota ->
            confirmarEliminacion(mascota)
        })
        recyclerViewMascotas.adapter = adapter
    }

    private fun filtrarMascotas(texto: String) {
        val listaFiltrada = listaCompletaMascotas.filter { it.nombre.contains(texto, ignoreCase = true) || it.especie.contains(texto, ignoreCase = true) }
        adapter.actualizarLista(listaFiltrada)
    }

    private fun confirmarEliminacion(mascota: Mascota) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Mascota")
            .setMessage("¿Estás seguro de que deseas eliminar a ${mascota.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                databaseHelper.eliminarMascota(mascota.id)
                Toast.makeText(this, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                cargarMascotas()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun guardarImagen(bitmap: Bitmap, context: Context): String {
        val fileName = "mascota_${System.currentTimeMillis()}.jpg"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file.absolutePath  // Retorna la ruta de la imagen guardada
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}
