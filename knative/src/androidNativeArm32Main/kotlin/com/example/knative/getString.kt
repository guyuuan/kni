package com.example.knative

import hello.armeabi_v7a.say
import kotlinx.cinterop.toKString

actual fun getString(): String {
    return say()!!.toKString()
}