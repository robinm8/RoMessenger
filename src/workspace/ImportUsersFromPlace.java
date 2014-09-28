package workspace;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ImportUsersFromPlace {
	int usersImported = 0;
	int guests = 0;
	String placeName = "";

	public void importUsersWithPage(final HtmlPage place) {
		Link.mobileManager.waitForBackgroundJavaScript(3000);

		System.out.println(place.asXml());
		List<?> friendList = place
				.getByXPath("//a[@style=\'display:inline-block;height:48px;width:48px;\']/@title");
		System.out.println("Gathered list = " + friendList.size());

		Iterator it = friendList.iterator();
		int duplicates = 0;
		while (it.hasNext()) {
			Object o = it.next();
			String name = o.toString().substring(25, o.toString().length() - 1);
			if (!name.equals("A friendly guest")) {
				System.out.println(name);
				boolean worked = Link.addItem(name);

				if (!worked) {
					duplicates++;
				} else {
					usersImported++;
				}
			} else {
				guests++;
				duplicates++;
			}

			if (guests == 0) {
				Link.g.changeState("Imported " + usersImported
						+ " users playing " + placeName);
			} else {
				Link.g.changeState("Imported " + usersImported + " users and "
						+ guests + " guests playing " + placeName);
			}
		}

		if (duplicates != friendList.size()) {
			HtmlElement controls = place
					.getHtmlElementById("ctl00_cphRoblox_TabbedInfo_GamesTab_RunningGamesList_RunningGamesDataPager_Footer");
			List<HtmlElement> nextButton = (List<HtmlElement>) controls
					.getByXPath("//a[text()=\'Next\']");
			System.out.println(nextButton.get(0).toString());

			try {
				HtmlPage nextPage = nextButton.get(0).click();

				importUsersWithPage(nextPage);

			} catch (IOException e) {}
		}
	}

	public void importUsersFromPlaceId(int placeId) {
		usersImported = 0;
		guests = 0;

		Link.g.changeState("Preparing to import users from placeId " + placeId
				+ ".");
		try {
			final HtmlPage placePage = (HtmlPage) Link.mobileManager
					.getPage("http://www.roblox.com/seo-place?id=" + placeId);

			placeName = placePage.getHtmlElementById("Item").getChildNodes()
					.get(0).getChildNodes().get(0).getTextContent();

			Link.g.changeState("Preparing to import users from " + placeName
					+ ".");

			Link.mobileManager.waitForBackgroundJavaScriptStartingBefore(1000);

			HtmlPage firstServerPage = placePage.getHtmlElementById(
					"__tab_ctl00_cphRoblox_TabbedInfo_GamesTab").click();

			Link.mobileManager.waitForBackgroundJavaScriptStartingBefore(3000);

			importUsersWithPage(firstServerPage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("All done.");
		if (guests == 0) {
			Link.g.changeState("Finished importing " + usersImported
					+ " players at " + placeName);
		} else {
			Link.g.changeState("Finished importing " + usersImported
					+ " users and " + guests + " guests at " + placeName);
		}

		Link.mobileManager.closeAllWindows();
	}
}