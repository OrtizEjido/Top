package com.example.top.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.R
import com.example.top.database.TopDB
import com.example.top.utils.Artista
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistaAdapter(
    var artistas: MutableList<Artista>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<ViewHolder>() {


    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        this.context = parent.context
        var artistaDAO = TopDB.getInstance(parent.context).artistaDao()
        artistas = artistaDAO.getAll()
        return ArtistaViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder) {
            is ArtistaViewHolder -> {
                val item= artistas[position]
                holder.setListener(item, listener)
                holder.bind(item)
                if (item.fotoUrl != null){
                    var options = RequestOptions()
                    options.diskCacheStrategy(DiskCacheStrategy.ALL)
                    options.centerCrop()
                    options.placeholder(R.drawable.ic_sentiment_satisfied)

                    context?.let {
                        Glide.with(it)
                            .load(item.fotoUrl)
                            .apply(options)
                            .into(holder.imgPhoto)
                    }
                }else{
                    holder.imgPhoto.setImageDrawable(context?.let { ContextCompat.getDrawable(it
                        ,R.drawable.ic_account_edit) })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        //Regresa la cantidad de items en el recycler
        return artistas.size
    }

    fun add(artista: Artista?) {
        if (!artistas.contains(artista)) {
            artistas.add(artista!!)
            notifyDataSetChanged()
        }
    }

    fun submitList(artistas: MutableList<Artista>){
        //actualizamos la lista
        this.artistas = artistas
    }

    fun remove(artista: Artista?) {
        if (artistas.contains(artista)) {
            artistas.remove(artista)
            notifyDataSetChanged()
        }
    }

    class ArtistaViewHolder(itemView: View): ViewHolder(itemView) {

        var imgPhoto: ImageView = itemView.imgPhoto
        var tvNombre: TextView = itemView.tvNombre
        var tvOrden: TextView = itemView.tvOrden
        var containerMain: RelativeLayout = itemView.containerMain



        fun bind (artista: Artista){
            tvNombre.text =artista.nombreCompleto()
            tvOrden.text = artista.orden.toString()
        }

        fun setListener(artista: Artista?, listener: OnItemClickListener) {
            containerMain.setOnClickListener(View.OnClickListener { listener.onItemClick(artista) })
            containerMain.setOnLongClickListener(OnLongClickListener {
                listener.onLongItemClick(artista)
                true
            })
        }
    }

}