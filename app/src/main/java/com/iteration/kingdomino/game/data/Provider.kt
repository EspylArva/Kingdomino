package com.iteration.kingdomino.game.data

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component()
interface Provider {
    fun dataManager(): DataManager
}