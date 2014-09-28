package workspace;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class TranslucentButton extends JButton {
	BufferedImage buttonImage = null;

	public TranslucentButton(String label, BufferedImage buttonImage) {
		super(label);
		if (buttonImage != null) {
			this.buttonImage = buttonImage;
		}
		
		setContentAreaFilled(false);
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setBorderPainted(false);
		setFocusPainted(false);
		setFont(new Font("sansserif", Font.BOLD, 20));
	}

	public void paint(Graphics g) {
		if (buttonImage == null || buttonImage.getWidth() != getWidth()
				|| buttonImage.getHeight() != getHeight()) {
			buttonImage = getGraphicsConfiguration().createCompatibleImage(
					getWidth(), getHeight());
		}
		Graphics gButton = buttonImage.getGraphics();
		gButton.setClip(g.getClip());
		super.paint(gButton);

		Graphics2D g2d = (Graphics2D) g;
		AlphaComposite newComposite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, .35f);
		g2d.setComposite(newComposite);
		g2d.drawImage(buttonImage, 0, 0, null);

		gButton.dispose();
		newComposite = null;
		g2d.dispose();
		buttonImage = null;
	}
}