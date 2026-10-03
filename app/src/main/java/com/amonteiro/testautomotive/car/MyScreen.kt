package com.amonteiro.testautomotive.car

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.amonteiro.testautomotive.data.Weather
import com.amonteiro.testautomotive.data.WeatherApiDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyScreen(carContext: CarContext) : Screen(carContext) {

    var items = emptyList<Weather>()
    var errorMessage: String? = null
    var loading = true

    init {// Action à l'arrivée sur l'écran
        loadWeathers("Paris")
    }

    fun loadWeathers(cityName: String) {
        loading = true
        errorMessage = null
        invalidate()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                items = WeatherApiDataSource.loadWeathers(cityName)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Une erreur est survenue"
            }
            loading = false
            invalidate() // Rafraîchir l'écran
        }
    }

    //Utilisation de SearchTemplate pour demander la ville
    override fun onGetTemplate(): Template {

        //Création de la liste
        val itemListBuilder = ItemList.Builder()

        //Création des lignes
        items.forEach { weather ->
            val row = Row.Builder()
                .setTitle(weather.name)
                .addText("${weather.temp}° - ${weather.description}")
                .addText("Vent : ${weather.speed} m/s")
                .setOnClickListener {
                    CarToast.makeText(carContext, weather.getResume(), CarToast.LENGTH_LONG).show()
                }
                .build()

            itemListBuilder.addItem(row)
        }

        val builder = SearchTemplate.Builder(
            object : SearchTemplate.SearchCallback {
                override fun onSearchSubmitted(searchText: String) {
                    loadWeathers(searchText)
                }
            }
        )
            .setHeaderAction(Action.BACK)
            .setSearchHint("Rechercher une ville")

        // Erreur
        if (!errorMessage.isNullOrBlank()) {
            itemListBuilder.setNoItemsMessage(errorMessage!!)
        }

        //Chargement en cours ou résultats
        if (loading) {
            builder.setLoading(true)
        } else {
            builder.setItemList(itemListBuilder.build())
        }

        return builder.build()
    }
}
