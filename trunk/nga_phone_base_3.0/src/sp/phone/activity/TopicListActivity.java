package sp.phone.activity;

import sp.phone.adapter.TabsAdapter;
import sp.phone.fragment.TopicListFragment;
import sp.phone.utils.ActivityUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TextView;

public class TopicListActivity extends FragmentActivity{

	TabHost tabhost;
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    int fid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagerview_article_list);
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager)findViewById(R.id.pager);


		fid = 7;
		
		fid = this.getIntent().getIntExtra("fid", 7);
		if(null != savedInstanceState)
			fid = savedInstanceState.getInt("fid");
		
		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,fid,TopicListFragment.class);
		
		TextView tv = null;

		for(int i = 1; i < 6; i++){
		tv = new TextView(this);
		tv.setTextSize(20);
		tv.setText(String.valueOf(i));
		tv.setGravity(Gravity.CENTER);
		mTabsAdapter.addTab(tabhost.newTabSpec(String.valueOf(i)).setIndicator(tv));
		}

		ActivityUtil.getInstance().noticeSaying(this);

		
		
		
        if (savedInstanceState != null) {
        	mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        }
		
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        outState.putInt("tab",mViewPager.getCurrentItem());
        outState.putInt("fid",fid);
        
    }

	@Override
	protected void onStop() {
		ActivityUtil.getInstance().dismiss();
		super.onStop();
	}



	

}