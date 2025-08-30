package com.example.juegocarreranaipes.model

import kotlin.random.Random

/**
 * Clase responsable de generar y manejar la baraja de cartas con aleatorización mejorada
 */
class GeneradorBaraja {
    
    companion object {
        // Semilla aleatoria basada en tiempo del sistema y factores adicionales
        private fun obtenerSemillaRobusta(): Long {
            val tiempoActual = System.currentTimeMillis()
            val nanoTiempo = System.nanoTime()
            val hashCode = Thread.currentThread().hashCode()
            return tiempoActual xor nanoTiempo xor hashCode.toLong()
        }
        
        /**
         * Genera una baraja completa de 52 cartas con aleatorización mejorada
         */
        fun generarBarajaMezclada(): MutableList<Carta> {
            val baraja = mutableListOf<Carta>()
            
            // Crear todas las cartas (13 valores x 4 palos = 52 cartas)
            Palo.values().forEach { palo ->
                for (valor in 1..13) {
                    baraja.add(Carta(valor, palo, revelada = false))
                }
            }
            
            // Aplicar múltiples métodos de barajeo para mayor aleatorización
            aplicarBarajeoAvanzado(baraja)
            
            return baraja
        }
        
        /**
         * Aplica múltiples algoritmos de barajeo para máxima aleatorización
         */
        private fun aplicarBarajeoAvanzado(baraja: MutableList<Carta>) {
            val random = Random(obtenerSemillaRobusta())
            
            // 1. Barajeo Fisher-Yates mejorado
            for (i in baraja.size - 1 downTo 1) {
                val j = random.nextInt(i + 1)
                val temp = baraja[i]
                baraja[i] = baraja[j]
                baraja[j] = temp
            }
            
            // 2. Barajeo por bloques aleatorios
            val bloqueSize = random.nextInt(3, 8) // Bloques de 3-7 cartas
            for (i in 0 until baraja.size step bloqueSize) {
                val fin = minOf(i + bloqueSize, baraja.size)
                val subLista = baraja.subList(i, fin)
                subLista.shuffle(random)
            }
            
            // 3. Intercambios aleatorios adicionales
            repeat(random.nextInt(20, 40)) {
                val pos1 = random.nextInt(baraja.size)
                val pos2 = random.nextInt(baraja.size)
                val temp = baraja[pos1]
                baraja[pos1] = baraja[pos2]
                baraja[pos2] = temp
            }
            
            // 4. Barajeo final con shuffle nativo
            baraja.shuffle(random)
        }
        
        /**
         * Genera cartas iniciales para cada palo (boca abajo)
         */
        fun generarCartasIniciales(cantidad: Int = 3): Map<Palo, MutableList<Carta>> {
            val cartasIniciales = mutableMapOf<Palo, MutableList<Carta>>()
            
            Palo.values().forEach { palo ->
                val cartas = mutableListOf<Carta>()
                repeat(cantidad) {
                    // Cartas placeholder boca abajo para visualización inicial
                    cartas.add(Carta(0, palo, revelada = false))
                }
                cartasIniciales[palo] = cartas
            }
            
            return cartasIniciales
        }
        
        /**
         * Crea una pista con un número específico de cartas de la baraja
         */
        fun crearPista(baraja: MutableList<Carta>, cantidadCartas: Int = 24): MutableList<Carta> {
            val pista = mutableListOf<Carta>()
            
            // Tomar las primeras cartas de la baraja mezclada para la pista
            repeat(minOf(cantidadCartas, baraja.size)) {
                if (baraja.isNotEmpty()) {
                    pista.add(baraja.removeAt(0))
                }
            }
            
            return pista
        }
        
        /**
         * Selecciona un palo aleatorio con distribución mejorada
         */
        fun seleccionarPaloAleatorio(palosDisponibles: List<Palo>): Palo {
            val random = Random(obtenerSemillaRobusta())
            
            // Aplicar múltiples selecciones para mayor aleatoriedad
            var paloSeleccionado = palosDisponibles.random(random)
            
            // Segundo nivel de aleatorización
            repeat(random.nextInt(1, 4)) {
                paloSeleccionado = palosDisponibles.random(random)
            }
            
            return paloSeleccionado
        }
        
        /**
         * Genera una distribución más equilibrada de cartas por color
         */
        fun generarBarajaEquilibrada(): MutableList<Carta> {
            val baraja = mutableListOf<Carta>()
            val random = Random(obtenerSemillaRobusta())
            
            // Crear cartas por color de forma intercalada para mejor distribución
            val palosRojos = listOf(Palo.CORAZONES, Palo.DIAMANTES)
            val palosNegros = listOf(Palo.TREBOLES, Palo.PICAS)
            
            for (valor in 1..13) {
                // Alternar entre colores para mejor distribución
                if (random.nextBoolean()) {
                    palosRojos.forEach { palo -> baraja.add(Carta(valor, palo, revelada = false)) }
                    palosNegros.forEach { palo -> baraja.add(Carta(valor, palo, revelada = false)) }
                } else {
                    palosNegros.forEach { palo -> baraja.add(Carta(valor, palo, revelada = false)) }
                    palosRojos.forEach { palo -> baraja.add(Carta(valor, palo, revelada = false)) }
                }
            }
            
            // Aplicar barajeo avanzado
            aplicarBarajeoAvanzado(baraja)
            
            return baraja
        }
    }
}