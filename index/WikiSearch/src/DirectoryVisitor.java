import java.io.File;

public class DirectoryVisitor {

	private File wikipaediaDirectory;

	public DirectoryVisitor(String wikipaediaDirectory) {
		this.wikipaediaDirectory = new File(wikipaediaDirectory);
	}

	public void visit(HtmlFileCollector htmlFileCollector) {
		visit(this.wikipaediaDirectory, htmlFileCollector);
	}

	private void visit(File toVisit, HtmlFileCollector htmlFileCollector) {
		if (toVisit.isDirectory() && htmlFileCollector.count() <= htmlFileCollector.MAX_FILES) {
			String[] filenames = toVisit.list();
			for (String entity : filenames) {
				if(htmlFileCollector.count() > htmlFileCollector.MAX_FILES)
					break;
				File child = new File(toVisit.getAbsolutePath()
						+ File.separatorChar + entity);
				if (child.isDirectory()) {
					visit(child, htmlFileCollector);
				} else {
					htmlFileCollector.collect(child);
				}
			}
		}

	}

}
