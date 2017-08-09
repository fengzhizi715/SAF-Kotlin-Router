package com.safframework.module1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.safframework.router.Module;
import com.safframework.router.RouterRule;

/**
 * Created by Tony Shen on 2017/8/10.
 */

@Module(value = "module1")
@RouterRule(url={"module1/main"})
public class Module1MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this,"module1 main",Toast.LENGTH_SHORT).show();
    }
}
