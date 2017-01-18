package com.safframework.router

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by Tony Shen on 2017/1/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
annotation class RouterRule(
        /** activity对应url  */
        val url: Array<String>, val enterAnim: Int = 0, val exitAnim: Int = 0)