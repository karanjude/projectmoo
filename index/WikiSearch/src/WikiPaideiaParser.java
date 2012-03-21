import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.text.ChangedCharSetException;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Parser;
import javax.swing.text.html.parser.TagElement;

public class WikiPaideiaParser extends Parser {

	private Stack stack;
	private List<String> atext = new ArrayList<String>();
	private List<String> htext = new ArrayList<String>();
	private List<String> titletext = new ArrayList<String>();
	private List<String> resttext = new ArrayList<String>();

	public WikiPaideiaParser(DTD arg0) {
		super(arg0);
		stack = new Stack();
	}

	public void parseFile(File file) throws IOException {
		Reader reader = new FileReader(file);
		this.parse(reader);
		for (String s : atext) {
			System.out.println(s);
		}
		for (String s : htext) {
			System.out.println(s);
		}
		for (String s : titletext) {
			System.out.println(s);
		}
		for (String s : resttext) {
			System.out.println(s);
		}
		atext.clear();
		htext.clear();
		titletext.clear();
		resttext.clear();
	}

	@Override
	protected void handleEndTag(TagElement tag) {
		stack.pop();
		super.handleEndTag(tag);
	}

	@Override
	protected void handleTitle(char[] text) {
		titletext.add(new String(text));
		super.handleTitle(text);
	}

	@Override
	protected void handleText(char[] text) {
		String tag = (String) stack.peek();
		if (tag.equals("a"))
			atext.add(new String(text));
		else if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3")
				|| tag.equals("h4") || tag.equals("h5") || tag.equals("h6"))
			htext.add(new String(text));
		else
			resttext.add(new String(text));
		super.handleText(text);
	}

	@Override
	protected void startTag(TagElement tag) throws ChangedCharSetException {
		this.stack.push(tag.getElement().getName());
		super.startTag(tag);
	}

}
