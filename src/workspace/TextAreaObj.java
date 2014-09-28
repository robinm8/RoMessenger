package workspace;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextAreaObj extends JTextArea {
	BufferedImage buttonImage = null;

	public TextAreaObj() {
		super("");
		setFont(new Font("sansserif", Font.BOLD, 18));
		setEditable(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(false);
		setForeground(Color.CYAN);
		setCaretColor(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
}