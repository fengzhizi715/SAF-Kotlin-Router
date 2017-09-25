package com.safframework.router;

import android.content.Context;

/**
 * Created by tony on 2017/9/25.
 */

public interface RouterCallback {

    boolean beforeOpen(Context context, String url);
}
