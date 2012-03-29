
public class InformationNeed {

	public String narr;
	public String desc;
	public String title;

	public void set(String tag, String v) {
		if(tag.equals("title")){
			setTitle(v.trim());
		}else if(tag.equals("desc")){
			setDesc(v.trim());
		}else if(tag.equals("narr")){
			setNarr(v.trim());
		}
		
	}

	private void setNarr(String v) {
		narr = v;
	}

	private void setDesc(String v) {
		desc = v;
	}

	private void setTitle(String v) {
		title = v;
	}

}
