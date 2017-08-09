package com.safframework.module2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.safframework.router.Module
import com.safframework.router.RouterRule

@Module(value = "module2")
@RouterRule(url = arrayOf("module2/main"))
class Module2MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.module2_activity_main)

        Toast.makeText(this, "module2 main", Toast.LENGTH_SHORT).show()
    }
}
