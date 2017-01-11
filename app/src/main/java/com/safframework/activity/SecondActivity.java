package com.safframework.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.safframework.router.RouterRule;

/**
 * Created by Tony Shen on 2017/1/10.
 */
@RouterRule(url={"second/:second","detail/:detailId"})
public class SecondActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i!=null) {
            String second = i.getStringExtra("second");
            Log.i("SecondActivity","second="+second);

            String detailId = i.getStringExtra("detailId");
            Log.i("SecondActivity","detailId="+detailId);
        }
    }
}
