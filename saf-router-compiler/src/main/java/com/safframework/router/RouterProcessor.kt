package com.safframework.router

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * Created by Tony Shen on 2017/1/10.
 */
class RouterProcessor: AbstractProcessor() {

    var mFiler: Filer?=null //文件相关的辅助类
    var mElementUtils: Elements?=null //元素相关的辅助类
    var mMessager: Messager?=null //日志相关的辅助类

    @Synchronized override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mMessager = processingEnv.messager
    }

    /**
     * @return 指定使用的 Java 版本。通常返回 SourceVersion.latestSupported()。
     */
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    override fun getSupportedAnnotationTypes(): Set<String> {
        val types = LinkedHashSet<String>()
        types.add(RouterRule::class.java.canonicalName)
        return types
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(RouterRule::class.java)

        try {
            val type = getRouterTableInitializer(elements)
            if (type != null) {
                JavaFile.builder("com.safframework.router", type).build().writeTo(mFiler)
            }
        } catch (e: FilerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            Utils.error(mMessager, e.message)
        }

        return true
    }

    @Throws(ClassNotFoundException::class)
    private fun getRouterTableInitializer(elements: Set<Element>?): TypeSpec? {
        if (elements == null || elements.size == 0) {
            return null
        }

        val routerInitBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeUtils.CONTEXT, "context")

        routerInitBuilder.addStatement("\$T.getInstance().setContext(context)", TypeUtils.ROUTER)
        routerInitBuilder.addStatement("\$T options = null", TypeUtils.ROUTER_OPTIONS)

        elements.map {
            it as TypeElement
        }.filter(fun(it: TypeElement): Boolean {
            return Utils.isValidClass(mMessager, it, "@RouterRule")
        }).forEach {
            val routerRule = it.getAnnotation(RouterRule::class.java)
            val routerUrls = routerRule.url
            val enterAnim = routerRule.enterAnim
            val exitAnim = routerRule.exitAnim
            if (routerUrls != null) {
                for (routerUrl in routerUrls) {
                    if (enterAnim > 0 && exitAnim > 0) {
                        routerInitBuilder.addStatement("options = new \$T()", TypeUtils.ROUTER_OPTIONS)
                        routerInitBuilder.addStatement("options.enterAnim = " + enterAnim)
                        routerInitBuilder.addStatement("options.exitAnim = " + exitAnim)
                        routerInitBuilder.addStatement("\$T.getInstance().map(\$S, \$T.class,options)", TypeUtils.ROUTER, routerUrl, ClassName.get(it))
                    } else {
                        routerInitBuilder.addStatement("\$T.getInstance().map(\$S, \$T.class)", TypeUtils.ROUTER, routerUrl, ClassName.get(it))
                    }
                }
            }
        }

        val routerInitMethod = routerInitBuilder.build()

        return TypeSpec.classBuilder("RouterManager")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(routerInitMethod)
                .build()
    }
}