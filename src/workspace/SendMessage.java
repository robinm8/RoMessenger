package workspace;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JTextArea;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SendMessage {
	int sentMessages = 0;

	public boolean sendMessages(final String subject, final JTextArea messageBox) {
		try {
			final Date now = new Date();
			final SimpleDateFormat df = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss aa zzz");

			for (Object name : Link.names) {
				if (name != null) {
					Link.g.changeState("Sent " + sentMessages
							+ " messages since " + df.format(now));
					boolean messageSent = false;

					do {
						messageSent = sendMessage(name.toString(), subject,
								messageBox);
						if (!messageSent) {
							Link.g.changeState("Page unavailable. Trying again to send message.");
						} else {
							Link.g.changeState("Message " + (sentMessages + 1)
									+ " of " + Link.names.length
									+ " Sent. Current User: "
									+ LoginManager.currentUser);
						}
						Thread.sleep(1000);
					} while (!messageSent);
					sentMessages++;
				}
			}
			Date current = new Date();
			Link.g.changeState("Done, sent " + sentMessages + " messages in "
					+ getTimeDiff(current, now));

			Link.names = new Object[Link.itemsInitial];
			Link.currentItemPos = 0;
			Link.g.list.setListData(Link.names);
			Link.g.list.requestFocusInWindow();
			System.out.println("Finished clear");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean sendMessage(String string, String subject,
			JTextArea messageBox) {
		try {
			int userId = Link.userInfo.getUserIdFromUserName(string);

			if (userId != -1) {
				HtmlPage messagePage = Link.mobileManager
						.getPage("http://m.roblox.com/Messages/SendMessage/"
								+ userId);

				System.out.println("Attempting to send message.");
				System.out.println("username = " + string);
				System.out.println("userId = " + userId);

				HtmlElement subjectElement = messagePage
						.getHtmlElementById("Subject");
				subjectElement.setAttribute("value", subject);

				HtmlElement messageElement = messagePage
						.getHtmlElementById("Body");
				messageElement.setAttribute("value", messageBox.getText());
				messageElement.setTextContent(messageBox.getText());

				System.out.println(messagePage.asXml());

				boolean floodcheckStoppedUs = true;

				do {
					List<?> submitMessageList = messagePage
							.getByXPath("//input[@value='Send']");
					HtmlElement submitMessage = (HtmlElement) submitMessageList
							.get(0);
					messagePage = submitMessage.click();

					System.out.println(messagePage.asXml());
					try {
						List<?> floodCheckList = messagePage
								.getByXPath("//span[@class='field-validation-error']");
						HtmlElement floodCheck = (HtmlElement) floodCheckList
								.get(0);
						System.out.println(floodCheck.getTextContent());
						Link.g.changeState(floodCheck.getTextContent());
						Thread.sleep(1000);

						if (floodCheck.getTextContent().contains("limit")) {
							System.out
									.println("You are allowed to send messages to this user.");
							System.out.println("floodcheck found.");
							if (LoginManager.canLoginToAnotherUser()) {
								int nextUser = LoginManager.loginAsNextUser();

								if (nextUser <= 0) {
									System.out
											.println("next user is first user, sleep 60 seconds");
									Thread.sleep(60000);
								}
							} else {
								System.out
										.println("next user is first user, sleep 60 seconds");
								Thread.sleep(60000);
							}
							floodcheckStoppedUs = true;
						} else {
							System.out
									.println("You are not allowed to send messages to this user.");
							System.out.println("floodcheck not found.");
							sentMessages--;
							floodcheckStoppedUs = false;
						}
					} catch (Exception e) {
						System.out
								.println("You are allowed to send messages to this user.");
						System.out.println("floodcheck not found.");
						floodcheckStoppedUs = false;
					}
				} while (floodcheckStoppedUs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static long[] getTimeDifference(Date d1, Date d2) {
		long[] result = new long[5];
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTime(d1);

		long t1 = cal.getTimeInMillis();
		cal.setTime(d2);

		long diff = Math.abs(cal.getTimeInMillis() - t1);
		final int ONE_DAY = 1000 * 60 * 60 * 24;
		final int ONE_HOUR = ONE_DAY / 24;
		final int ONE_MINUTE = ONE_HOUR / 60;
		final int ONE_SECOND = ONE_MINUTE / 60;

		long d = diff / ONE_DAY;
		diff %= ONE_DAY;

		long h = diff / ONE_HOUR;
		diff %= ONE_HOUR;

		long m = diff / ONE_MINUTE;
		diff %= ONE_MINUTE;

		long s = diff / ONE_SECOND;
		long ms = diff % ONE_SECOND;
		result[0] = d;
		result[1] = h;
		result[2] = m;
		result[3] = s;
		result[4] = ms;

		return result;
	}

	public String getTimeDiff(Date dateOne, Date dateTwo) {
		String diff = "";

		long[] diff1 = getTimeDifference(dateOne, dateTwo);

		diff = String.format(
				"%d day(s), %d hour(s), %d minute(s), %d second(s)", diff1[0],
				diff1[1], diff1[2], diff1[3]);
		return diff;
	}
}