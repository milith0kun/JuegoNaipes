package com.example.juegocarreranaipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.example.juegocarreranaipes.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // ViewBinding para acceso eficiente a las vistas
    private lateinit var binding: ActivityMainBinding

    // Variables para las cartas de cada palo (usando constantes para mejor rendimiento)
    private val treboles = arrayListOf<String>()
    private val corazones = arrayListOf<String>()
    private val corazonesNegros = arrayListOf<String>()
    private val diamantes = arrayListOf<String>()
    private val pista = arrayListOf<String>()

    // Variables para el manejo de movimientos
    private var movimientos = 0
    private var juegoTerminado = false

    // Variables para los adaptadores
    private lateinit var adapterTreboles: CartasAdapter
    private lateinit var adapterCorazones: CartasAdapter
    private lateinit var adapterCorazonesNegros: CartasAdapter
    private lateinit var adapterDiamantes: CartasAdapter
    private lateinit var adapterPista: CartasPistaAdapter

    // Constantes para los palos
    companion object {
        private const val PALO_TREBOLES = 1
        private const val PALO_CORAZONES = 2
        private const val PALO_PICAS = 3
        private const val PALO_DIAMANTES = 4
        private const val CARTAS_POR_PALO = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarJuego()
        configurarEventos()
    }

    /**
     * Configura los eventos de los botones de manera más limpia
     */
    private fun configurarEventos() {
        binding.btnSiguiente.setOnClickListener {
            if (!juegoTerminado) {
                procesarSiguienteCarta()
            }
        }

        binding.btnJuegoNuevo.setOnClickListener {
            iniciarJuego()
        }

        binding.btnInfo.setOnClickListener {
            mostrarInstrucciones()
        }
    }

    /**
     * Procesa la siguiente carta del juego con lógica optimizada
     */
    private fun procesarSiguienteCarta() {
        val carta = obtenerCartaAleatoria()
        movimientos++
        actualizarContadorMovimientos()

        when (carta) {
            PALO_TREBOLES -> agregarCartaAPalo(treboles, adapterTreboles)
            PALO_CORAZONES -> agregarCartaAPalo(corazones, adapterCorazones)
            PALO_PICAS -> agregarCartaAPalo(corazonesNegros, adapterCorazonesNegros)
            PALO_DIAMANTES -> agregarCartaAPalo(diamantes, adapterDiamantes)
        }
    }

    /**
     * Agrega una carta al palo especificado y verifica si el juego terminó
     */
    private fun agregarCartaAPalo(palo: ArrayList<String>, adapter: CartasAdapter) {
        palo.add("carta")
        adapter.notifyItemInserted(palo.size - 1) // Notificación más eficiente
        
        // Animar la adición de carta
        when (adapter) {
            adapterTreboles -> animarCarta(binding.rvTreboles)
            adapterCorazones -> animarCarta(binding.rvCorazones)
            adapterCorazonesNegros -> animarCarta(binding.rvPicas)
            adapterDiamantes -> animarCarta(binding.rvDiamantes)
        }
        
        if (palo.size == CARTAS_POR_PALO) {
            juegoTerminado = true
            mostrarMensajeGanador()
        }
    }

    /**
     * Actualiza el contador de movimientos en la UI
     */
    private fun actualizarContadorMovimientos() {
        binding.tvMovimientos.text = "Movimientos: $movimientos"
        animarTexto(binding.tvMovimientos)
    }

    /**
     * Inicializa el juego con estado limpio y configuración optimizada
     */
    private fun iniciarJuego() {
        // Limpiar todas las listas de cartas
        limpiarTodasLasCartas()
        
        // Reiniciar variables de estado
        movimientos = 0
        juegoTerminado = false
        actualizarContadorMovimientos()
        
        // Animar reinicio
        animarReinicio()

        // Inicializar las listas con una carta inicial para cada palo
        inicializarCartasIniciales()

        // Inicializar la pista con 24 cartas
        repeat(24) { pista.add("carta") }

        // Configurar todos los RecyclerViews
        configurarRecyclerViews()
    }

    /**
     * Limpia todas las listas de cartas de manera eficiente
     */
    private fun limpiarTodasLasCartas() {
        treboles.clear()
        corazones.clear()
        corazonesNegros.clear()
        diamantes.clear()
        pista.clear()
    }

    /**
     * Inicializa cada palo con cartas iniciales para mejor visualización
     */
    private fun inicializarCartasIniciales() {
        // Agregar 3 cartas iniciales a cada palo para mejor visualización
        repeat(3) {
            treboles.add("carta")
            corazones.add("carta")
            corazonesNegros.add("carta")
            diamantes.add("carta")
        }
    }

    /**
     * Configura todos los RecyclerViews de manera centralizada
     */
    private fun configurarRecyclerViews() {
        configurarRecyclerViewTreboles()
        configurarRecyclerViewCorazones()
        configurarRecyclerViewCorazonesNegros()
        configurarRecyclerViewDiamantes()
        configurarRecyclerViewPista()
    }

    /**
     * Genera una carta aleatoria entre los 4 palos disponibles
     */
    private fun obtenerCartaAleatoria(): Int {
        return Random.nextInt(PALO_TREBOLES, PALO_DIAMANTES + 1)
    }

    /**
     * Configura el RecyclerView para las cartas de tréboles
     */
    private fun configurarRecyclerViewTreboles() {
        adapterTreboles = CartasAdapter(this, treboles, PALO_TREBOLES)
        binding.rvTreboles.apply {
            adapter = adapterTreboles
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true) // Optimización para mejor rendimiento
            // Aplicar decoración personalizada para espaciado optimizado
            addItemDecoration(CartaItemDecoration(resources.getDimensionPixelSize(R.dimen.carta_margin)))
        }
    }

    /**
     * Configura el RecyclerView para las cartas de corazones
     */
    private fun configurarRecyclerViewCorazones() {
        adapterCorazones = CartasAdapter(this, corazones, PALO_CORAZONES)
        binding.rvCorazones.apply {
            adapter = adapterCorazones
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            // Aplicar decoración personalizada para espaciado optimizado
            addItemDecoration(CartaItemDecoration(resources.getDimensionPixelSize(R.dimen.carta_margin)))
        }
    }

    /**
     * Configura el RecyclerView para las cartas de picas (corazones negros)
     */
    private fun configurarRecyclerViewCorazonesNegros() {
        adapterCorazonesNegros = CartasAdapter(this, corazonesNegros, PALO_PICAS)
        binding.rvPicas.apply {
            adapter = adapterCorazonesNegros
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            // Aplicar decoración personalizada para espaciado optimizado
            addItemDecoration(CartaItemDecoration(resources.getDimensionPixelSize(R.dimen.carta_margin)))
        }
    }

    /**
     * Configura el RecyclerView para las cartas de diamantes
     */
    private fun configurarRecyclerViewDiamantes() {
        adapterDiamantes = CartasAdapter(this, diamantes, PALO_DIAMANTES)
        binding.rvDiamantes.apply {
            adapter = adapterDiamantes
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            // Aplicar decoración personalizada para espaciado optimizado
            addItemDecoration(CartaItemDecoration(resources.getDimensionPixelSize(R.dimen.carta_margin)))
        }
    }

    /**
     * Configura el RecyclerView para la pista de cartas
     */
    private fun configurarRecyclerViewPista() {
        adapterPista = CartasPistaAdapter(this, pista)
        binding.rvPista.apply {
            adapter = adapterPista
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            // Aplicar decoración personalizada para espaciado optimizado
            addItemDecoration(CartaItemDecoration(resources.getDimensionPixelSize(R.dimen.carta_margin)))
        }
    }

    /**
     * Muestra el mensaje de victoria con estadísticas del juego
     */
    private fun mostrarMensajeGanador() {
        // Animación de celebración
        animarCelebracion()
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🎉 ¡Felicidades! 🎉")
        builder.setMessage("Has completado el juego en $movimientos movimientos\n\n¡Excelente trabajo!\n\n¿Quieres jugar otra vez?")
        builder.setPositiveButton("🎮 Juego Nuevo") { _, _ ->
            iniciarJuego()
        }
        builder.setNegativeButton("🚪 Salir") { _, _ ->
            finish()
        }
        builder.setCancelable(false) // Evita cerrar accidentalmente
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
     * Libera recursos cuando la actividad se destruye
     */
    override fun onDestroy() {
        super.onDestroy()
        // ViewBinding se limpia automáticamente
    }
    
    // Funciones de animación
    private fun animarCarta(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        val scaleX = ObjectAnimator.ofFloat(recyclerView, "scaleX", 1.0f, 1.1f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(recyclerView, "scaleY", 1.0f, 1.1f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 300
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
        animatorSet.duration = 200
        animatorSet.start()
    }
    
    private fun animarReinicio() {
        // Animación de fade out y fade in para toda el área de juego
        val fadeOut = ObjectAnimator.ofFloat(binding.root, "alpha", 1.0f, 0.0f)
        val fadeIn = ObjectAnimator.ofFloat(binding.root, "alpha", 0.0f, 1.0f)
        
        fadeOut.duration = 200
        fadeIn.duration = 300
        
        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                fadeIn.start()
            }
        })
        
        fadeOut.start()
    }
    
    private fun animarCelebracion() {
        // Animación de celebración para toda la pantalla
        val pulseX = ObjectAnimator.ofFloat(binding.root, "scaleX", 1.0f, 1.05f, 1.0f)
        val pulseY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1.0f, 1.05f, 1.0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(pulseX, pulseY)
        animatorSet.duration = 600
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.start()
    }
}