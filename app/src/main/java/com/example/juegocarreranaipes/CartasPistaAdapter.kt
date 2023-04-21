package com.example.juegocarreranaipes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartasPistaAdapter(
    val context: Context,
    val lista: ArrayList<String>
): RecyclerView.Adapter<CartasPistaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_cartas, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0 || position == lista.size-1) {
            Glide.with(context).load(context.resources.getDrawable(R.drawable.grey_back)).centerInside().into(holder.carta)
        } else {
            Glide.with(context).load(context.resources.getDrawable(R.drawable.red_back)).centerInside().into(holder.carta)
        }

    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var carta = itemView.findViewById(R.id.ivCarta) as ImageView
    }
}