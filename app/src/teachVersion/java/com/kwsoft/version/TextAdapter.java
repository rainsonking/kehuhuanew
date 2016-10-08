package com.kwsoft.version;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

public class TextAdapter extends BaseAdapter {

	private Context mContext;
	private List<Map<String, Object>> mListData;
	private int selectPos;
	
	public void setSelectPos(int pos){
		this.selectPos = pos;
	}

	public TextAdapter(Context context, List<Map<String, Object>> listData) {

		mContext = context;
		mListData = listData;
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int i) {
		return mListData.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView =LayoutInflater.from(mContext).inflate(R.layout.layout_first_menu_item, parent, false);
		ImageView teach_menu_father_image= (ImageView) convertView.findViewById(R.id.teach_menu_father_image);
		TextView teach_menu_father_text= (TextView) convertView.findViewById(R.id.teach_menu_father_text);
		String menuName=String.valueOf(mListData.get(position).get("menuName"));
		teach_menu_father_text.setText(menuName);
		if (selectPos == position) {

			teach_menu_father_text.setTextColor(mContext.getResources().getColor(R.color.text_ff9c00));
			teach_menu_father_image.setBackground(mContext.getResources().getDrawable(StuPra.imgs[2*position+1]));
		} else {
			teach_menu_father_text.setTextColor(mContext.getResources().getColor(R.color.list_item_left));
			teach_menu_father_image.setBackground(mContext.getResources().getDrawable(StuPra.imgs[2*position]));
		}
		return convertView;
	}

}
