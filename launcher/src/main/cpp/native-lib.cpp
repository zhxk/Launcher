#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_changren_android_launcher_JNIActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
