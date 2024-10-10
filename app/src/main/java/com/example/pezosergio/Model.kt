package com.example.pezosergio

// Los datos como se van a enviar
data class RequestData(val data: List<Any>, val data2: List<Float>)
// los datos como se van a recibir
data class ResponseData(val prediction:List<Int>)
