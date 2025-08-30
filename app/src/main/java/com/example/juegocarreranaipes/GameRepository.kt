package com.example.juegocarreranaipes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Repository para manejar el estado del juego de carrera de naipes
 * Centraliza la lógica de datos y proporciona una fuente única de verdad
 */
class GameRepository {

    // Constantes del juego
    companion object {
        const val CARTAS_POR_PALO = 13
        const val CARTAS_INICIALES_PISTA = 24
        const val PALO_TREBOLES = 1
        const val PALO_CORAZONES = 2
        const val PALO_PICAS = 3
        const val PALO_DIAMANTES = 4
    }

    // Estado del juego usando StateFlow para mejor rendimiento
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    /**
     * Data class que representa el estado completo del juego
     */
    data class GameState(
        val treboles: List<String> = listOf("carta"),
        val corazones: List<String> = listOf("carta"),
        val corazonesNegros: List<String> = listOf("carta"),
        val diamantes: List<String> = listOf("carta"),
        val pista: List<String> = List(CARTAS_INICIALES_PISTA) { "carta" },
        val movimientos: Int = 0,
        val juegoTerminado: Boolean = false,
        val paloGanador: Int? = null
    )

    /**
     * Inicializa el juego con valores por defecto
     */
    fun inicializarJuego() {
        _gameState.value = GameState()
    }

    /**
     * Procesa la siguiente carta del juego
     * @return El tipo de carta agregada o null si el juego ya terminó
     */
    fun procesarSiguienteCarta(): Int? {
        val estadoActual = _gameState.value
        
        if (estadoActual.juegoTerminado) {
            return null
        }

        val cartaAleatoria = obtenerCartaAleatoria()
        val nuevoEstado = agregarCartaAPalo(estadoActual, cartaAleatoria)
        val estadoConMovimiento = nuevoEstado.copy(movimientos = estadoActual.movimientos + 1)
        val estadoFinal = verificarVictoria(estadoConMovimiento)
        
        _gameState.value = estadoFinal
        return cartaAleatoria
    }

    /**
     * Agrega una carta al palo correspondiente
     */
    private fun agregarCartaAPalo(estado: GameState, tipoCarta: Int): GameState {
        return when (tipoCarta) {
            PALO_TREBOLES -> estado.copy(
                treboles = estado.treboles + "carta"
            )
            PALO_CORAZONES -> estado.copy(
                corazones = estado.corazones + "carta"
            )
            PALO_PICAS -> estado.copy(
                corazonesNegros = estado.corazonesNegros + "carta"
            )
            PALO_DIAMANTES -> estado.copy(
                diamantes = estado.diamantes + "carta"
            )
            else -> estado
        }
    }

    /**
     * Verifica si algún palo ha alcanzado la condición de victoria
     */
    private fun verificarVictoria(estado: GameState): GameState {
        val paloGanador = when {
            estado.treboles.size >= CARTAS_POR_PALO -> PALO_TREBOLES
            estado.corazones.size >= CARTAS_POR_PALO -> PALO_CORAZONES
            estado.corazonesNegros.size >= CARTAS_POR_PALO -> PALO_PICAS
            estado.diamantes.size >= CARTAS_POR_PALO -> PALO_DIAMANTES
            else -> null
        }

        return if (paloGanador != null) {
            estado.copy(
                juegoTerminado = true,
                paloGanador = paloGanador
            )
        } else {
            estado
        }
    }

    /**
     * Genera una carta aleatoria entre los 4 palos disponibles
     */
    private fun obtenerCartaAleatoria(): Int {
        return Random.nextInt(PALO_TREBOLES, PALO_DIAMANTES + 1)
    }

    /**
     * Reinicia el juego a su estado inicial
     */
    fun reiniciarJuego() {
        inicializarJuego()
    }

    /**
     * Obtiene el estado actual del juego
     */
    fun obtenerEstadoActual(): GameState {
        return _gameState.value
    }

    /**
     * Obtiene el nombre del palo ganador
     */
    fun obtenerNombrePaloGanador(palo: Int): String {
        return when (palo) {
            PALO_TREBOLES -> "Tréboles"
            PALO_CORAZONES -> "Corazones"
            PALO_PICAS -> "Picas"
            PALO_DIAMANTES -> "Diamantes"
            else -> "Desconocido"
        }
    }
}