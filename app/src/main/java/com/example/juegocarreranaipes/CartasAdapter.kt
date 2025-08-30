package com.example.juegocarreranaipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Adaptador optimizado para mostrar las cartas de cada palo
 * Implementa mejores prácticas de rendimiento y gestión de memoria
 */
class CartasAdapter(
    private val context: Context,
    private val lista: ArrayList<String>,
    private val tipo: Int
): RecyclerView.Adapter<CartasAdapter.ViewHolder>() {

    // Constantes para los tipos de palo
    companion object {
        private const val PALO_TREBOLES = 1
        private const val PALO_CORAZONES = 2
        private const val PALO_PICAS = 3
        private const val PALO_DIAMANTES = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_cartas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Mostrar carta frontal en la última posición, trasera en las demás
        if (position == lista.size - 1) {
            cargarCartaFrontal(holder, tipo)
        } else {
            cargarCartaTrasera(holder)
        }
    }

    /**
     * Carga la imagen de la carta frontal según el tipo de palo
     */
    private fun cargarCartaFrontal(holder: ViewHolder, tipoPalo: Int) {
        val drawableRes = when (tipoPalo) {
            PALO_TREBOLES -> R.drawable.ac
            PALO_CORAZONES -> R.drawable.ah
            PALO_PICAS -> R.drawable.`as`
            PALO_DIAMANTES -> R.drawable.ad
            else -> R.drawable.red_back
        }
        
        cargarImagenOptimizada(holder.carta, drawableRes)
    }

    /**
     * Carga la imagen de la carta trasera
     */
    private fun cargarCartaTrasera(holder: ViewHolder) {
        cargarImagenOptimizada(holder.carta, R.drawable.red_back)
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

    override fun getItemCount(): Int = lista.size

    /**
     * ViewHolder optimizado con lazy initialization
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val carta: ImageView by lazy { itemView.findViewById(R.id.ivCarta) }
    }
}