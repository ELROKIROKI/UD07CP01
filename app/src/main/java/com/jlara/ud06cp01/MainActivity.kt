package com.jlara.ud06cp01

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jlara.ud06cp01.adapter.PeliculaAdapter
import com.jlara.ud06cp01.databinding.ActivityMainBinding
import com.jlara.ud06cp01.model.Pelicula
import com.jlara.ud06cp01.provider.PeliculaProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PeliculaAdapter
    private var listaPeliculas: MutableList<Pelicula> = mutableListOf()
    private var positionLongClicked = RecyclerView.NO_POSITION

    // Recibe la película editada desde EditarActivity
    private val editarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val editada = result.data?.let {
                    IntentCompat.getSerializableExtra(it, "pelicula", Pelicula::class.java)
                } ?: return@registerForActivityResult
                val indice = listaPeliculas.indexOfFirst { it.id == editada.id }
                if (indice != -1) {
                    listaPeliculas[indice] = editada
                    adapter.submitList(listaPeliculas.toList())
                    Snackbar.make(binding.main, getString(R.string.msg_editada, editada.titulo),
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        initRecyclerView()

        binding.swipeRefresh.setColorSchemeResources(R.color.purple, R.color.teal)
        binding.swipeRefresh.setOnRefreshListener {
            recargarLista()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun initRecyclerView() {
        listaPeliculas = PeliculaProvider.listaCarga.toMutableList()
        adapter = PeliculaAdapter { pelicula -> onItemSelected(pelicula) }

        binding.rvPeliculas.layoutManager = LinearLayoutManager(this)
        binding.rvPeliculas.setHasFixedSize(true)
        binding.rvPeliculas.itemAnimator = DefaultItemAnimator()
        binding.rvPeliculas.adapter = adapter
        adapter.submitList(listaPeliculas.toList())

        // Pulsación larga sobre un elemento -> menú contextual
        binding.rvPeliculas.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    val child = rv.findChildViewUnder(e.x, e.y)
                    child?.setOnLongClickListener {
                        positionLongClicked = rv.getChildAdapterPosition(child)
                        if (positionLongClicked == RecyclerView.NO_POSITION) return@setOnLongClickListener false
                        registerForContextMenu(child)
                        openContextMenu(child)
                        unregisterForContextMenu(child)
                        true
                    }
                }
                return false
            }
        })
    }

    private fun onItemSelected(pelicula: Pelicula) {
        val intent = Intent(this, DetalleActivity::class.java).putExtra("pelicula", pelicula)
        startActivity(intent)
    }

    private fun recargarLista() {
        listaPeliculas = PeliculaProvider.listaCarga.toMutableList()
        adapter.submitList(listaPeliculas.toList()) {
            binding.rvPeliculas.scrollToPosition(0)
        }
    }

    // ---------- Menú general ----------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_recargar -> {
                recargarLista()
                true
            }
            R.id.action_limpiar -> {
                confirmarLimpiar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmarLimpiar() {
        AlertDialog.Builder(this)
            .setTitle(R.string.titulo_limpiar)
            .setMessage(R.string.msg_limpiar)
            .setNegativeButton(R.string.dialogo_cerrar, null)
            .setPositiveButton(R.string.dialogo_aceptar) { _, _ ->
                val copia = listaPeliculas.toMutableList()
                listaPeliculas.clear()
                adapter.submitList(listaPeliculas.toList())
                Snackbar.make(binding.main, R.string.msg_lista_eliminada, Snackbar.LENGTH_LONG)
                    .setAction(R.string.deshacer) {
                        listaPeliculas = copia
                        adapter.submitList(listaPeliculas.toList())
                    }.show()
            }
            .create().show()
    }

    // ---------- Menú contextual ----------
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_contextual, menu)
        if (positionLongClicked in listaPeliculas.indices) {
            menu?.setHeaderTitle(listaPeliculas[positionLongClicked].titulo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (positionLongClicked !in listaPeliculas.indices) return super.onContextItemSelected(item)
        val pelicula = listaPeliculas[positionLongClicked]
        return when (item.itemId) {
            R.id.action_eliminar -> {
                confirmarEliminar(pelicula)
                true
            }
            R.id.action_editar -> {
                val intent = Intent(this, EditarActivity::class.java).putExtra("pelicula", pelicula)
                editarLauncher.launch(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun confirmarEliminar(pelicula: Pelicula) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.titulo_eliminar_pelicula, pelicula.titulo))
            .setMessage(getString(R.string.msg_eliminar_pelicula, pelicula.titulo))
            .setNegativeButton(R.string.dialogo_cerrar, null)
            .setPositiveButton(R.string.dialogo_aceptar) { _, _ ->
                val indice = listaPeliculas.indexOfFirst { it.id == pelicula.id }
                if (indice != -1) {
                    listaPeliculas.removeAt(indice)
                    adapter.submitList(listaPeliculas.toList())
                    Snackbar.make(binding.main, getString(R.string.msg_eliminada, pelicula.titulo),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.deshacer) {
                            listaPeliculas.add(indice.coerceAtMost(listaPeliculas.size), pelicula)
                            adapter.submitList(listaPeliculas.toList())
                        }.show()
                }
            }
            .create().show()
    }
}
