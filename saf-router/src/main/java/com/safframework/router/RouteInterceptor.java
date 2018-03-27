package com.safframework.router;

import android.content.Context;

/**
 * Router的拦截器
 * Created by tony on 2017/9/25.
 */

public interface RouteInterceptor {

    /**
     * 跳转之前的拦截
     * @param context
     * @param url
     * @return
     */
    boolean beforeOpen(Context context, String url);
}
