package com.safframework.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.safframework.router.RouterRule;

/**
 * Created by Tony Shen on 2017/1/10.
 */
@RouterRule(url={"second/:second","detail/:detailId"})
public class SecondActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent i = getIntent();
        if (i!=null) {
            String second = i.getStringExtra("second");
            Log.i("SecondActivity","second="+second);

            String detailId = i.getStringExtra("detailId");
            Log.i("SecondActivity","detailId="+detailId);
        }

        TextView textView = (TextView) findViewById(R.id.back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "从SecondActivity返回");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
