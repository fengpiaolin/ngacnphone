package sp.phone.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class AppendableTopicAdapter extends TopicListAdapter {
	final private List<TopicListInfo> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonTopicListLoader loader;
	Set<Integer> tidSet;
	public AppendableTopicAdapter(Context context,PullToRefreshAttacher attacher,NextJsonTopicListLoader loader ) {
		super(context);
		infoList = new ArrayList<TopicListInfo>();
		tidSet = new HashSet<Integer>();
        this.attacher = attacher;
        this.loader = loader;
	}

	@Override
	protected ThreadPageInfo getEntry(int position) {
		for(int i=0; i< infoList.size(); i++){
			if(position < infoList.get(i).get__T__ROWS()){
				return infoList.get(i).getArticleEntryList().get(position);
			}
			position -= infoList.get(i).get__T__ROWS();
		}
		return null;
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
        isLoading = false;
        if(attacher !=null)
            attacher.setRefreshComplete();
        if(result == null)
            return;
        ActivityUtil.getInstance().dismiss();

		if (count != 0) {
			List<ThreadPageInfo> threadList = new ArrayList<ThreadPageInfo>();
			for (int i = 0; i < result.getArticleEntryList().size(); i++) {
				ThreadPageInfo info = result.getArticleEntryList().get(i);
				if(info == null){
					continue;
				}
				int tid = info.getTid();
				if (!tidSet.contains(tid)) {
					threadList.add(info);
					tidSet.add(tid);
				}
			}
			result.set__T__ROWS(threadList.size());
			result.setArticleEntryList(threadList);
		}else{
			for (int i = 0; i < result.getArticleEntryList().size(); i++) {
				ThreadPageInfo info = result.getArticleEntryList().get(i);
				if(info == null){
					continue;
				}
				int tid = info.getTid();
				tidSet.add(tid);
			}
			
		}

		
		infoList.add(result);
		count += result.get__T__ROWS();
		if(count != result.get__T__ROWS())
		{

			this.notifyDataSetChanged();
			
			//Toast.makeText(context, "finish load page:" + infoList.size(), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void clear(){
		count = 0;
		infoList.clear();
		tidSet.clear();
		setSelected(-1);
	}
	
	public int getNextPage(){
		return infoList.size() + 1;
	}

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View ret = super.getView(position, view, parent);
        if( position +1 == this.getCount() && !isLoading){
            isLoading = true;
            loader.loadNextPage(this);
        }
        return  ret;
    }
    private boolean isLoading = false;
}
