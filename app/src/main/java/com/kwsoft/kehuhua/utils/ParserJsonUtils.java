package com.kwsoft.kehuhua.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParserJsonUtils {
    public static Map<String, String> parserJson(String jsonStr) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject object = new JSONObject(jsonStr);
            Iterator it = object.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                map.put(key, object.getString(key));
            }
            return map;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static List<Map<String, String>> parserJsonCourseTable(String jsonStr) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONObject object = new JSONObject(jsonStr);
            Iterator it = object.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("rows".equals(key)) {
                    JSONArray arr = object.getJSONArray(key);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object2 = arr.getJSONObject(i);
                        Iterator it1 = object2.keys();
                        Map<String, String> map = new HashMap<String, String>();
                        while (it1.hasNext()) {
                            String key1 = it1.next().toString();
                            map.put(key1, object2.getString(key1));
                        }
                        list.add(map);
                    }
                }
            }
            return list;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static List<Map<String, String>> resultStuInfo(String jsonData) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONArray pafsArray = object.getJSONArray("phoneUpdateFieldSet");
            if (pafsArray.length() > 0) {
                for (int i = 0; i < pafsArray.length(); i++) {
                    JSONObject object1 = pafsArray.getJSONObject(i);
                    String fieldRole = object1.getString("fieldRole");
                    Map<String, String> map = new HashMap<>();
//                    if (("21".equals(fieldRole)) || ("20".equals(fieldRole))) {
                    if ("21".equals(fieldRole)) {
                        JSONObject dialogDataList = new JSONObject(object1.getString("dialogDataList"));
                        String dialogDataListStr = dialogDataList.toString();
                        map.put("dialogDataList", dialogDataListStr);
                        JSONArray dialogFieldSetarr = object1.getJSONArray("dialogFieldSet");
                        String dialogFieldSet = dialogFieldSetarr.toString();
                        map.put("dialogFieldSet", dialogFieldSet);
                        map.put("dialogField", object1.getString("dialogField"));
                        map.put("relationTableId", object1.getString("relationTableId"));
                        map.put("showFieldArr", object1.getString("showFieldArr"));
                    } else if ("16".equals(fieldRole)) {
                        String dicOptions = object1.getString("dicOptions");
                        map.put("dicOptions", dicOptions);
                    }
                    map.put("fieldRole", fieldRole);
                    map.put("fieldCnName", object1.getString("fieldCnName"));
                    if (object1.has("fieldAliasName")) {
                        map.put("fieldAliasName", object1.getString("fieldAliasName"));
                    }
                    map.put("fieldRole", fieldRole);
                    map.put("fieldCnName", object1.getString("fieldCnName"));
                    map.put("fieldId", object1.getString("fieldId"));
                    map.put("ifMust", object1.getString("ifMust"));
                    map.put("fieldId", object1.getString("fieldId"));
                    list.add(map);
                }
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> parserJsonCourse(String jsonStr) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            JSONObject object = new JSONObject(jsonStr);
            Iterator it = object.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("rows".equals(key)) {
                    JSONArray arr = object.getJSONArray(key);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object2 = arr.getJSONObject(i);
                        Iterator it1 = object2.keys();
                        Map<String, Object> map = new HashMap<String, Object>();
                        while (it1.hasNext()) {
                            String key1 = it1.next().toString();
                            if("学员校区".equals(key1)||"学员机构".equals(key1)){
                                JSONObject object3=object2.getJSONObject(key1);
                                JSONArray arrXueyuanXiaoqu=object3.getJSONArray("rows");
                                List<Map<String,Object>> listXueyuanXiaoqu=new ArrayList<Map<String, Object>>();
                                for (int j=0;j<arrXueyuanXiaoqu.length();j++){
                                    Map<String,Object> mapXueyuanXiaoqu=new HashMap<String,Object>();
                                    JSONObject objectXueyuanXiaoqu=arrXueyuanXiaoqu.getJSONObject(j);
                                    Iterator itXueyuanXiaoqu=objectXueyuanXiaoqu.keys();
                                    while (itXueyuanXiaoqu.hasNext()){
                                        String keyXueyuanXiaoqu=itXueyuanXiaoqu.next().toString();
                                        mapXueyuanXiaoqu.put(keyXueyuanXiaoqu,objectXueyuanXiaoqu.get(keyXueyuanXiaoqu));
                                    }
                                    listXueyuanXiaoqu.add(mapXueyuanXiaoqu);

                                }
                                map.put(key1,listXueyuanXiaoqu);
                            }else {
                                map.put(key1, object2.getString(key1));
                            }

                        }
                        list.add(map);
                    }
                }
            }
            return list;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}