package com.safframework.router

import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

/**
 * Created by Tony Shen on 2017/1/10.
 */
object Utils {

    fun isInterface(typeMirror: TypeMirror): Boolean {
        if (typeMirror !is DeclaredType) {
            return false
        }
        return typeMirror.asElement().getKind() === ElementKind.INTERFACE
    }

    /**
     * 判断类描述符是否是public的
     * @param annotatedClass 需要判断的类
     *
     * @return 如果是public的返回true，其他返回false
     */
    fun isPublic(annotatedClass: TypeElement): Boolean {
        return annotatedClass.modifiers.contains(Modifier.PUBLIC)
    }

    /**
     * 判断类描述符是否是abstract的
     * @param annotatedClass 需要判断的类
     *
     * @return 如果是abstract的返回true，其他返回false
     */
    fun isAbstract(annotatedClass: TypeElement): Boolean {
        return annotatedClass.modifiers.contains(Modifier.ABSTRACT)
    }

    @JvmStatic
    fun error(messager: Messager?, msg: String?, vararg args: Any) {

        if (msg==null) return;

        messager?.printMessage(Diagnostic.Kind.ERROR, String.format(msg, *args))
    }

    @JvmStatic
    fun info(messager: Messager?, msg: String?, vararg args: Any) {

        if (msg==null) return;

        messager?.printMessage(Diagnostic.Kind.NOTE, String.format(msg, *args))
    }

    @JvmStatic
    fun isValidClass(messager: Messager?, annotatedClass: TypeElement, annotationName: String): Boolean {

        if (!isPublic(annotatedClass)) {
            val message = String.format("Classes annotated with %s must be public.", annotationName)
            error(messager, message)
            return false
        }

        if (isAbstract(annotatedClass)) {
            val message = String.format("Classes annotated with %s must not be abstract.", annotationName)
            error(messager, message)
            return false
        }

        return true
    }
}