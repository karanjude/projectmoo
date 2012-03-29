import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class InformationNeeds {

	public static void main(String[] args) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			dom = builder.parse(new File(args[0]));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InformationNeed in = populateTitleDescriptionNarrative(dom);
		System.out.println(in.title);
		System.out.println(in.desc);
		System.out.println(in.narr);
		
		System.out.println(">>>>title>>>>>>>");
		Searcher.search(args[1], in.title);
		System.out.println(">>>>desc>>>>>>>>");
		Searcher.search(args[1], in.desc);
		System.out.println(">>>narr>>>>>>>>");
		Searcher.search(args[1], in.narr);
		System.out.println(">>>>title>>desc>>");
		Searcher.search(args[1], in.title + "  " + in.desc);

	}

	private static InformationNeed populateTitleDescriptionNarrative(Document dom) {
		Element root = dom.getDocumentElement();
		InformationNeed in = new InformationNeed();
		parseElement(root, in, "title");
		parseElement(root, in, "desc");
		parseElement(root, in, "narr");
		return in;
	}

	private static void parseElement(Element root, InformationNeed in, String tag) {
		NodeList list = root.getElementsByTagName(tag);
		if (null != list && list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element) list.item(i);
				String v = e.getFirstChild().getNodeValue();
				in.set(tag, v);
			}
		}
	}
}
