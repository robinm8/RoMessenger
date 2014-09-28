package workspace;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;

public class Downloader {
	static double lver = 0;

	public void checkForUpdate() {
		new SwingWorker() {
			protected Object doInBackground() throws Exception {
				if (isOutOfDate()) {
					Object[] options = { "Yes", "No" };
					int n = JOptionPane.showOptionDialog(
							null,
							"Would you like to update "
									+ Link.app
									+ " to "
									+ Link.app.substring(0,
											Link.app.length() - 3)
									+ Downloader.lver + "?", Link.app
									+ " Update Available",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (n == 0) {
						File u = update();
						System.out.println("Launching updated jar.");
						try {
							Desktop.getDesktop().open(u);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					} else {
						Link.startApp();
					}
				} else {
					Link.startApp();
				}
				return null;
			}
		}.execute();
	}

	public File update() {
		JFrame fr = new JFrame("Update Progress");
		JPanel c = new JPanel(new BorderLayout());
		JLabel tf = new JLabel("Initializing HtmlUnit");
		tf.setHorizontalAlignment(JLabel.CENTER);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		c.add(tf, BorderLayout.SOUTH);
		c.add(progressBar, BorderLayout.CENTER);
		fr.add(c);
		fr.setVisible(true);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();

		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setBounds((dim.width * 38) / 100, (dim.height * 40) / 100,
				(dim.width * 25) / 100, (dim.height * 20) / 100);
		File n = null;
		try {
			tf.setText("Fetching download link for Ro-Messenger version "
					+ lver);
			UnexpectedPage p = Link.manager
					.getPage("https://dl.dropboxusercontent.com/sh/0p0z1tzqb0l5ap1/VM_5tF8e41/RoMessenger.jar?dl=1");
			TextPage bitly = Link.manager
					.getPage("https://api-ssl.bitly.com/v3/shorten?version=2.01&login=sim4city&apiKey=R_a619ced6137f263a44382ed25a92b667&format=txt&longUrl="
							+ p.getUrl().toString());
			tf.setText("Downloading Ro-Messenger version " + lver + " from "
					+ bitly.getContent());
			System.out.println(bitly.getContent());
			InputStream is = p.getWebResponse().getContentAsStream();

			try {
				n = new File(System.getProperty("user.dir")
						+ "/RoMessenger.jar");
				OutputStream out = new FileOutputStream(n);
				int estEnd = is.available();
				System.out.println(estEnd + " bytes downloaded");
				int read = 0;
				int cdlsize = 0;
				byte[] bytes = new byte[1024];
				progressBar.setIndeterminate(false);

				while ((read = is.read(bytes)) > 0) {
					cdlsize += read;
					double cur = (double) cdlsize / estEnd;
					progressBar.setValue((int) (cur * 100));
					out.write(bytes, 0, read);
				}

				tf.setText("Launching Ro-Messenger version " + lver);
				is.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e);
			tf.setText("Download Failed: Ro-Messenger version " + lver
					+ " from ");
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
		}
		Link.manager.closeAllWindows();
		return n;
	}

	public boolean isOutOfDate() {
		System.out.println("Checking for update");
		try {
			TextPage p = Link.manager
					.getPage("https://dl.dropboxusercontent.com/sh/0p0z1tzqb0l5ap1/ZQoSXXxkem/v.txt");
			double ver = Double.parseDouble(p.getContent());
			System.out.println("Current version: " + Link.version
					+ " | Latest version: " + ver);
			lver = ver;
			Link.manager.closeAllWindows();

			return (ver > Link.version);
		} catch (Exception e) {
			System.out.println(e);
		}
		Link.manager.closeAllWindows();
		return false;
	}
}