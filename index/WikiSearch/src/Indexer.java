import java.io.IOException;

import javax.swing.text.html.parser.DTD;

public class Indexer {
	public static void main(String[] args) {
		if (args.length < 1)
			System.exit(0);

		String wikipaediaDirectory = args[0];
		HtmlFileCollector htmlFileCollector = new HtmlFileCollector();
		DirectoryVisitor directoryVisitor = new DirectoryVisitor(
				wikipaediaDirectory);
		directoryVisitor.visit(htmlFileCollector);

		parseFiles(htmlFileCollector);
	}

	private static void parseFiles(HtmlFileCollector htmlFileCollector) {
		try {
			DTD dtd = DTD.getDTD("html.dtd");
			WikiPaideiaParser wikiPaediaParser = new WikiPaideiaParser(dtd);
			htmlFileCollector.parseCollectedFiles(wikiPaediaParser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
