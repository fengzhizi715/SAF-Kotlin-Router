package com.safframework.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tony Shen on 2017/1/10.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouterRule {

    /** activity对应url */
    String[] url();

    int enterAnim() default 0;

    int exitAnim() default 0;
}
