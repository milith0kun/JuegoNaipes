package com.example.juegocarreranaipes

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Gestor de audio para el juego de carrera de naipes
 * Maneja efectos de sonido y música de fondo
 */
class AudioManager(private val context: Context) : DefaultLifecycleObserver {
    
    companion object {
        private const val PREFS_NAME = "audio_preferences"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_SOUND_VOLUME = "sound_volume"
        private const val KEY_MUSIC_VOLUME = "music_volume"
        
        // Volúmenes por defecto optimizados
        private const val DEFAULT_SOUND_VOLUME = 0.8f
        private const val DEFAULT_MUSIC_VOLUME = 0.4f
        
        // Límites de tiempo para espaciado de audio (en milisegundos)
        private const val MAX_STREAMS = 3 // Reducido para evitar superposición
        private const val MIN_SOUND_INTERVAL = 200L // Aumentado para mejor espaciado
        private const val CARD_SOUND_INTERVAL = 250L // Aumentado para sonidos de cartas
        private const val BUTTON_SOUND_INTERVAL = 300L // Aumentado para botones
        private const val VICTORY_SOUND_DELAY = 800L // Aumentado para el sonido de victoria
        private const val FADE_DURATION = 150L // Duración de fade para transiciones suaves
    }
    
    // SoundPool para efectos de sonido cortos
    private var soundPool: SoundPool? = null
    
    // MediaPlayer para música de fondo
    private var backgroundMusicPlayer: MediaPlayer? = null
    
    // IDs de los sonidos cargados
    private var cardSelectSound: Int = 0
    private var cardMoveSound: Int = 0
    private var cardPlaceSound: Int = 0
    private var victorySound: Int = 0
    private var buttonClickSound: Int = 0
    private var gameStartSound: Int = 0
    private var shuffleSound: Int = 0
    
    // Preferencias de audio
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Control de espaciado temporal y prioridades
    private var lastSoundTime = 0L
    private var lastCardSoundTime = 0L
    private var lastButtonSoundTime = 0L
    private var isPlayingVictorySequence = false
    private var currentPlayingStreams = mutableSetOf<Int>() // Streams activos
    private var highPrioritySoundPlaying = false // Control de sonidos prioritarios
    
    // Estados de audio
    var isSoundEnabled: Boolean
        get() = preferences.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = preferences.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    
    var isMusicEnabled: Boolean
        get() = preferences.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = preferences.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()
    
    var soundVolume: Float
        get() = preferences.getFloat(KEY_SOUND_VOLUME, DEFAULT_SOUND_VOLUME)
        set(value) {
            preferences.edit().putFloat(KEY_SOUND_VOLUME, value.coerceIn(0f, 1f)).apply()
            updateSoundVolume()
        }
    
    var musicVolume: Float
        get() = preferences.getFloat(KEY_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME)
        set(value) {
            preferences.edit().putFloat(KEY_MUSIC_VOLUME, value.coerceIn(0f, 1f)).apply()
            updateMusicVolume()
        }
    
    init {
        initializeSoundPool()
        loadSoundEffects()
    }
    
