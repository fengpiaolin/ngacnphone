package sp.phone.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArticlePage {

	private List<Article> listArticle; // 回帖列表
	private HashMap<String, String> now;

	private ArrayList<HashMap<String, String>> list;// 列表

	private HashMap<String, String> page;// pre next first last current num

	public HashMap<String, String> getNow() {
		return now;
	}

	public void setNow(HashMap<String, String> now) {
		this.now = now;
	}

	public List<Article> getListArticle() {
		return listArticle;
	}

	public void setListArticle(List<Article> listArticle) {
		this.listArticle = listArticle;
	}

	public ArrayList<HashMap<String, String>> getList() {
		return list;
	}

	public void setList(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}

	public HashMap<String, String> getPage() {
		return page;
	}

	public void setPage(HashMap<String, String> page) {
		this.page = page;
	}

}
