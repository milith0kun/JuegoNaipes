package com.example.juegocarreranaipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartasAdapter(
    val context: Context,
    val listaCartas: ArrayList<String>,
    val tipo: String,
    val onClick: OnItemClicked
): RecyclerView.Adapter<CartasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_cartas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when(tipo) {
            "c" -> Glide.with(context).load(context.resources.getDrawable(R.drawable.`ac`)).centerInside().into(holder.carta)
            "h" -> Glide.with(context).load(context.resources.getDrawable(R.drawable.`ah`)).centerInside().into(holder.carta)
            "s" -> Glide.with(context).load(context.resources.getDrawable(R.drawable.`as`)).centerInside().into(holder.carta)
            "d" -> Glide.with(context).load(context.resources.getDrawable(R.drawable.`ad`)).centerInside().into(holder.carta)
        }

        if (position < listaCartas.size-1) {
            Glide.with(context).load(context.resources.getDrawable(R.drawable.red_back)).centerInside().into(holder.carta)
        } else if(position == 13) {
            onClick.mensajeGanador(tipo)
        }
    }

    override fun getItemCount(): Int {
        return listaCartas.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var carta = itemView.findViewById(R.id.ivCarta) as ImageView
    }

    interface OnItemClicked {
        fun mensajeGanador(tipo: String)
    }
}