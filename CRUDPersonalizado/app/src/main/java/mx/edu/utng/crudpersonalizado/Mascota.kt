package mx.edu.utng.crudpersonalizado

data class Mascota(
    val id: Int,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int,
    val vacunada: Boolean,
    val imagen:String
)