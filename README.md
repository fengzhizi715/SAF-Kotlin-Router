# SAF-Kotlin-Router

[![@Tony沈哲 on weibo](https://img.shields.io/badge/weibo-%40Tony%E6%B2%88%E5%93%B2-blue.svg)](http://www.weibo.com/fengzhizi715)
[![License](https://img.shields.io/badge/license-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

![](logo.JPG)

# 最新版本

模块|saf-router|saf-router-compiler|saf-router-annotation
---|:-------------:|:-------------:|:-------------:
最新版本|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/saf-router/images/download.svg) ](https://bintray.com/fengzhizi715/maven/saf-router/_latestVersion)|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/saf-router-compiler/images/download.svg) ](https://bintray.com/fengzhizi715/maven/saf-router-compiler/_latestVersion)|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/saf-router-annotation/images/download.svg) ](https://bintray.com/fengzhizi715/maven/saf-router-annotation/_latestVersion)

# 下载安装

在根目录下的build.gradle中添加

```groovy
 buildscript {
     repositories {
         jcenter()
     }
     dependencies {
         classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
     }
 }
```

在app 模块目录下的build.gradle中添加

```groovy
apply plugin: 'com.neenbedankt.android-apt'

...

dependencies {
    compile 'com.safframework.router:saf-router:1.1.1'
    apt 'com.safframework.router:saf-router-compiler:1.1.0'
    ...
}
```

# 特性
它提供了类似于rails的router功能，可以轻易地实现app的应用内跳转,包括Activity之间、Fragment之间实现相互跳转，并传递参数。

这个框架的saf-router-compiler模块是用`kotlin`编写的。

# 使用方法

## 1. Activity跳转

它支持Annotation方式和非Annotation的方式来进行Activity页面跳转。使用Activity跳转时，必须在App的Application中做好router的映射。 

我们会做这样的映射，表示从某个Activity跳转到另一个Activity需要传递user、password这2个参数

```Java
Router.getInstance().setContext(getApplicationContext()); // 这一步是必须的，用于初始化Router
Router.getInstance().map("user/:user/password/:password", DetailActivity.class);
```

有时候，activity跳转还会有动画效果，那么我们可以这么做

```Java
RouterOptions options = new RouterOptions();
options.enterAnim = R.anim.slide_right_in;
options.exitAnim = R.anim.slide_left_out;
Router.getInstance().map("user/:user/password/:password", DetailActivity.class, options);
```

### Annotation方式
在任意要跳转的目标Activity上，添加@RouterRule,它是编译时的注解。

```java
@RouterRule(url={"second/:second"})
public class SecondActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i!=null) {
            String second = i.getStringExtra("second");
            Log.i("SecondActivity","second="+second);
        }
    }
}

```
而且，使用@RouterRule也支持跳转的动画效果。

用Annotation方式来进行页面跳转时，Application无需做router的映射。因为，saf-router-compiler模块已经在编译时生成了一个类RouterManager。它长得形如：

```java
package com.safframework.router;

import android.content.Context;
import com.safframework.activity.SecondActivity;
import com.safframework.router.RouterParameter.RouterOptions;

public class RouterManager {
  public static void init(Context context) {
    Router.getInstance().setContext(context);
    RouterOptions options = null;
    Router.getInstance().map("second/:second", SecondActivity.class);
  }
}
```

Application只需做如下调用：

```java
RouterManager.init(this);// 这一步是必须的，用于初始化Router

```

### 非Annotation方式

在Application中定义好router映射之后，activity之间跳转只需在activity中写下如下的代码，即可跳转到相应的Activity，并传递参数
```Java
Router.getInstance().open("user/fengzhizi715/password/715");
```

如果在跳转前需要先做判断，看看是否满足跳转的条件,doCheck()返回false表示不跳转，true表示进行跳转到下一个activity

```Java
Router.getInstance().open("user/fengzhizi715/password/715",new RouterChecker(){

     public boolean doCheck() {
           return true;
      }
 );
```

## 2. Fragment跳转
Fragment之间的跳转也无须在Application中定义跳转映射。直接在某个Fragment写下如下的代码
```Java
Router.getInstance().openFragment(new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);
```

当然在Fragment之间跳转可以传递参数
```Java
Router.getInstance().openFragment("user/fengzhizi715/password/715",new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);
```

## 3. 其他跳转
单独跳转到某个网页，调用系统电话，调用手机上的地图app打开地图等无须在Application中定义跳转映射。

```Java
Router.getInstance().openURI("http://www.g.cn");

Router.getInstance().openURI("tel://18662430000");

Router.getInstance().openURI("geo:0,0?q=31,121");
```

## 4. 模块化

## 5. 支持Kotlin项目
在module目录下的build.gradle中添加

```groovy
apply plugin: 'kotlin-kapt'

...

dependencies {
    compile 'com.safframework.router:saf-router:1.1.1'
    kapt 'com.safframework.router:saf-router-compiler:1.1.0'
    ...
}
```

注意，apply plugin: 'com.neenbedankt.android-apt' 无需再使用了。




TODO
===
1. 更优雅的API
2. 增加降级策略
3. 使用http/https地址时,可以直接跳转至使用浏览器

License
-------

    Copyright (C) 2017 Tony Shen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



