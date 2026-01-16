package dev.avadhut.wist

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform