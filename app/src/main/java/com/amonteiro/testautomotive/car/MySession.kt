package com.amonteiro.testautomotive.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class MySession : Session() {

    //https://developer.android.com/training/cars/apps?hl=fr#create-carappservice
    override fun onCreateScreen(intent: Intent): Screen {
        //1er écran que verra l'utilisateur
        //Mais en fonction de l'Intent on pourrait ajouter des écrans à la stack d'écran
        return MainScreen(carContext)
    }
}
