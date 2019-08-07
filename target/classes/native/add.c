#include "com_zk_demo_jni_AddJni.h"
JNIEXPORT jint JNICALL Java_com_zk_demo_jni_AddJni_Add
  (JNIEnv * env, jclass jc, jint x, jint y){
    return x+y;
  }