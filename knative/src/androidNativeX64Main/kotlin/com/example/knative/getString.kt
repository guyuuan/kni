package com.example.knative
import hello.x86_64.say
import kotlinx.cinterop.toKString

actual fun getString(): String {
    return  say()!!.toKString()
}