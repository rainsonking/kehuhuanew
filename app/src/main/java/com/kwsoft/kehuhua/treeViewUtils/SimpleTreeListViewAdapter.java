package com.kwsoft.kehuhua.treeViewUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import java.util.HashMap;
import java.util.List;


public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter<T>
{
	private static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况
	private String mIsMulti;
	private List<Integer> idArrList;



	public SimpleTreeListViewAdapter(ListView tree, Context context,
			List<T> datas, int defaultExpandLevel, String isMulti,List<Integer> idArrList)
			throws IllegalArgumentException, IllegalAccessException
	{
		super(tree, context, datas, defaultExpandLevel);
		this.mIsMulti = isMulti;
		this.idArrList=idArrList;
		Constant.idArrList=idArrList;
		isSelected = new HashMap<>();
		// 初始化数据
		initDate();
	}
	// 初始化isSelected的数据
	private void initDate() {

		for (int i = 0; i < mAllNodes.size(); i++) {
			getIsSelected().put(mAllNodes.get(i).getId(),idArrList.contains(mAllNodes.get(i).getId()));
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		SimpleTreeListViewAdapter.isSelected = isSelected;
	}

	@Override
	public View getConvertView(Node node, int position, View convertView,
			ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.list_item_tree, parent, false);
			holder = new ViewHolder();
			holder.mIcon = (ImageView) convertView
					.findViewById(R.id.id_item_icon);
			holder.mText = (TextView) convertView
					.findViewById(R.id.id_item_text);

			holder.mCheckBox=(CheckBox) convertView
					.findViewById(R.id.tree_check);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		if (node.getIcon() == -1)
		{
			holder.mIcon.setVisibility(View.INVISIBLE);
		} else
		{
			holder.mIcon.setVisibility(View.VISIBLE);
			holder.mIcon.setImageResource(node.getIcon());
		}
		final int nowId=node.getId();//拿到当前项的id，而不是position

		holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mIsMulti.equals("true")) {
					Log.e("TAG","mCheckBox监测点1");
					if (isSelected.get(nowId)) {
						Log.e("TAG","mCheckBox监测点2");
						isSelected.put(nowId, false);
						Log.e("TAG","mCheckBox监测点3");

						for (int i = 0; i < Constant.idArrList.size(); i++) {
							Log.e("TAG","mCheckBox监测点4");
							if (Constant.idArrList.get(i) ==nowId) {
								Log.e("TAG","mCheckBox监测点5");
								Constant.idArrList.remove(i);
							}
						}






//						for(Integer i : Constant.idArrList)
//						{
//							Log.e("TAG","mCheckBox监测点4");
//							if(Constant.idArrList.get(i) == nowId)
//							{
//								Log.e("TAG","mCheckBox监测点5");
//								Constant.idArrList.remove(i);
//							}
//						}
						Log.e("TAG","mCheckBox监测点6");
						setIsSelected(isSelected);
					} else{
						Log.e("TAG","mCheckBox监测点7");
						isSelected.put(nowId, true);
						Log.e("TAG","mCheckBox监测点8");
						Constant.idArrList.add(nowId);
						Log.e("TAG","mCheckBox监测点9");
						setIsSelected(isSelected);
						Log.e("TAG","mCheckBox监测点10");
					}
				}else if (mIsMulti.equals("false")){
					boolean cu = !isSelected.get(nowId);
					// 先将所有的置为FALSE
					for(Integer p : isSelected.keySet()) {
						isSelected.put(p, false);

					}
					for (int i = 0; i < Constant.idArrList.size(); i++) {
						Log.e("TAG","mCheckBox监测点4");
							Constant.idArrList.remove(i);

					}
					// 再将当前选择CB的实际状态
					isSelected.put(nowId, cu);
					Constant.idArrList.add(nowId);
					SimpleTreeListViewAdapter.this.notifyDataSetChanged();

				}
			}
		});

		holder.mText.setText(node.getName());
		holder.mCheckBox.setChecked(isSelected.get(nowId));
		return convertView;
	}

	private class ViewHolder
	{
		ImageView mIcon;
		TextView mText;
		CheckBox mCheckBox;
	}

	/**
	 * 动态插入节点
	 * 
	 * @param position
	 * @param string
	 */
	public void addExtraNode(int position, String string)
	{
		Node node = mVisibleNodes.get(position);
		int indexOf = mAllNodes.indexOf(node);
		// Node
		Node extraNode = new Node(-1, node.getId(), string);
		extraNode.setParent(node);
		node.getChildren().add(extraNode);
		mAllNodes.add(indexOf + 1, extraNode);

		mVisibleNodes = TreeHelper.filterVisibleNodes(mAllNodes);
		notifyDataSetChanged();

	}

}
