package sp.phone.activity;

import sp.phone.bean.RSSFeed;
import sp.phone.bean.RSSItem;
import sp.phone.forumoperation.FloorOpener;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class TopicListActivity1 extends Activity {

	ActivityUtil activityUtil = ActivityUtil.getInstance();

	private TabHost tabHost;
	private final String TAG = TopicListActivity1.class.getSimpleName();
	private static final String TABID_PRE = "tab_f";
	private static final String TABID_NEXT = "tab_n";
	private RSSFeed rssFeed;
	private int page;
	//private HashMap<Object, RSSFeed> map;
	private int max_num = 5;
	private MyApp app;
	private TopicFlingListener flingListener;
	protected ListView currentListview;
	private CheckReplyNotificationTask asynTask;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemeManager.SetContextTheme(this);	
		super.onCreate(savedInstanceState);
		
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		setRequestedOrientation(orentation);
		
		setContentView(R.layout.tab1);

		initDate();
		initView();
		setListener();
	}

	
	
	@Override
	protected void onRestart() {
		setRequestedOrientation(ThemeManager.getInstance().screenOrentation);
		super.onRestart();
	}

	

	@Override
	protected void onResume() {
		if(asynTask !=null){
			asynTask.cancel(true);
			asynTask = null;
		}
		long now = System.currentTimeMillis();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if(now - config.lastMessageCheck > 60*1000 && config.notification)
		{
			Log.d(TAG, "start to check Reply Notification");
			asynTask = new CheckReplyNotificationTask(this);
			asynTask.execute(config.getCookie());
		}
		super.onResume();
	}
	
	
	



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.threadlist_menu, menu);
		
		final int flags = ThemeManager.ACTION_BAR_FLAG;


		/*int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
		int i = 0;
		for(i = 0;i< menu.size();i++){
			ReflectionUtil.setShowAsAction(
					menu.getItem(i), actionNum);
		}*/

		 ReflectionUtil.actionBar_setDisplayOption(this, flags);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				new BoardPageNumChangeListener().onTabChanged(
						tabHost.getCurrentTabTag());
				break;
			case R.id.goto_bookmark_item:
				Intent intent_bookmark = new Intent(this, BookmarkActivity.class);
				startActivity(intent_bookmark);
				break;
			case R.id.threadlist_menu_item3 :
			default:
				//case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return true;
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return	flingListener.getDetector().onTouchEvent(event);
	}

	private boolean handlePostThread(MenuItem item){
		final String fid_start_tag = "fid=";
		final String fid_end_tag = "&";
		String fid = rssFeed.getLink();
		int start = fid.indexOf(fid_start_tag);
		if(start == -1)
		{
			Toast.makeText(this, R.string.error,
					Toast.LENGTH_LONG).show();
			return false;
		}
		start += fid_start_tag.length();
		int end= fid.indexOf(fid_end_tag, start) ;
		if(end ==-1)
			end = fid.length();
		fid = fid.substring(start,end);
		
		int intFid=0;
		try{
		intFid = Integer.parseInt(fid);
		}catch(Exception e){
			Toast.makeText(this, R.string.error,
					Toast.LENGTH_LONG).show();
			return false;
		}
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", intFid);
		intent.putExtra("action", "new");
		
		intent.setClass(this, PostActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.zoom_enter,
				R.anim.zoom_exit);
		return true;
	}

	private void initDate() {
		app = ((MyApp) getApplication());
		rssFeed = app.getRssFeed();
		//map = app.getMap();
		Log.d(this.getClass().getSimpleName(), "rssFeed=" + rssFeed);
		app.getMap_article();
		flingListener = new TopicFlingListener(this);


	}

	//SoundPool soundPool = null;
	//private int hitOkSfx;

	private void initView() {


		if(rssFeed == null){
			Log.i(this.getClass().getSimpleName(), "rssFeed= null,finish");
			this.finish();
			return;
		}
		
		setTitle(rssFeed.getTitle());
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		// ��ҳ
		TabSpec ts_first = tabHost.newTabSpec(TABID_PRE);
		TextView tv2 = new TextView(TopicListActivity1.this);
		tv2.setBackgroundResource(R.drawable.page_first);
		ts_first.setIndicator(tv2);
		ts_first.setContent(new tabFactory2());
		tabHost.addTab(ts_first);
		// ҳ����
		page = StringUtil.getNowPageNum(rssFeed.getLink());
		for (int i = max_num - 4; i <= max_num; i++) {
			if (i == page) {
				TabSpec spec = tabHost.newTabSpec("tab_" + i);
				TextView tv = new TextView(TopicListActivity1.this);
				tv.setText(i + "");
				tv.setGravity(Gravity.CENTER);
				tv.setTextColor(getResources().getColor(R.color.white));
				tv.setTextSize(20);
				spec.setIndicator(tv);
				spec.setContent(new tabFactory());
				tabHost.addTab(spec);
				tabHost.setCurrentTabByTag("tab_" + i);
			} else {
				TabSpec spec = tabHost.newTabSpec("tab_" + i);
				TextView tv = new TextView(TopicListActivity1.this);
				tv.setText(i + "");
				tv.setTextSize(20);
				tv.setGravity(Gravity.CENTER);
				spec.setIndicator(tv);
				spec.setContent(new tabFactory2());
				tabHost.addTab(spec);
			}
		}

		TabSpec ts_next = tabHost.newTabSpec(TABID_NEXT);
		TextView tv3 = new TextView(TopicListActivity1.this);
		tv3.setBackgroundResource(R.drawable.page_next);
		ts_next.setIndicator(tv3);
		ts_next.setContent(new tabFactory2());
		tabHost.addTab(ts_next);

	}

	private void setListener() {
		if(tabHost !=null)
			tabHost.setOnTabChangedListener(new BoardPageNumChangeListener());
	}

	class BoardPageNumChangeListener implements OnTabChangeListener{
		boolean isWorking = false;
		public void onTabChanged(final String tabId) {

			//soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);
			synchronized(this){
				if(isWorking){
					return;
				}
				Log.d(this.getClass().getSimpleName(), "is loading, return");
				isWorking = true;
			}

			String link = rssFeed.getLink();
			final int num;
			if (tabId.equals(TABID_PRE)) {
				if(page ==1){
					activityUtil.dismiss();
					TopicListActivity1.this.finish();
					return;
				}
				
				num = page > 1? page -1:1;
				max_num = max_num>5?max_num-1:5;
			} else if (tabId.equals(TABID_NEXT)) {
				num = page + 1;
				if (num <= 5) {
					max_num = 5;
				} else {
					max_num = num;
				}
			} else {
				num = Integer.parseInt(tabId.split("_")[1]);
				if (num <= 5) {
					max_num = 5;
				} else {
					max_num = num;
				}
			}
			//RSSFeed rssFeed2 = null;
			link = link.substring(0, link.length() - 1);
			final String newURL;
			if (link.indexOf("page") == -1) {
				newURL = link + "&page=" + num + "&rss=1";
			} else {
				newURL = link.replaceAll("page=\\d", "page=" + num
						+ "&rss=1");
			}
			new Thread() {
				@Override
				public void run() {
					activityUtil.noticeSaying(TopicListActivity1.this);
					RSSUtil rssUtil = new RSSUtil();
					rssUtil.parseXml(newURL);
					rssFeed = rssUtil.getFeed();
					//map.put(num, rssFeed);
					synchronized(BoardPageNumChangeListener.this){
						isWorking = false;
					}
					Message message = new Message();
					handler_rebuild.sendMessage(message);
				}
			}.start();
		}
		
	}
	
	private Handler handler_rebuild = new Handler() {
		public void handleMessage(Message msg) {
			if (rssFeed != null && rssFeed.getItems().size() != 0) {
				reBuild();
				activityUtil.dismiss();
			}
		}
	};

	private void reBuild() {
		tabHost.setOnTabChangedListener(null);
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();
		initView();
		setListener();
	}

	//for other tabs
	class tabFactory2 implements TabContentFactory {
		public View createTabContent(String tag) {
			TextView view = new TextView(TopicListActivity1.this);
			return view;
		}
	}
	
	//factory for current tab;
	class tabFactory implements TabContentFactory {
		public View createTabContent(String tag) {

			ListView listView = new ListView(TopicListActivity1.this);
			// listView.setBackgroundResource(R.drawable.bodybg);
			listView.setCacheColorHint(0);
			// listView.setDivider(null);
			listView.setVerticalScrollBarEnabled(false);
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
					(TopicListActivity1.this, R.anim.topic_list_anim);
			listView.setLayoutAnimation(anim);
			
			MyAdapter adapter = new MyAdapter(TopicListActivity1.this);
			listView.setAdapter(adapter);
			listView.setOnTouchListener(flingListener);
			listView.setOnItemClickListener(
					new ArticlelistItemClickListener(TopicListActivity1.this));
			//listView.indexOfChild(child)
			
			/*LayoutAnimationController controller = 
					AnimationUtils.loadLayoutAnimation(TopicListActivity1.this,
			        R.anim.test_anim);
			
			listView.setLayoutAnimation(controller);*/
			
			currentListview = listView;
			return listView;
		}
		
		
	}
	


	class ArticlelistItemClickListener extends FloorOpener
		implements OnItemClickListener{
			
		public ArticlelistItemClickListener(Activity activity) {
			super(activity);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			RSSItem item = rssFeed.getItems().get(arg2);
			String guid = item.getGuid();
			 String url;
			if (guid.indexOf("\n") != -1) {
				url = guid.substring(0, guid.length() - 1);
			} else {
				url = guid;
			}
			handleFloor(url);
		}
			
	}
	

	class MyAdapter extends BaseAdapter {
		//SparseArray<View> m = new SparseArray< View>();
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public int getCount() {
			return rssFeed.getItems().size();
		}

		public long getItemId(int arg0) {
			return arg0;
		}
		long start;
		long end;
		
		class ViewHolder{
			public TextView num ;
			public TextView title ;
			public TextView author ;
			public TextView lastReply ;
			
		}
		public View getView(int position, View view, ViewGroup parent) {
			if(position ==0){
				start = System.currentTimeMillis();
			}
			
			View convertView = view;//m.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.topic_list, null);
				TextView num = (TextView) convertView.findViewById(R.id.num);
				TextView title = (TextView) convertView
						.findViewById(R.id.title);
				TextView author = (TextView) convertView
						.findViewById(R.id.author);
				TextView lastReply = (TextView) convertView
						.findViewById(R.id.last_reply);
				holder = new ViewHolder();
				holder.num = num;
				holder.title = title;
				holder.author = author;
				holder.lastReply = lastReply;
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
				int colorId = ThemeManager.getInstance().getBackgroundColor();
				convertView.setBackgroundResource(colorId);
				
				
				
				RSSItem item = rssFeed.getItems().get(position);

				String description = item.getDescription();
				String[] arr = description.split("\n");
				
				holder.author.setText("¥��:"+item.getAuthor());
				if(arr[1]!=null){
				int start = arr[1].indexOf('(') + 1;//93���ظ� ��  (hgfan)
				int end = arr[1].indexOf(')');
					String lastReplyUser = arr[1].substring(start,end);
					holder.lastReply.setText("���ظ�:"+lastReplyUser);
				}
				int last_index = arr.length -1;
				String reply_count = "0";
				int count_in_desc = arr[last_index].indexOf("��");
				if( count_in_desc !=-1)
					reply_count = arr[last_index].substring(0, arr[last_index].indexOf("��"));
				holder.num.setText("" + reply_count);
				//replies.setText("[" + reply_count + " RE]");
				try{
					holder.title.setTextColor(parent.getResources().getColor(
							ThemeManager.getInstance().getForegroundColor()));
					float size = PhoneConfiguration.getInstance().getTextSize();
					holder.title.setTextSize(size);
				}catch(Exception e){
					Log.e(getClass().getSimpleName(),Log.getStackTraceString(e));
				}
				holder.title.setText(arr[0]);
				

			//}
			
			
			if(position == this.getCount()-1){
				end = System.currentTimeMillis();
				Log.i(getClass().getSimpleName(),"render cost:"+(end-start));
			}
			return convertView;
		}
		

		


	}

	class TopicFlingListener extends SimpleOnGestureListener implements
			OnTouchListener {
		Context context;
		GestureDetector gDetector;
		final float FLING_MIN_DISTANCE = 100;
		final float FLING_MIN_VELOCITY = 5000;
		BoardPageNumChangeListener pageChange = new BoardPageNumChangeListener();
		/*public ArticleFlingListener() {
			super();
		}*/

		public TopicFlingListener(Context context) {
			this(context, null);
		}

		public TopicFlingListener(Context context, GestureDetector gDetector) {

			if (gDetector == null)
				gDetector = new GestureDetector(context, this);

			this.context = context;
			this.gDetector = gDetector;
		}
		
		

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			
			if(e1 == null || e2 == null)
				return false;
			float deltaX = Math.abs(e1.getX() - e2.getX());
			float deltaY = Math.abs(e1.getY() - e2.getY()); //distanceX is not equal to e1x-e2x
			
			if(deltaX<FLING_MIN_DISTANCE*2
					||deltaX < 1.73*deltaY
					|| (Math.abs(distanceY) < 1.73* Math.abs(distanceY))
				)
			{
				return false;
			}
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE*1.5 )
			{//left
				
				pageChange.onTabChanged(TABID_NEXT);
				 return false;
			}
			
			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE*1.5)
			{
				//right
				Log.i(this.getClass().getSimpleName(), "invoke change to previous tab");
				pageChange.onTabChanged(TABID_PRE);
				 return false;
				
			}
			return false;
			//return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1 == null || e2 == null)
				return false;
			float deltaX = Math.abs(e1.getX() - e2.getX());
			float deltaY = Math.abs(e1.getY() - e2.getY());
			if ( (e1.getX() - e2.getX() > FLING_MIN_DISTANCE)
					&& (deltaX > 1.73*deltaY)
					&& (Math.abs(velocityX) > 3*Math.abs(velocityY)
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY)
				){
				//left
				
				pageChange.onTabChanged(TABID_NEXT);
				 return true;
			}
			
			if ( (e2.getX() - e1.getX() > FLING_MIN_DISTANCE)
					&& (deltaX > 1.73*deltaY)
					&& (Math.abs(velocityX) > 3*Math.abs(velocityY)
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY)
				) {
				//right
				pageChange.onTabChanged(TABID_PRE);
				 return true;
				
			}
			return false;//super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;

		}

		public boolean onTouch(View v, MotionEvent event) {

			// Within the MyGestureListener class you can now manage the
			// event.getAction() codes.

			// Note that we are now calling the gesture Detectors onTouchEvent.
			// And given we've set this class as the GestureDetectors listener
			// the onFling, onSingleTap etc methods will be executed.

			 return gDetector.onTouchEvent(event);
			 
		}

		public GestureDetector getDetector() {
			return gDetector;
		}
	}
	
}
