package kwsoft.coursetabledemo;


public interface OnDataListener {
	void onGetDataSuccess(String jsonData);
	void onGetDataError();
	void onLoading(long total, long current);
}
