/**
 * 
 */
package com.safframework.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 要使用Router功能时，必须在application中做好router的映射<br>
 * 从某个Activity跳转到SecondActivity需要传递user、password2个参数，需要做这样的映射
 * <pre>
 * <code>
 * Router.getInstance().map("user/:user/password/:password", SecondActivity.class);
 * </code>
 * </pre>
 * 跳转时可以增加动画效果
 * <pre>
 * <code>
 * RouterOptions options = new RouterOptions();
 * options.enterAnim = R.anim.slide_left_in;
 * options.exitAnim = R.anim.slide_right_out;
 * Router.getInstance().map("user/:user/password/:password", SecondActivity.class, options);
 * </code>
 * </pre>
 * Intent Router可以完成各个Intent之间的跳转，类似rails的router功能
 * @author Tony Shen
 *
 */
public class Router {
	
	public static final int DEFAULT_CACHE_SIZE = 1024;
	
	private Context context;
//	private LruCache<String, RouterParameter> cachedRoutes = new LruCache<String, RouterParameter>(DEFAULT_CACHE_SIZE); // 缓存跳转的参数

	private final Map<String, Mapping> routes = new HashMap<String, Mapping>();
	private Class errorActivityClass;
	
	private static final Router router = new Router();
	
	private Router() {
	}
	
