package com.example.juegocarreranaipes

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.juegocarreranaipes.databinding.ActivitySplashBinding

/**
 * Actividad de pantalla de inicio con diseño minimalista
 * Muestra el logo y título del juego con un botón para iniciar
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar pantalla completa para experiencia inmersiva
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Iniciar animaciones de entrada
        iniciarAnimaciones()
        
        // Configurar el botón de iniciar juego
        configurarBotonIniciar()
    }
    
    /**
     * Inicia las animaciones de entrada para los elementos de la pantalla
     */
    private fun iniciarAnimaciones() {
        // Animación del logo principal
        binding.gameIcon.alpha = 0f
        binding.gameIcon.scaleX = 0.8f
        binding.gameIcon.scaleY = 0.8f
        
        binding.gameIcon.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setStartDelay(300)
            .start()
        
        // Animación del título
        binding.titleText.alpha = 0f
        binding.titleText.translationY = 50f
        
        binding.titleText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(600)
            .start()
        
        // Animación del subtítulo
        binding.subtitleText.alpha = 0f
        binding.subtitleText.translationY = 30f
        
        binding.subtitleText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(900)
            .start()
            
        // Animación del botón de iniciar (aparece después de las otras animaciones)
        binding.btnIniciarJuego.alpha = 0f
        binding.btnIniciarJuego.translationY = 50f
        
        binding.btnIniciarJuego.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(1200)
            .start()
    }
    
    /**
     * Configura el comportamiento del botón de iniciar juego
     */
    private fun configurarBotonIniciar() {
        binding.btnIniciarJuego.setOnClickListener {
            // Navegar a MainActivity cuando el usuario presione el botón
            val intent = Intent(this, MainActivity::class.java)
            
            // Usar la nueva API de transiciones para Android 13+ o la antigua para versiones anteriores
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivity(intent)
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_OPEN,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            } else {
                // Para versiones anteriores a Android 14, usar ActivityOptions
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
            }
            finish()
        }
    }
}