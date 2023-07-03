package com.example.knative

class SayHello {
    companion object {
        init {
//            java.lang.System.loadLibrary("hello")
            System.loadLibrary("knlib")
        }
    }

    external fun sayHello(): String
}