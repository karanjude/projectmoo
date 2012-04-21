package ir;
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

	public void parseFile(File file, IndexBuilder indexBuilder) throws IOException {
		Reader reader = new FileReader(file);
		atext = new ArrayList<String>();
		htext = new ArrayList<String>();
		titletext = new ArrayList<String>();
		resttext = new  ArrayList<String>();
		stack = new Stack();
		this.parse(reader);
		indexBuilder.addToIndex(atext, htext, titletext, resttext, file.getAbsolutePath());
		atext = null;
		htext = null;
		titletext = null;
		resttext = null;
		stack = null;
		reader.close();
		System.gc();
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
