package com.jlara.ud06cp01

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.jlara.ud06cp01.databinding.ActivityEditarBinding
import com.jlara.ud06cp01.model.Pelicula

class EditarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pelicula = IntentCompat.getSerializableExtra(intent, "pelicula", Pelicula::class.java) ?: return
        Glide.with(this).load(pelicula.imagen).fitCenter().into(binding.ivPoster)
        binding.etNombre.setText(pelicula.titulo)

        binding.btnCambiar.setOnClickListener {
            val nuevoTitulo = binding.etNombre.text.toString().trim()
            if (nuevoTitulo.isBlank()) {
                binding.etNombre.error = getString(R.string.editar_vacio)
            } else {
                setResult(RESULT_OK, Intent().putExtra("pelicula", pelicula.copy(titulo = nuevoTitulo)))
                finish()
            }
        }

        binding.btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
