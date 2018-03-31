package com.safframework.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.safframework.router.RouterAction;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: com.safframework.activity.TestActions.java
 * @author: Tony Shen
 * @date: 2018-03-30 16:34
 */
public class TestActions {

    @RouterAction("logout")
    public static void logout(Context context, Bundle bundle) {
        Toast.makeText(context, "logout", Toast.LENGTH_SHORT).show();
    }
}
