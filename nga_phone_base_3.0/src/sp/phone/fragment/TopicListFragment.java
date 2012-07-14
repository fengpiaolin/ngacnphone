package sp.phone.fragment;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import gov.pianzong.androidnga.activity.BookmarkActivity;
import gov.pianzong.androidnga.activity.MainActivity;
import gov.pianzong.androidnga.activity.PostActivity;
import gov.pianzong.androidnga.activity.TopicListActivity;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopicListFragment extends Fragment
	implements OnTopListLoadFinishedListener{
	static final String TAG = TopicListFragment.class.getSimpleName();
	ListView listview=null;
	TopicListAdapter adapter=null;
	JsonTopicListLoadTask task=null;
	String key = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if(adapter== null)
			adapter =new TopicListAdapter(this.getActivity());
		Log.d(TAG,"onCreate" + (1+getArguments().getInt("page")) );


		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView" + (1+getArguments().getInt("page")) );
		listview = new ListView(getActivity());
		listview.setOnItemClickListener(new EnterJsonArticle());
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
					(getActivity(), R.anim.topic_list_anim);
			listview.setLayoutAnimation(anim);
		}
		listview.setDivider(null);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		return listview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG,"onActivityCreated" + (1+getArguments().getInt("page")) );
		listview.setAdapter(adapter);
	}
	
	

	@Override
	public void onStart() {
		Log.d(TAG,"onStart" + (1+getArguments().getInt("page")) );
		this.loadPage();
		super.onStart();
	}
	
	

	@Override
	public void onResume() {
		this.setHasOptionsMenu(true);
		Log.d(TAG,"onResume" + (1+getArguments().getInt("page")) );
		
		super.onResume();
	}
	
	private void loadPage(){
		if(task == null)
		{
			int index = getArguments().getInt("page");
			Activity activity = getActivity();
			final String page = String.valueOf(1 + index );
			String fidString = String.valueOf(getArguments().getInt("id",0));
			String authoridString = String.valueOf(getArguments().getInt("authorid",0));
			int searchpost = getArguments().getInt("searchpost",0);
			int favor = getArguments().getInt("favor", 0);
			key = getArguments().getString("key");
			
			String jsonUri = HttpUtil.Server + "/thread.php?";
			if(!fidString.equals("0"))
				jsonUri +="fid=" + fidString + "&";
			if(!authoridString.equals("0"))
				jsonUri +="authorid=" + authoridString + "&";
			if(searchpost !=0)
				jsonUri +="searchpost=" + searchpost + "&";
			if(favor !=0)
				jsonUri +="favor=" + favor + "&";
			if(!StringUtil.isEmpty(key)){
				try {
					jsonUri += "key=" + URLEncoder.encode(key, "GBK") + "&";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			jsonUri += "page="+ page + "&lite=js&noprefix";
			
			task = new JsonTopicListLoadTask(activity,this);
			task.execute(jsonUri);
		}else{
			ActivityUtil.getInstance().dismiss();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		task.cancel(false);
		super.onSaveInstanceState(outState);
		outState.putInt("page", this.getArguments().getInt("page"));
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected,fid="
				+getArguments().getInt("id")+ 
				",page="+ (1+getArguments().getInt("page")) );
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				if(task != null){
					task.cancel(false);
					task = null;
				}
				ActivityUtil.getInstance().noticeSaying(getActivity());
				loadPage();
				break;
			case R.id.goto_bookmark_item:
				Intent intent_bookmark = new Intent(this.getActivity(), TopicListActivity.class);
				intent_bookmark.putExtra("favor", 1);
				startActivity(intent_bookmark);
				break;
			case R.id.search:
				handleSearch();
				break;
			case R.id.threadlist_menu_item3 :
			default:
				//case android.R.id.home:
				Intent intent = new Intent(this.getActivity(), MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void handleSearch(){
		Bundle arg  = new Bundle();
		arg.putInt("id",getArguments().getInt("id",-7));
		arg.putInt("authorid", getArguments().getInt("authorid",0));
		DialogFragment df = new SearchDialogFragment();
		df.setArguments(arg);
		final String dialogTag = "search_dialog";
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }

        try{
        	df.show(ft, dialogTag);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));

		}
	}
	
	private boolean handlePostThread(MenuItem item){
		
	
		String fid = String.valueOf(getArguments().getInt("id"));
		
		int intFid=0;
		try{
		intFid = Integer.parseInt(fid);
		}catch(Exception e){

			return false;
		}
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", intFid);
		intent.putExtra("action", "new");
		
		intent.setClass(this.getActivity(), PostActivity.class);
		startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			getActivity().overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
		}
		return true;
	}
	
	
	


	class EnterJsonArticle implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String guid = (String) parent.getItemAtPosition(position);
			if(StringUtil.isEmpty(guid))
				return;
			
			guid = guid.trim();

			int pid = StringUtil.getUrlParameter(guid, "pid");
			int tid = StringUtil.getUrlParameter(guid, "tid");
			int authorid = StringUtil.getUrlParameter(guid, "authorid");
			
			Intent intent = new Intent();
			intent.putExtra("tab", "1");
			intent.putExtra("tid",tid );
			intent.putExtra("pid",pid );
			intent.putExtra("authorid",authorid );
			
			intent.setClass(getActivity(), ArticleListActivity.class);
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
			
		}
		
	}
	
	


	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		if(result == null){
			return;
		}
		
		adapter.jsonfinishLoad(result);
		listview.setAdapter(adapter);
		if(getArguments().getInt("searchpost",0) != 0){
			int page = 1 + getArguments().getInt("page",0);
			result.set__ROWS(page);
		}
		OnTopListLoadFinishedListener father = null;
		try{
			father = (OnTopListLoadFinishedListener)getActivity();
			if(father != null)
				father.jsonfinishLoad(result);
		}catch(ClassCastException e){
			Log.e(TAG, "father activity should implements " + OnTopListLoadFinishedListener.class.getCanonicalName());
		}
	}



	
	
	
	
	
}