    /**
     * Inicializa el SoundPool para efectos de sonido con configuración optimizada
     */
    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_LOW_LATENCY) // Baja latencia para mejor respuesta
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(8) // Aumentado a 8 streams para mejor superposición
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Configurar listener para detectar cuando los sonidos están listos
        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            android.util.Log.d("AudioManager", "Sound loaded - ID: $sampleId, Status: $status")
        }
    }
    
    /**
     * Carga todos los efectos de sonido
     */
    private fun loadSoundEffects() {
        android.util.Log.d("AudioManager", "Loading sound effects...")
        soundPool?.let { pool ->
            try {
                // Nota: Estos archivos de audio deben agregarse al directorio res/raw
                cardSelectSound = pool.load(context, R.raw.card_select, 1)
                cardMoveSound = pool.load(context, R.raw.card_move, 1)
                cardPlaceSound = pool.load(context, R.raw.card_place, 1)
                victorySound = pool.load(context, R.raw.victory, 1)
                buttonClickSound = pool.load(context, R.raw.button_click, 1)
                gameStartSound = pool.load(context, R.raw.game_start, 1)
                shuffleSound = pool.load(context, R.raw.shuffle, 1)
                android.util.Log.d("AudioManager", "Sound effects loaded - cardSelect: $cardSelectSound, cardMove: $cardMoveSound, cardPlace: $cardPlaceSound, victory: $victorySound, buttonClick: $buttonClickSound, gameStart: $gameStartSound, shuffle: $shuffleSound")
            } catch (e: Exception) {
                // Si no se encuentran los archivos de audio, continuar sin sonidos
                android.util.Log.e("AudioManager", "Error loading sound effects", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Reproduce un efecto de sonido con control de espaciado temporal
     */
    private fun playSound(soundId: Int, volume: Float = soundVolume, soundType: SoundType = SoundType.GENERAL) {
        android.util.Log.d("AudioManager", "playSound called - soundId: $soundId, volume: $volume, type: $soundType, isSoundEnabled: $isSoundEnabled")
        
        if (!isSoundEnabled || soundId == 0) {
            return
        }
        
        val currentTime = System.currentTimeMillis()
        
        // Verificar espaciado temporal según el tipo de sonido
        when (soundType) {
            SoundType.CARD -> {
                if (currentTime - lastCardSoundTime < CARD_SOUND_INTERVAL) {
                    android.util.Log.d("AudioManager", "Card sound skipped - too soon (${currentTime - lastCardSoundTime}ms)")
                    return
                }
                lastCardSoundTime = currentTime
            }
            SoundType.BUTTON -> {
                if (currentTime - lastButtonSoundTime < BUTTON_SOUND_INTERVAL) {
                    android.util.Log.d("AudioManager", "Button sound skipped - too soon (${currentTime - lastButtonSoundTime}ms)")
                    return
                }
                lastButtonSoundTime = currentTime
            }
            SoundType.VICTORY -> {
                if (isPlayingVictorySequence) {
                    android.util.Log.d("AudioManager", "Victory sound skipped - sequence already playing")
                    return
                }
                isPlayingVictorySequence = true
                // Resetear flag después del delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isPlayingVictorySequence = false
                }, VICTORY_SOUND_DELAY + 1000) // 1 segundo adicional
            }
            SoundType.GENERAL -> {
                if (currentTime - lastSoundTime < MIN_SOUND_INTERVAL) {
                    android.util.Log.d("AudioManager", "General sound skipped - too soon (${currentTime - lastSoundTime}ms)")
                    return
                }
            }
        }
        
        lastSoundTime = currentTime
        
        // Reproducir con parámetros optimizados para calidad
        val streamId = soundPool?.play(
            soundId,
            volume,
            volume,
            1, // Prioridad alta
            0, // Sin loop
            1.0f // Velocidad normal para mejor calidad
        )
        
        if (streamId != null && streamId > 0) {
            android.util.Log.d("AudioManager", "Sound played successfully - streamId: $streamId, type: $soundType")
        } else {
            android.util.Log.w("AudioManager", "Failed to play sound - soundId: $soundId, type: $soundType")
        }
    }
    
    /**
     * Enum para tipos de sonido y su espaciado temporal
     */
    private enum class SoundType {
        CARD,
        BUTTON,
        VICTORY,
        GENERAL
    }
    
    // Métodos públicos para reproducir sonidos específicos
    
    /**
     * Reproduce sonido al seleccionar una carta con control de superposición
     */
    fun playCardSelect() {
        if (!isSoundEnabled || highPrioritySoundPlaying) return
        
        try {
            stopLowPrioritySounds()
            val streamId = soundPool?.play(cardSelectSound, soundVolume * 0.5f, soundVolume * 0.5f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastCardSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de selección de carta", e)
        }
    }
    
    /**
     * Reproduce sonido al mover una carta con control de superposición
     */
    fun playCardMove() {
        if (!isSoundEnabled || highPrioritySoundPlaying) return
        
        try {
            stopLowPrioritySounds()
            val streamId = soundPool?.play(cardMoveSound, soundVolume * 0.4f, soundVolume * 0.4f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastCardSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de movimiento de carta", e)
        }
    }
    
    /**
     * Reproduce sonido al colocar una carta con control de superposición
     */
    fun playCardPlace() {
        if (!isSoundEnabled || highPrioritySoundPlaying) return
        
        try {
            stopLowPrioritySounds()
            val streamId = soundPool?.play(cardPlaceSound, soundVolume * 0.6f, soundVolume * 0.6f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastCardSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de colocación de carta", e)
        }
    }
    
    /**
     * Reproduce sonido de victoria con máxima prioridad
     */
    fun playVictory() {
        if (!isSoundEnabled) return
        
        // Marcar sonido de alta prioridad y detener todos los demás
        highPrioritySoundPlaying = true
        stopAllSounds()
        
        // Reproducir con delay para mejor impacto
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                val streamId = soundPool?.play(victorySound, soundVolume * 0.8f, soundVolume * 0.8f, 1, 0, 1.0f)
                if (streamId != null && streamId > 0) {
                    currentPlayingStreams.add(streamId)
                }
                
                // Resetear el flag después de que termine el sonido
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    highPrioritySoundPlaying = false
                    if (streamId != null) {
                        currentPlayingStreams.remove(streamId)
                    }
                }, 3000L) // Duración estimada del sonido de victoria
                
            } catch (e: Exception) {
                android.util.Log.e("AudioManager", "Error reproduciendo sonido de victoria", e)
                highPrioritySoundPlaying = false
            }
        }, VICTORY_SOUND_DELAY)
    }
    
    /**
     * Reproduce sonido de clic de botón con control de superposición
     */
    fun playButtonClick() {
        if (!isSoundEnabled || highPrioritySoundPlaying) return
        
        try {
            val streamId = soundPool?.play(buttonClickSound, soundVolume * 0.3f, soundVolume * 0.3f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastButtonSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de botón", e)
        }
    }
    
    /**
     * Reproduce sonido de inicio de juego con prioridad alta
     */
    fun playGameStart() {
        if (!isSoundEnabled) return
        
        try {
            stopAllSounds() // Limpiar cualquier sonido previo
            val streamId = soundPool?.play(gameStartSound, soundVolume * 0.7f, soundVolume * 0.7f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de inicio", e)
        }
    }
    
    /**
     * Reproduce sonido de barajar cartas con prioridad media
     */
    fun playShuffle() {
        if (!isSoundEnabled) return
        
        try {
            stopLowPrioritySounds()
            val streamId = soundPool?.play(shuffleSound, soundVolume * 0.5f, soundVolume * 0.5f, 1, 0, 1.0f)
            if (streamId != null && streamId > 0) {
                currentPlayingStreams.add(streamId)
                removeStreamImmediately(streamId)
            }
            lastCardSoundTime = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error reproduciendo sonido de barajado", e)
        }
    }
    
    /**
     * Inicia la música de fondo con fade in suave
     */
    fun startBackgroundMusic() {
        android.util.Log.d("AudioManager", "startBackgroundMusic called - isMusicEnabled: $isMusicEnabled")
        if (!isMusicEnabled) return
        
        try {
            stopBackgroundMusic() // Detener música anterior si existe
            
            backgroundMusicPlayer = MediaPlayer.create(context, R.raw.background_music)
            android.util.Log.d("AudioManager", "MediaPlayer created: ${backgroundMusicPlayer != null}")
            backgroundMusicPlayer?.apply {
                // Configuración optimizada para calidad de audio
                isLooping = true
                
                // Configurar atributos de audio para música
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                            .build()
                    )
                }
                
                // Fade in más gradual y suave
                setVolume(0f, 0f) // Empezar en silencio
                start()
                
                // Aplicar fade in gradual con volumen reducido
                fadeInMusic()
                
                android.util.Log.d("AudioManager", "Background music started with fade-in - isPlaying: $isPlaying")
            }
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error starting background music", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Aplica fade in gradual a la música de fondo con suavizado
     */
    private fun fadeInMusic() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val fadeSteps = 30 // Más pasos para transición más suave
        val fadeInterval = 67L // 67ms entre pasos para fade de 2 segundos
        val targetVolume = musicVolume * 0.7f // Volumen más bajo
        val volumeStep = targetVolume / fadeSteps
        
        for (i in 1..fadeSteps) {
            handler.postDelayed({
                // Aplicar curva de suavizado (ease-out)
                val progress = i.toFloat() / fadeSteps
                val easedProgress = 1f - (1f - progress) * (1f - progress)
                val currentVolume = targetVolume * easedProgress
                backgroundMusicPlayer?.setVolume(currentVolume, currentVolume)
            }, fadeInterval * i)
        }
    }
    
    /**
     * Detiene la música de fondo con fade out suave
     */
    fun stopBackgroundMusic() {
        backgroundMusicPlayer?.apply {
            if (isPlaying) {
                // Aplicar fade out más lento antes de detener
                fadeOutMusic {
                    stop()
                    release()
                    backgroundMusicPlayer = null
                }
            } else {
                release()
                backgroundMusicPlayer = null
            }
        } ?: run {
            backgroundMusicPlayer = null
        }
    }
    
    /**
     * Pausa la música de fondo con fade out suave
     */
    fun pauseBackgroundMusic() {
        backgroundMusicPlayer?.apply {
            if (isPlaying) {
                fadeOutMusic {
                    pause()
                }
            }
        }
    }
    
    /**
     * Aplica fade out gradual a la música de fondo con suavizado
     */
    private fun fadeOutMusic(onComplete: () -> Unit) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val fadeSteps = 25 // Más pasos para transición más suave
        val fadeInterval = 60L // 60ms entre pasos para fade out de 1.5 segundos
        val currentVolume = musicVolume * 0.7f // Ajustar al volumen actual reducido
        val volumeStep = currentVolume / fadeSteps
        
        for (i in 1..fadeSteps) {
            handler.postDelayed({
                // Aplicar curva de suavizado (ease-in)
                val progress = i.toFloat() / fadeSteps
                val easedProgress = progress * progress
                val newVolume = currentVolume * (1f - easedProgress)
                backgroundMusicPlayer?.setVolume(newVolume.coerceAtLeast(0f), newVolume.coerceAtLeast(0f))
                
                // Ejecutar callback al final del fade out
                if (i == fadeSteps) {
                    handler.postDelayed(onComplete, 50L)
                }
            }, fadeInterval * i)
        }
    }
    
    /**
     * Reanuda la música de fondo con fade in suave
     */
    fun resumeBackgroundMusic() {
        if (isMusicEnabled) {
            backgroundMusicPlayer?.apply {
                if (!isPlaying) {
                    setVolume(0f, 0f) // Comenzar en silencio
                    start()
                    fadeInMusic() // Aplicar fade in gradual
                }
            }
        }
    }
    
    /**
     * Actualiza el volumen de los efectos de sonido con validación
     */
    private fun updateSoundVolume() {
        // Validar rango de volumen
        val validVolume = soundVolume.coerceIn(0f, 1f)
        if (validVolume != soundVolume) {
            android.util.Log.w("AudioManager", "Sound volume adjusted from $soundVolume to $validVolume")
        }
        // El volumen se aplica individualmente en cada reproducción
    }
    
    /**
     * Actualiza el volumen de la música de fondo con transición suave
     */
    private fun updateMusicVolume() {
        // Validar rango de volumen
        val validVolume = musicVolume.coerceIn(0f, 1f)
        if (validVolume != musicVolume) {
            android.util.Log.w("AudioManager", "Music volume adjusted from $musicVolume to $validVolume")
        }
        
        backgroundMusicPlayer?.apply {
            if (isPlaying) {
                // Aplicar cambio de volumen gradual para evitar saltos bruscos
                val handler = android.os.Handler(android.os.Looper.getMainLooper())
                val steps = 10
                val interval = 30L
                val currentVol = 0f // Obtener volumen actual (simplificado)
                val volumeDiff = validVolume - currentVol
                val stepSize = volumeDiff / steps
                
                for (i in 1..steps) {
                    handler.postDelayed({
                        val newVolume = currentVol + (stepSize * i)
                        setVolume(newVolume, newVolume)
                    }, interval * i)
                }
            } else {
                setVolume(validVolume, validVolume)
            }
        }
    }
    
    /**
     * Detiene sonidos de baja prioridad para evitar superposición
     */
    private fun stopLowPrioritySounds() {
        if (currentPlayingStreams.size >= MAX_STREAMS) {
            // Detener algunos streams para hacer espacio
            val streamsToStop = currentPlayingStreams.take(currentPlayingStreams.size - MAX_STREAMS + 1)
            streamsToStop.forEach { streamId ->
                try {
                    soundPool?.stop(streamId)
                    currentPlayingStreams.remove(streamId)
                } catch (e: Exception) {
                    android.util.Log.e("AudioManager", "Error deteniendo stream", e)
                }
            }
        }
    }
    
    /**
     * Detiene todos los sonidos activos
     */
    private fun stopAllSounds() {
        try {
            currentPlayingStreams.forEach { streamId ->
                soundPool?.stop(streamId)
            }
            currentPlayingStreams.clear()
        } catch (e: Exception) {
            android.util.Log.e("AudioManager", "Error deteniendo todos los sonidos", e)
        }
    }
    
    /**
     * Remueve un stream del conjunto activo inmediatamente
     * Optimizado para evitar memory leaks y mejorar rendimiento
     */
    private fun removeStreamImmediately(streamId: Int) {
        currentPlayingStreams.remove(streamId)
    }
    
    /**
     * Libera todos los recursos de audio de forma segura
     */
    fun release() {
        android.util.Log.d("AudioManager", "Releasing audio resources...")
        
        // Detener todos los sonidos activos
        stopAllSounds()
        
        // Detener música con fade out
        stopBackgroundMusic()
        
        // Liberar SoundPool de forma segura
        soundPool?.apply {
            // Detener todos los streams activos
            autoPause()
            release()
        }
        soundPool = null
        
        // Resetear variables de control temporal
        lastSoundTime = 0L
        lastCardSoundTime = 0L
        lastButtonSoundTime = 0L
        isPlayingVictorySequence = false
        currentPlayingStreams.clear()
        highPrioritySoundPlaying = false
        
        android.util.Log.d("AudioManager", "Audio resources released successfully")
    }
    
    // Implementación de DefaultLifecycleObserver
    
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        resumeBackgroundMusic()
    }
    
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pauseBackgroundMusic()
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        release()
    }
    
    /**
     * Alterna el estado de los efectos de sonido
     */
    fun toggleSound(): Boolean {
        isSoundEnabled = !isSoundEnabled
        return isSoundEnabled
    }
    
    /**
     * Alterna el estado de la música de fondo
     */
    fun toggleMusic(): Boolean {
        isMusicEnabled = !isMusicEnabled
        if (isMusicEnabled) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
        return isMusicEnabled
    }
}