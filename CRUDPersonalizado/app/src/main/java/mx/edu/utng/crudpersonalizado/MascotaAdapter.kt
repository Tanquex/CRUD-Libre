package mx.edu.utng.crudpersonalizado

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import android.widget.ImageView
import java.io.File


class MascotaAdapter(
    private var listaMascotas: List<Mascota>,
    private val onItemClick: (Mascota) -> Unit,
    private val onDeleteClick: (Mascota) -> Unit
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    // Creamos una lista mutable para manejar la filtración correctamente
    private var listaMascotasFiltradas: MutableList<Mascota> = listaMascotas.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = listaMascotasFiltradas[position]
        holder.bind(mascota, onItemClick, onDeleteClick)

        // Agregar animación de entrada
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 50f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .start()
    }

    override fun getItemCount(): Int = listaMascotasFiltradas.size

    fun actualizarLista(nuevaLista: List<Mascota>) {
        listaMascotasFiltradas.clear()
        listaMascotasFiltradas.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(mascota: Mascota, onItemClick: (Mascota) -> Unit, onDeleteClick: (Mascota) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvNombre).text = mascota.nombre
            itemView.findViewById<TextView>(R.id.tvEspecie).text = mascota.especie
            itemView.findViewById<TextView>(R.id.tvRaza).text = mascota.raza
            itemView.findViewById<TextView>(R.id.tvEdad).text = mascota.edad.toString()
            itemView.findViewById<TextView>(R.id.tvVacunada).text = if (mascota.vacunada) "Sí" else "No"

            // Cargar imagen con Glide
            val ivMascota = itemView.findViewById<ImageView>(R.id.ivMascotaLista)
            Glide.with(itemView.context)
                .load(mascota.imagen) // Puede ser URL o URI local
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .into(ivMascota)




            itemView.setOnClickListener { onItemClick(mascota) }
            itemView.findViewById<Button>(R.id.btnEliminar).setOnClickListener { onDeleteClick(mascota) }
        }
    }

}
