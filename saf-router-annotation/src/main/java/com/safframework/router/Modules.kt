package com.safframework.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Tony Shen on 2017/8/9.
 */
@Retention(RetentionPolicy.CLASS)
annotation class Modules(vararg val value: String)