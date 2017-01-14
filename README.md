# SAF-Kotlin-Router

#下载安装

在根目录下的build.gradle中添加

```groovy
 buildscript {
     repositories {
         jcenter()
     }
     dependencies {
         classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
     }
 }
```

在app 模块目录下的build.gradle中添加

```groovy
apply plugin: 'com.neenbedankt.android-apt'

...

dependencies {
    compile 'com.safframework.router:saf-router:1.0.0'
    apt 'com.safframework.router:saf-router-compiler:1.0.0'
    ...
}
```

#特性
它提供了类似于rails的router功能，可以轻易地实现app的应用内跳转,包括Activity之间、Fragment之间可以轻易实现相互跳转，并传递参数。

#使用方法

##Activity跳转

它支持Annotation方式和非Annotation的方式来进行Activity页面跳转。使用Activity跳转时，必须在Application中做好router的映射。 

我们会做这样的映射，表示从某个Activity跳转到另一个Activity需要传递user、password2个参数

```Java
Router.getInstance().setContext(getApplicationContext()); // 这一步是必须的，用于初始化Router
Router.getInstance().map("user/:user/password/:password", SecondActivity.class);
```

有时候，activity跳转还会有动画效果，那么我们可以这么做

```Java
RouterOptions options = new RouterOptions();
options.enterAnim = R.anim.slide_right_in;
options.exitAnim = R.anim.slide_left_out;
Router.getInstance().map("user/:user/password/:password", SecondActivity.class, options);
```

###Annotation方式

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouterRule {

    /** activity对应url */
    String[] url();

    int enterAnim() default 0;

    int exitAnim() default 0;
}
```
@RouterRule，它使用编译时注解。

###非Annotation方式


在Application中定义好映射，activity之间跳转只需在activity中写下如下的代码，即可跳转到相应的Activity，并传递参数
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

##Fragment跳转
Fragment之间的跳转也无须在Application中定义跳转映射。直接在某个Fragment写下如下的代码
```Java
Router.getInstance().openFragment(new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);
```

当然在Fragment之间跳转可以传递参数
```Java
Router.getInstance().openFragment("user/fengzhizi715/password/715",new FragmentOptions(getFragmentManager(),new Fragment2()), R.id.content_frame);
```

##其他跳转
单独跳转到某个网页，调用系统电话，调用手机上的地图app打开地图等无须在Application中定义跳转映射。

```Java
Router.getInstance().openURI("http://www.g.cn");

Router.getInstance().openURI("tel://18662430000");

Router.getInstance().openURI("geo:0,0?q=31,121");
```





