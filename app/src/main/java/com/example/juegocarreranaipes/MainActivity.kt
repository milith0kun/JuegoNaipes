package com.example.juegocarreranaipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.juegocarreranaipes.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), CartasAdapter.OnItemClicked {

    private lateinit var binding: ActivityMainBinding

    val treboles = arrayListOf<String>()
    val corazones = arrayListOf<String>()
    val corazonesNegros = arrayListOf<String>()
    val diamantes = arrayListOf<String>()
    var pista = arrayListOf("g", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "g")
    var numMovimientos = 0

    private lateinit var cartasAdapter: CartasAdapter
    private lateinit var cartasPistaAdapter: CartasPistaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        iniciarJuego()

        binding.btnSiguiente.setOnClickListener {
            var num = obtenerCartaAleatoria()
            numMovimientos++
            when(num) {
                1 -> {
                    binding.cartasPozo.setImageResource(R.drawable.qc)
                    treboles.add("ac")
                    binding.tvMovimientos.text = "${resources.getString(R.string.msg_movimiento)} $numMovimientos ${resources.getString(R.string.msg_avance)} Treboles"
                    setupRecyclerViewTreboles()
                }
                2 -> {
                    binding.cartasPozo.setImageResource(R.drawable.qh)
                    corazones.add("ah")
                    binding.tvMovimientos.text = "${resources.getString(R.string.msg_movimiento)} $numMovimientos ${resources.getString(R.string.msg_avance)} Corazones"
                    setupRecyclerViewCorazones()
                }
                3 -> {
                    binding.cartasPozo.setImageResource(R.drawable.qs)
                    corazonesNegros.add("as")
                    binding.tvMovimientos.text = "${resources.getString(R.string.msg_movimiento)} $numMovimientos ${resources.getString(R.string.msg_avance)} Corazones Negros"
                    setupRecyclerViewCorazonesNegros()
                }
                4 -> {
                    binding.cartasPozo.setImageResource(R.drawable.qd)
                    diamantes.add("ad")
                    binding.tvMovimientos.text = "${resources.getString(R.string.msg_movimiento)} $numMovimientos ${resources.getString(R.string.msg_avance)} Diamantes"
                    setupRecyclerViewDiamantes()
                }
            }
        }

        binding.btnJuegoNuevo.setOnClickListener {
            iniciarJuego()
        }

        binding.btnInfo.setOnClickListener {
            instrucciones()
        }
    }

    private fun iniciarJuego() {
        binding.btnSiguiente.isEnabled = true

        numMovimientos = 0
        binding.tvMovimientos.text = "Movimiento: $numMovimientos"

        treboles.clear()
        corazones.clear()
        corazonesNegros.clear()
        diamantes.clear()
        
        treboles.add("ac")
        corazones.add("ah")
        corazonesNegros.add("as")
        diamantes.add("ad")

        setupRecyclerViewTreboles()
        setupRecyclerViewCorazones()
        setupRecyclerViewCorazonesNegros()
        setupRecyclerViewDiamantes()
        setupRecyclerViewPista()
    }

    private fun obtenerCartaAleatoria(): Int {
        return (1..4).random()
    }

    private fun setupRecyclerViewTreboles() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.cartasTreboles.layoutManager = layoutManager
        cartasAdapter = CartasAdapter(this, treboles, "c", this)
        binding.cartasTreboles.adapter = cartasAdapter
    }

    private fun setupRecyclerViewCorazones() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.cartasCorazones.layoutManager = layoutManager
        cartasAdapter = CartasAdapter(this, corazones, "h", this)
        binding.cartasCorazones.adapter = cartasAdapter
    }

    private fun setupRecyclerViewCorazonesNegros() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.cartasCorazonesNegros.layoutManager = layoutManager
        cartasAdapter = CartasAdapter(this, corazonesNegros, "s", this)
        binding.cartasCorazonesNegros.adapter = cartasAdapter
    }

    private fun setupRecyclerViewDiamantes() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.cartasDiamantes.layoutManager = layoutManager
        cartasAdapter = CartasAdapter(this, diamantes, "d", this)
        binding.cartasDiamantes.adapter = cartasAdapter
    }

    private fun setupRecyclerViewPista() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.cartasPista.layoutManager = layoutManager
        cartasPistaAdapter = CartasPistaAdapter(this, pista)
        binding.cartasPista.adapter = cartasPistaAdapter
    }

    override fun mensajeGanador(tipo: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("GANADOR")

        var carta = ""
        when(tipo) {
            "c" -> carta = "TREBOLES"
            "h" -> carta = "CORAZONES"
            "s" -> carta = "CORAZONES NEGROS"
            "d" -> carta = "DIAMANTES"
        }

        builder.setMessage("${resources.getString(R.string.msg_ganador)} $carta")

        builder.create()
        builder.setCancelable(true)
        builder.show()

        binding.btnSiguiente.isEnabled = false

        binding.tvMovimientos.text = "Movimiento: $numMovimientos, Ganador: $carta"
    }

    private fun instrucciones() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("INSTRUCCIONES")

        builder.setMessage(resources.getString(R.string.instrucciones))

        builder.create()
        builder.setCancelable(true)
        builder.show()
    }
}