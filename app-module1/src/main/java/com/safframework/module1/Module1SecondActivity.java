package com.safframework.module1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.safframework.router.RouterRule;

/**
 * Created by Tony Shen on 2017/8/10.
 */

@RouterRule(url={"module1/second/:id"})
public class Module1SecondActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i!=null) {
            String id = i.getStringExtra("id");
            Toast.makeText(this,"module1 second id="+id,Toast.LENGTH_SHORT).show();
        }
    }
}
