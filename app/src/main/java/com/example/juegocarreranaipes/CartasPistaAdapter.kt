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
 * Adaptador optimizado para mostrar la pista de cartas
 * Implementa mejores prácticas de rendimiento
 */
class CartasPistaAdapter(
    private val context: Context,
    private val lista: ArrayList<String>
): RecyclerView.Adapter<CartasPistaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_cartas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Mostrar cartas grises en los extremos, rojas en el medio
        val drawableRes = if (position == 0 || position == lista.size - 1) {
            R.drawable.grey_back
        } else {
            R.drawable.red_back
        }
        
        cargarImagenOptimizada(holder.carta, drawableRes)
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

    override fun getItemCount(): Int = lista.size

    /**
     * ViewHolder optimizado con lazy initialization
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val carta: ImageView by lazy { itemView.findViewById(R.id.ivCarta) }
    }
}