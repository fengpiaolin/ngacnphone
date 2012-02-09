package sp.phone.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ActivityUtil {


	static ActivityUtil instance;
	static Object lock= new Object();
	public static ActivityUtil getInstance(){
		/*if(instance == null){
			instance = new ActivityUtil();
		}*/
		return new ActivityUtil();//instance;
		
	}
	private ActivityUtil(){
		
	}
	private ProgressDialog proDialog;

	/*private Context context;

	public ActivityUtil(Context context) {
		this.context = context;
	}*/
	public void noticeSaying(Context context){
		
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			/*notice("����ģʽ", str.split(";")[0]
					+ "-----" + str.split(";")[1]);*/
			notice("����ģʽ",str.replace(";", "-----"),context);
		} else {
			notice("����ģʽ", str,context);
		}
	}
	
	public void noticeError(String error,Context context){
		
		notice("����", error,context);
	}

	private void notice(String title, String content,Context c) {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("content", content);
		message.obj = c;
		message.setData(b);
		handler.sendMessage(message);
	}

	private Handler handler = new Handler() {
		public void handleMessage (final Message msg) {
			Context context = (Context) msg.obj;
			Bundle b = msg.getData();
			if (b != null) {
				String title = b.getString("title");
				String content = b.getString("content");
				synchronized( lock){
				if (proDialog != null) {

					if ("ERROR".equals(title)) {
						System.out.println("sleep 3 error");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("sleep 2 info");
						// try {
						// Thread.sleep(2000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}
					proDialog.setTitle(title);
					proDialog.setMessage(content);
					proDialog.show();
				} else {
					try{
					proDialog = ProgressDialog.show(context, title, content);
					proDialog.setCanceledOnTouchOutside(true);
					}catch(Exception e){
						Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
					}
				}
				}//sync
			}
		};
	};

	public void dismiss() {
		synchronized( lock){
		if (proDialog != null) {
			proDialog.dismiss();
			proDialog = null;
		}
		}
	}

}