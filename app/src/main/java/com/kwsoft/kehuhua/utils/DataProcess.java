package com.kwsoft.kehuhua.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwsoft.version.StuPra.imgs;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class DataProcess {


    /**
     * 抽取父级菜单方法
     *
     * @param mDataList
     * @return
     */

    public static List<Map<String, Object>> toParentList(List<Map<String, Object>> mDataList) {

        //逐条封装
        List<Map<String, Object>> parentListTemp = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {

            int parent_menuId = Integer.valueOf(String.valueOf(mDataList.get(i).get("parent_menuId")));
            int num = 0;
            for (int j = 0; j < mDataList.size(); j++) {
                int menuId = Integer.valueOf(String.valueOf(mDataList.get(j).get("menuId")));
                if (menuId == parent_menuId) {
                    num++;
                    break;
                }
            }
            //确定没有父亲的菜单
            if (num == 0) {
                parentListTemp.add(mDataList.get(i));

            }
            Log.e("TAG", "menuId " + parent_menuId);
        }
        Log.e("TAG", "menuId运行完毕 ");
        if (parentListTemp.size() == 0) {
            for (int k = 0; k < mDataList.size(); k++) {


                parentListTemp.add(mDataList.get(k));
            }
        }
        Log.e("TAG", "parentList " + parentListTemp.toString());
        //去掉手机端三个字
        for (int m = 0; m < parentListTemp.size(); m++) {
            String menuNameNew = String.valueOf(parentListTemp.get(m).get("menuName")).replace("手机端", "");
            parentListTemp.get(m).put("image", imgs[m]);
            parentListTemp.get(m).put("menuName", menuNameNew);
        }
        Log.e("TAG", "parentList去掉手机端 " + parentListTemp.toString());

        return parentListTemp;

    }

    //将学员端父类菜单添加图片和去掉手机端三个字
    public static List<Map<String, Object>> toStuParentList(List<Map<String, Object>> mDataList) {

        //去掉手机端三个字
        for (int m = 0; m < mDataList.size(); m++) {
            String menuNameNew = String.valueOf(mDataList.get(m).get("menuName")).replace("手机端", "");
            mDataList.get(m).put("image", imgs[m]);
            mDataList.get(m).put("menuName", menuNameNew);
        }
        Log.e("TAG", "parentList去掉手机端 " + mDataList.toString());
        return mDataList;
    }

    public static List<Map<String, Object>> toImgList(List<Map<String, Object>> mDataList) {
        int[] imgs = {R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
                R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
                R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
                R.mipmap.fuc_menu_5, R.mipmap.fuc_menu_2,
                R.mipmap.fuc_menu_7, R.mipmap.fuc_menu_8,
        };


        for (int k = 0; k < mDataList.size(); k++) {
            mDataList.get(k).put("image", imgs[k]);
            String menuNameNew = String.valueOf(mDataList.get(k).get("menuName")).replace("手机端", "");
            mDataList.get(k).put("menuName", menuNameNew);
        }

        return mDataList;

    }

    public static List<Map<String, Object>> noPhoneList(List<Map<String, Object>> mDataList) {

        for (int k = 0; k < mDataList.size(); k++) {
            String menuNameNew = String.valueOf(mDataList.get(k).get("menuName")).replace("手机端", "");
            mDataList.get(k).put("menuName", menuNameNew);
        }

        return mDataList;

    }
    /**
     * 专门为添加提交时撰写的最后阶段拼接方法
     * 第一个参数为activity
     * 第二个参数为提交的数据群
     *
     * @param mActivity
     * @param dataCommit
     * @return
     */
    public static String toCommitStr(Activity mActivity,
                                     List<Map<String, Object>> dataCommit) {
        String commitUrl = "";
        int numNull = 0;//判断必填项是否填写
        for (int i = 0; i < dataCommit.size(); i++) {
            int ifMust = Integer.valueOf(String.valueOf(dataCommit.get(i).get("ifMust")));
            String tempValue = String.valueOf(dataCommit.get(i).get("tempValue"));
            //如果存在1个必填项为空则提示
            if (ifMust == 1 && (tempValue.equals("") || tempValue.equals("null"))) {
                numNull++;
                break;
            }
        }
        //如果必填的都填写了，则拼参数

        if (numNull == 0) {

            Map<String, Object> commitMap1 = new HashMap<>();
            //拼接网址
            for (int i = 0; i < dataCommit.size(); i++) {
                String key = String.valueOf(dataCommit.get(i).get("tempKey"));
                String value;
                if (dataCommit.get(i).get("tempValue") != null) {
                    value = String.valueOf(dataCommit.get(i).get("tempValue"));
                } else {
                    value = "";
                }
                commitMap1.put(key, value);

            }
            String pinJie1 = "";
            for (Map.Entry entry : commitMap1.entrySet()) {
                pinJie1 += entry.getKey() + "=" + entry.getValue() + "&";
            }
            Log.e("TAG", "pinJie1" + pinJie1);
            for (int i = 0; i < dataCommit.size(); i++) {
                if (dataCommit.get(i).get("idArr") != null) {
                    String[] ids = String.valueOf(dataCommit.get(i).get("idArr")).split(",");
                    String pinJie2 = "";
                    if (dataCommit.get(i).get("tempKeyIdArr") != null) {
                        String keyChild = String.valueOf(dataCommit.get(i).get("tempKeyIdArr"));
                        for (String id : ids) {
                            pinJie2 += keyChild + "=" + id + "&";
                        }
                    }
                    pinJie1 += pinJie2;
                }
            }
            commitUrl = pinJie1.substring(0, pinJie1.length() - 1);
            //请求网络提交
            Log.e("TAG", "添加合成数据" + commitUrl);
        } else {
            commitUrl = "no";
            Toast.makeText(mActivity, "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
        return commitUrl;

    }


    //拼|接|隐|藏|字|段|参|数|hidePageSet

    public static String toHidePageSet(List<Map<String, Object>> hideFieldSet) {
        String result = "";
        if (hideFieldSet.size() > 0) {
            for (int i = 0; i < hideFieldSet.size(); i++) {
                String montageName = String.valueOf(hideFieldSet.get(i).get("montageName"));
                String dicDefaultSelect;
                if (hideFieldSet.get(i).get("dicDefaultSelect") != null) {
                    dicDefaultSelect = String.valueOf(hideFieldSet.get(i).get("dicDefaultSelect"));
                } else {
                    dicDefaultSelect = "";
                }
                result += montageName + "=" + dicDefaultSelect + "&";
            }
            return result.substring(0, result.length() - 1);
        } else {
            return result;
        }
    }

    public static String commit(Activity mActivity,
                                List<Map<String, Object>> dataCommit) {
//必填项判断
        String commitUrl = "";
        int numNull = 0;//判断必填项是否填写
        for (int i = 0; i < dataCommit.size(); i++) {
            if (dataCommit.get(i).get("ifMust") != null) {
                int ifMust = Integer.valueOf(String.valueOf(dataCommit.get(i).get("ifMust")));
                String tempValue = String.valueOf(dataCommit.get(i).get(Constant.itemValue));
                //如果存在1个必填项为空则提示
                if (ifMust == 1 && (tempValue.equals(""))) {
                    numNull++;
                    break;
                }
            }
        }
        if (numNull == 0) {
            Map<String, Object> commitMap1 = new HashMap<>();
            for (int i = 0; i < dataCommit.size(); i++) {
                String key = String.valueOf(dataCommit.get(i).get(Constant.primKey));
                String value = "";
                int fieldRole = Integer.valueOf(String.valueOf(dataCommit.get(i).get("fieldRole")));
                if (dataCommit.get(i).get(Constant.itemValue) != null &&
                        !String.valueOf(dataCommit.get(i).get(Constant.itemValue)).equals("")) {
                    if (fieldRole == 21) {
                        String valueTemp = String.valueOf(dataCommit.get(i).get(Constant.itemValue));

                        String[] valueArr = valueTemp.split(",");
                        int valueSize = valueArr.length;
                        String montageName1 = "";
                        value = valueSize + "&";
                        try {
                            montageName1 = String.valueOf(dataCommit.get(i).get(Constant.secondKey));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (String aValueArr : valueArr) {

                            value += montageName1 + "=" + aValueArr + "&";
                        }
                        value = value.substring(0, value.length() - 1);

                    } else {
                        value = String.valueOf(dataCommit.get(i).get(Constant.itemValue));
                    }
                } else {
                    if (fieldRole == 21) {
                        value = "0";
                    } else {
                        value = "";
                    }
                }
                commitMap1.put(key, value);
            }
            for (Map.Entry entry : commitMap1.entrySet()) {
                commitUrl += entry.getKey() + "=" + entry.getValue() + "&";
            }
            commitUrl = commitUrl.substring(0, commitUrl.length() - 1);

        } else {
            commitUrl = "no";
            Toast.makeText(mActivity, "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
        return commitUrl;
    }


    /**
     * 合并配置和数据，并添加参数
     */
    public static List<List<Map<String, String>>> combineSetData(String tableId,List<Map<String, Object>> set, List<Map<String, Object>> data) {
        List<List<Map<String, String>>> newData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<Map<String, String>> itemNum = new ArrayList<>();
            for (int j = 0; j < set.size(); j++) {
                Map<String, String> property = new HashMap<>();
                if (j == 0) {
                    property.put("isCheck", "false");
                    String mainId = "T_" + tableId + "_0";
                    if (data.get(i).get(mainId) != null) {
                        property.put("mainId", String.valueOf(data.get(i).get(mainId)));
                    } else {
                        property.put("mainId", "");
                    }
                    property.put("tableId", tableId);
                    property.put("allItemData", data.get(i).toString());
                }
                property.put("fieldCnName", String.valueOf(set.get(j).get("fieldCnName")));
                String fieldAliasName = String.valueOf(set.get(j).get("fieldAliasName"));
                String fieldCnName2 = "";
                if (data.get(i).get(fieldAliasName) != null) {
                    fieldCnName2 = String.valueOf(data.get(i).get(fieldAliasName));
                }
                property.put("fieldCnName2", fieldCnName2);
                itemNum.add(property);
            }
            newData.add(itemNum);
        }
        return newData;
    }










}



