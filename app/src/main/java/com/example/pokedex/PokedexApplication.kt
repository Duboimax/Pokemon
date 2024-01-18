package com.example.pokedex

import android.app.Application
import com.example.pokedex.data.AppContainer
import com.example.pokedex.data.DefaultAppContainer

class PokedexApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}