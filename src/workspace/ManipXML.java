package workspace;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ManipXML {
	File fXmlFile = new File(System.getProperty("user.dir")
			+ "\\RoMessenger_Data.xml");

	public void Do(String w) {
		if (w == "load") {
			try {
				if (fXmlFile.exists()) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
					Link.doc = doc;
					Link.openMessage = doc.getDocumentElement()
							.getElementsByTagName("openMessage").item(0)
							.getAttributes().getNamedItem("name")
							.getTextContent();
					Element message = getOpenMessage();
					Link.g.subjectBox.setText(message.getAttribute("subject"));
					Link.g.messageBox.setText(message.getTextContent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (w == "create") {
			try {
				if (fXmlFile.exists()) {
					this.Do("load");
				} else {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();
					Document doc = docBuilder.newDocument();

					Element rootElement = doc.createElement("Data");
					doc.appendChild(rootElement);

					Element openMessage = doc.createElement("openMessage");
					openMessage.setAttribute("name", "Message 1");
					doc.getDocumentElement().appendChild(openMessage);

					Element useOSLookAndFeel = doc
							.createElement("useOSLookAndFeel");
					useOSLookAndFeel.setAttribute("value", "false");
					doc.getDocumentElement().appendChild(useOSLookAndFeel);

					Element checkedGroup = doc.createElement("checkedGroup");
					checkedGroup.setAttribute("id", "0");
					doc.getDocumentElement().appendChild(checkedGroup);

					Element message = doc.createElement("message");
					message.setAttribute("subject", " ");
					message.setAttribute("name", "Message 1");
					message.setTextContent(" ");
					doc.getDocumentElement().appendChild(message);

					Link.openMessage = message.getAttribute("name");
					Link.doc = doc;
					OutputFormat format = new OutputFormat(Link.doc);
					format.setIndenting(true);
					XMLSerializer serializer;
					serializer = new XMLSerializer(new FileOutputStream(
							new File(System.getProperty("user.dir")
									+ "\\RoMessenger_Data.xml")), format);
					serializer.serialize(Link.doc);
					docFactory = null;
					docBuilder = null;
					rootElement = null;
					doc = null;
					format = null;
					serializer = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (w == "save" && Link.doc != null) {
			try {
				Element message = getOpenMessage();
				message.setTextContent(Link.g.messageBox.getText());
				message.setAttribute("subject", Link.g.subjectBox.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
			OutputFormat format = new OutputFormat(Link.doc);
			format.setIndenting(true);
			XMLSerializer serializer;
			try {
				serializer = new XMLSerializer(new FileOutputStream(new File(
						System.getProperty("user.dir")
								+ "\\RoMessenger_Data.xml")), format);
				serializer.serialize(Link.doc);
				serializer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			format = null;
		}
		
		try {
			finalize();
		} catch (Throwable e) {}
	}

	public Element getOpenMessage() {
		for (int item = 0; item < Link.doc.getDocumentElement()
				.getElementsByTagName("message").getLength(); item++) {
			Node message = Link.doc.getDocumentElement()
					.getElementsByTagName("message").item(item);
			String openMessageName = Link.doc.getDocumentElement()
					.getElementsByTagName("openMessage").item(0)
					.getAttributes().getNamedItem("name").getTextContent();
			if (openMessageName.equals(message.getAttributes()
					.getNamedItem("name").getTextContent())
					|| openMessageName == message.getAttributes()
							.getNamedItem("name").getTextContent()) {
				return (Element) message;
			}
		}
		return null;
	}
}