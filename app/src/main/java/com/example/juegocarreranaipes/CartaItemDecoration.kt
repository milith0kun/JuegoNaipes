package com.example.juegocarreranaipes

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration personalizado para optimizar el espaciado entre cartas
 * Permite una distribución más compacta y uniforme
 */
class CartaItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Aplicar espaciado optimizado
        when (position) {
            // Primera carta: solo margen derecho
            0 -> {
                outRect.right = spacing / 2
            }
            // Última carta: solo margen izquierdo
            itemCount - 1 -> {
                outRect.left = spacing / 2
            }
            // Cartas del medio: margen en ambos lados
            else -> {
                outRect.left = spacing / 2
                outRect.right = spacing / 2
            }
        }
    }
}