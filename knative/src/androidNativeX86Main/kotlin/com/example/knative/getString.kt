package com.example.knative
import hello.x86.say
import kotlinx.cinterop.toKString

actual fun getString(): String {
    return say()!!.toKString()
}