package dev.avadhut.wist

import dev.avadhut.wist.config.configurePlugins
import dev.avadhut.wist.config.configureRouting
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val services = configurePlugins()
    configureRouting(services)
}