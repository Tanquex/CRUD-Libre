package mx.edu.utng.crudpersonalizado

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MiBaseDatos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "mascotas"
        private const val ID_MASCOTA = "id"
        private const val NOMBRE_MASCOTA = "nombre"
        private const val ESPECIE_MASCOTA = "especie"
        private const val RAZA_MASCOTA = "raza"
        private const val EDAD_MASCOTA = "edad"
        private const val VACUNADA_MASCOTA = "vacunada"
        private const val IMAGEN_MASCOTA = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $ID_MASCOTA INTEGER PRIMARY KEY AUTOINCREMENT, 
                $NOMBRE_MASCOTA TEXT,
                $ESPECIE_MASCOTA TEXT,
                $RAZA_MASCOTA TEXT,
                $EDAD_MASCOTA INTEGER,
                $VACUNADA_MASCOTA INTEGER,
                $IMAGEN_MASCOTA TEXT
                
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // **Método para insertar una mascota**
    fun insertarMascota(nombre: String, especie: String, raza: String, edad: Int, vacunada: Boolean,imagen: String?): Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(NOMBRE_MASCOTA, nombre)
            put(ESPECIE_MASCOTA, especie)
            put(RAZA_MASCOTA, raza)
            put(EDAD_MASCOTA, edad)
            put(VACUNADA_MASCOTA, if (vacunada) 1 else 0)  // SQLite usa 1 y 0 en lugar de BOOLEAN
            put(IMAGEN_MASCOTA, imagen)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // **Método para obtener todas las mascotas**
    fun obtenerMascotas(): List<Mascota> {
        val listaMascotas = mutableListOf<Mascota>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_MASCOTA))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_MASCOTA))
                val especie = cursor.getString(cursor.getColumnIndexOrThrow(ESPECIE_MASCOTA))
                val raza = cursor.getString(cursor.getColumnIndexOrThrow(RAZA_MASCOTA))
                val edad = cursor.getInt(cursor.getColumnIndexOrThrow(EDAD_MASCOTA))
                val vacunada = cursor.getInt(cursor.getColumnIndexOrThrow(VACUNADA_MASCOTA)) == 1
                val imagen = cursor.getString(cursor.getColumnIndexOrThrow(IMAGEN_MASCOTA))

                listaMascotas.add(Mascota(id, nombre, especie, raza, edad, vacunada,imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaMascotas
    }

    // **Método para actualizar una mascota**
    fun actualizarMascota(id: Int, nombre: String, especie: String, raza: String, edad: Int, vacunada: Boolean,imagen: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NOMBRE_MASCOTA, nombre)
            put(ESPECIE_MASCOTA, especie)
            put(RAZA_MASCOTA, raza)
            put(EDAD_MASCOTA, edad)
            put(VACUNADA_MASCOTA, if (vacunada) 1 else 0)
            put(IMAGEN_MASCOTA, imagen)
        }
        return db.update(TABLE_NAME, values, "$ID_MASCOTA = ?", arrayOf(id.toString()))
    }

    // **Método para eliminar una mascota**
    fun eliminarMascota(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$ID_MASCOTA = ?", arrayOf(id.toString()))
    }
    fun obtenerMascotaPorId(id: Int): Mascota? {
        val db = this.readableDatabase
        var mascota: Mascota? = null
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_MASCOTA = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_MASCOTA))
            val especie = cursor.getString(cursor.getColumnIndexOrThrow(ESPECIE_MASCOTA))
            val raza = cursor.getString(cursor.getColumnIndexOrThrow(RAZA_MASCOTA))
            val edad = cursor.getInt(cursor.getColumnIndexOrThrow(EDAD_MASCOTA))
            val vacunada = cursor.getInt(cursor.getColumnIndexOrThrow(VACUNADA_MASCOTA)) == 1
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow(IMAGEN_MASCOTA))

            mascota = Mascota(id, nombre, especie, raza, edad, vacunada, imagen)
        }
        cursor.close()
        return mascota
    }

}
