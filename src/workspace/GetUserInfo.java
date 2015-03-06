package workspace;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GetUserInfo {
	WebClient manager = new WebClient(BrowserVersion.FIREFOX_10);

	public int getUserIdFromUserName(String userName) {
		manager.setCssEnabled(false);
		manager.setJavaScriptEnabled(false);
		manager.setAjaxController(new NicelyResynchronizingAjaxController());
		manager.setRedirectEnabled(true);
		manager.setThrowExceptionOnFailingStatusCode(false);
		manager.setThrowExceptionOnScriptError(false);
		manager.setPrintContentOnFailingStatusCode(false);
		manager.setCssEnabled(false);

		try {
			HtmlPage page = manager
					.getPage("http://www.roblox.com/User.aspx?username="
							+ userName);
			if (page.getUrl().equals(
					"http://www.roblox.com/Error/DoesntExist.aspx")) {
				System.out.println("UserId does not exist.");
			} else {
				manager.closeAllWindows();
				return Integer.parseInt(page.getUrl().toString().substring(35));
			}
		} catch (Exception e) {
			System.out.println("Error getting user page");
		}
		manager.closeAllWindows();
		return -1;
	}

	public String getUserNameFromUserId(int userId) {
		try {
			HtmlPage page = manager
					.getPage("http://www.roblox.com/User.aspx?ID=" + userId);
			HtmlElement avatarImg = (HtmlElement) page
					.getElementById("ctl00_cphRoblox_rbxUserPane_AvatarImage");

			if (page.getUrl().equals(
					"http://www.roblox.com/Error/DoesntExist.aspx")) {
				System.out.println("Username does not exist.");
			} else {
				manager.closeAllWindows();
				return avatarImg.getAttribute("title").toString();
			}
		} catch (Exception e) {
			System.out.println("Error getting user page");
		}
		manager.closeAllWindows();
		return "";
	}

	public boolean IsInGroup(int userId, int groupId) {
		if (!GUI.groupIdCheck.getText().equals("0")) {
			try {
				HtmlPage page = manager
						.getPage("http://www.roblox.com/Game/LuaWebService/HandleSocialRequest.ashx?method=IsInGroup&playerid="
								+ userId + "&groupid=" + groupId);
				manager.closeAllWindows();
				return Boolean.parseBoolean(page.asText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		manager.closeAllWindows();
		return false;
	}
}