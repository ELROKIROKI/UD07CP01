package com.jlara.ud06cp01.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jlara.ud06cp01.R
import com.jlara.ud06cp01.databinding.ItemPeliculaBinding
import com.jlara.ud06cp01.model.Pelicula

class PeliculaViewHolder(private val binding: ItemPeliculaBinding) : ViewHolder(binding.root) {

    fun render(pelicula: Pelicula, onClickListener: (Pelicula) -> Unit) {
        binding.tvTitulo.text = pelicula.titulo
        binding.tvAnio.text = binding.root.context.getString(R.string.detalle_anio, pelicula.anio)
        Glide.with(binding.ivPoster.context)
            .load(pelicula.imagen)
            .fitCenter()
            .placeholder(R.drawable.oscar)
            .error(R.drawable.oscar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivPoster)
        binding.root.setOnClickListener { onClickListener(pelicula) }
    }
}
