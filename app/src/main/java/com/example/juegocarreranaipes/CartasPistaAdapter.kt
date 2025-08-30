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

/**
 * Adaptador optimizado para mostrar la pista de cartas con carga eficiente de imágenes
 * Utiliza DiffUtil para actualizaciones eficientes de la lista
 */
class CartasPistaAdapter(
    private val context: Context,
    private val onCartaClick: (Carta, Int) -> Unit = { _, _ -> }
) : ListAdapter<Carta, CartasPistaAdapter.ViewHolder>(PistaDiffCallback()) {

    /**
     * DiffCallback para comparar elementos de la pista de manera eficiente
     */
    class PistaDiffCallback : DiffUtil.ItemCallback<Carta>() {
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
        
        val drawableRes = if (carta.revelada) {
            // Carta revelada - mostrar carta específica
            obtenerRecursoCartaEspecifica(carta)
        } else {
            // Carta no revelada - mostrar dorso con color correspondiente al palo
            when (carta.palo) {
                com.example.juegocarreranaipes.model.Palo.CORAZONES, 
                com.example.juegocarreranaipes.model.Palo.DIAMANTES -> R.drawable.red_back // Palos rojos
                com.example.juegocarreranaipes.model.Palo.TREBOLES, 
                com.example.juegocarreranaipes.model.Palo.PICAS -> R.drawable.grey_back // Palos negros
            }
        }
        
        cargarImagenOptimizada(holder.carta, drawableRes)
        
        // Configurar click listener solo para cartas reveladas
        holder.itemView.setOnClickListener {
            if (carta.revelada) {
                onCartaClick(carta, position)
            }
        }
    }
    
    /**
     * Obtiene el recurso drawable para una carta específica
     */
    private fun obtenerRecursoCartaEspecifica(carta: Carta): Int {
        // MEJORA: Usar diferentes imágenes según el valor de la carta
        return when (carta.palo) {
            com.example.juegocarreranaipes.model.Palo.TREBOLES -> {
                when (carta.valor) {
                    12 -> R.drawable.qc // Reina de tréboles
                    else -> R.drawable.ac // As de tréboles para otros valores
                }
            }
            com.example.juegocarreranaipes.model.Palo.CORAZONES -> {
                when (carta.valor) {
                    12 -> R.drawable.qh // Reina de corazones
                    else -> R.drawable.ah // As de corazones para otros valores
                }
            }
            com.example.juegocarreranaipes.model.Palo.PICAS -> {
                when (carta.valor) {
                    12 -> R.drawable.qs // Reina de picas
                    else -> R.drawable.`as` // As de picas para otros valores
                }
            }
            com.example.juegocarreranaipes.model.Palo.DIAMANTES -> {
                when (carta.valor) {
                    12 -> R.drawable.qd // Reina de diamantes
                    else -> R.drawable.ad // As de diamantes para otros valores
                }
            }
        }
    }

    /**
     * Carga imágenes de manera optimizada usando Glide
     */
    private fun cargarImagenOptimizada(imageView: ImageView, drawableRes: Int) {
        Glide.with(context)
            .load(drawableRes)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache para mejor rendimiento
            .into(imageView)
    }

    /**
     * Actualiza la pista con nuevas cartas de manera eficiente
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