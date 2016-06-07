package com.kwsoft.kehuhua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kwsoft.kehuhua.adapter.SchselAdapter;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.model.ViewBaseAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/1/13 0013.
 */
public class ViewRight extends RelativeLayout implements ViewBaseAction {
    private ListView mListView;
    //    private final String[] items = new String[] { "item1", "item2", "item3", "item4", "item5", "item6" };//显示字段
//    private final String[] itemsVaule = new String[] { "1", "2", "3", "4", "5", "6" };//隐藏id
    private OnSelectListener mOnSelectListener;
    private SchselAdapter adapter;
    private String mDistance;
    private String showText = "item1";
    private Context mContext;
    private Button btn_clear, btn_confirm;
    public List<String> listleft = new ArrayList<String>();
    public List<String> listRight = new ArrayList<String>();

    public String getShowText() {
        return showText;
    }

    public ViewRight(Context context, List<Map<String, String>> items) {
        super(context);
        init(context, items);
    }

    public ViewRight(Context context, AttributeSet attrs, int defStyle, List<Map<String, String>> items) {
        super(context, attrs, defStyle);
        init(context, items);
    }

    public ViewRight(Context context, AttributeSet attrs, List<Map<String, String>> items) {
        super(context, attrs);
        init(context, items);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context, List<Map<String, String>> items) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.view_distance, this, true);

        mListView = (ListView) findViewById(R.id.listView);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        adapter = new SchselAdapter(context, items);
        mListView.setAdapter(adapter);

        btn_confirm.setOnClickListener(new OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Map<String, String> maps = adapter.maps;
                                               String url = confirmUrl(maps);
                                               Log.e("ViewRight===", url);
                                               if (mOnSelectListener != null) {
                                                   mOnSelectListener.getValue(url);
                                               }
                                           }
                                       }
        );

        btn_clear.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             Map<String, String> maps = adapter.maps;
                                             maps.clear();
                                             Map<String, String> mapOpts = adapter.mapOpts;
                                             mapOpts.clear();
                                             adapter.notifyDataSetChanged();
                                         }
                                     }

        );
    }

    public String confirmUrl(Map<String, String> maps) {
        String url = "", url1 = "", url2 = "", url3 = "";
        String urlTotal = "";
        if (maps.size() > 0) {
            Set<String> keySet = maps.keySet();
            //遍历key集合，获取value
            for (String key : keySet) {
                String valall = maps.get(key);
                String valuearr[] = valall.split("/");
                String fieldType = valuearr[1].trim();
                String fieldSearchName = valuearr[2].trim();
                String value = "";
                if (valuearr.length > 4) {
                    value = valuearr[4];
                }
                url = getUrl(fieldType, fieldSearchName, value, valuearr);
                urlTotal = urlTotal + "&" + url;
            }
        }
        return urlTotal;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        public void getValue(String url);
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    public String getUrl(String fieldType, String fieldSearchName, String value, String[] valuearr) {
        String url = "", url1 = "", url2 = "", url3 = "";
        if ("2".equals(fieldType)) {
            url1 = fieldSearchName + "_" + fieldType + "_andOr=0";
            url2 = fieldSearchName + "_strCond_pld=0";
            url3 = fieldSearchName + "_strVal_pld" + "=" + value;
        } else if ("1".equals(fieldType)) {
            String fieldRole = valuearr[3].trim();
            if ("16".equals(fieldRole)) {
                String fieldstr = fieldSearchName.substring(7, fieldSearchName.length());
                url1 = fieldSearchName + "_d" + "_andOr=0";
                url2 = "";
                url3 = fieldstr + "strCond_dic" + "=" + value;
            } else {
                url1 = fieldSearchName + "_" + fieldType + "_andOr=0";
                url2 = fieldSearchName + "_numCondOne_pld=0";
                url3 = fieldSearchName + "_numValOne_pld" + "=" + value;
            }
        } else if ("3".equals(fieldType)) {
            String[] date = {"", ""};
            if (value != null && value.length() > 0) {
                date[0] = value.substring(0, 10);
                date[1] = value.substring(11);
            }
            Log.e("date==viewRight==", date[0] + "T" + date[1]);
            url1 = fieldSearchName + "_" + fieldType + "_andOr=0";
            url2 = fieldSearchName + "_startDates_pld" + "=" + date[0];
            url3 = fieldSearchName + "_endDates_pld" + "=" + date[1];
        } else if ("d".equals(fieldType)) {
            String fieldstr = fieldSearchName.substring(7, fieldSearchName.length());
            url1 = fieldSearchName + "_" + fieldType + "_andOr=0";
            url2 = "";
            url3 = fieldstr + "strCond_dic" + "=" + value;
        }
        url = url1 + "&" + url2 + "&" + url3;
        return url;
    }
}
