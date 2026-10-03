package com.amonteiro.testautomotive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amonteiro.testautomotive.data.Weather
import com.amonteiro.testautomotive.data.WeatherApiDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

suspend fun main() {
    val viewModel = MainViewModel()
    println("Avant requête")
    viewModel.loadWeathers("")
    println("Après requête")

    while (viewModel.runInProgress.value) {
        println("Attente...")
        delay(500)
    }

    //Affichage de la liste (qui doit être remplie) contenue dans la donnée observable
    println("List : ${viewModel.dataList.value}")
    println("ErrorMessage : ${viewModel.errorMessage.value}")

    //Pour que le programme s'arrête, inutile sur Android
    WeatherApiDataSource.close()
}

class MainViewModel : ViewModel() {
    //MutableStateFlow est une donnée observable
    val dataList = MutableStateFlow(emptyList<Weather>())
    val runInProgress = MutableStateFlow(false)
    val errorMessage = MutableStateFlow("")


    fun loadWeathers(cityName: String) {

        runInProgress.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataList.value = WeatherApiDataSource.loadWeathers(cityName)
            } catch (e: Exception) {
                //Affiche la stacktrace
                e.printStackTrace()
                errorMessage.value = e.message ?: "Une erreur est survenue"
            }
            finally {
                runInProgress.value = false
            }
        }

    }


    fun loadFakeData(runInProgress :Boolean = false, errorMessage:String = "" ) {
        this.runInProgress.value = runInProgress
        this.errorMessage.value = errorMessage
        dataList.value = listOf(
            Weather(
                id = 1,
                name = "Paris",
                temp = 18.5,
                description = "ciel dégagé",
                icon = "https://picsum.photos/200",
                speed = 5.0
            ),
            Weather(
                id = 2,
                name = "Toulouse",
                temp = 22.3,
                description = "partiellement nuageux",
                icon = "https://picsum.photos/201",
                speed = 3.2
            ),
            Weather(
                id = 3,
                name = "Toulon",
                temp = 25.1,
                description = "ensoleillé",
                icon = "https://picsum.photos/202",
                speed = 6.7
            ),
            Weather(
                id = 4,
                name = "Lyon",
                temp = 19.8,
                description = "pluie légère",
                icon = "https://picsum.photos/203",
                speed = 4.5
            )
        ).shuffled() //shuffled() pour avoir un ordre différent à chaque appel
    }

}