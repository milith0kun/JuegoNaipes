package com.example.juegocarreranaipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.juegocarreranaipes.databinding.ActivityMainBinding
import com.example.juegocarreranaipes.model.Carta
import com.example.juegocarreranaipes.model.GeneradorBaraja
import com.example.juegocarreranaipes.model.Palo

class MainActivity : AppCompatActivity() {

    // Constantes del juego
    companion object {
        private const val CARTAS_POR_PALO = 13
        private const val ESPACIADO_CARTAS = 8 // Espaciado entre cartas en dp
        // Constantes para animaciones optimizadas
        private const val DURACION_ANIMACION_CARTA = 150L // Reducido para mejor rendimiento
        private const val DURACION_ANIMACION_TEXTO = 100L // Reducido para mejor rendimiento
        private const val DURACION_ANIMACION_REINICIO_FADE = 100L // Reducido para mejor rendimiento
        private const val DURACION_ANIMACION_FADE_IN = 150L // Reducido para mejor rendimiento
        private const val DURACION_ANIMACION_CELEBRACION = 300L // Reducido para mejor rendimiento
    }

    // ViewBinding para acceso eficiente a las vistas
    private lateinit var binding: ActivityMainBinding

    // Variables para las cartas de cada palo usando el nuevo modelo
    private val treboles = mutableListOf<Carta>()
    private val corazones = mutableListOf<Carta>()
    private val picas = mutableListOf<Carta>()
    private val diamantes = mutableListOf<Carta>()
    private val pista = mutableListOf<Carta>()
    
    // Baraja principal para el juego progresivo
    private lateinit var barajaCompleta: MutableList<Carta>
    private var indicePistaActual = 0

    // Variables para el manejo de movimientos
    private var movimientos = 0
    private var juegoTerminado = false
    private var procesandoCarta = false

    // Variables para los adaptadores
    private lateinit var adapterTreboles: CartasAdapter
    private lateinit var adapterCorazones: CartasAdapter
    private lateinit var adapterPicas: CartasAdapter
    private lateinit var adapterDiamantes: CartasAdapter
    private lateinit var adapterPista: CartasPistaAdapter
    
    // Vibrador para feedback háptico (puede ser null si no está disponible)
    private var vibrator: Vibrator? = null
    
    // Gestor de audio para sonidos y música
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar pantalla completa inmersiva para evitar elementos del sistema
        configurarPantallaCompleta()
        
        // Inicializar vibrador para feedback háptico (manejo seguro de null)
        vibrator = ContextCompat.getSystemService(this, Vibrator::class.java)
        
        // Inicializar gestor de audio
        audioManager = AudioManager(this)
        lifecycle.addObserver(audioManager)

