import java.io.File;
import java.io.IOException;

public class DirectoryVisitor {

	private File wikipaediaDirectory;

	public DirectoryVisitor(String wikipaediaDirectory) {
		this.wikipaediaDirectory = new File(wikipaediaDirectory);
	}

	public void visit(HtmlFileCollector htmlFileCollector) {
		visit(this.wikipaediaDirectory, htmlFileCollector);
	}

	private void visit(File toVisit, HtmlFileCollector htmlFileCollector) {
		if (toVisit.isDirectory()) {
			String[] filenames = toVisit.list();
			for (String entity : filenames) {
				File child = new File(toVisit.getAbsolutePath()
						+ File.separatorChar + entity);
				if (child.isDirectory()) {
					visit(child, htmlFileCollector);
				} else {
					try {
						htmlFileCollector.collect(child);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
