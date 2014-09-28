package workspace;

import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ImportUsersFromFriends {
	int friendsImported = 0;
	String importingUser = "";

	final WebClient manager = new WebClient(BrowserVersion.FIREFOX_3_6);

	public void importBestFriends() {
		if (!LoginManager.currentUser.isEmpty()) {
			Link.g.changeState("Importing best friends.");
			try {
				HtmlPage friends = Link.mobileManager
						.getPage("http://www.roblox.com/my/best-friends");
				List<?> friendList = friends
						.getByXPath("//a[@class=\'name\']/text()");
				System.out.println("Gathered list = " + friendList.size());
				Iterator it = friendList.iterator();
				int bestFriendsImported = 0;
				while (it.hasNext()) {
					Object o = it.next();
					System.out.println(o.toString());
					Link.addItem(o.toString());
					bestFriendsImported++;
					Link.g.changeState("Analyzed " + bestFriendsImported
							+ " of your best friends.");
				}

				Link.g.changeState("Finished analyzing your best friends");
			} catch (Exception e) {
			}
		} else {
			Link.g.changeState("Must be logged in before analyzing best friends");
		}
	}

	public void importFromPage(final HtmlPage friends) {
		try {
			System.out.println(friends.getReadyState());

			List<?> friendList = friends
					.getByXPath("//a[@class=\'text-link\']/text()");
			System.out.println("Gathered list = " + friendList.size());

			Iterator it = friendList.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				System.out.println(o.toString());
				Link.addItem(o.toString());
				friendsImported++;
				Link.g.changeState("Analyzed " + friendsImported + " of "
						+ importingUser + "'s friends");
			}

			System.out.println("done with this page");
			List<HtmlElement> friendPageNextButton = (List<HtmlElement>) friends
					.getByXPath("//span[@class=\'pager next friends-next\']");
			HtmlElement next = friendPageNextButton.get(0);
			System.out.println("Pressing next button");
			final HtmlPage nextPage = (HtmlPage) next.click();
			System.out.println("Next button pressed");
			manager.waitForBackgroundJavaScriptStartingBefore(500);

			importFromPage(nextPage);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Exception within from page");
		}
	}

	public void importFriendsList(int userId) {
		friendsImported = 0;
		Link.g.changeState("Preparing to analyze friends.");

		manager.setJavaScriptEnabled(Boolean.TRUE);
		manager.setCssEnabled(false);
		manager.setRedirectEnabled(true);
		manager.setThrowExceptionOnFailingStatusCode(false);
		manager.setThrowExceptionOnScriptError(false);
		manager.setPrintContentOnFailingStatusCode(false);
		manager.setAjaxController(new NicelyResynchronizingAjaxController());
		manager.setActiveXNative(true);
		manager.setRefreshHandler(new ImmediateRefreshHandler());
		try {
			final HtmlPage friendPage = (HtmlPage) manager
					.getPage("http://www.roblox.com/Friends.aspx?UserID="
							+ userId);

			importingUser = Link.userInfo.getUserNameFromUserId(userId);
			manager.waitForBackgroundJavaScriptStartingBefore(500);
			importFromPage(friendPage);
		} catch (Exception e) {
		}

		System.out.println("All done.");
		Link.g.changeState("Finished analyzing all " + friendsImported + " of "
				+ importingUser + "'s friends");
		manager.closeAllWindows();
	}
}