	public static Router getInstance() {
		return router;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 设置全局错误的Activity，如果路由服务找不到对应的Activity则跳转到errorActivity，防止app引起crash，它是一种降级策略。
	 * 全局错误的Activity需要开发者自己创建。
	 * @param errorActivityClass
	 */
	public void setErrorActivity(Class errorActivityClass) {
		this.errorActivityClass = errorActivityClass;
	}

	/******************************** map 相关操作 start ********************************／

	/**
	 *
	 * @param format
	 * @param method
	 */
	public void map(String format,MethodInvoker method) {

		routes.put(format,new Mapping(format, null,null, method));
	}

	/**
	 * @param format 形如"user/:user/password/:password"这样的格式，其中user、password为参数名
	 * @param clazz
	 */
	public void map(String format, Class<? extends Activity> clazz) {
		this.map(format, clazz, null);
	}

	public void map(String format, Class<? extends Activity> clazz,RouterParameter.RouterOptions options) {
		if (options == null) {
			options = new RouterParameter.RouterOptions();
		}
		options.clazz = clazz;
		routes.put(format,new Mapping(format,clazz,options));
	}

	/******************************** map 相关操作 end ********************************／

	/******************************** openURI 相关操作 start ********************************／

	/**
	 * 使用openURI的相关方法，无需调用map()存到路由表
	 *
	 * 跳转到网页，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("http://www.g.cn");
	 * </code>
	 * </pre>
	 * 
	 * 调用系统电话，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("tel://18662430000");
	 * </code>
	 * </pre>
	 * 
	 * 调用手机上的地图app，打开地图，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("geo:0,0?q=31,121");
	 * </code>
	 * </pre>
	 * @param url
	 */
	public void openURI(String url) {
		this.openURI(url,this.context);
	}
	
	public void openURI(String url,Context context) {
		this.openURI(url, context, null);
	}
	
	public void openURI(String url,Context context,Bundle extras) {
		openURI(url,context,extras,Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public void openURI(String url,Context context,Bundle extras, int flags) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router " + this.toString());
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		this.addFlagsToIntent(intent, context, flags);
		if (extras != null) {
			intent.putExtras(extras);
		}
		
		context.startActivity(intent);
	}

	public void openURI(Uri uri) {
		this.openURI(uri,this.context);
	}

	public void openURI(Uri uri,Context context) {
		this.openURI(uri, context, null);
	}

	public void openURI(Uri uri,Context context,Bundle extras) {
		openURI(uri,context,extras,Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public void openURI(Uri uri,Context context,Bundle extras, int flags) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router " + this.toString());
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		this.addFlagsToIntent(intent, context, flags);
		if (extras != null) {
			intent.putExtras(extras);
		}

		context.startActivity(intent);
	}

	/******************************** openURI 相关操作 end ********************************／


	/******************************** open 相关操作 start ********************************／

	/**
	 * 跳转到某个activity并传值，使用默认的全局的拦截器
	 * <pre>
	 * <code>
	 * Router.getInstance().open("user/fengzhizi715/password/715");
	 * </code>
	 * </pre>
	 * @param url
	 */
	public void open(String url) {
		this.open(url, getGlobalRouteInterceptor(context));
	}
	
	/**
	 * 跳转到某个activity并传值,router跳转前的先判断是否满足跳转的条件
	 * 支持自定义的拦截器
	 * @param url
	 * @param interceptor
	 */
	public void open(String url,RouteInterceptor interceptor) {
		this.open(url,this.context,interceptor);
	}
	
	public void open(String url,Context context,RouteInterceptor interceptor) {
		this.open(url, context, null,interceptor);
	}
	
	public void open(String url,Context context,Bundle extras,RouteInterceptor interceptor) {
		open(url,context,extras,Intent.FLAG_ACTIVITY_NEW_TASK,interceptor); // 默认的跳转类型,将Activity放到一个新的Task中
	}
	
	public void open(String url,Context context,Bundle extras,int flags,RouteInterceptor interceptor) {

		if (context == null) {
			throw new RouterException("You need to supply a context for Router "+ this.toString());
		}
		
		if (interceptor!=null && !interceptor.intercept(context,url)) {
			return;
		}

		RouterParameter param = parseUrl(url);
		RouterParameter.RouterOptions options = param.routerOptions;
		
		Intent intent = this.parseRouterParameter(param);
		if (intent == null) {
			return;
		}
		if (extras != null) {
			intent.putExtras(extras);
		}
		this.addFlagsToIntent(intent, context, flags);
		
		context.startActivity(intent);
		
		if (options.enterAnim>0 && options.exitAnim>0) {
			if (context instanceof Activity) { // 如果需要使用动画,context需要开发者传入
				((Activity)context).overridePendingTransition(options.enterAnim, options.exitAnim);
			}
		}
	}

    /******************************** open 相关操作 end ********************************／

	/******************************** openForResult 相关操作 start ********************************／

	/**
	 *
	 * @param url
	 * @param context
	 * @param requestCode
	 */
	public void openForResult(String url,Activity context,int requestCode) {
		this.openForResult(url,context,requestCode, getGlobalRouteInterceptor(context));
	}

	public void openForResult(String url,Activity context,int requestCode,RouteInterceptor interceptor) {
		this.openForResult(url,context,requestCode,null,interceptor);
	}

	public void openForResult(String url,Activity context,int requestCode,Bundle extras,RouteInterceptor interceptor) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router "+ this.toString());
		}

		if (interceptor!=null && !interceptor.intercept(context,url)) {
			return;
		}

		RouterParameter param = parseUrl(url);
		RouterParameter.RouterOptions options = param.routerOptions;

		Intent intent = this.parseRouterParameter(param);
		if (intent == null) {
			return;
		}
		if (extras != null) {
			intent.putExtras(extras);
		}

		context.startActivityForResult(intent,requestCode);

		if (options.enterAnim>0 && options.exitAnim>0) {
			if (context instanceof Activity) {
				((Activity)context).overridePendingTransition(options.enterAnim, options.exitAnim);
			}
		}
	}

	/******************************** openForResult 相关操作 end ********************************／

	/******************************** openFragment 相关操作 start ********************************／

	 /**
	 *
	 * @param fragmentOptions
	 * @param containerViewId
	 */
	public void openFragment(FragmentOptions fragmentOptions, int containerViewId) {
		if (!(fragmentOptions != null
				&& fragmentOptions.mFragmentInstnace != null
				&& fragmentOptions.fragmentManager != null))
			return;
		
		if (fragmentOptions.mArg!=null) {
			fragmentOptions.mFragmentInstnace.setArguments(fragmentOptions.mArg);
		}
		
		fragmentOptions.fragmentManager.beginTransaction().replace(containerViewId , fragmentOptions.mFragmentInstnace).addToBackStack(null).commit();
	}
	
	public void openFragment(String url,FragmentOptions fragmentOptions,int containerViewId) {
		if (!(fragmentOptions != null
				&& fragmentOptions.mFragmentInstnace != null
				&& fragmentOptions.fragmentManager != null))
			return;
		
		Fragment fragment = parseFragmentUrl(url,fragmentOptions);
		
		fragmentOptions.fragmentManager.beginTransaction().replace(containerViewId , fragment).addToBackStack(null).commit();
	}

    /******************************** openFragment 相关操作 end ********************************／

	 /**
	 *
	 * @param url
	 * @param fragmentOptions
	 * @return
	 */
	private Fragment parseFragmentUrl(String url, FragmentOptions fragmentOptions) {
		String[] givenParts = url.split("/");
		int length = givenParts.length;
		
		if (length > 0) {
			if (fragmentOptions.mArg==null) {
				fragmentOptions.mArg = new Bundle();
			}
			
			for (int i=0;i<length;i=i+2){
				fragmentOptions.mArg.putString(givenParts[i], givenParts[i+1]);
			}
			
			fragmentOptions.mFragmentInstnace.setArguments(fragmentOptions.mArg);
		}

		return fragmentOptions.mFragmentInstnace;
	}

	/**
	 * intent增加额外的flag
	 * @param intent
	 * @param context
	 * @param flags
	 */
	private void addFlagsToIntent(Intent intent, Context context,int flags) {
		intent.addFlags(flags);
	}

	private Intent parseRouterParameter(RouterParameter param) {
		RouterParameter.RouterOptions options = param.routerOptions;
		Intent intent = new Intent();

		if (param.openParams!=null) {
			for (Entry<String, String> entry : param.openParams.entrySet()) {
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}
		
		intent.setClass(context, options.clazz);
		
		return intent;
	}

	private RouterParameter parseUrl(String url) {
//		if (this.cachedRoutes.get(url) != null) {
//			return this.cachedRoutes.get(url);
//		}

		String[] givenParts = url.split("/");

		RouterParameter.RouterOptions openOptions = null;
		RouterParameter openParams = null;
		for (Entry<String, Mapping> entry : this.routes.entrySet()) {
			String routerUrl = entry.getKey();
			Mapping mapping = entry.getValue();
			RouterParameter.RouterOptions routerOptions = mapping.getOptions();
			String[] routerParts = routerUrl.split("/");

			if (routerParts.length != givenParts.length) {
				continue;
			}

			Map<String, String> givenParams = urlToParamsMap(givenParts, routerParts);
			if (givenParams == null) {
				continue;
			}

			openOptions = routerOptions;
			openParams = new RouterParameter();
			openParams.openParams = givenParams;
			openParams.routerOptions = routerOptions;
			openParams.matchType = mapping.getMatchType();
			break;
		}

		if (openOptions == null || openParams == null) {

			if (errorActivityClass!=null){
				openParams = new RouterParameter();
				openParams.routerOptions = new RouterParameter.RouterOptions();
				openParams.routerOptions.clazz = errorActivityClass;
				return openParams;
			} else {
				throw new RouterException("No route found for url " + url);
			}
		}

//		this.cachedRoutes.put(url, openParams);
		return openParams;
	}

	private Map<String, String> urlToParamsMap(String[] givenUrlSegments, String[] routerUrlSegments) {
		Map<String, String> formatParams = new HashMap<String, String>();
		int length = routerUrlSegments.length;
		for (int index = 0; index < length; index++) {
			String routerPart = routerUrlSegments[index];
			String givenPart = givenUrlSegments[index];

			if (routerPart.charAt(0) == ':') {
				String key = routerPart.substring(1, routerPart.length());
				formatParams.put(key, givenPart);
				continue;
			}

			if (!routerPart.equals(givenPart)) { // 奇数个参数进行参数名的匹配，如果不相等则退出
				return null;
			}
		}

		return formatParams;
	}
	
	/**
	 * App退出时，建议清空缓存数据
	 */
	public void clear() {
//		cachedRoutes.evictAll();
	}


	/**
	 * 返回全局的拦截器
	 * 全局的拦截器需要Application去实现RouteInterceptorProvider接口
	 *
	 * @param context
	 * @return
	 */
	private RouteInterceptor getGlobalRouteInterceptor(Context context) {

		return context.getApplicationContext() instanceof RouteInterceptorProvider ? ((RouteInterceptorProvider) context.getApplicationContext()).provideRouteInterceptor() : null;
	}
}
