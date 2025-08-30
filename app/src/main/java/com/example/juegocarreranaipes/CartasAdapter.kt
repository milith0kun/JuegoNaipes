package com.example.juegocarreranaipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juegocarreranaipes.model.Carta
import com.example.juegocarreranaipes.model.Palo

/**
 * Adaptador optimizado para mostrar las cartas de cada palo
 * Implementa mejores prácticas de rendimiento y gestión de memoria
 * Utiliza DiffUtil para actualizaciones eficientes de la lista
 */
class CartasAdapter(
    private val context: Context,
    private val palo: Palo
): ListAdapter<Carta, CartasAdapter.ViewHolder>(CartaDiffCallback()) {

    // Constantes para los tipos de palo
    companion object {
        private const val PALO_TREBOLES = 1
        private const val PALO_CORAZONES = 2
        private const val PALO_PICAS = 3
        private const val PALO_DIAMANTES = 4
    }

    /**
     * DiffCallback para comparar elementos de la lista de manera eficiente
     */
    class CartaDiffCallback : DiffUtil.ItemCallback<Carta>() {
        override fun areItemsTheSame(oldItem: Carta, newItem: Carta): Boolean {
            return oldItem.valor == newItem.valor && oldItem.palo == newItem.palo
        }

        override fun areContentsTheSame(oldItem: Carta, newItem: Carta): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_cartas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val carta = getItem(position)
        
        if (carta.revelada) {
            cargarCartaEspecifica(holder, carta)
        } else {
            cargarCartaTrasera(holder)
        }
    }

    /**
     * Carga la imagen de una carta específica
     */
    private fun cargarCartaEspecifica(holder: ViewHolder, carta: Carta) {
        val drawableRes = obtenerRecursoDrawable(carta)
        cargarImagenOptimizada(holder.carta, drawableRes)
    }
    
    /**
     * Obtiene el recurso drawable correspondiente a una carta
     */
    private fun obtenerRecursoDrawable(carta: Carta): Int {
        // Si es carta placeholder (valor 0), mostrar carta del palo correspondiente
        if (carta.valor == 0) {
            return when (carta.palo) {
                Palo.TREBOLES -> R.drawable.ac
                Palo.CORAZONES -> R.drawable.ah
                Palo.PICAS -> R.drawable.`as`
                Palo.DIAMANTES -> R.drawable.ad
            }
        }
        
        // MEJORA: Para cartas reales, usar diferentes imágenes según el valor
        return when (carta.palo) {
            Palo.TREBOLES -> {
                when (carta.valor) {
                    12 -> R.drawable.qc // Reina de tréboles
                    else -> R.drawable.ac // As de tréboles para otros valores
                }
            }
            Palo.CORAZONES -> {
                when (carta.valor) {
                    12 -> R.drawable.qh // Reina de corazones
                    else -> R.drawable.ah // As de corazones para otros valores
                }
            }
            Palo.PICAS -> {
                when (carta.valor) {
                    12 -> R.drawable.qs // Reina de picas
                    else -> R.drawable.`as` // As de picas para otros valores
                }
            }
            Palo.DIAMANTES -> {
                when (carta.valor) {
                    12 -> R.drawable.qd // Reina de diamantes
                    else -> R.drawable.ad // As de diamantes para otros valores
                }
            }
        }
    }

    /**
     * Carga la imagen de la carta trasera con el color correspondiente al palo
     */
    private fun cargarCartaTrasera(holder: ViewHolder) {
        val reversoRes = when (palo) {
            Palo.CORAZONES, Palo.DIAMANTES -> R.drawable.red_back // Palos rojos
            Palo.TREBOLES, Palo.PICAS -> R.drawable.grey_back // Palos negros
        }
        cargarImagenOptimizada(holder.carta, reversoRes)
    }

    /**
     * Carga imágenes de manera optimizada usando Glide
     */
    private fun cargarImagenOptimizada(imageView: ImageView, drawableRes: Int) {
        Glide.with(context)
            .load(drawableRes)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache para mejor rendimiento
            .placeholder(R.drawable.red_back) // Imagen por defecto mientras carga
            .error(R.drawable.red_back) // Imagen en caso de error
            .into(imageView)
    }

    /**
     * Actualiza la lista de cartas de manera eficiente
     * Optimizado para evitar copias innecesarias de memoria
     */
    fun actualizarCartas(nuevasCartas: List<Carta>) {
        submitList(ArrayList(nuevasCartas)) // Crear nueva lista optimizada para trigger DiffUtil
    }

    /**
     * ViewHolder optimizado con lazy initialization
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val carta: ImageView by lazy { itemView.findViewById(R.id.ivCarta) }
    }
}