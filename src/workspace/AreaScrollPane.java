package workspace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class AreaScrollPane extends JScrollPane {
	BufferedImage buttonImage = null;

	public AreaScrollPane(JTextArea t) {
		super(t);
		
		setOpaque(false);
		setAutoscrolls(false);
		setBorder(BorderFactory.createEmptyBorder());
		
		getViewport().setOpaque(false);
		
		getHorizontalScrollBar().setBackground(new Color(0, 0, 0, 0));
		getHorizontalScrollBar().setForeground(new Color(0, 0, 0, 0));
		getHorizontalScrollBar().setBorder(null);

		getVerticalScrollBar().setBackground(new Color(0, 0, 0, 0));
		getVerticalScrollBar().setForeground(new Color(0, 0, 0, 0));
		getVerticalScrollBar().setBorder(null);

		for (int com = 0; com < getHorizontalScrollBar().getComponents().length; com++) {
			getHorizontalScrollBar().getComponents()[com]
					.setBackground(new Color(0, 0, 0, 0));
			getHorizontalScrollBar().getComponents()[com]
					.setForeground(new Color(0, 0, 0, 0));

			getHorizontalScrollBar().getComponents()[com].setEnabled(false);
		}
		
		for (int com = 0; com < getVerticalScrollBar().getComponents().length; com++) {
			getVerticalScrollBar().getComponents()[com]
					.setBackground(new Color(0, 0, 0, 0));
			getVerticalScrollBar().getComponents()[com]
					.setForeground(new Color(0, 0, 0, 0));

			getVerticalScrollBar().getComponents()[com].setEnabled(false);
		}

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int displaySizeX = (dim.width * 60) / 100;
		int displaySizeY = (dim.height * 50) / 100;

		setPreferredSize(new Dimension((displaySizeX * 60) / 100,
				(displaySizeY * 5) / 100));
	}
}