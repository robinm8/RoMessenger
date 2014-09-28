package workspace;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GetGroupInfo {
	public String getGroupName(int groupId) {
		try {
			HtmlPage page = Link.mobileManager
					.getPage("http://www.roblox.com/Groups/Group.aspx?gid="
							+ groupId);

			return page.getDocumentElement().getElementById("description")
					.getChildNodes().get(0).getChildNodes().get(1)
					.getChildNodes().get(0).getTextContent();
		} catch (Exception e) {
		}
		return "";
	}
}