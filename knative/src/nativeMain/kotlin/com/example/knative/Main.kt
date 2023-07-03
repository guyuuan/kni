package com.example.knative

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import platform.android.JNIEnvVar
import platform.android.jobject
import platform.android.jstring
@CName("Java_com_example_knative_SayHello_sayHello")
fun sayHello(env: CPointer<JNIEnvVar>, thiz: jobject): jstring {
    memScoped {
        val str = "Hi,\nI'm kotlin/native,\nget string from C:\n${getString()}"
        return env.pointed.pointed!!.NewStringUTF!!.invoke(env, str.cstr.ptr)!!
    }
}

expect fun getString():String