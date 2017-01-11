package com.safframework.router

import com.squareup.javapoet.ClassName

/**
 * Created by Tony Shen on 2017/1/10.
 */
class TypeUtils {

    companion object {
        val CONTEXT = ClassName.get("android.content", "Context");
        val ROUTER = ClassName.get("com.safframework.router", "Router")
        val ROUTER_OPTIONS = ClassName.get("com.safframework.router.RouterParameter", "RouterOptions")
    }
}