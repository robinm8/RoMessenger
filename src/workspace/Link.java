package workspace;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Link {
	static double version = 5.2;
	static String app = "Ro-Messenger " + version;
	static String desc = app
			+ " \n"
			+ "A Roblox message system built for speed, simplicity, and the community \n\nCreated by Mark Robinson (sim4city)";
	static int itemsInitial = 3;
	static int currentItemPos = 0;
	static Object[] names = new String[itemsInitial];
	static boolean resizeRequested = true;
	static boolean handledData = false;
	static String openMessage = "Message 1";
	static String loginStatusCompare = "";
	static WebClient manager = new WebClient(BrowserVersion.FIREFOX_3_6);
	static WebClient mobileManager = new WebClient(
			BrowserVersion.INTERNET_EXPLORER_8);
	static Downloader d = new Downloader();
	static Timer timer = new Timer();
	static GetUserInfo userInfo = new GetUserInfo();
	static ImportUsersFromPlace importUsersWithPlace = new ImportUsersFromPlace();
	static GetGroupMembers getGroupMembers = new GetGroupMembers();
	static GetGroupInfo getGroupInfo = new GetGroupInfo();
	static SendMessage sendMessage = new SendMessage();
	static ManipXML x = new ManipXML();
	static Date previousTime;
	static GUI g;
	static Document doc = null;

	public static String checkAllowed() {
		try {
			System.out.println("Checking allowed");
			TextPage p = manager
					.getPage("https://dl.dropboxusercontent.com/sh/0p0z1tzqb0l5ap1/oW0QmQ7gJD/allowed.txt");
			System.out
					.println("https://dl.dropboxusercontent.com/sh/0p0z1tzqb0l5ap1/oW0QmQ7gJD/allowed.txt");

			if (p.getContent() == "0" || p.getContent().equals("0")) {
				return "false";
			}
		} catch (Exception e) {
			return "failed";
		}
		return "true";
	}

	public static void startApp() {
		g = new GUI();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String result = checkAllowed();

				if (result == "true") {
					System.out.println("Allowed.");
					d.checkForUpdate();
				} else if (result == "false") {
					System.out.println("Not allowed.");
					JOptionPane.showMessageDialog(null,
							"Ro-Messenger has been disabled by sim4city.",
							"Alert", JOptionPane.WARNING_MESSAGE);
				} else if (result == "failed") {
					System.out.println("Not allowed.");
					JOptionPane
							.showMessageDialog(
									null,
									"Ro-Messenger was unable to connect to Dropbox. Try again later.",
									"Alert - Internet Unavailable",
									JOptionPane.WARNING_MESSAGE);
				}

				System.runFinalization();
				System.gc();
			}
		});
	}

	public static boolean addItem(Object num) {
		try {
			boolean duplicateFound = false;
			String name = num.toString();
			System.out.println(name);

			for (Object itm : names) {
				String item = "" + itm;
				if (item != null && name != null && (item.equals(name))
						|| item == name
						|| item.toLowerCase() == name.toLowerCase()
						|| item.toLowerCase().equals(name.toLowerCase())) {
					duplicateFound = true;
					System.out.println("Duplicate of " + item + " found.");
				}
			}
			if (!duplicateFound
					&& !Link.x.getCheckedGroup().getAttribute("id").equals("0")
					&& !Link.userInfo.IsInGroup(Link.userInfo
							.getUserIdFromUserName(name), Integer
							.parseInt(Link.x.getCheckedGroup().getAttribute(
									"id")))) {
				if (currentItemPos + 1 >= names.length) {
					Object[] temp = new Object[names.length * 2];
					System.arraycopy(names, 0, temp, 0, names.length);
					names = temp;
					System.out.println("Created larger array");
				}
				names[currentItemPos] = num;
				System.out.println("set " + num + " as names[" + currentItemPos
						+ "]");
				currentItemPos += 1;
				return true;
			} else if (!duplicateFound) {
				if (currentItemPos + 1 >= names.length) {
					Object[] temp = new Object[names.length * 2];
					System.arraycopy(names, 0, temp, 0, names.length);
					names = temp;
					System.out.println("Created larger array");
				}
				names[currentItemPos] = num;
				System.out.println("set " + num + " as names[" + currentItemPos
						+ "]");
				currentItemPos += 1;
				return true;
			}

		} catch (NullPointerException e) {
		}
		return false;
	}

	public static void makeTimerTask() {
		System.out.println("New timer created.");
		previousTime = null;
		timer.cancel();
		timer.purge();
		timer = new Timer();

		TimerTask updateLoginStatus = new TimerTask() {
			@Override
			public void run() {
				if (GUI.loginStatus.getText() == loginStatusCompare
						&& !LoginManager.currentUser.isEmpty()) {
					GUI.loginStatus.setText("Current User: "
							+ LoginManager.currentUser);
				} else {
					loginStatusCompare = GUI.loginStatus.getText();
				}
			}
		};

		TimerTask task = new TimerTask() {
			public void run() {
				if (resizeRequested && g != null) {
					g.getGraphics().clearRect(0, 0, g.getSize().width,
							g.getSize().height);
					g.getContentPane().removeAll();

					if (g.resizedimage2 != null) {
						g.resizedimage2.flush();
					}

					g.resizedimage2 = (Image) g.BackgroundPanel(g.getWidth(),
							g.getHeight());
					g.resizedimage3 = new ImageIcon(g.resizedimage2);
					g.content = new JLabel(g.resizedimage3);
					g.setContentPane(g.content);
					g.content.setLayout(new BorderLayout());
					g.content.add(g.right, BorderLayout.CENTER);
					g.content.add(g.tabbedPane, BorderLayout.WEST);
					g.content.add(g.bottom, BorderLayout.SOUTH);
					g.validate();
					g.tabbedPane.validate();

					System.runFinalization();
					System.gc();

					if (!handledData) {
						handledData = true;
						x.Do("create");

						System.out.println(doc
								.getElementsByTagName("useOSLookAndFeel")
								.item(0).getAttributes().getNamedItem("value")
								.getTextContent() == "true");
						if (doc.getElementsByTagName("useOSLookAndFeel")
								.item(0).getAttributes().getNamedItem("value")
								.getTextContent() == "true"
								|| doc.getElementsByTagName("useOSLookAndFeel")
										.item(0).getAttributes()
										.getNamedItem("value").getTextContent()
										.equals("true")) {
							try {
								UIManager.setLookAndFeel(UIManager
										.getSystemLookAndFeelClassName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					System.out.println(g.getWidth() + "x" + g.getHeight());
					System.out.flush();
					resizeRequested = false;
				}

				try {
					timer.purge();
					if (previousTime != null) {
						Date newTime = Calendar.getInstance().getTime();
						long diff = newTime.getTime() - previousTime.getTime();
						if (diff + 2000 >= 1000) {
							x.Do("save");

							previousTime = Calendar.getInstance().getTime();
						} else {
							this.cancel();
							makeTimerTask();

						}
					} else {
						x.Do("save");

						previousTime = Calendar.getInstance().getTime();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 600, 1000);
		timer.scheduleAtFixedRate(updateLoginStatus, 600, 2000);
	}

	@SuppressWarnings("serial")
	public static void Start() {
		Link.manager.setCssEnabled(false);
		Link.manager.setJavaScriptEnabled(true);
		Link.manager.setRedirectEnabled(true);
		Link.manager.setThrowExceptionOnFailingStatusCode(false);
		Link.manager.setThrowExceptionOnScriptError(false);
		Link.manager.setPrintContentOnFailingStatusCode(false);
		Link.manager.setAjaxController(new AjaxController() {
			@Override
			public boolean processSynchron(HtmlPage page, WebRequest request,
					boolean async) {
				return true;
			}
		});

		Link.mobileManager.setCssEnabled(false);
		Link.mobileManager.setJavaScriptEnabled(false);
		Link.mobileManager.setRedirectEnabled(true);
		Link.mobileManager.setThrowExceptionOnFailingStatusCode(false);
		Link.mobileManager.setThrowExceptionOnScriptError(false);
		Link.mobileManager.setPrintContentOnFailingStatusCode(false);
		Link.mobileManager.setAjaxController(new AjaxController() {
			@Override
			public boolean processSynchron(HtmlPage page, WebRequest request,
					boolean async) {
				return true;
			}
		});

		Link.mobileManager.setRefreshHandler(new ImmediateRefreshHandler());
		Link.mobileManager.setCookieManager(Link.manager.getCookieManager());

		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
				.setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
				.setLevel(Level.OFF);

		makeTimerTask();
	}
}