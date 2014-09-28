package workspace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JTextArea;

public class ManipulateText {
	public void change(String something, boolean append, JTextArea textArea) {
		try {
			FileWriter fWriter = new FileWriter(System.getProperty("user.dir")
					+ "/Draft Message.txt", append);
			BufferedWriter writer = new BufferedWriter(fWriter);
			FileReader fr = new FileReader(System.getProperty("user.dir")
					+ "/Draft Message.txt");
			BufferedReader reader = new BufferedReader(fr);

			if (something == "save") {
				String text = textArea.getText();
				writer.write(text);
			} else {
				if (something == "load") {
					String text = "";
					String textLine = "";
					while (textLine != null) {
						textLine = reader.readLine();
						if (textLine != null) {
							text = text + textLine + "\n";
						}
					}
					textArea.setText(text);
				} else {
					writer.write(something);
					writer.newLine();
				}
			}

			writer.close();
			fWriter.close();
			reader.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}