package com.example.juegocarreranaipes.model

/**
 * Representa una carta de la baraja con su palo y valor específico
 */
data class Carta(
    val valor: Int, // 1-13 (As=1, J=11, Q=12, K=13)
    val palo: Palo,
    var revelada: Boolean = false // Si la carta está boca arriba o boca abajo
) {
    /**
     * Obtiene el nombre del valor de la carta
     */
    fun obtenerNombreValor(): String {
        return when (valor) {
            1 -> "A"
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            else -> valor.toString()
        }
    }

    /**
     * Obtiene el recurso drawable correspondiente a esta carta
     */
    fun obtenerRecursoDrawable(): String {
        return if (revelada) {
            "${obtenerNombreValor().lowercase()}_${palo.nombre.lowercase()}"
        } else {
            "red_back" // Carta boca abajo
        }
    }

    /**
     * Revela la carta (la pone boca arriba)
     */
    fun revelar() {
        revelada = true
    }

    /**
     * Obtiene el color de la carta (rojo o negro)
     */
    fun obtenerColor(): String {
        return palo.color
    }

    /**
     * Verifica si la carta es roja
     */
    fun esRoja(): Boolean {
        return palo.color == "rojo"
    }

    /**
     * Verifica si la carta es negra
     */
    fun esNegra(): Boolean {
        return palo.color == "negro"
    }
}

/**
 * Enum que representa los palos de las cartas
 */
enum class Palo(val id: Int, val nombre: String, val color: String) {
    TREBOLES(1, "treboles", "negro"),
    CORAZONES(2, "corazones", "rojo"),
    PICAS(3, "picas", "negro"),
    DIAMANTES(4, "diamantes", "rojo");

    companion object {
        /**
         * Obtiene un palo por su ID
         */
        fun obtenerPorId(id: Int): Palo {
            return values().find { it.id == id } ?: TREBOLES
        }

        /**
         * Obtiene un palo aleatorio
         */
        fun obtenerAleatorio(): Palo {
            return values().random()
        }
    }
}