package com.example.juegocarreranaipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel para manejar la lógica de negocio del juego de carrera de naipes
 * Separa la lógica de la UI siguiendo el patrón MVVM
 * Utiliza GameRepository como fuente única de verdad
 */
class GameViewModel(private val repository: GameRepository = GameRepository()) : ViewModel() {

    // Estado del juego - LiveData para observar cambios
    private val _treboles = MutableLiveData<List<String>>()
    val treboles: LiveData<List<String>> = _treboles

    private val _corazones = MutableLiveData<List<String>>()
    val corazones: LiveData<List<String>> = _corazones

    private val _corazonesNegros = MutableLiveData<List<String>>()
    val corazonesNegros: LiveData<List<String>> = _corazonesNegros

    private val _diamantes = MutableLiveData<List<String>>()
    val diamantes: LiveData<List<String>> = _diamantes

    private val _pista = MutableLiveData<List<String>>()
    val pista: LiveData<List<String>> = _pista

    private val _movimientos = MutableLiveData<Int>()
    val movimientos: LiveData<Int> = _movimientos

    private val _juegoTerminado = MutableLiveData<Boolean>()
    val juegoTerminado: LiveData<Boolean> = _juegoTerminado

    private val _mostrarVictoria = MutableLiveData<Boolean>()
    val mostrarVictoria: LiveData<Boolean> = _mostrarVictoria

    private val _paloGanador = MutableLiveData<String>()
    val paloGanador: LiveData<String> = _paloGanador

    // Eventos de UI
    private val _eventoNuevaCarta = MutableLiveData<Int?>()
    val eventoNuevaCarta: LiveData<Int?> = _eventoNuevaCarta

    init {
        observarEstadoJuego()
        inicializarJuego()
    }

    /**
     * Observa los cambios en el estado del juego desde el repository
     */
    private fun observarEstadoJuego() {
        repository.gameState
            .onEach { estado ->
                _treboles.value = estado.treboles
                _corazones.value = estado.corazones
                _corazonesNegros.value = estado.corazonesNegros
                _diamantes.value = estado.diamantes
                _pista.value = estado.pista
                _movimientos.value = estado.movimientos
                _juegoTerminado.value = estado.juegoTerminado
                
                if (estado.juegoTerminado && estado.paloGanador != null) {
                    _mostrarVictoria.value = true
                    _paloGanador.value = repository.obtenerNombrePaloGanador(estado.paloGanador)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Inicializa el juego con valores por defecto
     */
    fun inicializarJuego() {
        repository.inicializarJuego()
        _mostrarVictoria.value = false
    }

    /**
     * Procesa la siguiente carta del juego
     */
    fun procesarSiguienteCarta() {
        val cartaAgregada = repository.procesarSiguienteCarta()
        if (cartaAgregada != null) {
            _eventoNuevaCarta.value = cartaAgregada
        } else {
            _mostrarVictoria.value = true
        }
    }

    /**
     * Reinicia el juego a su estado inicial
     */
    fun reiniciarJuego() {
        repository.reiniciarJuego()
        _mostrarVictoria.value = false
    }

    /**
     * Marca que el evento de nueva carta ha sido consumido
     */
    fun eventoNuevaCartaConsumido() {
        _eventoNuevaCarta.value = null
    }

    /**
     * Marca que el evento de victoria ha sido consumido
     */
    fun eventoVictoriaConsumido() {
        _mostrarVictoria.value = false
    }

    /**
     * Obtiene el estado actual del juego
     */
    fun obtenerEstadoActual(): GameRepository.GameState {
        return repository.obtenerEstadoActual()
    }
}