        iniciarJuego()
        configurarEventos()
    }

    /**
     * Configura los eventos de los botones con feedback mejorado
     */
    private fun configurarEventos() {
        binding.btnSiguiente.setOnClickListener {
            if (!juegoTerminado) {
                proporcionarFeedbackBoton(it as com.google.android.material.button.MaterialButton)
                procesarSiguienteCarta()
            }
        }

        binding.btnJuegoNuevo.setOnClickListener {
            proporcionarFeedbackBoton(it as com.google.android.material.button.MaterialButton)
            iniciarJuego()
        }

        binding.btnInfo.setOnClickListener {
            proporcionarFeedbackBoton(it as com.google.android.material.button.MaterialButton)
            mostrarInstrucciones()
        }
    }

    /**
     * Procesa la siguiente carta del juego tomándola de la pista
     */
    private fun procesarSiguienteCarta() {
        if (juegoTerminado || procesandoCarta) {
            return
        }
        
        // Verificar si hay cartas disponibles en la pista
        if (indicePistaActual >= pista.size) {
            // No hay más cartas en la pista
            mostrarMensaje("No hay más cartas disponibles")
            return
        }
        
        // Reproducir sonido de movimiento de carta
        audioManager.playCardMove()
        
        // Activar estado de procesamiento
        procesandoCarta = true
        binding.btnSiguiente.isEnabled = false
        binding.btnSiguiente.text = getString(R.string.procesando_carta)
        
        // Procesar carta inmediatamente sin delay artificial
        // Tomar la siguiente carta de la pista
        val cartaSeleccionada = pista[indicePistaActual]
        indicePistaActual++
            
            // En un juego de carrera, cada carta avanza su palo correspondiente
            // Mostrar qué palo avanzó en el header con símbolo
            val nombrePalo = when (cartaSeleccionada.palo) {
                Palo.TREBOLES -> "Tréboles ♣"
                Palo.CORAZONES -> "Corazones ♥"
                Palo.PICAS -> "Picas ♠"
                Palo.DIAMANTES -> "Diamantes ♦"
            }
            
            // Mostrar información completa de la carta en el header
            val infoCartaHeader = "${cartaSeleccionada.valor} de ${nombrePalo}"
            actualizarHeaderConCarta(infoCartaHeader, cartaSeleccionada.palo)
            
            // Agregar la carta a su palo correspondiente
            // Solo la primera carta (posición 0) debe estar revelada, las demás boca abajo
            when (cartaSeleccionada.palo) {
                Palo.TREBOLES -> {
                    // Agregar la nueva carta al principio (izquierda)
                    treboles.add(0, cartaSeleccionada)
                    // Solo la última carta (As) debe estar revelada, las demás boca abajo
                    treboles.forEachIndexed { index, carta ->
                        carta.revelada = (index == treboles.size - 1)
                    }
                    adapterTreboles.actualizarCartas(treboles) // Evitar copia innecesaria
                    animarCarta(binding.rvTreboles)
                }
                Palo.CORAZONES -> {
                    // Agregar la nueva carta al principio (izquierda)
                    corazones.add(0, cartaSeleccionada)
                    // Solo la última carta (As) debe estar revelada, las demás boca abajo
                    corazones.forEachIndexed { index, carta ->
                        carta.revelada = (index == corazones.size - 1)
                    }
                    adapterCorazones.actualizarCartas(corazones) // Evitar copia innecesaria
                    animarCarta(binding.rvCorazones)
                }
                Palo.PICAS -> {
                    // Agregar la nueva carta al principio (izquierda)
                    picas.add(0, cartaSeleccionada)
                    // Solo la última carta (As) debe estar revelada, las demás boca abajo
                    picas.forEachIndexed { index, carta ->
                        carta.revelada = (index == picas.size - 1)
                    }
                    adapterPicas.actualizarCartas(picas) // Evitar copia innecesaria
                    animarCarta(binding.rvPicas)
                }
                Palo.DIAMANTES -> {
                    // Agregar la nueva carta al principio (izquierda)
                    diamantes.add(0, cartaSeleccionada)
                    // Solo la última carta (As) debe estar revelada, las demás boca abajo
                    diamantes.forEachIndexed { index, carta ->
                        carta.revelada = (index == diamantes.size - 1)
                    }
                    adapterDiamantes.actualizarCartas(diamantes) // Evitar copia innecesaria
                    animarCarta(binding.rvDiamantes)
                }
            }
            
            // Actualizar adaptador de la pista - mostrar solo cartas restantes
            val cartasRestantes = pista.drop(indicePistaActual)
            adapterPista.actualizarCartas(cartasRestantes)
            
            // Actualizar el estado del juego
            actualizarEstadoJuego()
            
        // Restaurar estado del botón inmediatamente
        procesandoCarta = false
        binding.btnSiguiente.isEnabled = !juegoTerminado
        binding.btnSiguiente.text = if (!juegoTerminado) {
            getString(R.string.msg_btn_siguiente)
        } else {
            "Juego Terminado"
        }
    }

    /**
     * Mueve una carta revelada al palo correspondiente solo si es la siguiente en secuencia
     */
    // Función eliminada: moverCartaAPalo ya no es necesaria con la nueva lógica simplificada

    // Función eliminada: actualizarVistasPista ya no es necesaria con la nueva lógica
    
    // Función eliminada: moverCartaDesdePista ya no es necesaria con la nueva lógica simplificada
    
    /**
     * Actualiza el estado del juego después de agregar una carta
     */
    private fun actualizarEstadoJuego() {
        movimientos++
        actualizarIndicadorCarrera()
        
        // Verificar si algún palo ha alcanzado exactamente 13 cartas (condición de victoria)
        val ganador = when {
            treboles.size == CARTAS_POR_PALO -> "Tréboles"
            corazones.size == CARTAS_POR_PALO -> "Corazones"
            picas.size == CARTAS_POR_PALO -> "Picas"
            diamantes.size == CARTAS_POR_PALO -> "Diamantes"
            else -> null
        }
        
        if (ganador != null) {
            juegoTerminado = true
            mostrarMensajeGanador(ganador)
        }
        
        // Verificar si se han agotado las cartas de la pista sin ganador
        if (indicePistaActual >= pista.size && ganador == null) {
            juegoTerminado = true
            mostrarMensaje("¡Empate! Se agotaron las cartas sin ganador")
        }
    }



    /**
     * Actualiza el contador de movimientos en la UI
     */
    /**
     * Actualiza el indicador de la carrera
     */
    private fun actualizarIndicadorCarrera() {
        val cartasRestantes = pista.size - indicePistaActual
        val progreso = "Movimiento $movimientos - Cartas restantes: $cartasRestantes"
        binding.tvMovimientos.text = progreso
        binding.tvMovimientos.setTextColor(ContextCompat.getColor(this, R.color.negro_cartas))
        animarTexto(binding.tvMovimientos)
    }
    
    /**
     * Actualiza el header con información de la carta que salió
     */
    private fun actualizarHeaderConCarta(infoCarta: String, palo: Palo) {
        // Log para depuración
        android.util.Log.d("MainActivity", "Actualizando header con: $infoCarta")
        
        binding.tvCartaActual.text = infoCarta
        
        // Cambiar color del texto según el palo de la carta
        val colorTexto = when (palo) {
            Palo.TREBOLES, Palo.PICAS -> R.color.negro_cartas // Negro para tréboles y picas
            Palo.CORAZONES, Palo.DIAMANTES -> R.color.rojo_cartas // Rojo para corazones y diamantes
        }
        
        // Cambiar imagen de la carta según el color del palo
        val imagenCarta = when (palo) {
            Palo.TREBOLES, Palo.PICAS -> R.drawable.grey_back // Imagen gris para palos negros
            Palo.CORAZONES, Palo.DIAMANTES -> R.drawable.red_back // Imagen roja para palos rojos
        }
        
        binding.tvCartaActual.setTextColor(ContextCompat.getColor(this, colorTexto))
        binding.cartasPozo.setImageResource(imagenCarta)
        animarTexto(binding.tvCartaActual)
        
        // Log adicional para verificar que el texto se estableció
        android.util.Log.d("MainActivity", "Texto del header después de actualizar: ${binding.tvCartaActual.text}")
    }

    /**
     * Inicializa el juego con estado limpio y configuración optimizada
     */
    private fun iniciarJuego() {
        // Reproducir sonido de inicio de juego
        audioManager.playGameStart()
        
        // Iniciar música de fondo
        audioManager.startBackgroundMusic()
        
        reiniciarEstadoJuego()
        animarReinicio()
        inicializarCartasDelJuego()
        configurarRecyclerViews()
    }

    /**
     * Reinicia el estado del juego a valores iniciales
     */
    private fun reiniciarEstadoJuego() {
        limpiarTodasLasCartas()
        movimientos = 0
        juegoTerminado = false
        procesandoCarta = false
        indicePistaActual = 0
        
        // Restaurar estado de los botones
        binding.btnSiguiente.isEnabled = true
        binding.btnSiguiente.text = getString(R.string.msg_btn_siguiente)
        
        actualizarIndicadorCarrera()
    }

    /**
     * Inicializa todas las cartas del juego (palos y pista)
     */
    private fun inicializarCartasDelJuego() {
        // Limpiar todas las listas primero
        limpiarTodasLasCartas()
        
        // Reproducir sonido de barajado
        audioManager.playShuffle()
        
        // Generar baraja completa mezclada
        barajaCompleta = GeneradorBaraja.generarBarajaMezclada()
        
        // Crear pista con cartas de la baraja (todas boca abajo inicialmente)
        val cartasPista = GeneradorBaraja.crearPista(barajaCompleta, 52)
        pista.addAll(cartasPista)
        
        // Reiniciar índice de pista
        indicePistaActual = 0
    }

    /**
     * Función eliminada: inicializarCartasPlaceholder ya no es necesaria
     */

    /**
     * Limpia todas las listas de cartas de manera eficiente
     */
    private fun limpiarTodasLasCartas() {
        treboles.clear()
        corazones.clear()
        picas.clear()
        diamantes.clear()
        pista.clear()
    }

    // Función eliminada: inicializarCartasIniciales ya no es necesaria con la nueva lógica

    /**
     * Configura todos los RecyclerViews con sus adaptadores y decoraciones
     */
    private fun configurarRecyclerViews() {
        configurarRecyclerViewPalo(binding.rvTreboles, treboles, Palo.TREBOLES) { adapterTreboles = it }
        configurarRecyclerViewPalo(binding.rvCorazones, corazones, Palo.CORAZONES) { adapterCorazones = it }
        configurarRecyclerViewPalo(binding.rvPicas, picas, Palo.PICAS) { adapterPicas = it }
        configurarRecyclerViewPalo(binding.rvDiamantes, diamantes, Palo.DIAMANTES) { adapterDiamantes = it }
        configurarRecyclerViewPista()
    }

    /**
     * Configura un RecyclerView para un palo específico
     */
    private fun configurarRecyclerViewPalo(
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        lista: MutableList<Carta>,
        palo: Palo,
        setAdapter: (CartasAdapter) -> Unit
    ) {
        val adapter = CartasAdapter(this, palo)
        adapter.actualizarCartas(lista) // Usar el método para actualizar la lista
        setAdapter(adapter)
        recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            addItemDecoration(CartaItemDecoration(ESPACIADO_CARTAS))
        }
    }

    /**
     * Configura el RecyclerView de la pista
     */
    private fun configurarRecyclerViewPista() {
        binding.rvPista.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterPista = CartasPistaAdapter(this) { carta, position ->
            // Lógica para seleccionar carta de la pista
            seleccionarCartaDePista(carta)
        }
        // Mostrar todas las cartas al inicio
        val cartasRestantes = pista.drop(indicePistaActual)
        adapterPista.actualizarCartas(cartasRestantes)
        binding.rvPista.adapter = adapterPista
    }
    
    /**
     * Selecciona una carta de la pista para moverla a un palo
     */
    private fun seleccionarCartaDePista(carta: Carta) {
        if (juegoTerminado || procesandoCarta) {
            return
        }
        
        // Verificar si la carta ya está revelada
        if (!carta.revelada) {
            mostrarMensaje("Esta carta no está disponible")
            return
        }
        
        // Mostrar opciones de palo para mover la carta
        mostrarOpcionesPalo(carta)
    }
    
    /**
     * Muestra las opciones de palo para mover una carta
     */
    private fun mostrarOpcionesPalo(carta: Carta) {
        val opciones = arrayOf("Tréboles", "Corazones", "Picas", "Diamantes")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar palo")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> moverCartaAPalo(carta, treboles, adapterTreboles, binding.rvTreboles)
                1 -> moverCartaAPalo(carta, corazones, adapterCorazones, binding.rvCorazones)
                2 -> moverCartaAPalo(carta, picas, adapterPicas, binding.rvPicas)
                3 -> moverCartaAPalo(carta, diamantes, adapterDiamantes, binding.rvDiamantes)
            }
        }
        builder.show()
    }
    
    /**
     * Mueve una carta a un palo específico
     */
    private fun moverCartaAPalo(
        carta: Carta,
        listaPalo: MutableList<Carta>,
        adapter: CartasAdapter,
        recyclerView: androidx.recyclerview.widget.RecyclerView
    ) {
        // Reproducir sonido de colocación de carta
        audioManager.playCardPlace()
        
        // Remover la carta de la pista
        pista.remove(carta)
        
        // Agregar la carta al palo al principio (izquierda)
        listaPalo.add(0, carta)
        // Solo la última carta (As) debe estar revelada, las demás boca abajo
        listaPalo.forEachIndexed { index, cartaEnLista ->
            cartaEnLista.revelada = (index == listaPalo.size - 1)
        }
        
        // Actualizar adaptadores
         adapter.actualizarCartas(listaPalo.toList())
         // Actualizar pista mostrando solo cartas restantes
         val cartasRestantes = pista.drop(indicePistaActual)
         adapterPista.actualizarCartas(cartasRestantes)
        
        // Animar la carta
        animarCarta(recyclerView)
        
        // Actualizar estado del juego
        actualizarEstadoJuego()
    }
    
    /**
     * Muestra un mensaje temporal
     */
    private fun mostrarMensaje(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    /**
     * Muestra el mensaje de victoria con estadísticas detalladas del juego
     */
    private fun mostrarMensajeGanador(ganador: String) {
        // Reproducir sonido de victoria
        audioManager.playVictory()
        
        // Animación de celebración
        animarCelebracion()
        
        // Calcular estadísticas del juego
        val cartasRestantes = pista.size - indicePistaActual
        val eficiencia = if (movimientos > 0) ((CARTAS_POR_PALO.toFloat() / movimientos) * 100).toInt() else 0
        val paloGanadorEmoji = when (ganador) {
            "Tréboles" -> "♣️"
            "Corazones" -> "♥️"
            "Picas" -> "♠️"
            "Diamantes" -> "♦️"
            else -> "🎯"
        }
        
        // Crear mensaje detallado con estadísticas
        val mensajeDetallado = StringBuilder()
        mensajeDetallado.append("🏆 ¡FELICITACIONES! 🏆\n\n")
        mensajeDetallado.append("$paloGanadorEmoji $ganador ha ganado la carrera! $paloGanadorEmoji\n\n")
        mensajeDetallado.append("📊 ESTADÍSTICAS DEL JUEGO:\n")
        mensajeDetallado.append("• Movimientos realizados: $movimientos\n")
        mensajeDetallado.append("• Cartas restantes en pista: $cartasRestantes\n")
        mensajeDetallado.append("• Eficiencia del juego: $eficiencia%\n")
        mensajeDetallado.append("• Cartas del palo ganador: ${CARTAS_POR_PALO}\n\n")
        
        // Mensaje de rendimiento
        val mensajeRendimiento = when {
            movimientos <= 15 -> "🌟 ¡Excelente! Juego muy eficiente"
            movimientos <= 25 -> "👍 ¡Buen trabajo! Rendimiento sólido"
            movimientos <= 35 -> "😊 ¡No está mal! Puedes mejorar"
            else -> "💪 ¡Sigue practicando! La suerte mejorará"
        }
        mensajeDetallado.append(mensajeRendimiento)
        mensajeDetallado.append("\n\n¿Quieres intentar superar este resultado?")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🎉 ¡VICTORIA! 🎉")
        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("🎮 Nuevo Juego") { _, _ ->
            iniciarJuego()
        }
        builder.setNegativeButton("📊 Ver Estadísticas") { _, _ ->
            mostrarEstadisticasDetalladas(ganador, movimientos, eficiencia)
        }
        builder.setNeutralButton("🚪 Salir") { _, _ ->
            finish()
        }
        builder.setCancelable(false) // Evita cerrar accidentalmente
        
        val dialog = builder.create()
        dialog.show()
        
        // Personalizar colores de los botones
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(this, R.color.verde_mesa)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(this, R.color.negro_cartas)
        )
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(
            ContextCompat.getColor(this, R.color.rojo_cartas)
        )
    }

    /**
     * Muestra estadísticas detalladas del juego en una ventana separada
     */
    private fun mostrarEstadisticasDetalladas(ganador: String, movimientos: Int, eficiencia: Int) {
        val cartasJugadas = indicePistaActual
        val cartasRestantes = pista.size - indicePistaActual
        val porcentajeCompletado = if (pista.size > 0) ((cartasJugadas.toFloat() / pista.size) * 100).toInt() else 0
        
        // Estadísticas por palo
        val estadisticasPalos = StringBuilder()
        estadisticasPalos.append("📈 DISTRIBUCIÓN POR PALOS:\n\n")
        estadisticasPalos.append("♣️ Tréboles: ${treboles.size}/${CARTAS_POR_PALO} cartas\n")
        estadisticasPalos.append("♥️ Corazones: ${corazones.size}/${CARTAS_POR_PALO} cartas\n")
        estadisticasPalos.append("♠️ Picas: ${picas.size}/${CARTAS_POR_PALO} cartas\n")
        estadisticasPalos.append("♦️ Diamantes: ${diamantes.size}/${CARTAS_POR_PALO} cartas\n\n")
        
        estadisticasPalos.append("🎯 RESUMEN GENERAL:\n")
        estadisticasPalos.append("• Palo ganador: $ganador\n")
        estadisticasPalos.append("• Total de movimientos: $movimientos\n")
        estadisticasPalos.append("• Cartas procesadas: $cartasJugadas\n")
        estadisticasPalos.append("• Cartas restantes: $cartasRestantes\n")
        estadisticasPalos.append("• Progreso completado: $porcentajeCompletado%\n")
        estadisticasPalos.append("• Eficiencia del juego: $eficiencia%\n\n")
        
        // Consejos para mejorar
        estadisticasPalos.append("💡 CONSEJOS PARA MEJORAR:\n")
        when {
            eficiencia >= 80 -> estadisticasPalos.append("¡Excelente estrategia! Mantén este nivel.")
            eficiencia >= 60 -> estadisticasPalos.append("Buen rendimiento. Intenta ser más selectivo.")
            eficiencia >= 40 -> estadisticasPalos.append("Puedes mejorar observando patrones de cartas.")
            else -> estadisticasPalos.append("Practica más para desarrollar mejor intuición.")
        }
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📊 Estadísticas Detalladas")
        builder.setMessage(estadisticasPalos.toString())
        builder.setPositiveButton("🎮 Jugar de Nuevo") { _, _ ->
            iniciarJuego()
        }
        builder.setNegativeButton("✅ Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    /**
     * Muestra las instrucciones del juego de manera más clara
     */
    private fun mostrarInstrucciones() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📋 Instrucciones del Juego")
        builder.setMessage(
            "🎯 OBJETIVO:\n" +
            "Completa cualquiera de las 4 filas de cartas (una por cada palo).\n\n" +
            "🎮 CÓMO JUGAR:\n" +
            "• Presiona 'Siguiente' para agregar una carta aleatoria\n" +
            "• Las cartas se distribuyen entre los 4 palos\n" +
            "• Ganas cuando un palo llegue a 13 cartas\n\n" +
            "🏆 ¡Intenta completar el juego en el menor número de movimientos!"
        )
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    /**
     * Proporciona feedback háptico, visual y auditivo para los botones
     */
    private fun proporcionarFeedbackBoton(button: com.google.android.material.button.MaterialButton) {
        // Feedback auditivo
        audioManager.playButtonClick()
        
        // Feedback háptico (solo si el vibrador está disponible)
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
        
        // Feedback visual optimizado - animación de escala más rápida
        val scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 0.97f)
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 0.97f)
        val scaleUp = ObjectAnimator.ofFloat(button, "scaleX", 0.97f, 1.0f)
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.97f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleDown, scaleDownY)
        animatorSet.duration = 50 // Reducido para mejor rendimiento
        
        val animatorSetUp = AnimatorSet()
        animatorSetUp.playTogether(scaleUp, scaleUpY)
        animatorSetUp.duration = 50 // Reducido para mejor rendimiento
        
        animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                animatorSetUp.start()
            }
        })
        
        animatorSet.start()
    }

    // Función mostrarMensajeLimiteMovimientos eliminada - ya no hay límite de movimientos

    /**
     * Libera recursos cuando la actividad se destruye
     */
    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de audio
        audioManager.release()
        // ViewBinding se limpia automáticamente
    }
    
    // Funciones de animación
    private fun animarCarta(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        val scaleX = ObjectAnimator.ofFloat(recyclerView, "scaleX", 1.0f, 1.1f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(recyclerView, "scaleY", 1.0f, 1.1f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = DURACION_ANIMACION_CARTA
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.start()
    }
    
    private fun animarTexto(textView: android.widget.TextView) {
        val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.3f)
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0.3f, 1.0f)
        val scale = ObjectAnimator.ofFloat(textView, "scaleX", 1.0f, 1.2f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeOut, fadeIn)
        animatorSet.playTogether(fadeIn, scale)
        animatorSet.duration = DURACION_ANIMACION_TEXTO
        animatorSet.start()
    }
    
    private fun animarReinicio() {
        // Animación de fade out y fade in para toda el área de juego
        val fadeOut = ObjectAnimator.ofFloat(binding.root, "alpha", 1.0f, 0.0f)
        val fadeIn = ObjectAnimator.ofFloat(binding.root, "alpha", 0.0f, 1.0f)
        
        fadeOut.duration = DURACION_ANIMACION_REINICIO_FADE
        fadeIn.duration = DURACION_ANIMACION_FADE_IN
        
        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                fadeIn.start()
            }
        })
        
        fadeOut.start()
    }
    
    private fun animarCelebracion() {
        // Animación de celebración más espectacular para toda la pantalla
        val pulseX = ObjectAnimator.ofFloat(binding.root, "scaleX", 1.0f, 1.08f, 0.95f, 1.02f, 1.0f)
        val pulseY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1.0f, 1.08f, 0.95f, 1.02f, 1.0f)
        val rotation = ObjectAnimator.ofFloat(binding.root, "rotation", 0f, 2f, -1f, 0f)
        
        // Animación de los RecyclerViews de palos
        val animacionTreboles = ObjectAnimator.ofFloat(binding.rvTreboles, "alpha", 1.0f, 0.7f, 1.0f)
        val animacionCorazones = ObjectAnimator.ofFloat(binding.rvCorazones, "alpha", 1.0f, 0.7f, 1.0f)
        val animacionPicas = ObjectAnimator.ofFloat(binding.rvPicas, "alpha", 1.0f, 0.7f, 1.0f)
        val animacionDiamantes = ObjectAnimator.ofFloat(binding.rvDiamantes, "alpha", 1.0f, 0.7f, 1.0f)
        
        // Animación del encabezado
        val animacionHeader = ObjectAnimator.ofFloat(binding.tvMovimientos, "scaleX", 1.0f, 1.2f, 1.0f)
        val animacionHeaderY = ObjectAnimator.ofFloat(binding.tvMovimientos, "scaleY", 1.0f, 1.2f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            pulseX, pulseY, rotation,
            animacionTreboles, animacionCorazones, animacionPicas, animacionDiamantes,
            animacionHeader, animacionHeaderY
        )
        animatorSet.duration = DURACION_ANIMACION_CELEBRACION * 2 // Duración más larga
        animatorSet.interpolator = OvershootInterpolator(1.5f)
        
        // Vibración de celebración si está disponible
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibracionCelebracion = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100, 50, 200),
                    intArrayOf(0, 255, 0, 255, 0, 255),
                    -1
                )
                it.vibrate(vibracionCelebracion)
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(longArrayOf(0, 100, 50, 100, 50, 200), -1)
            }
        }
        
        animatorSet.start()
    }
    
    /**
      * Configura el modo pantalla completa inmersivo
      */
     private fun configurarPantallaCompleta() {
         WindowCompat.setDecorFitsSystemWindows(window, false)
         
         val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
         // Ocultar barras del sistema
         windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
         
         // Configurar comportamiento inmersivo
         windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
     }
}