package com.safframework.router;

import android.app.Activity;
import android.net.Uri;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: com.safframework.router.Mapping.java
 * @author: Tony Shen
 * @date: 2018-03-28 15:30
 */
public class Mapping {

    private String format;
    private Class<? extends Activity> activity;
    private RouterParameter.RouterOptions options;
    private MethodInvoker method;

    public Mapping(String format,Class<? extends Activity> activity,RouterParameter.RouterOptions options,MethodInvoker method) {

        if (format == null) {
            throw new RouterException("format can not be null");
        }
        this.format = format;
        this.activity = activity;
        this.options = options;
        this.method = method;

    }

    public String getFormat() {
        return format;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public RouterParameter.RouterOptions getOptions() {
        return options;
    }

    public MethodInvoker getMethod() {
        return method;
    }
}
