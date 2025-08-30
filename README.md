# 🃏 Juego Carrera de Naipes - Android App

## 📱 Descripción

Una aplicación de juego de cartas desarrollada en **Kotlin** para **Android Studio** que simula una carrera entre los cuatro palos de una baraja francesa (Tréboles ♣, Corazones ♥, Picas ♠, Diamantes ♦). El objetivo es ser el primer palo en completar 13 cartas para ganar la carrera.

## 🎯 Características Principales

### 🎮 Mecánica del Juego
- **Objetivo**: Completar 13 cartas de un mismo palo para ganar
- **Baraja completa**: 52 cartas distribuidas aleatoriamente
- **Pista de cartas**: Sistema de cartas disponibles para seleccionar
- **Movimientos ilimitados**: El juego continúa hasta que un palo complete 13 cartas
- **Interfaz intuitiva**: Selección de cartas mediante toque y opciones de palo

### 🎨 Interfaz de Usuario
- **Diseño moderno**: Colores temáticos de casino con fondo verde mesa
- **Header dinámico**: Muestra información de la carta actual y progreso del juego
- **Indicadores visuales**: Colores específicos para cada palo
- **Animaciones fluidas**: Transiciones suaves entre estados del juego
- **Feedback táctil**: Vibración en dispositivos compatibles

### 📊 Sistema de Estadísticas
- **Contador de movimientos**: Seguimiento de acciones del jugador
- **Progreso por palo**: Visualización del avance de cada palo
- **Eficiencia del juego**: Cálculo de rendimiento basado en movimientos
- **Estadísticas detalladas**: Distribución de cartas y consejos de mejora

## 🏗️ Arquitectura Técnica

### 📁 Estructura del Proyecto
```
app/src/main/
├── java/com/example/juegocarreranaipes/
│   ├── MainActivity.kt              # Actividad principal del juego
│   ├── CartasAdapter.kt             # Adaptador para RecyclerView de cartas
│   ├── CartasPistaAdapter.kt        # Adaptador para cartas de la pista
│   ├── CartaItemDecoration.kt       # Decoración de espaciado entre cartas
│   ├── GameRepository.kt            # Repositorio de datos del juego
│   ├── GameViewModel.kt             # ViewModel para lógica de negocio
│   └── model/
│       ├── Carta.kt                 # Modelo de datos de carta
│       ├── GeneradorBaraja.kt       # Generador de baraja completa
│       └── Palo.kt                  # Enum de palos de cartas
└── res/
    ├── layout/
    │   ├── activity_main.xml         # Layout principal
    │   ├── item_carta.xml           # Layout de carta individual
    │   └── item_carta_pista.xml     # Layout de carta en pista
    ├── values/
    │   ├── colors.xml               # Paleta de colores del juego
    │   └── strings.xml              # Textos de la aplicación
    └── drawable/                    # Recursos gráficos
```

### 🔧 Tecnologías Utilizadas
- **Lenguaje**: Kotlin
- **IDE**: Android Studio
- **UI**: View Binding, RecyclerView, AlertDialog
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Animaciones**: ObjectAnimator, AnimatorSet
- **Gestión de estado**: LiveData, ViewModel

## 🎪 Funcionalidades Implementadas

### ✅ Sistema de Juego Completo
- [x] Generación aleatoria de baraja completa (52 cartas)
- [x] Distribución inicial de cartas por palo
- [x] Sistema de pista con cartas disponibles
- [x] Lógica de selección y movimiento de cartas
- [x] Detección automática de ganador
- [x] Reinicio de juego

### ✅ Interfaz de Usuario Avanzada
- [x] Header dinámico con información de carta actual
- [x] Indicadores de progreso por palo
- [x] Contador de movimientos en tiempo real
- [x] Colores temáticos y diseño responsive
- [x] Botones de acción con feedback visual

### ✅ Sistema de Estadísticas
- [x] Ventana de victoria mejorada con estadísticas detalladas
- [x] Cálculo de eficiencia del jugador
- [x] Distribución de cartas por palo
- [x] Consejos personalizados de mejora
- [x] Resumen completo del rendimiento

### ✅ Animaciones y Efectos
- [x] Animaciones de celebración espectaculares
- [x] Transiciones suaves entre estados
- [x] Efectos de escala y rotación
- [x] Vibración de celebración
- [x] Feedback visual en botones

### ✅ Optimización y Limpieza
- [x] Eliminación de código no utilizado
- [x] Corrección de referencias de colores
- [x] Optimización de imports
- [x] Limpieza de constantes innecesarias

## 🎮 Cómo Jugar

1. **Inicio**: La aplicación genera automáticamente una baraja completa y la distribuye
2. **Selección**: Toca una carta de la pista para seleccionarla
3. **Movimiento**: Elige el palo al que quieres mover la carta
4. **Objetivo**: Completa 13 cartas de un mismo palo para ganar
5. **Victoria**: Disfruta de las animaciones de celebración y revisa tus estadísticas

## 🏆 Sistema de Puntuación

- **Movimientos**: Cada acción cuenta como un movimiento
- **Eficiencia**: Calculada como `(52 / movimientos) * 100`
- **Rendimiento**:
  - Excelente: > 80% eficiencia
  - Bueno: 60-80% eficiencia
  - Regular: 40-60% eficiencia
  - Necesita mejorar: < 40% eficiencia

## 🔮 Características Destacadas

### 🎨 Diseño Visual
- **Paleta de colores**: Verde mesa, colores de cartas tradicionales
- **Tipografía**: Fuentes legibles y apropiadas para el tema
- **Iconografía**: Símbolos de palos integrados en la interfaz

### ⚡ Rendimiento
- **Optimización**: Código limpio sin elementos no utilizados
- **Memoria**: Gestión eficiente de recursos
- **Responsividad**: Interfaz fluida y responsive

### 🎯 Experiencia de Usuario
- **Intuitividad**: Controles fáciles de entender
- **Feedback**: Respuesta inmediata a las acciones del usuario
- **Accesibilidad**: Colores con buen contraste

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Android Studio Arctic Fox o superior
- SDK de Android 21 o superior
- Dispositivo Android o emulador

### Pasos de Instalación
1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   ```
2. Abre el proyecto en Android Studio
3. Sincroniza las dependencias de Gradle
4. Ejecuta la aplicación en un dispositivo o emulador

### Compilación
```bash
./gradlew assembleDebug
```

### Instalación en dispositivo
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 🎯 Próximas Mejoras

- [ ] Sistema de puntuación global
- [ ] Diferentes niveles de dificultad
- [ ] Modo multijugador
- [x] Sonidos y música de fondo
- [ ] Temas visuales alternativos
- [ ] Guardado de estadísticas históricas
- [ ] Logros y desafíos

## 👨‍💻 Desarrollo

**Versión actual**: 1.0.0  
**Estado**: Funcional y optimizado  
**Última actualización**: Enero 2025

### Historial de Versiones
- **v1.0.0**: Versión inicial completa con todas las funcionalidades básicas
- **v1.1.0**: Mejoras en la ventana de victoria y estadísticas detalladas
- **v1.2.0**: Animaciones mejoradas y limpieza de código

## 📄 Licencia

Este proyecto está desarrollado con fines educativos y de aprendizaje.

---

**¡Disfruta del juego y que gane el mejor palo! 🃏🏆**
