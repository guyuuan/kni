package com.example.knative

import hello.arm64_v8a.say
import kotlinx.cinterop.toKString

actual fun getString(): String {
    return say()!!.toKString()
}