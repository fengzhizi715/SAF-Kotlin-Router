package com.safframework.router;

/**
 * 需要Application实现该接口，才能设置全局的RouteInterceptor
 * Created by tony on 2017/9/25.
 */

public interface RouteInterceptorProvider {

    RouteInterceptor provideRouteInterceptor();
}
