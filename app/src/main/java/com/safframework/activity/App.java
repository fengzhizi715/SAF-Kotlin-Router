package com.safframework.activity;

import android.app.Application;

import com.safframework.router.Router;
import com.safframework.router.RouterManager;

/**
 * Created by Tony Shen on 2017/1/10.
 */

public class App extends Application {

    public void onCreate() {
        super.onCreate();
        RouterManager.init(this);// 这一步是必须的，用于初始化Router

        Router.getInstance().setErrorActivity(ErrorActivity.class);
    }
}
