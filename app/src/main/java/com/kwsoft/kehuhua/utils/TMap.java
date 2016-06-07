package com.kwsoft.kehuhua.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/*这里集成HashMap建 根据 实例化 TMap的建保持一直， 值 这是一个List 接口，我们把相同建的值存在这个List里面*/
public class TMap<T, V> extends HashMap<T, List<V>> {

    private static final long serialVersionUID = 1L;
      public List<V> putValue(T key, V value){
        if (!super.containsKey(key)) {
            super.put(key, new Vector<V>());
        }
        /* 这里获取 key对应的List*/
        List<V> list = super.get(key);
         /* 将当前值，放入到 key对应的List中 */
        list.add(value);
        /* 返回当前 key对于的List对象*/
        return super.get(list);

}

    @Override
    public List<V> get(Object key) {

        return super.get( key ) ;
    }



}
