package com.msoula.convention

@DslMarker
annotation class MultiplatformDSL

@MultiplatformDSL
open class MultiplatformConfigExtension {
    var useFirebase: Boolean = false
    var useDecompose: Boolean = false
    var useDecomposeWithCompose: Boolean = false
    var useCoil: Boolean = false

    fun useFirebase() { useFirebase = true }
    fun useDecompose() { useDecompose = true }
    fun useDecomposeWithCompose() { useDecomposeWithCompose = true }
    fun useCoil() { useCoil = true }
}
