package com.safframework.router;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: com.safframework.router.Mapping.java
 * @author: Tony Shen
 * @date: 2018-03-28 15:30
 */
public class Mapping {

    private String format;
    private Class<? extends Activity> activity;
    private Fragment fragment;
    private RouterParameter.RouterOptions options;
    private MethodInvoker method;
    private MatchType matchType;

    public Mapping(String format, Class<? extends Activity> activity, RouterParameter.RouterOptions options) {

        this(format, activity, options, null);
    }

    public Mapping(String format, Class<? extends Activity> activity, RouterParameter.RouterOptions options, MethodInvoker method) {

        this(format, activity, options, method, null);
    }

    public Mapping(String format, Class<? extends Activity> activity, RouterParameter.RouterOptions options, MethodInvoker method, MatchType matchType) {

        this(format, activity, null, options, method, null);
    }

    public Mapping(String format, Class<? extends Activity> activity, Fragment fragment, RouterParameter.RouterOptions options, MethodInvoker method, MatchType matchType) {

        if (format == null) {
            throw new RouterException("format can not be null");
        }
        this.format = format;
        this.activity = activity;
        this.fragment = fragment;
        this.options = options;
        this.method = method;
        this.matchType = matchType;

        if (matchType != null) {

            this.matchType = matchType;
        } else {

            if (format.toLowerCase().startsWith("http://") || format.toLowerCase().startsWith("https://")) {
                this.matchType = MatchType.BROWSER;
            } else if (format.contains("://")) {
                this.matchType = MatchType.SCHEME;
            } else if (activity != null) {
                this.matchType = MatchType.PATH_ACTITY;
            } else if (fragment != null) {
                this.matchType = MatchType.PATH_FRAGMNET;
            } else {
                this.matchType = MatchType.PATH_ACTION;
            }
        }
    }

    public String getFormat() {
        return format;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public RouterParameter.RouterOptions getOptions() {
        return options;
    }

    public MethodInvoker getMethod() {
        return method;
    }

    public MatchType getMatchType() {
        return matchType;
    }
}
