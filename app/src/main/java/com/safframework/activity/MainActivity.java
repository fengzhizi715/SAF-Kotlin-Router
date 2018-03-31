package com.safframework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.safframework.router.Module;
import com.safframework.router.Modules;
import com.safframework.router.Router;

/**
 * Created by Tony Shen on 2017/1/10.
 */

@Module(value = "main")
@Modules(value = {"main","module1","module2"})
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().open("second/2");
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().open("detail/4");
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().openForResult("detail/4",MainActivity.this,REQUEST_CODE);
            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().open("module1/main");
            }
        });

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().open("module1/second/123");
            }
        });

        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().open("module2/main");
            }
        });

        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Router.getInstance().open("https://www.baidu.com/");
            }
        });

        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Router.getInstance().open("logout");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String result = data.getExtras().getString("result");
            if (!TextUtils.isEmpty(result)) {

                Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
            }
        }

    }
}
