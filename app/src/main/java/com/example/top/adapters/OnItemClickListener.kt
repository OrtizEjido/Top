package com.example.top.adapters

import com.example.top.utils.Artista

interface OnItemClickListener {

    fun onItemClick(artista: Artista?)
    fun onLongItemClick(artista: Artista?)

}