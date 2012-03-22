import java.io.IOException;

import javax.swing.text.html.parser.DTD;

import org.apache.lucene.index.CorruptIndexException;

public class Indexer {
	public static void main(String[] args) {
		if (args.length < 1)
			System.exit(0);

		String wikipaediaDirectory = args[0];
		HtmlFileCollector htmlFileCollector = new HtmlFileCollector();
		DirectoryVisitor directoryVisitor = new DirectoryVisitor(
				wikipaediaDirectory);
		directoryVisitor.visit(htmlFileCollector);

		IndexBuilder indexBuilder = null;
		try {
			indexBuilder = new IndexBuilder("index");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parseFiles(htmlFileCollector, indexBuilder);
		try {
			indexBuilder.save();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parseFiles(HtmlFileCollector htmlFileCollector, IndexBuilder indexBuilder) {
		try {
			DTD dtd = DTD.getDTD("html.dtd");
			WikiPaideiaParser wikiPaediaParser = new WikiPaideiaParser(dtd);
			htmlFileCollector.parseCollectedFiles(wikiPaediaParser, indexBuilder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
