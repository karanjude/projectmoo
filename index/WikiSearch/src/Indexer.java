import java.io.IOException;

import javax.swing.text.html.parser.DTD;

import org.apache.lucene.index.CorruptIndexException;

public class Indexer {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("java Indexer <wikipaedia_path> <index_path>");
			System.exit(0);
		}

		String wikipaediaDirectory = args[0];
		DTD dtd = null;
		try {
			dtd = DTD.getDTD("html.dtd");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		WikiPaideiaParser wikiPaediaParser = new WikiPaideiaParser(dtd);

		IndexBuilder indexBuilder = null;
		try {
			indexBuilder = new IndexBuilder(args[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		HtmlFileCollector htmlFileCollector = new HtmlFileCollector(
				wikiPaediaParser, indexBuilder);

		DirectoryVisitor directoryVisitor = new DirectoryVisitor(
				wikipaediaDirectory);
		directoryVisitor.visit(htmlFileCollector);

		System.out.println(htmlFileCollector.count() + " files added");
		
		try {
			indexBuilder.save();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
