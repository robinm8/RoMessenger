package workspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	boolean showingList = false;
	boolean sending = false;

	static JTextField groupIdCheck = new JTextField("0");
	static JLabel loginStatus = new JLabel("Ready to Login");

	JLabel state = new JLabel("Welcome");
	JButton about = new TranslucentButton("About", null);
	JButton login = new TranslucentButton("Login Manager", null);
	JButton importNames = new TranslucentButton("Import Recipients", null);
	JButton savedMessages = new TranslucentButton("Saved Messages", null);
	JButton send = new TranslucentButton("Send", null);
	JButton toggleLookAndFeel = new TranslucentButton("Toggle Look and Feel",
			null);

	JLabel sTitle = new JLabel("Subject:");
	JLabel mTitle = new JLabel("Message:");
	JLabel openMessage = new JLabel();

	JTextArea messageBox = null;
	JScrollPane messageScroller = null;
	JTextArea subjectBox = null;

	Image resizedimage2;
	ImageIcon resizedimage3;
	JLabel content;

	JPanel bottom = new JPanel(new BorderLayout());
	JPanel right = new JPanel(new BorderLayout());

	JFrame importGUI = new JFrame("Import Recipients");
	JFrame messagesGUI = new JFrame("Saved Messages");
	JFrame loginManagerGUI = new JFrame("Login Manager");

	JTabbedPane tabbedPane = new JTabbedPane();

	JList list = new JList();
	JList usernameList = new JList();

	public GUI() {
		super(Link.app + " by sim4city");

		Link.Start();

		list.setListData(Link.names);
		usernameList.setListData(LoginManager.users.keySet().toArray());

		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.tabsOverlapBorder", false);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		this.setBounds((dim.width * 20) / 100, (dim.height * 25) / 100,
				(dim.width * 60) / 100, (dim.height * 50) / 100);

		tabbedPane.setOpaque(false);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setForeground(Color.LIGHT_GRAY);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(-1, -1, -1, 0));

		tabbedPane.setPreferredSize(new Dimension(((dim.width * 60) / 100) / 4,
				(dim.height * 50) / 100));

		tabbedPane.setUI(new BasicTabbedPaneUI() {
			@Override
			protected void installDefaults() {
				super.installDefaults();
				highlight = new Color(0, 0, 0, 0);
				lightHighlight = new Color(0, 0, 0, 0);
				shadow = new Color(0, 0, 0, 0);
				darkShadow = new Color(0, 0, 0, 0);
				focus = new Color(0, 0, 0, 0);
			}
		});

		state.setText("Welcome to " + Link.app);
		try {
			Image img = Toolkit
					.getDefaultToolkit()
					.getImage(new URL("http://www.imgbomb.com/i/d15/to878.png"))
					.getScaledInstance(32, 32, 128);
			this.setIconImage(img);
			img.flush();
		} catch (Exception e3) {
		}

		subjectBox = new TextAreaObj();
		subjectBox.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		subjectBox.setText("");

		messageBox = new TextAreaObj();
		messageScroller = new AreaScrollPane(messageBox);

		mTitle.setForeground(Color.WHITE);
		sTitle.setForeground(Color.WHITE);

		mTitle.setHorizontalAlignment(SwingConstants.CENTER);
		sTitle.setHorizontalAlignment(SwingConstants.CENTER);

		mTitle.setFont(new Font("sansserif", Font.BOLD, 16));
		sTitle.setFont(new Font("sansserif", Font.BOLD, 16));

		JPanel rightUp = new JPanel(new GridLayout(1, 0));
		JPanel rightUpDub = new JPanel(new GridLayout(3, 0));
		JPanel rightDown = new JPanel(new BorderLayout());

		JPanel settings = new JPanel(new GridLayout(9, 0, 0, 7));
		JPanel menu = new JPanel(new GridLayout(9, 0, 0, 7));

		settings.add(toggleLookAndFeel);

		menu.add(about);
		menu.add(login);
		menu.add(importNames);
		menu.add(savedMessages);

		rightUpDub.add(sTitle);
		rightUpDub.add(subjectBox);
		rightUpDub.add(mTitle);
		rightUp.add(rightUpDub);
		rightDown.add(messageScroller);
		rightDown.add(send, BorderLayout.SOUTH);

		right.add(rightUp, BorderLayout.NORTH);
		right.add(rightDown, BorderLayout.CENTER);

		tabbedPane.addTab("Menu", null, menu, "Show RoMessenger's Menu");
		tabbedPane.addTab("Settings", null, settings,
				"Show RoMessenger's Settings");

		menu.setOpaque(false);
		settings.setOpaque(false);
		right.setOpaque(false);
		rightDown.setOpaque(false);
		rightUpDub.setOpaque(false);
		rightUp.setOpaque(false);
		bottom.setOpaque(false);

		state.setFont(new Font("sansserif", Font.BOLD, 20));
		state.setForeground(Color.LIGHT_GRAY);

		bottom.add(state, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Link.resizeRequested = true;
			}
		});
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Waiting for data save operation.");

				Link.timer.cancel();

				new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						setTitle("Preparing to Exit");
						Thread.sleep(1000);
						setTitle("Data Saved");
						Thread.sleep(1000);
						setTitle("Exiting");
						System.out.println("Exiting.");
						System.exit(0);
						return null;
					}
				}.execute();
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, Link.desc, "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!LoginManager.currentUser.isEmpty() && !sending) {
					sending = true;
					send.setEnabled(false);
					new SwingWorker() {
						protected Object doInBackground() throws Exception {
							Link.sendMessage.sendMessages(subjectBox.getText(),
									messageBox);
							sending = false;
							send.setEnabled(true);
							return null;
						}
					}.execute();
				} else {
					changeState("Must be logged in before sending messages.");
				}
			}
		});

		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLoginManager();
			}
		});

		importNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showList();
			}
		});

		savedMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessages();
			}
		});

		toggleLookAndFeel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element lookAndFeel = (Element) Link.doc.getDocumentElement()
						.getElementsByTagName("useOSLookAndFeel").item(0);
				lookAndFeel.setAttribute("value", lookAndFeel
						.getAttribute("value") == "false" ? "true" : "false");

				if (lookAndFeel.getAttribute("value") == "true") {
					try {
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
						Link.g.changeState("RoMessenger is now using system look and feel.");
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else {
					try {
						UIManager.setLookAndFeel(UIManager
								.getCrossPlatformLookAndFeelClassName());
						Link.g.changeState("RoMessenger is now using cross platform look and feel.");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}

			}
		});

		loginManagerGUI.setBounds((dim.width * 35) / 100,
				(dim.height * 30) / 100, (dim.width * 30) / 100,
				(dim.height * 40) / 100);
		loginManagerGUI.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		loginManagerGUI.setVisible(false);
		loginManagerGUI.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				usernameList.setListData(Link.names);
				usernameList.setSelectedIndex(0);
				usernameList.ensureIndexIsVisible(0);
			}

			public void windowClosing(WindowEvent arg0) {
				hideLoginManager();
			}
		});

		JPanel manContent = new JPanel(new BorderLayout());
		JPanel manSouth = new JPanel(new GridLayout(3, 2));

		JLabel usernameInputDescription = new JLabel("Username:");
		JLabel passwordInputDescription = new JLabel("Password:");

		final JTextField usernameInput = new JTextField(20);
		final JPasswordField passwordInput = new JPasswordField();

		final JButton addLoginCredential = new JButton("Add User");
		final JButton delLoginCredential = new JButton("Remove User");

		usernameList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		usernameList.setLayoutOrientation(JList.VERTICAL);
		usernameList.setVisibleRowCount(0);
		usernameList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (!LoginManager.currentUser.equals((String) usernameList
						.getSelectedValue())) {
					addLoginCredential.setEnabled(false);
					delLoginCredential.setEnabled(false);

					LoginManager.changeUser((String) usernameList
							.getSelectedValue());

					addLoginCredential.setEnabled(true);
					delLoginCredential.setEnabled(true);
				}
			}
		});

		final JScrollPane usernameScroller = new JScrollPane(usernameList);
		usernameScroller.setPreferredSize(new Dimension(200, 80));

		loginStatus.setOpaque(false);
		usernameInputDescription.setOpaque(false);
		passwordInputDescription.setOpaque(false);
		manContent.setOpaque(false);
		manSouth.setOpaque(false);

		addLoginCredential.setFocusPainted(false);
		delLoginCredential.setFocusPainted(false);

		passwordInputDescription.setFont(new Font("sansserif", Font.BOLD, 20));
		usernameInputDescription.setFont(new Font("sansserif", Font.BOLD, 20));
		addLoginCredential.setFont(new Font("sansserif", Font.BOLD, 20));
		delLoginCredential.setFont(new Font("sansserif", Font.BOLD, 20));
		usernameInput.setFont(new Font("sansserif", Font.BOLD, 20));
		passwordInput.setFont(new Font("sansserif", Font.BOLD, 20));
		loginStatus.setFont(new Font("sansserif", Font.BOLD, 20));
		usernameList.setFont(new Font("sansserif", Font.BOLD, 16));

		manSouth.add(usernameInputDescription);
		manSouth.add(usernameInput);
		manSouth.add(passwordInputDescription);
		manSouth.add(passwordInput);
		manSouth.add(addLoginCredential);
		manSouth.add(delLoginCredential);

		manContent.add(loginStatus, BorderLayout.NORTH);
		manContent.add(usernameScroller, BorderLayout.CENTER);
		manContent.add(manSouth, BorderLayout.SOUTH);

		loginManagerGUI.add(manContent);

		addLoginCredential.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addLoginCredential.setEnabled(false);
				delLoginCredential.setEnabled(false);
				new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						char[] password = null;
						password = passwordInput.getPassword();
						String pass = String.valueOf(password);
						if (!usernameInput.getText().isEmpty()
								&& !pass.isEmpty()) {
							LoginManager.addUser(usernameInput.getText(), pass);
							usernameList.setListData(LoginManager.users
									.keySet().toArray());
							usernameList.setSelectedIndex(0);
							usernameList.ensureIndexIsVisible(0);
						}
						passwordInput.setText("");
						password = null;
						pass = null;
						addLoginCredential.setEnabled(true);
						delLoginCredential.setEnabled(true);
						return null;
					}
				}.execute();
			}
		});

		passwordInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int c = e.getKeyCode();
				if (c == KeyEvent.VK_ENTER) {
					addLoginCredential.setEnabled(false);
					delLoginCredential.setEnabled(false);
					new SwingWorker() {
						@Override
						protected Object doInBackground() throws Exception {
							char[] password = null;
							password = passwordInput.getPassword();
							String pass = String.valueOf(password);
							if (!usernameInput.getText().isEmpty()
									&& !pass.isEmpty()) {
								LoginManager.addUser(usernameInput.getText(),
										pass);
								usernameList.setListData(LoginManager.users
										.keySet().toArray());
								usernameList.setSelectedIndex(0);
								usernameList.ensureIndexIsVisible(0);
							}
							passwordInput.setText("");
							password = null;
							pass = null;
							addLoginCredential.setEnabled(true);
							delLoginCredential.setEnabled(true);
							return null;
						}
					}.execute();
				}
			}
		});

		delLoginCredential.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delLoginCredential.setEnabled(false);
				addLoginCredential.setEnabled(false);
				new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						int index = usernameList.getSelectedIndex();
						System.out.println(LoginManager.users.keySet()
								.toArray()[index]);
						LoginManager.removeUser((String) LoginManager.users
								.keySet().toArray()[index]);

						usernameList.requestFocusInWindow();
						System.out.println("Finished removal");
						usernameList.setListData(LoginManager.users.keySet()
								.toArray());

						if (LoginManager.users.keySet().toArray().length > 0) {
							System.out
									.println(index - 1 > 0 ? LoginManager.users
											.keySet().toArray()[index - 1]
											: LoginManager.users.keySet()
													.toArray()[0]);
							LoginManager
									.changeUser(index - 1 > 0 ? LoginManager.users
											.keySet().toArray()[index - 1]
											.toString() : LoginManager.users
											.keySet().toArray()[0].toString());

							usernameList.setSelectedIndex(index - 1);
							usernameList.ensureIndexIsVisible(index - 1);
						} else {
							loginStatus.setText("Ready to Login");
						}

						delLoginCredential.setEnabled(true);
						addLoginCredential.setEnabled(true);
						return null;
					}
				}.execute();
			}
		});

		messagesGUI.setBounds((dim.width * 28) / 100, (dim.height * 37) / 100,
				(dim.width * 45) / 100, (dim.height * 27) / 100);
		messagesGUI.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		messagesGUI.setVisible(false);
		messagesGUI.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				hideMessages();
			}
		});

		JPanel mesContent = new JPanel(new BorderLayout());
		JPanel mesNorth = new JPanel(new GridLayout(3, 2));
		JPanel mesCenter = new JPanel(new BorderLayout());

		final JButton makeMessage = new JButton("Create Message");
		final JButton switchMessage = new JButton("Switch Message");
		final JButton renameMessage = new JButton("Rename Message");
		final JButton deleteMessage = new JButton("Delete Message");
		final JButton importMessage = new JButton("Load Message From Text File");
		final JButton exportMessage = new JButton("Save Message To Text File");

		openMessage.setHorizontalAlignment(SwingConstants.CENTER);
		openMessage.setFont(new Font("sansserif", Font.BOLD, 16));

		makeMessage.setFocusPainted(false);
		switchMessage.setFocusPainted(false);
		renameMessage.setFocusPainted(false);
		deleteMessage.setFocusPainted(false);
		importMessage.setFocusPainted(false);
		exportMessage.setFocusPainted(false);

		deleteMessage.setFont(new Font("sansserif", Font.BOLD, 20));
		renameMessage.setFont(new Font("sansserif", Font.BOLD, 20));
		switchMessage.setFont(new Font("sansserif", Font.BOLD, 20));
		makeMessage.setFont(new Font("sansserif", Font.BOLD, 20));
		importMessage.setFont(new Font("sansserif", Font.BOLD, 20));
		exportMessage.setFont(new Font("sansserif", Font.BOLD, 20));

		mesNorth.add(switchMessage);
		mesNorth.add(renameMessage);
		mesNorth.add(makeMessage);
		mesNorth.add(deleteMessage);
		mesNorth.add(importMessage);
		mesNorth.add(exportMessage);

		mesCenter.add(openMessage, BorderLayout.NORTH);
		mesContent.add(mesCenter, BorderLayout.NORTH);
		mesContent.add(mesNorth, BorderLayout.CENTER);

		messagesGUI.add(mesContent, BorderLayout.CENTER);

		importMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new MyFilter());
					int returnVal = fc.showOpenDialog(GUI.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						try {
							FileInputStream fstream = new FileInputStream(file);
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(in));
							String strLine;
							String message = "";

							while ((strLine = br.readLine()) != null) {
								message = message + strLine + "\n";
							}

							System.out.println(message);
							Link.g.messageBox.setText(message);

							br.close();
							in.close();
							fstream.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					} else {
						System.out.println("Operation canceled by user.");
					}
				} catch (Exception e1) {
				}
			}
		});

		exportMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new MyFilter());
					int returnVal = fc.showSaveDialog(GUI.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();

						System.out.println("approved for save");

						BufferedWriter writer = new BufferedWriter(
								new FileWriter(file));

						for (String line : Link.g.messageBox.getText().split(
								"\n")) {
							System.out.println("writing " + line);
							writer.write(line);
							writer.newLine();
						}
						writer.close();

						list.setListData(Link.names);
						list.setSelectedIndex(0);
						list.ensureIndexIsVisible(0);

					}
				} catch (Exception e2) {
				}
			}
		});

		renameMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(null,
						"Input the new name for the open message.",
						"Rename Message", JOptionPane.QUESTION_MESSAGE);

				if (name != null && !name.isEmpty()) {
					boolean duplicate = false;
					for (int ni = 0; ni < Link.doc.getDocumentElement()
							.getElementsByTagName("message").getLength(); ni++) {
						if (Link.doc.getDocumentElement()
								.getElementsByTagName("message").item(ni)
								.getAttributes().getNamedItem("name")
								.getTextContent().equals(name)) {
							duplicate = true;
						}
					}

					if (!duplicate) {
						Link.x.getOpenMessage().setAttribute("name", name);
						Link.openMessage = name;

						Element xmlOpenMessage = (Element) Link.doc
								.getDocumentElement()
								.getElementsByTagName("openMessage").item(0);
						xmlOpenMessage.setAttribute("name", Link.openMessage);

						openMessage
								.setText("Open Message: " + Link.openMessage);

					} else {
						JOptionPane
								.showMessageDialog(
										null,
										"Message name already exists. Messages must be uniquely named.",
										"Error", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});

		switchMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] possibilities = (Object[]) new Object[Link.doc
						.getDocumentElement().getElementsByTagName("message")
						.getLength()];

				for (int ni = 0; ni < Link.doc.getDocumentElement()
						.getElementsByTagName("message").getLength(); ni++) {
					possibilities[ni] = Link.doc.getDocumentElement()
							.getElementsByTagName("message").item(ni)
							.getAttributes().getNamedItem("name")
							.getTextContent();
				}

				String s = (String) JOptionPane.showInputDialog(null,
						"Which message would you like to use?",
						"Select Message", JOptionPane.PLAIN_MESSAGE, null,
						possibilities, "");

				if ((s != null) && (s.length() > 0)) {
					Link.openMessage = s;

					Element xmlOpenMessage = (Element) Link.doc
							.getDocumentElement()
							.getElementsByTagName("openMessage").item(0);

					xmlOpenMessage.setAttribute("name", Link.openMessage);

					openMessage.setText("Open Message: " + Link.openMessage);

					if (Link.x.getOpenMessage() != null) {
						Element message = Link.x.getOpenMessage();
						subjectBox.setText(message.getAttribute("subject"));
						messageBox.setText(message.getTextContent());
					}
				}
			}
		});

		makeMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = "0";
				boolean repeat = true;

				do {
					name = String.valueOf(Integer.parseInt(name) + 1);
					boolean found = false;
					for (int ni = 0; ni < Link.doc.getDocumentElement()
							.getElementsByTagName("message").getLength(); ni++) {

						if (("Message " + name).equals(Link.doc
								.getDocumentElement()
								.getElementsByTagName("message").item(ni)
								.getAttributes().getNamedItem("name")
								.getTextContent())
								|| ("Message " + name) == Link.doc
										.getDocumentElement()
										.getElementsByTagName("message")
										.item(ni).getAttributes()
										.getNamedItem("name").getTextContent()) {
							found = true;
							break;
						}
					}
					if (found == false) {
						repeat = false;
					}
				} while (repeat == true);

				Element person = Link.doc.createElement("message");
				person.setAttribute("name", "Message " + name);
				person.setAttribute("subject", " ");
				Link.doc.getDocumentElement().appendChild(person);
				Link.openMessage = "Message " + name;

				Element xmlOpenMessage = (Element) Link.doc
						.getDocumentElement()
						.getElementsByTagName("openMessage").item(0);
				xmlOpenMessage.setAttribute("name", Link.openMessage);

				openMessage.setText("Open Message: " + Link.openMessage);

				if (person != null) {
					Element message1 = Link.x.getOpenMessage();
					subjectBox.setText(message1.getAttribute("subject"));
					messageBox.setText(message1.getTextContent());
				}
			}
		});

		deleteMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Link.doc.getDocumentElement()
						.getElementsByTagName("message").getLength() > 1) {
					int oneBefore = 0;

					for (int ni = 0; ni < Link.doc.getDocumentElement()
							.getElementsByTagName("message").getLength(); ni++) {
						if (Link.doc.getDocumentElement()
								.getElementsByTagName("message").item(ni) == Link.x
								.getOpenMessage()) {
							oneBefore = ni - 1;
						}
					}

					Link.doc.getDocumentElement().removeChild(
							Link.x.getOpenMessage());

					if (oneBefore != -1) {

						Link.openMessage = Link.doc.getDocumentElement()
								.getElementsByTagName("message")
								.item(oneBefore).getAttributes()
								.getNamedItem("name").getTextContent();

						Element xmlOpenMessage = (Element) Link.doc
								.getDocumentElement()
								.getElementsByTagName("openMessage").item(0);
						xmlOpenMessage.setAttribute("name", Link.openMessage);

						openMessage
								.setText("Open Message: " + Link.openMessage);

						if (Link.x.getOpenMessage() != null) {

							Element message = Link.x.getOpenMessage();
							subjectBox.setText(message.getAttribute("subject"));
							messageBox.setText(message.getTextContent());
						}
					} else if (Link.doc.getDocumentElement()
							.getElementsByTagName("message").item(0) != null) {

						Link.openMessage = Link.doc.getDocumentElement()
								.getElementsByTagName("message").item(0)
								.getAttributes().getNamedItem("name")
								.getTextContent();

						Element xmlOpenMessage = (Element) Link.doc
								.getDocumentElement()
								.getElementsByTagName("openMessage").item(0);
						xmlOpenMessage.setAttribute("name", Link.openMessage);

						openMessage
								.setText("Open Message: " + Link.openMessage);

						if (Link.x.getOpenMessage() != null) {

							Element message = Link.x.getOpenMessage();
							subjectBox.setText(message.getAttribute("subject"));
							messageBox.setText(message.getTextContent());
						}
					} else {
						openMessage.setText("Open Message: Unknown");
					}
				} else {
					System.out
							.println("Can't remove the last remaining message.");
				}
			}
		});

		importGUI.setBounds((dim.width * 35) / 100, (dim.height * 30) / 100,
				(dim.width * 30) / 100, (dim.height * 40) / 100);
		importGUI.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		importGUI.setVisible(false);
		importGUI.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				list.setListData(Link.names);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
			}

			public void windowClosing(WindowEvent arg0) {
				hideList();
			}
		});

		JPanel content = new JPanel(new BorderLayout());
		JPanel north = new JPanel();
		JPanel center = new JPanel(new BorderLayout());
		JPanel south = new JPanel(new BorderLayout());
		JPanel southUp = new JPanel(new GridLayout(4, 0));
		JPanel southUpLow = new JPanel(new GridLayout(0, 3));
		JPanel southLow = new JPanel(new GridLayout(4, 1));

		final JTextField input = new JTextField(20);
		final JButton clear = new JButton("Clear");
		final JButton remove = new JButton("Remove");
		final JButton add = new JButton("Add User");
		final JButton importFromPlace = new JButton("Import From Place");
		final JButton importGroup = new JButton("Import From Group");
		final JButton importTextFile = new JButton("Import From List");
		final JButton exportToTextFile = new JButton("Save List");

		final JLabel manualImport = new JLabel("Import Methods");
		final JLabel groupCheckTitle = new JLabel("GroupId Check:");

		manualImport.setHorizontalAlignment(SwingConstants.CENTER);
		manualImport.setFont(new Font("sansserif", Font.BOLD, 16));

		groupCheckTitle.setHorizontalAlignment(SwingConstants.CENTER);
		groupCheckTitle.setFont(new Font("sansserif", Font.BOLD, 16));

		clear.setFocusPainted(false);
		remove.setFocusPainted(false);
		add.setFocusPainted(false);
		importFromPlace.setFocusPainted(false);
		importGroup.setFocusPainted(false);
		importTextFile.setFocusPainted(false);
		exportToTextFile.setFocusPainted(false);

		input.setText("Input Username or Userid Here!");
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(0);
		final JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(200, 80));

		southLow.add(importGroup);

		southLow.add(importFromPlace);

		southLow.add(importTextFile);

		southLow.add(groupIdCheck);

		southUpLow.add(remove);
		southUpLow.add(input);
		southUpLow.add(add);

		southUp.add(clear);
		southUp.add(exportToTextFile);
		southUp.add(manualImport);
		southUp.add(southUpLow);

		south.add(southUp, BorderLayout.NORTH);
		south.add(southLow, BorderLayout.CENTER);

		center.add(listScroller);
		content.add(south, BorderLayout.SOUTH);
		content.add(center, BorderLayout.CENTER);
		content.add(north, BorderLayout.NORTH);
		importGUI.add(content, BorderLayout.CENTER);

		list.setListData(Link.names);
		list.setSelectedIndex(0);
		list.ensureIndexIsVisible(0);

		importGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker() {
					protected Object doInBackground() throws Exception {
						try {
							if (!LoginManager.currentUser.isEmpty()) {
								String groupId = "";
								groupId = JOptionPane
										.showInputDialog(
												importGUI,
												"Input GroupId to select imported ranks.",
												"Input GroupId",
												JOptionPane.QUESTION_MESSAGE);
								System.out.println(groupId);

								if (groupId.equals(null) || groupId == null
										|| groupId == "null") {
									importGroup.setEnabled(true);
								}

								if (!groupId.isEmpty()) {
									importGroup.setEnabled(false);

									final int groupid = Integer
											.valueOf(groupId);

									final JFrame group = new JFrame(
											"Select Group Ranks");

									ArrayList groupRanks = Link.getGroupMembers
											.getGroupRanks(groupid);
									JPanel content = new JPanel(
											new BorderLayout());
									JPanel rankPane = new JPanel(
											new GridLayout(groupRanks.size(), 0));

									JLabel title = new JLabel(
											"Select Ranks From "
													+ Link.getGroupInfo
															.getGroupName(groupid)
													+ " to Import");

									final JButton importGroupRanks = new JButton(
											"Import");
									group.add(title);

									final ArrayList checkBoxes = new ArrayList();

									for (int rankNum = 0; rankNum < groupRanks
											.size(); rankNum++) {

										String rank = groupRanks.get(rankNum)
												.toString();

										JCheckBox rankCheckBox = new JCheckBox(
												rank);

										checkBoxes.add(rankCheckBox);

										rankPane.add(rankCheckBox);

									}
									content.add(title, BorderLayout.NORTH);
									content.add(rankPane, BorderLayout.CENTER);
									content.add(importGroupRanks,
											BorderLayout.SOUTH);

									importGroupRanks
											.addActionListener(new ActionListener() {
												public void actionPerformed(
														ActionEvent e) {
													new SwingWorker() {
														protected Object doInBackground()
																throws Exception {
															importGroupRanks
																	.setEnabled(false);
															for (int index = 0; index < checkBoxes
																	.size(); index++) {
																JCheckBox box = (JCheckBox) checkBoxes
																		.get(index);
																if (box.isSelected()) {
																	Link.getGroupMembers
																			.importMembersOfRankThroughPublicList(
																					groupid,
																					index);
																}
															}

															list.setListData(Link.names);
															list.setSelectedIndex(0);
															list.ensureIndexIsVisible(0);
															importGroupRanks
																	.setEnabled(true);
															return null;
														}
													}.execute();
												}
											});

									group.add(content);
									group.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
									group.addWindowListener(new WindowAdapter() {
										public void windowClosing(WindowEvent e) {
											group.dispose();
											importGroup.setEnabled(true);
											super.windowClosing(e);
										}
									});
									group.setVisible(true);
									group.pack();
								} else {
									importGroup.setEnabled(true);
								}
							} else {
								importGroup.setEnabled(true);
							}
						} catch (NumberFormatException e1) {
							importGroup.setEnabled(true);
						}
						return null;
					}
				}.execute();
			}
		});

		importFromPlace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker() {
					protected Object doInBackground() throws Exception {
						try {
							if (!LoginManager.currentUser.isEmpty()) {
								String place = JOptionPane
										.showInputDialog(
												importGUI,
												"Input placeId to import users from that place.",
												"Input PlaceId",
												JOptionPane.QUESTION_MESSAGE);
								System.out.println(place);

								if (place.equals(null) || place == null) {
									importFromPlace.setEnabled(true);
								}

								if (!place.isEmpty()) {
									importFromPlace.setEnabled(false);

									final int placeId = Integer.valueOf(place);

									Link.importUsersWithPlace
											.importUsersFromPlaceId(placeId);

									list.setListData(Link.names);
									list.setSelectedIndex(0);
									list.ensureIndexIsVisible(0);
								}
							}
						} catch (NumberFormatException e1) {
							importFromPlace.setEnabled(true);
						}
						importFromPlace.setEnabled(true);
						return null;
					}
				}.execute();
			}
		});

		exportToTextFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new MyFilter());
					int returnVal = fc.showSaveDialog(GUI.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();

						System.out.println("approved");

						BufferedWriter writer = new BufferedWriter(
								new FileWriter(file));

						for (Object obj : Link.names) {
							if (obj != null) {
								System.out.println("writing " + obj);
								writer.write((String) obj);
								writer.newLine();
							}
						}
						writer.close();

						list.setListData(Link.names);
						list.setSelectedIndex(0);
						list.ensureIndexIsVisible(0);

					}
				} catch (Exception e2) {

				}
			}
		});

		importTextFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new MyFilter());
				int returnVal = fc.showOpenDialog(GUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						FileInputStream fstream = new FileInputStream(file);
						DataInputStream in = new DataInputStream(fstream);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(in));
						String strLine;
						while ((strLine = br.readLine()) != null) {
							Link.addItem(strLine);

						}

						list.setListData(Link.names);
						list.setSelectedIndex(0);
						list.ensureIndexIsVisible(0);
						br.close();
						in.close();
						fstream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					System.out.println("Operation canceled by user.");
				}

				list.setListData(Link.names);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
			}
		});

		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Link.names = new Object[Link.itemsInitial];
				Link.currentItemPos = 0;
				list.setListData(Link.names);
				list.requestFocusInWindow();
				System.out.println("Finished removal");
			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = list.getSelectedIndex();
				System.out.println(Link.names[index] + " is removing.");
				Link.names[index] = null;

				if (Link.names.length == 0) {
					remove.setEnabled(false);
				} else {
					if (index == Link.names.length) {
						index--;
					}
					remove.setEnabled(true);
				}

				Object[] temp = new Object[Link.names.length];
				if (Link.currentItemPos > 0) {
					Link.currentItemPos -= 1;
				}

				int currentpos = 0;
				for (Object ob : Link.names) {
					if (ob != null) {
						temp[currentpos] = ob;
						currentpos += 1;
					}
				}

				Link.names = temp;
				list.setListData(Link.names);
				list.requestFocusInWindow();
				System.out.println("Finished removal");

				input.setText("Input Username or Userid Here!");
				list.setListData(Link.names);
				list.setSelectedIndex(index - 1);
				list.ensureIndexIsVisible(index - 1);
			}
		});
		input.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				input.setText("");
				list.setListData(Link.names);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
				add.setText("Add User");
			}

			public void focusLost(FocusEvent arg0) {
				if (input.getText().equals("")) {
					input.setText("Input Username or Userid Here!");
					list.setListData(Link.names);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);
				}
			}
		});

		groupIdCheck.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				groupIdCheck.setText("");
			}

			public void focusLost(FocusEvent arg0) {
				if (groupIdCheck.getText().isEmpty()) {
					groupIdCheck.setText("0");
				}
			}
		});

		groupIdCheck.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					Integer.parseInt(groupIdCheck.getText());
				} catch (NumberFormatException e) {
					groupIdCheck.setText("");
				}
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (input.getText() != "Input Username or Userid Here!"
						&& !input.getText().equals(
								"Input Username or Userid Here!")) {
					try {
						String username = Link.userInfo
								.getUserNameFromUserId(Integer.parseInt(input
										.getText()));
						if (username != "" && !username.equals("")) {
							Link.addItem(username);
							input.setText("Input Username or Userid Here!");
							list.setListData(Link.names);
							list.setSelectedIndex(0);
							list.ensureIndexIsVisible(0);
						}
					} catch (NumberFormatException e) {
						if (Link.userInfo.getUserIdFromUserName(input.getText()) != -1) {
							Link.addItem(input.getText());
							input.setText("Input Username or Userid Here!");
							list.setListData(Link.names);
							list.setSelectedIndex(0);
							list.ensureIndexIsVisible(0);
						}
					}
					input.setText("");
				}
			}
		});

		list.setListData(Link.names);
		list.setSelectedIndex(0);
		list.ensureIndexIsVisible(0);
		list.repaint();
		list.requestFocusInWindow();

		TimerTask task = new TimerTask() {
			public void run() {
				input.requestFocusInWindow();
				add.setText("Add User");
				list.setListData(Link.names);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
				list.repaint();
				list.requestFocusInWindow();
			}
		};

		System.out.println("Refreshing list.");
		add.setText("Refreshing list.");
		Link.timer.schedule(task, 1000);
		task = null;
	}

	public void changeState(String to) {
		if (!state.getText().equals("Waiting for credentials.")) {
			state.setText(to);
		}
	}

	public BufferedImage BackgroundPanel(int width, int height) {
		try {
			BufferedImage br = BackgroundResize(
					width,
					height,
					new File(System.getProperty("user.dir") + "/background.jpg"));

			System.runFinalization();
			System.gc();
			return br;

		} catch (Exception e) {
		}

		return null;
	}

	public BufferedImage BackgroundResize(int dimx, int dimy, File img)
			throws IOException {
		BufferedImage resizedImage = null;

		if (!img.exists()) {
			JOptionPane.showMessageDialog(null,
					"Ro-Messenger was unable find background.jpg",
					"Alert - Background.jpg is non-existent",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}

		try {
			BufferedImage originalImage = ImageIO.read(img);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: originalImage.getType();
			resizedImage = new BufferedImage(dimx, dimy, type);
			Graphics g = resizedImage.getGraphics();

			g.setColor(getBackground());
			g.fillRect(0, 0, this.getSize().width, this.getSize().height);
			g.setColor(getForeground());
			paint(g);
			g.drawImage(originalImage, 0, 0, dimx, dimy, this);
			g.dispose();
			originalImage.flush();
		} catch (Exception e) {
		}
		return resizedImage;
	}

	public void showLoginManager() {
		loginManagerGUI.setVisible(true);
		System.gc();
	}

	public void hideLoginManager() {
		loginManagerGUI.setVisible(false);
		System.gc();
	}

	public void showList() {
		showingList = true;
		importGUI.setVisible(true);
		System.gc();
	}

	public void hideList() {
		showingList = false;
		importGUI.setVisible(false);
		System.gc();
	}

	public void showMessages() {
		openMessage.setText("Open Message: " + Link.openMessage);
		messagesGUI.setVisible(true);
	}

	public void hideMessages() {
		messagesGUI.setVisible(false);
		System.gc();
	}
}

class MyFilter extends FileFilter {
	public boolean accept(File file) {
		boolean result = (extentionEquals(file, "rtf") ? true
				: extentionEquals(file, "doc") ? true : extentionEquals(file,
						"txt") ? true : extentionEquals(file, "docx") ? true
						: false);

		return result;
	}

	public String getDescription() {
		return ".rtf, .txt, .doc, .docx";
	}

	public boolean extentionEquals(File f, String ext) {
		String exti = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			exti = s.substring(i + 1).toLowerCase();
		}
		if (exti != null) {

			return (exti.equals(ext));
		}
		if (f.isDirectory()) {
			return true;
		}
		return false;
	}
}