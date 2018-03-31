package com.safframework.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 路由的行为，跟RouterRule是有区别的。
 *
 * @FileName:
 *          com.safframework.router.RouterAction.java
 * @author: Tony Shen
 * @date: 2018-03-31 01:09
 * @version V1.0 <描述当前版本功能>
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.CLASS)
annotation class RouterAction(
        /** action对应url  */
        val value: String)