package com.example.pokedex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Objects

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val type: List<String>,
    val description: String,
    val image_url: String,
    val evolutions: Evolution
)