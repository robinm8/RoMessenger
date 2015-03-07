package workspace;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class LoginManager {
	static String currentUser = "";
	static Map<String, String> users = new HashMap<String, String>();
	static boolean loggedInToMobile = false;
	static boolean loggedInToDesktop = false;
	static String userLoggingIn = "";
	static String userPass = "";
	static HtmlElement captcha = null;

	public static void addUser(String user, String pass) {
		if (!users.containsKey(user)) {
			userLoggingIn = user;
			userPass = pass;

			Link.manager.closeAllWindows();
			Link.manager = new WebClient(BrowserVersion.FIREFOX_3_6);

			loggedInToDesktop = false;
			loggedInToMobile = false;

			loginToRobloxMobile(user, pass);
		}
	}

	public static void removeUser(String user) {
		Link.manager.closeAllWindows();
		Link.manager = new WebClient(BrowserVersion.FIREFOX_3_6);
		Link.manager.setCssEnabled(false);
		Link.manager.setJavaScriptEnabled(true);
		Link.manager.setRedirectEnabled(true);
		Link.manager.setThrowExceptionOnFailingStatusCode(false);
		Link.manager.setThrowExceptionOnScriptError(false);
		Link.manager.setPrintContentOnFailingStatusCode(false);

		loggedInToDesktop = false;
		loggedInToMobile = false;

		userLoggingIn = "";
		userPass = "";

		currentUser = "";

		try {
			Link.mobileManager.getPage("http://m.roblox.com/Account/LogOff");
		} catch (Exception e) {
			e.printStackTrace();
		}

		users.remove(user);
	}

	public static void changeUser(String user) {
		if (users.containsKey(user)) {
			userLoggingIn = user;
			userPass = users.get(user);

			Link.manager.closeAllWindows();
			Link.manager = new WebClient(BrowserVersion.FIREFOX_3_6);
			Link.manager.setCssEnabled(false);
			Link.manager.setJavaScriptEnabled(true);
			Link.manager.setRedirectEnabled(true);
			Link.manager.setThrowExceptionOnFailingStatusCode(false);
			Link.manager.setThrowExceptionOnScriptError(false);
			Link.manager.setPrintContentOnFailingStatusCode(false);
			loggedInToDesktop = false;
			loggedInToMobile = false;

			loginToRobloxMobile(user, users.get(user));
		}
		System.gc();
	}

	public static int loginAsNextUser() {
		int next = -1;

		for (int index = 0; index < users.size(); index++) {
			if (users.keySet().toArray()[index].equals(currentUser)) {
				if (index + 1 < users.size()) {
					next = index + 1;
				} else {
					next = 0;
				}
			}
		}

		if (next > -1) {
			changeUser((String) users.keySet().toArray()[next]);
		}

		return next;
	}

	public static boolean canLoginToAnotherUser() {
		return users.keySet().size() > 1;
	}

	public static void loginToRobloxMobile(final String username,
			final String pass) {
		try {
			Link.mobileManager.getPage("http://m.roblox.com/Account/LogOff");
			final HtmlPage loginPage = Link.mobileManager
					.getPage("https://m.roblox.com/Login");

			if (!loginPage.getUrl().toString().equals("https://m.roblox.com/")) {
				System.out.println("Logging into Roblox Mobile");

				GUI.loginStatus.setText("Attempting Login - m.Roblox.com");

				HtmlElement userName = loginPage.getHtmlElementById("UserName");
				userName.setAttribute("value", username);
				HtmlElement password = loginPage.getHtmlElementById("Password");
				password.setAttribute("value", pass);

				boolean captchaIsRequired = false;
				try {
					List<?> iframeList = loginPage.getByXPath("//iframe");

					HtmlElement iframe = (HtmlElement) iframeList.get(0);

					final HtmlPage captchaPage = Link.mobileManager
							.getPage(iframe.getAttribute("src").toString());

					List<?> imgList = captchaPage.getByXPath("//img");

					HtmlElement captchaImg = (HtmlElement) imgList.get(0);

					final JFrame captchaInput = new JFrame("Submit Captcha");
					JPanel container = new JPanel(new BorderLayout());
					JPanel bottom = new JPanel(new BorderLayout());
					JPanel bottomEast = new JPanel();
					final JLabel thumb = new JLabel();
					final JTextField input = new JTextField(20);
					JButton submitRecaptcha = new JButton("Submit");

					bottomEast.add(submitRecaptcha);
					bottom.add(input, BorderLayout.WEST);
					bottom.add(bottomEast, BorderLayout.EAST);
					container.add(thumb, BorderLayout.NORTH);
					container.add(bottom, BorderLayout.SOUTH);
					captchaInput.add(container);
					captchaInput
							.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					captchaInput.addWindowListener(new WindowAdapter() {

						public void windowClosing(WindowEvent e) {
							super.windowClosed(e);
							captchaInput.dispose();
							GUI.loginStatus.setText("Login Canceled!");
						}
					});

					captchaInput.setAlwaysOnTop(true);
					captchaInput.setVisible(true);

					GUI.loginStatus.setText("Recaptcha Detected!");

					System.out.println("https://www.google.com/recaptcha/api/"
							+ captchaImg.getAttribute("src").toString());
					ImageIcon icon = new ImageIcon(
							new URL("https://www.google.com/recaptcha/api/"
									+ captchaImg.getAttribute("src").toString()));
					thumb.setIcon(icon);
					captchaInput.pack();
					captchaIsRequired = true;
					captchaInput.repaint();

					submitRecaptcha.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								HtmlElement attemptedSolution = captchaPage
										.getElementById("recaptcha_response_field");
								attemptedSolution.setAttribute("value",
										input.getText());

								HtmlElement submit = (HtmlElement) captchaPage
										.getElementByName("submit");

								HtmlPage result = (HtmlPage) submit.click();

								List<?> link = result.getByXPath("//textarea");
								HtmlElement confirmation = (HtmlElement) link
										.get(0);

								HtmlElement challengeField = (HtmlElement) loginPage
										.getElementByName("recaptcha_challenge_field");
								challengeField.setTextContent(confirmation
										.getTextContent());

								List<?> list = loginPage.getByXPath("//button");
								HtmlButton loginButton = (HtmlButton) list
										.get(0);
								HtmlPage afterLoginPage = (HtmlPage) loginButton
										.click();
								System.out.println(afterLoginPage.getUrl()
										.toString());
								if (afterLoginPage.getUrl().toString()
										.equals("http://m.roblox.com/home")) {
									System.out.println("Success");
									loggedInToMobile = true;
									loginToRobloxDesktop(username, pass);
								} else {
									GUI.loginStatus
											.setText("Invalid Credentials.");
								}
							} catch (Exception e1) {
								System.out
										.println("Recaptcha response incorrect, fetching new challenge");
								GUI.loginStatus.setText("Response incorrect");
								loginToRobloxMobile(username, pass);
							}
							captchaInput.dispose();
						}
					});
				} catch (Exception e) {
				}

				if (!captchaIsRequired) {
					List<?> link = loginPage.getByXPath("//button");
					HtmlButton submit = (HtmlButton) link.get(0);
					HtmlPage result = (HtmlPage) submit.click();
					System.out.println(result.getUrl().toString());
					if (result.getUrl().toString()
							.equals("http://m.roblox.com/home")) {
						System.out.println("Success");
						loggedInToMobile = true;
						loginToRobloxDesktop(username, pass);
					} else {
						GUI.loginStatus.setText("Invalid Credentials.");
					}
				}
			} else {
				loggedInToMobile = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			loggedInToMobile = false;
		}
		userLoggingIn = "";
		userPass = "";
	}

	public static void loginToRobloxDesktop(final String username,
			String password) {
		if (loggedInToMobile) {
			System.out.println("Logging into Roblox");

			GUI.loginStatus.setText("Attempting Login - Roblox.com");

			try {
				System.out.println("Getting page");

				HtmlPage login = Link.mobileManager
						.getPage("https://www.roblox.com/Login/iFrameLogin.aspx");

				System.out.println(login.getUrl().toString());

				if (login.getUrl().toString()
						.contains("http://www.roblox.com/home")) {
					loggedInToDesktop = true;
					currentUser = username;

					GUI.loginStatus
							.setText("You have successfully logged in to Roblox as "
									+ username + ".");
					Link.makeTimerTask();

					if (!users.containsKey(userLoggingIn)) {
						users.put(userLoggingIn, userPass);
					}
				} else {
					HtmlElement userName = login.getElementById("UserName");

					userName.type(username);

					HtmlElement pass = login.getElementById("Password");

					pass.type(password);

					HtmlElement submit = login.getElementById("LoginButton");

					submit.focus();

					final HtmlPage mostRecent = submit.dblClick();

					System.out.println(mostRecent.asXml());

					Link.manager.waitForBackgroundJavaScript(5000);

					try {
						captcha = mostRecent.getForms().get(0)
								.getElementById("recaptcha_widget_div");
					} catch (Exception e) {
					}

					if (captcha == null) {
						loggedInToDesktop = true;
						currentUser = username;

						GUI.loginStatus
								.setText("You have successfully logged in to Roblox as "
										+ username + ".");
						Link.makeTimerTask();

						if (!users.containsKey(userLoggingIn)) {
							users.put(userLoggingIn, userPass);
						}

						Link.manager.closeAllWindows();
					} else {
						try {
							GUI.loginStatus.setText("Recaptcha Detected!");

							System.out.println("recaptcha");

							login = mostRecent;
							final JFrame captchaInput = new JFrame(
									"Submit ReCAPTCHA");
							JPanel container = new JPanel(new BorderLayout());
							JPanel bottom = new JPanel(new BorderLayout());
							JPanel bottomEast = new JPanel();
							final JLabel thumb = new JLabel();
							final JTextField input = new JTextField(20);
							JButton submitRecaptcha = new JButton("Submit");
							JButton refresh = new JButton("Refresh");

							refresh.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										captcha.getElementById(
												"recaptcha_reload").click();

										DomNode img1 = captcha
												.getElementById(
														"recaptcha_image")
												.getChildNodes().get(0);

										String imgscr1 = img1.getAttributes()
												.getNamedItem("src")
												.getTextContent();

										System.out.println(imgscr1); // "https://www.google.com/recaptcha/api/"+

										ImageIcon icon = new ImageIcon(new URL(
												imgscr1)); // "https://www.google.com/recaptcha/api/"+

										thumb.setIcon(icon);

										captchaInput.pack();
									} catch (Exception e4) {
										System.out
												.println("Unable to refresh ReCAPTCHA image.");
									}
								}
							});

							bottomEast.add(submitRecaptcha);
							bottomEast.add(refresh);

							bottom.add(input, BorderLayout.WEST);
							bottom.add(bottomEast, BorderLayout.EAST);

							container.add(thumb, BorderLayout.NORTH);
							container.add(bottom, BorderLayout.SOUTH);

							captchaInput.add(container);
							captchaInput
									.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
							captchaInput.addWindowListener(new WindowAdapter() {
								public void windowClosing(WindowEvent e) {
									super.windowClosed(e);

									GUI.loginStatus.setText("Login Canceled");

									captchaInput.dispose();
								}
							});

							captchaInput.setAlwaysOnTop(true);
							captchaInput.setVisible(true);

							DomNode img1 = captcha
									.getElementById("recaptcha_image")
									.getChildNodes().get(0);

							String imgscr1 = img1.getAttributes()
									.getNamedItem("src").getTextContent();

							System.out.println(imgscr1); // "https://www.google.com/recaptcha/api/"

							ImageIcon icon = new ImageIcon(new URL(imgscr1));// "https://www.google.com/recaptcha/api/"

							thumb.setIcon(icon);
							captchaInput.pack();

							submitRecaptcha
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent e) {
											try {
												if (input.getText().isEmpty()) {
													input.setText(" ");
												}

												HtmlElement response = captcha
														.getElementById("recaptcha_response_field");

												response.setAttribute("value",
														input.getText());

												mostRecent
														.getForms()
														.get(0)
														.getElementById(
																"LoginButton")
														.click();

												Link.manager
														.waitForBackgroundJavaScript(2000);

												HtmlPage tried = ((HtmlPage) Link.manager
														.getCurrentWindow()
														.getEnclosedPage());

												System.out.println(tried
														.asXml());
												System.out.println(tried
														.getUrl().toString());

												if (tried
														.getUrl()
														.toString()
														.equals("http://www.roblox.com/my/home.aspx")
														|| tried.getUrl()
																.toString() == "http://www.roblox.com/my/home.aspx") {
													try {
														loggedInToDesktop = true;
														currentUser = username;

														GUI.loginStatus
																.setText("You have successfully logged in to Roblox as "
																		+ username
																		+ ".");

														Link.makeTimerTask();

														if (!users
																.containsKey(userLoggingIn)) {
															users.put(
																	userLoggingIn,
																	userPass);
														}

														userLoggingIn = "";
														userPass = "";
													} catch (Exception e1) {
													}
												} else {
													System.out
															.println("ReCAPTCHA answer was invalid");
													GUI.loginStatus
															.setText("Invalid ReCAPTCHA answer and/or credentials.");
												}

												response = null;
												captchaInput.dispose();
											} catch (Exception e1) {
												System.out
														.println("Login Error On Submit");
											}
										}
									});
						} catch (ElementNotFoundException e) {
							GUI.loginStatus
									.setText("Login Error. Please try again.");
							e.printStackTrace();
						} catch (Exception e) {
							GUI.loginStatus.setText("Invalid Credentials");
						}
					}
					submit = null;
				}
			} catch (Exception e) {
				GUI.loginStatus.setText("Login Error");
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		userLoggingIn = "";
		userPass = "";
	}
}