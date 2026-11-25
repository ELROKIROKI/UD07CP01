package com.jlara.ud06cp01

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.jlara.ud06cp01.databinding.ActivityDetalleBinding
import com.jlara.ud06cp01.model.Pelicula

class DetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pelicula = IntentCompat.getSerializableExtra(intent, "pelicula", Pelicula::class.java) ?: return
        mostrarPelicula(pelicula)
    }

    private fun mostrarPelicula(pelicula: Pelicula) {
        supportActionBar?.title = pelicula.titulo
        binding.tvTitulo.text = pelicula.titulo
        Glide.with(this).load(pelicula.imagen).fitCenter().into(binding.ivPoster)
        binding.tvDescripcion.text = getString(R.string.detalle_descripcion, pelicula.descripcion)
        binding.tvAnio.text = getString(R.string.detalle_anio, pelicula.anio)
        binding.tvDuracion.text = getString(R.string.detalle_duracion, pelicula.duracion)
        binding.tvNacionalidad.text = getString(R.string.detalle_nacionalidad, pelicula.nacionalidad)
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
