package com.safframework.router

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * Created by Tony Shen on 2017/1/10.
 */
class RouterProcessor: AbstractProcessor() {

    var mFiler: Filer?=null           //文件相关的辅助类
    var mElementUtils: Elements?=null //元素相关的辅助类
    var mMessager: Messager?=null     //日志相关的辅助类

    val ACTIVITY_FULL_NAME = "android.app.Activity"
    val FRAGMENT_FULL_NAME = "android.app.Fragment"
    val FRAGMENT_V4_FULL_NAME = "android.support.v4.app.Fragment"

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mMessager = processingEnv.messager
    }

    /**
     * @return 指定使用的 Java 版本 通常返回 SourceVersion.latestSupported()。
     */
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    override fun getSupportedAnnotationTypes(): Set<String> {
        val types = LinkedHashSet<String>()
        types.add(RouterAction::class.java.canonicalName)
        types.add(RouterRule::class.java.canonicalName)
        types.add(Module::class.java.canonicalName)
        types.add(Modules::class.java.canonicalName)
        return types
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        var hasModule = false
        var hasModules = false

        // module
        var moduleName = "RouterMapping"
        val moduleList = roundEnv.getElementsAnnotatedWith(Module::class.java)
        if (moduleList.isNotEmpty()) {
            var annotation = moduleList.iterator().next().getAnnotation(Module::class.java)
            moduleName = moduleName + "_" + annotation.value;
            hasModule = true
        }

        // modules
        var moduleNames:Array<out String>? = null
        var modulesList = roundEnv.getElementsAnnotatedWith(Modules::class.java)
        if (modulesList.isNotEmpty()) {
            val modules = modulesList.iterator().next()
            moduleNames = modules.getAnnotation(Modules::class.java).value
            hasModules = true
        }

        if (hasModules) {
            generateModulesRouterInit(moduleNames)
        }

        if (hasModule){
            generateModuleRouterMap(moduleName,roundEnv)
        } else {
            generateDefaultRouterInit(roundEnv)
        }

        return true
    }

    private fun generateModulesRouterInit(moduleNames: Array<out String>?) {

        if (moduleNames == null) return

        val initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeUtils.CONTEXT, "context")

        initMethod.addStatement("\$T.getInstance().setContext(context)", TypeUtils.ROUTER)

        for (module in moduleNames) {
            initMethod.addStatement("RouterMapping_$module.map()")
        }

        val routerInit = TypeSpec.classBuilder("RouterManager")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(initMethod.build())
                .build()
        try {
            JavaFile.builder("com.safframework.router", routerInit)
                    .build()
                    .writeTo(mFiler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun  generateModuleRouterMap(moduleName: String, roundEnv: RoundEnvironment) {

        val routerRuleElements = roundEnv.getElementsAnnotatedWith(RouterRule::class.java)

        val routerMapBuilder = MethodSpec.methodBuilder("map")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)

        if (routerRuleElements.isNotEmpty()) {

            routerMapBuilder.addStatement("\$T options = null", TypeUtils.ROUTER_OPTIONS)

            routerRuleElements
                    .map {
                        it as TypeElement
                    }.filter(fun(it: TypeElement): Boolean {
                        return isValidClass(mMessager, it, "@RouterRule")
                    }).forEach {

                        if (isSubtype(processingEnv, it, ACTIVITY_FULL_NAME)) {
                            val routerRule = it.getAnnotation(RouterRule::class.java)
                            val routerUrls = routerRule.url
                            val enterAnim = routerRule.enterAnim
                            val exitAnim = routerRule.exitAnim
                            if (routerUrls != null) {
                                for (routerUrl in routerUrls) {

                                    if (enterAnim > 0 && exitAnim > 0) {
                                        routerMapBuilder.addStatement("options = new \$T()", TypeUtils.ROUTER_OPTIONS)
                                        routerMapBuilder.addStatement("options.enterAnim = " + enterAnim)
                                        routerMapBuilder.addStatement("options.exitAnim = " + exitAnim)
                                        routerMapBuilder.addStatement("\$T.getInstance().map(\$S, \$T.class,options)", TypeUtils.ROUTER, routerUrl, ClassName.get(it))
                                    } else {
                                        routerMapBuilder.addStatement("\$T.getInstance().map(\$S, \$T.class)", TypeUtils.ROUTER, routerUrl, ClassName.get(it))
                                    }
                                }
                            }
                        } else if (isSubtype(processingEnv, it, FRAGMENT_V4_FULL_NAME)) {

                            val routerRule = it.getAnnotation(RouterRule::class.java)
                            val routerUrls = routerRule.url
                            if (routerUrls != null) {
                                for (routerUrl in routerUrls) {

                                    routerMapBuilder.addStatement("\$T.getInstance().map(\$S, \$T.PATH_FRAGMNET)", TypeUtils.ROUTER, routerUrl, TypeUtils.MATCH_TYPE)
                                }
                            }
                        }

                    }
        }

        val routerActionElements = roundEnv.getElementsAnnotatedWith(RouterAction::class.java)

        if (routerActionElements.isNotEmpty()) {

            routerActionElements
                    .forEach {
                        val routerAction = it.getAnnotation(RouterAction::class.java)
                        val action = routerAction.value
                        val className = ClassName.get(it.enclosingElement as TypeElement);
                        val methodName = it.simpleName

                        routerMapBuilder.addStatement("\$T.getInstance().map(\$S, " +
                                "new MethodInvoker() {\n" +
                                "   public void invoke(android.content.Context context, android.os.Bundle bundle) {\n" +
                                "       \$T.\$N(context, bundle);\n" +
                                "   }\n" +
                                "})", TypeUtils.ROUTER, action, className ,methodName)
                    }
        }

        val routerMapMethod = routerMapBuilder.build()

        val type =  TypeSpec.classBuilder(moduleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(routerMapMethod)
                .build()

        if (type != null) {
            JavaFile.builder("com.safframework.router", type).build().writeTo(mFiler)
        }
    }

    /**
     * 不使用模块化，通过apt生成的路由表
     */
    @Throws(ClassNotFoundException::class)
    private fun generateDefaultRouterInit(roundEnv: RoundEnvironment) {

        val routerRuleElements = roundEnv.getElementsAnnotatedWith(RouterRule::class.java)

        val routerActionElements = roundEnv.getElementsAnnotatedWith(RouterAction::class.java)

        if (routerRuleElements.isEmpty() && routerActionElements.isEmpty()) return

        val routerInitBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeUtils.CONTEXT, "context")

        if (routerRuleElements.isNotEmpty()) {

            routerInitBuilder.addStatement("\$T.getInstance().setContext(context)", TypeUtils.ROUTER)

            routerInitBuilder.addStatement("\$T options = null", TypeUtils.ROUTER_OPTIONS)

            routerRuleElements
                    .map {
                        it as TypeElement
                    }.filter(fun(it: TypeElement): Boolean {
                        return isValidClass(mMessager, it, "@RouterRule")
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
        }

        if (routerActionElements.isNotEmpty()) {

            routerActionElements
                    .forEach {
                        val routerAction = it.getAnnotation(RouterAction::class.java)
                        val action = routerAction.value
                        val className = ClassName.get(it.enclosingElement as TypeElement);
                        val methodName = it.simpleName

                        routerInitBuilder.addStatement("\$T.getInstance().map(\$S, " +
                                "new MethodInvoker() {\n" +
                                "   public void invoke(android.content.Context context, android.os.Bundle bundle) {\n" +
                                "       \$T.\$N(context, bundle);\n" +
                                "   }\n" +
                                "})", TypeUtils.ROUTER, action, className ,methodName)
                    }
        }

        val routerInitMethod = routerInitBuilder.build()

        val type =  TypeSpec.classBuilder("RouterManager")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(routerInitMethod)
                .build()

        if (type != null) {
            JavaFile.builder("com.safframework.router", type).build().writeTo(mFiler)
        }
    }
}