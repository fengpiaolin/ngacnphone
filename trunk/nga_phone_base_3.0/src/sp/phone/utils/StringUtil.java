package sp.phone.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringUtil {
	private final static String HOST = "http://bbs.ngacn.cc/";

	/** ��֤�Ƿ������� */
	public static boolean isEmail(String email) {
		String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(pattern1);
		Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			return false;
		} else {
			return true;
		}
	}

	/** �ж��Ƿ��� "" ���� null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isEmpty(StringBuffer str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	/** yy-M-dd hh:mm */
	public static Long sDateToLong(String sDate) {
		DateFormat df = new SimpleDateFormat("yy-M-dd hh:mm");
		Date date = null;
		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static boolean isNumer(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static Long parseLong(String str) {
		if (str == null) {
			return null;
		} else {
			if (str.equals("")) {
				return 0l;
			} else {
				return Long.parseLong(str);
			}
		}
	}

	public static Long sDateToLong(String sDate, String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}



	public static String parseHTML(String s) {
		// ת������
		if (s.indexOf("[quote]") != -1) {
			s = s.replace("[quote]", "");
			s = s
					.replace("[/quote]",
							"</font><font color='#1d2a63' size='10'>");

			s = s.replace("[b]", "<font color='red' size='1'>");
			s = s.replace("[/b]", "</font>");
			s = s.replace("<br/><br/>", "<br/>");
			s = s.replace("<br/><br/>", "<br/>");

			s = s.replace("[/pid]", "<font color='blue' size='2'>");
			s = s + "</font>";
		} else {
			s = "<font color='#1d2a63' size='10'>" + s;
			s = s + "</font>";
		}
		// ת�� ����

		s = s.replaceAll("(\\[s:\\d\\])", "<img src='$1'>");
		return s;
	}

	public static String decodeForumTag(String s) {
		// ת������
		
		//quote
		String quoteStyle = "<div style='background:#E8E8E8;border:1px solid #888' >";
		if(ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT)
			quoteStyle = "<div style='background:#000000;border:1px solid #888' >";
		
		final String styleLeft = "<div style='float:left' >";
		final String styleRight = "<div style='float:right' >";
		
		s = s.replaceAll("\\[l\\]", styleLeft);
		s = s.replaceAll("\\[/l\\]", "</div>");
		
		s = s.replaceAll("\\[r\\]", styleRight);
		s = s.replaceAll("\\[/r\\]", "</div>");
		
		final String styleAlignRight = "<div style='text-align:right' >";
		final String styleAlignLeft = "<div style='text-align:left' >";
		s = s.replaceAll("\\[align=right\\]", styleAlignRight);
		s = s.replaceAll("\\[align=left\\]", styleAlignLeft);
		s = s.replaceAll("\\[/align\\]", "</div>");
		
		s = s.replaceAll("\\[quote\\]",quoteStyle);
		s = s.replaceAll("\\[/quote\\]", "</div>");
		//reply
		s = s.replaceAll(
				"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
		
		//topic
		s = s.replaceAll(
				"\\[tid=\\d+\\]Topic\\[/pid\\]", "Topic");
		//reply
		//s = s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
		//		, "Reply to Reply <b>$1</b>");
		// ת�� tag
		s = s.replaceAll("\\[b\\]", "<b>");
		s = s.replaceAll("\\[/b\\]","</b>"/* "</font>"*/);
		
		s = s.replaceAll("\\[u\\]", "<u>");
		s = s.replaceAll("\\[/u\\]","</u>");
		
		s = s.replaceAll("(\\[s:\\d+\\])", "<img src='$1'>");
		//[url][/url]
		s = s.replaceAll("\\[url\\](http[^\\[|\\]]+)\\[/url\\]",
				"<a href=\"$1\">$1</a>");
		s = s.replaceAll("\\[url=(http[^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]"
				,"<a href=\"$1\">$2</a>");
		//flash
		s = s.replaceAll("\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
				"<a href=\"$1\">$1</a>");
		//color
		
		s = s.replaceAll("\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
				,"<b style=\"color:$1\">$2</b>");
		
		s = s.replaceAll("\\[table\\]","<table style='color:green'>");
		s = s.replaceAll("\\[/table\\]","</table>");
		s = s.replaceAll("\\[tr\\]","<tr>");
		s = s.replaceAll("\\[/tr\\]","<tr>");
		s = s.replaceAll("\\[td\\]",
				"<td style=\"border:1px solid red\">");
		s = s.replaceAll("\\[/td\\]","<td>");
		//[i][/i]
		s = s.replaceAll("\\[i\\]", "<i style=\"font-style:italic\">");
		s = s.replaceAll("\\[/i\\]", "</i>");
		//[del][/del]
		s = s.replaceAll("\\[del\\]", "<del class=\"gray\">");
		s = s.replaceAll("\\[/del\\]","</del>");
		
		s = s.replaceAll("\\[font=(\\w+?)\\]","<span style=\"font-family:$1\">");
		s = s.replaceAll("\\[/font\\]","</span>");
		
		s = s.replaceAll("\\[/font\\]","</span>");
		s = s.replaceAll("\\[/font\\]","</span>");
		
		s = s.replaceAll("\\[size=(\\d+)%\\]","<span style=\"font-size:$1%;line-height:$1%\">");
		s = s.replaceAll("\\[/size\\]","</span>");
		
		//[img]./ddd.jpg[/img]
		s = s.replaceAll("\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]", 
				"<a href='http://img.ngacn.cc/attachments$1'><img src='http://img.ngacn.cc/attachments$1' style= 'max-width:100%;' ></a>");
		//s = s.replaceAll("\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]", 
		//		"<img src=\"http://img.ngacn.cc/attachments$1\" width=\"100%\">");
		//[img]http://[/img]
		s = s.replaceAll("\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]", 
				"<a href='$1'><img src='$1' style= 'max-width:100%;' ></a>");
		//s = s.replaceAll("\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]", 
		//		"<img src='$1' width=\"100%\">");
		return s;
	}

	public static String removeBrTag(String s){
		s =s.replaceAll("<br/>", "\n");
		return s;
	}
	/**
	 * ����URL
	 * 
	 * @param url
	 * @return
	 */
	public static String doURL(String url) {
		if (!url.startsWith(HOST)) {
			return HOST + url;
		} else {
			return url;
		}
	}

	public static String getSaying() {
		Random random = new Random();
		int num = random.nextInt(SAYING.length);
		return SAYING[num];
	}

	public static String unEscapeHtml(String s){
		String ret = "";
		ret = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(s);
		return ret;
	}
	

	
	public static String buildThreadURLByTid(String tid){
		return "/read.php?tid="+tid;
	}
	private static final String[] SAYING = {
			"ս�����죬����������;��˹����˹",
			"���ӵ����ߡ� ",
			"���಻����ս����ս���ͻ�������ࡣ;Լ����F�������",
			"ս���޷�����˭����ȷ�ģ�ֻ�ܾ���˭�Ǵ���һ����;���� ",
			"û�к���½ս�ӵľ�������û�п��ӵ��·��� ;���������Ͻ� ������D������",
			"��ý��������Ҫ��˼��������;��³����",
			"������ϲ��ϲ������ʷ��վ��������ߣ������ǽ����������ᣡ;��³����",
			"�����ڵ�����ڣ��˴˱˴ˡ�;�����ռ� ",
			"öս��Ѳ����������ۣ�90����Ԫ�� ",
			"һ��F-22����ս��������ۣ�1.35����Ԫ",
			"һ��F-117A��ҹӥ������ս��������ۣ�1.22����Ԫ ",
			"һ��B-2��ը������ۣ�22����Ԫ",
			"ֻҪ��������ڣ��ͻ���ս�� ��;����˹̹ ",
			"���ŵ�����׼��;���� ����������ϵ�ʹ��˵�� ",
			"�κγ�ʵ�ľ���ָ�ӹٶ���������ڶ��þ���ʱ���¹�����;�޲��ء����������",
			"������ñ������Ȩ���������������ձ��;Ҷ���� ",
			"������������������½ս�Ӻ�����ǹ��;�������� Լ�� J. ����",
			"վ������һ�ߵ��˲��ܳ�Ϊ�ֲ����ӡ�;��������",
			"û�бȰ���ǹ�ӻ���Ȼ������ˬ���ˡ� ;�𼪶�",
			"����û������ս��������˵��ս����Ȼ���������ġ�;����˹�� ",
			"�Ѿ��˺��������Ѻá�",
			"����ʿ���ǽ���ս��һ�����⽻�������ǿ�ʼս���Ĺؼ���",
			"�Ƽ��ڱ�Ӧ��֮ǰ�����ǵ��������ġ���֮�����ƣ���֮�����;����������ɭ",
			"�ϼһ�����ս����ǰ�ߵ�ȴ��С�����ǡ�;�ղ��ء����� ����ʮһλ������ͳ",
			"ս���ϵ�ָ�ӹ����ǶԵģ�ƨ�ɺ����˾����Ǵ��ģ����Ǿٳ�������;������ ����ǰ������",
			"���ɲ�������ѵģ�������������½ս�ӻ�������ͷ��",
			"�Ҳ�֪�������������ս�������ʲô�����������Ĵ������ս���ǹ�����ʯͷ�� ;����˹̹",
			"������֪������ʲô�����ѵ���ȥ����",
			"֪��֪�ˣ���ս������;����",
			"���������˶��ܴ������澳�У������������֪��һ���˵���ʵһ�棬�����������;�ֿ�",
			"������ǲ��������֤�����ǵ�������м�ֵ������������¼������ǵ�����,�޲��ء����������",
			"����֮�������ñ����Ͱ����ߵ���Ѫһ����һ���ϴˢ;����˹�����ѷ (�������μ�, ��������ͳ, indpdc���Ե������)",
			"��������ɵñȻ������죬�ǰ˳��Ǽ�ֱ�����������������ⲻ��ȫ��",
			"5�������ֻ��3��;�����ռ�",
			"�������̫��˳������ô��ϲ���ϵ��ˡ�;ʿ���ռ� ",
			"û���κ���ս�ƻ����������������Ч��;������",
			"������˿���ε������������Ͳ��������ǵ������ˡ�;����½��ѵ����ʾ",
			"������������������������־���档;�����",
			"ö��ǹ��̹�˵�������ۣ�8����Ԫ ",
			"�Ե������������Ա�ǿ������",
			"�����ǲ�ס��66ʽ(XD)���ǳ��������� ",
			"ֻ���������˽�½ս�ӣ�½ս�Ӻ����ĵ��ˣ������˶��ڳ����ֵ���",
			"�����ߵ�½ս��ԱԽ�࣬��ԽHappy ��;����½�������˽��� ",
			"�����ˣ�����������ɳ�����͵ľ���������ġ� ",
			"��סҪ͸�����󿴱��ʡ���Ҫ��Ϊ��������İ����������",
			"�٣������㣬����Ҳ��û�ӵ��ˡ�;ʿ���ռ�",
			"������粻�����ר����֧�䡣;������Ƿ� ",
			"�������ܻ���Щ΢����������£����ǻ�֧�ַ��ɣ�Ȼ��ݻ�����;����̩ ",
			"Ӣ�۲����ñȱ��˸��¸ң������Ƕ�����5���ӡ�;���",
			"��󣬺����ˣ�����������ˡ��ӽ���ս�����������ˡ�;�޲��ء����������",
			"��Щ�˻���һ���ӣ�һֱϣ����Щʲô���£���½ս��Ա��û���Ǹ����⡣ ;���",
			"һ��������ֱ�ӽ����ڸոձ���ը���������ǲ����ǵġ� ;�����վ��н�",
			"����֮�����ܹ��ڴ���˯���Ⱦ�������Ϊ���������Ϊ����վ�ڡ�;���Ρ���Τ��",
			"�����һ��ʼû�㶨���Ͽ���п���֧Ԯ�� ",
			"ҷ�ⵯ�����Ĳ����ǵ��ˡ�;����½������",
			"�Ŷ�Э������Ҫ�������ñ������㰤ǹ�ӡ�",
			"ֻ�к�ƽ�Ż������յ�ʤ���� ;��Ĭ��",
			"��һ���ֲ��������ӵ�пƼ��������������ǲ���ȡ�ж�������ʮ�ֺ�ڡ�;������������˹ (������66�ι�����) ",
			"���ߣ����Ҳ��;����",
			"����Ŀɿ��Ժͺ���������Բ��ȶ�����ϻ����������ҡ�;�޲��ء����������",
			"��ս���У���Ӯ������ֻ��һ��֮� ;������˹����˰�ɪ����",
			"�㲻��˵����û�н�������������ÿ��ս���У����Ƕ������·������ɵ��㡣",
			"�������׺�������ô��֮ǰ����������ܻ�������ˡ�;�޲��ء����������",
			"ָ�ӵļһﲻ�䵱Ӣ�ۣ�������Ӣ����ս���е�����",
			"�κκϸ��ʿ����Ӧ�÷���ս����ͬʱ��Ҳ����ֵ��Ϊ֮ս���Ķ�����",
			"�����ʤ�̾ͱ�ȥ������ ",
			"��֪��������������;����",
			"������μ�ս�����ˣ��϶�û�����������;���� ��˹",
			"˵����ǿ�ڽ������˿϶�û�����Զ�������;������˹����˰�ɪ����",
			"а��ĵó�������������Ϊ ;�����ɡ�����",
			"If a man has done his best, what else is there?;���� S. �Ͷٽ���",
			"���׵ı�ը�뾶���Ǳ������Ծ�����һ�㡣",
			"������һ�߿�ҫ����ΰ���һ���ڲк����ա�",
			"ÿ���������������ɡ������Լ������ɡ�;�������ء�������(��������)",
			"�����ս��������˵�����Ǹ�ϣ�����е�ս������û�б�����;�������֡�D����˹��",
			"�ɹ������յ㣬ʧ��Ҳ�����սᣬֻ�������������㡣;��˹�١��𼪶� ",
			"û�б�ʤ�ľ��ģ�ս���ذ����ɡ�;������˹����˰�ɪ ",
			"���е�ս��������ս����Ϊ���е����඼��ͬ����;�������ߡ�����¡ ",
			"��ս���У��ڶ�����û�н��͵ġ�;���ꡤ������������ ",
			"�ö��벻�����ǽ����ĵ�һ����Ʒ��",
			"time is money",
			"���۶�ôʦ��������Ҳ�������������Ϊս��������ġ�;����˹��������",
			"Ϊ�������˰������޴��ģ����Ƿ���Ӧ�ø�л�ϵ�����������������;���Σ��Ͷٽ���",
			"ս����Ŀ�Ĳ���Ҫ��Ϊ������������Ҫ�ø����ĵ���Ϊ���Ĺ���������;���Σ��Ͷٽ���",
			"һ������� �� û���ˣ��Ͳ�����ս����;Լɪ��˹����",
			"һ���˵����������Ĳ��ң����������˵�������ֻ�Ǽ򵥵�ͳ�����֡�;Լɪ��˹����",
			"�Һ�ս������˵س�񣬷������ǿ��»ᰮ������;�޲��أ���",
			"���Լ����̲�ס��ʼ����ֱ������ʶ���Լ���ɣ��������ϯ�� ;Peter Reid,�ڲ����ն�ɣ�����ı����������",
			"�Ҹ����Ҷ���Josh �������¡�������ɭϣ���ְ�ΪӢ�����ӱ���������������¸������ҵ�Ů��Olivia��Ȼ�����ǵ����ж�������ˮ���ҡ����ǲ���˵���㲻��Ϊ��ɭ�������ˣ�;Lee Dixon ",

			"սʿ��Ը��Ϊ��һС��ѫ�¶���ս���ס�;�����أ����ð���",
			"����ս�ܵ���һ����ս�ܡ�;�����أ����ð���",
			"��������ͬһ���˶���̫�ã���������ѧ�������е�ս����;�����أ����ð���",
			"��������������û��û�Σ����ػ�����������ͷ���ԡ�;�վ�",
			"...��......��ʾ�ĺ����ǲ�ͬ�ġ�",
			"���죡������������������",
			"ĳ�У���˵�����������û���е�������������",
			"1024",
			"�㶮�ģ�",
			"YSLM",
			"5¥:people don't want face,sky down no enemy.",
			"1¥:no ����,who's your ����.",
			"Your brain has two parts:the left&the right .Your left brain has nothing right,and your right brain has nothing left.",
			"������һ����ɵ��������Ư��,�����ֺ���Ǯ�ֿϵ�����Ů����ô��ô��?", "8¥:������������Ӣ�ﶼ��ô�ã��ѹ�������ôǿ�� ",
			"�Ǽ�����2:Ŀ��֮��", "�Ǽ�����2:��������ʹ ", "���ŭ��:�Ǹ���,��Ҫһֱ�����ҿ�����",
			"��������Ц��:DK̹û�ö�,������",
			"¥������æ���Ҽ��������ĺ���ȡ�����������֡��������²��ڡ�¥�������,�����������������ճ¡�",
			"������ջ�ǹŴ�����������ջ��", "�����޶�����ҩ����������������",
			"ƽʱ��Ϧ�ദ���ˣ�ֻҪ����ҹ���£����ɸ���ɴ���Է��Ͳ���ʶ�ˡ�",
			"û�õ�С��ɫ�õ��书�����к�ǿ����ѧ�ԺͶ����ԣ����ǲ�����á�", "���ų����׷�+���ӵľ����ǿ������ˣ�����Ҫ���ù�ϵ��",
			"Ӣ����һ�Ѻñ������õ��Ӳ���ȥ����������",
			"���Ҽ��У�Ӣ��Ҫ�ǲ��������;�����������һ���˼�����Ҳ����Ϊһ���д����Ю�������˵���Ӣ�۷��ġ�",
			"һ��Ҫ�����ԵĴ��£��ų����У������ſ�ˮ��У�ȥ���ɣ���",
			"ʹ����ɱ��Ҫ���ܻ��ڵĶ�������Ҫ����һ�����ӣ������˾�����˻�͵Ϯ���������Ǹ��û��ᡭ��",
			"���ֶ��������������������ҷ��ҷɵ÷ɿ졣����Ҫ�Ǹ�Զ·��ȴ��������",
			"�����ײͣ�2����ţ��+�ϵ�Ů���졣(������ջ���ڹ�Ӧ......)",
			"���˴Ӳ��¶������˴Ӳ����¶��������˴Ӳ��¶�ȴ�ϱ������¶������˴Ӳ����¶�ȴû�˻�������",
			"��������ʾ�Լ�����Ϊ�����������һ����֦����֪��ߵغ��С��ɫ��ܣ�����������ջ��ʼ��Ӧ��֦����",
			"��һ����ֱ�Ľֵ�����׷ɱ�������кܶ���Ҫ������Ū�����Ե�С̯������Ҫ�ģ�",
			"�����ð�������ʽ���ƣ���Ŷ��գ�һ�����У������ð����Ǳ����޳ܣ���������������˶��Ӳ��С���",
			"����ǧ����������ˣ����ᱻ�������ž�ʹ�ŵ��������ſ�ˮ��У����ɣ�", "���о������˾����а�����Ӣ�ۣ��ղ���Ҳ���顭��",
			"��ʱ����ΰ����ã��������Σ����е����", "���г�ɱ���ľ�����һ���ӣ���������",
			"����������鲼һ��һ���������������Ҳ���鲼һ��һ���������",
			"����һ����������һ����˳��һ�������Ļῲ��������һ����˳�ĴӲ���Ʊ����",
			"�����˶�����Ǯ��ͭ����ٳ��֣�һ��һ�ŵ���Ʊ�Ȳ�ֽ�����ˡ�", " (������ջ��)��С��֪ʶԨ��������(+Ǯ)�ش�",
			"�����¾�1������(�Ϻ�������8��)��1��ͽ���������������ܲˡ�",
			"����ʱ�� �� ���� �� һ��֧�Ų�ס�ˣ��ͻẰ�˰�æ�� �� �Ը�����ħͷ�����ú�����ʲô�������壬���һ���ϣ� ��",
			"����ͼ��ݾ���ʧ�ԡ���", "һ���˺����ƾ�һ�����±��ꡣ", "����������У��Ĵ�?#���Ĵ�%�����Ĵ�*(����",
			"�ν�ʱ����ʱ���н�������ʱ��β���������", "��͢�Ĵ󽫾�����ʺ���������Ǹ��֡�",
			"��Ժ��������Ժ(�һ������������ŵ��ӹ�˾����)��", "��Ů�������ǣ����������Ƶġ��� ",
			"�������ﲻ�ܺ�С�ձ���AKB"

	};

	public static int getNowPageNum(String link) {
		// link: http://bbs.ngacn.cc/thread.php?fid=7&page=1&rss=1&OOXX=
		int ret = 1;
		if (link.indexOf("\n") != -1) {
			link = link.substring(0, link.length() - 1);
		}
		if (link.indexOf("&") == -1) {
			return ret;
		} else {
			try{
			ret = Integer.parseInt(link.substring(link.indexOf("page=") + 5,
					link.length()));
			}catch(Exception E){
				
			}
		}
		return ret;
	}
	final static String tips = "֧�ַ�ͼ��";/*
		  "1.�����水menu��¼.\n"
		+ "2.���������ѡ��ҹ��ģʽ���Ƿ�����ͼƬ\n"
		+ "3.����������ӻ���ɾ���Զ���汾\n"
		+ "4.����鰴menu�����Է���\n"
		+ "5.����ں������ڿ������һ���\n"
		+"6.����¥�㣬������������\n";*/
	public static String getTips(){
		
		return tips;
		
	}
}

