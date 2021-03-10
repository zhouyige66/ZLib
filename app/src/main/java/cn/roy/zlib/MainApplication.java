package cn.roy.zlib;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/12 17:12
 * @Version: v1.0
 */
public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Class<?> trace = null;
        try {
            trace = Class.forName("android.os.Trace");
            Method setAppTracingAllowed = trace.getDeclaredMethod("setAppTracingAllowed", boolean.class);
            setAppTracingAllowed.invoke(null, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
