import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HtmlFileCollector {
	public static final int MAX_FILES = 1;
	List<File> htmlFiles = new ArrayList<File>(); 

	public void collect(File htmlFile) {
		System.out.println(htmlFile.getAbsolutePath());
		htmlFiles.add(htmlFile);
	}

	public int count() {
		return htmlFiles.size();
	}

	public void parseCollectedFiles(WikiPaideiaParser wikiPaediaParser) throws IOException {
		for (File file : htmlFiles) {
			wikiPaediaParser.parseFile(file);
		}
		
	}

}
