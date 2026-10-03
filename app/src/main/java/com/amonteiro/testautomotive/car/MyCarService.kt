package com.amonteiro.testautomotive.car

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class MyCarService : CarAppService() {

    //Appelé quand l'application démarre et qu'une nouvelle session est nécessaire
    //Point d'entrée de l'application
    override fun onCreateSession(): Session {
        return MySession()
    }

    //Qui peut venir appeler le service
    //Autres applications? L'os? ...
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }
}
