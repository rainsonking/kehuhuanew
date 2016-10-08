package kwsoft.coursetabledemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Rainson on 2015/12/11 0011.
 * volley单例模式
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;

    public VolleySingleton(Context context) {
        this.mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache(){
                    private final LruCache<String,Bitmap> cache = new LruCache<String ,Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url){
                        return cache.get(url);
                    }
                    @Override
                    public void putBitmap(String url,Bitmap bitmap){
                        cache.put(url,bitmap);
                    }
                });
    }
    public static synchronized VolleySingleton getVolleySingleton(Context context){
        if(volleySingleton == null){
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static void onErrorResponseMessege(Context context, VolleyError volleyError) {
        //专门处理error的库，下面就是调用了其中的一些，可以方便调试的时候查找到错误
        Log.d("TAG", "Volley returned error________________:" + volleyError);
        Class errorClass = volleyError.getClass();
        if (errorClass == com.android.volley.NetworkError.class) {
            Log.d("TAG", "NetworkError");
            Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG).show();
        } else if (errorClass == com.android.volley.NoConnectionError.class) {
            Log.d("TAG", "NoConnectionError");
        } else if (errorClass == com.android.volley.ServerError.class) {
            Log.d("TAG", "ServerError");
            Toast.makeText(context, "服务器未知错误", Toast.LENGTH_LONG).show();
        } else if (errorClass == com.android.volley.TimeoutError.class) {
            Log.d("TAG", "TimeoutError");
            Toast.makeText(context, "连接超时", Toast.LENGTH_LONG).show();
        }

    }

}
