package com.msoula.convention

@DslMarker
annotation class MultiplatformDSL

@MultiplatformDSL
open class MultiplatformConfigExtension {
    var useFirebase: Boolean = false
    var useCoil: Boolean = false

    fun useFirebase() { useFirebase = true }
    fun useCoil() { useCoil = true }
}
