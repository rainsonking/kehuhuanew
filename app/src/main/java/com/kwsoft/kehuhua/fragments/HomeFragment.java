package com.kwsoft.kehuhua.fragments;




/**
 */
public class HomeFragment {



//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                adapter.notifyDataSetChanged();
//                homeGridView.onRefreshComplete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //refreshListView.getRefreshableView().setSelection(start);
//        }
//    };

    public HomeFragment() {
        // Required empty public constructor
    }



    }



//    /**
//     * 下拉刷新
//     */
//    private void pullDownToRefresh() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (null != Utils.getActiveNetwork(getActivity())) {
//                    try {
//                        Thread.sleep(1000);
//                        if (parentList != null) {
//                            parentList.clear();
//                            Log.e("TAG", "clear成功");
//                        } else {
//                            Log.e("TAG", "没有clear功");
//                        }
//                        try {
//                            requestMenu();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                mHandler.sendEmptyMessage(0);
//            }
//        }).start();
//    }







    /**
     * 设置选中的tip的背景
     */
//    private void setImageBackground(int selectItems) {
//        for (int i = 0; i < tips.length; i++) {
//            if (i == selectItems) {
//                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
//            } else {
//                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
//            }
//        }
//    }

//
//    public   boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//
//                String msg = "再按一次返回键退出";
//                Toast.makeText(getActivity(), msg,Toast.LENGTH_SHORT).show();
//
//                exitTime = System.currentTimeMillis();
//            } else {
//                CloseActivityClass.exitClient(getActivity());
//            }
//            return true;
//        }
//        return true;
//    }


