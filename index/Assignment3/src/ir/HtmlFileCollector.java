package ir;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HtmlFileCollector {
	public static final int MAX_FILES = 20;
	private final WikiPaideiaParser wikiPaediaParser;
	private final IndexBuilder indexBuilder; 
	int count = 0;

	public void collect(File htmlFile) throws IOException {
		System.out.println(htmlFile.getAbsolutePath() + " " + count);
		wikiPaediaParser.parseFile(htmlFile, indexBuilder);
		count++;
	}
	
	public HtmlFileCollector(WikiPaideiaParser wikiPaediaParser, IndexBuilder indexBuilder) {
		this.wikiPaediaParser = wikiPaediaParser;
		this.indexBuilder = indexBuilder;
	}

	public int count() {
		return count;
	}

}
