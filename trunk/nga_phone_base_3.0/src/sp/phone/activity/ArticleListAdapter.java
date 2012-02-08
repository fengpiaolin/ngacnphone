package sp.phone.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ArticleListAdapter extends ArrayAdapter<HashMap<String, String>> {

	ZipFile zf;

	HashMap<Integer, View> m = new HashMap<Integer, View>();
	ListView listView;
	private LayoutInflater inflater;
	private Activity activity;
	OnTouchListener gestureListener;

	public ArticleListAdapter(Activity activity,OnTouchListener gestureListener,
			List<HashMap<String, String>> lMap, ListView listView, ZipFile zf) {
		super(activity, 0, lMap);
		this.activity = activity;
		this.listView = listView;
		this.zf = zf;
		this.gestureListener  = gestureListener;
		inflater = LayoutInflater.from(activity);
		


	}
	private boolean isInWifi(){
		ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == State.CONNECTED;
	}
	public View getView(int position, View view, ViewGroup parent) {

		View rowView = m.get(position);
		if (rowView != null && m.size() > 1) {
			return rowView;
		} else {
			final MyApp app = (MyApp) activity.getApplication();
			//rowView = inflater.inflate(R.layout.article_list_2, null);
			rowView = inflater.inflate(R.layout.relative_aritclelist, null);
			HashMap<String, String> map = getItem(position);
			final String floor = map.get("floor");// 楼层
			// 头像处理
			final ImageView avatarIV = (ImageView) rowView
					.findViewById(R.id.avatarImage);
			avatarIV.setImageDrawable(null);

			avatarIV.setTag(floor);// 设置 tag 为楼层
			final String avatarImage = map.get("avatarImage");// 头像
			final String userId = map.get("userId");
			if (!StringUtil.isEmpty(avatarImage)) {
				final String newImage = ImageUtil.newImage(avatarImage, userId);
				if (newImage != null) {
					final Handler handler2 = new Handler() {
						public synchronized void handleMessage(Message message) {
							if (avatarIV != null) {
								avatarIV.setImageBitmap((Bitmap) message.obj);
							}
						}
					};

					new Thread() {
						@Override
						public void run() {

							File file = new File(newImage);
							if (file.exists()) { // 开始检查缓存文件夹
								Bitmap bitmap = BitmapFactory
										.decodeFile(newImage);
								if (bitmap != null) {
									System.out.println("from file" + floor);
									Message message = handler2.obtainMessage(0,
											bitmap);
									handler2.sendMessage(message);
								}
							} else {
								InputStream is = null;
								if (zf != null) { // 开始检查缓存ZIP

									String extension = ImageUtil
											.getImageType(avatarImage);
									ZipEntry entry = zf.getEntry("avatarImage/"
											+ userId + "." + extension);
									if (entry != null) {
										try {
											is = zf.getInputStream(entry);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
								if (is == null) {
									System.out.println("from net" + floor);
									// 下载
									if(!app.isDownImgWithoutWifi() && !isInWifi() ){
										Bitmap bitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.default_avatar);
										Message message = handler2.obtainMessage(0, bitmap);
										handler2.sendMessage(message);
									}else
									{
										HttpUtil.downImage(avatarImage, newImage);
										if (file.exists()) {
											Bitmap bitmap = BitmapFactory
													.decodeFile(newImage);
											if (bitmap != null) {
												Message message = handler2
														.obtainMessage(0, bitmap);
												handler2.sendMessage(message);
											} else {
												System.out
														.println("decodeStream fall"
																+ floor);
											}
										}
									}
								} else {
									System.out.println("from zip" + floor);
									Bitmap bitmap = BitmapFactory
											.decodeStream(is);
									if (bitmap != null) {
										Message message = handler2
												.obtainMessage(0, bitmap);
										handler2.sendMessage(message);
									}
								}

							}

						}
					}.start();
				}
			}

			// 其他处理

			TextView nickNameTV = (TextView) rowView
					.findViewById(R.id.nickName);
			nickNameTV.setText(map.get("nickName"));

			WebView contentTV = (WebView) rowView.findViewById(R.id.content);
			contentTV.setBackgroundColor(0);
			contentTV.setFocusable(false);
			int bgColor = parent.getContext().getResources()
			.getColor(ThemeManager.getInstance().getBackgroundColor());
			bgColor = bgColor & 0xffffff;
			String colorStr = Integer.toHexString(bgColor);
			String ngaHtml = StringUtil.parseHTML3(map.get("content"));
			ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=UTF-8 \">" 
				+"<body bgcolor= \"#"+ colorStr +" \">"
				+ ngaHtml + 
				"</body>";
			
			if(!app.isDownImgWithoutWifi() && !isInWifi() )
				contentTV.getSettings().setBlockNetworkImage(true);
			else
				contentTV.getSettings().setBlockNetworkImage(false);
			contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
			contentTV.setOnTouchListener(gestureListener);
			
			TextView floorTV = (TextView) rowView.findViewById(R.id.floor);
			floorTV.setText("[" + floor + " 楼]");

			TextView postTimeTV = (TextView) rowView
					.findViewById(R.id.postTime);
			postTimeTV.setText(map.get("postTime"));

			TextView titleTV = (TextView) rowView.findViewById(R.id.title);
			if (!StringUtil.isEmpty(map.get("title"))) {
				titleTV.setText(map.get("title"));
			} else {
				titleTV.setVisibility(View.GONE);
			}
			//rowView.setOnLongClickListener(new FloorLongClickListen() );
			//rowView.setOnCreateContextMenuListener( new FloorCreateContextMenuListener() );
			//rowView.set
			
			m.put(position, rowView);
		}
		

		
		return rowView;
	}
	


}
