package com.msoula.convention

@DslMarker
annotation class MultiplatformDSL

@MultiplatformDSL
open class MultiplatformConfigExtension {
    var enableAndroid: Boolean = false
    var enableIos: Boolean = false
    var useFirebase: Boolean = false
    var useCoil: Boolean = false
    var useCompose: Boolean = false

    fun useFirebase() {
        useFirebase = true
    }

    fun useCoil() {
        useCoil = true
    }
}
