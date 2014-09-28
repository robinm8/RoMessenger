package workspace;

import java.io.IOException;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class GetGroupMembers {

	public ArrayList getGroupRanks(int groupId) {
		ArrayList list = new ArrayList();

		HtmlPage groupOrig = null;
		try {
			groupOrig = Link.mobileManager
					.getPage("http://www.roblox.com/Groups/Group.aspx?gid="
							+ String.valueOf(groupId));
		} catch (Exception e) {
			System.out.println("Failed to get group page with ranks.");
		}

		HtmlPage group = groupOrig;
		HtmlForm rForm = group.getElementByName("aspnetForm");
		HtmlSelect rSelect;
		if (!LoginManager.currentUser.isEmpty()) {
			try {
				rSelect = rForm
						.getSelectByName("ctl00$ctl00$cphRoblox$cphMyRobloxContent$rbxGroupRoleSetMembersPane$dlRolesetList");
			} catch (ElementNotFoundException e) {
				rSelect = rForm
						.getSelectByName("ctl00$cphRoblox$rbxGroupRoleSetMembersPane$dlRolesetList");
			}
		} else {
			rSelect = rForm
					.getSelectByName("ctl00$cphRoblox$rbxGroupRoleSetMembersPane$dlRolesetList");
		}

		rForm = null;
		for (HtmlOption opt : rSelect.getOptions()) {
			System.out.println(opt.getText());
			list.add(opt.getText());
		}

		return list;
	}

	public boolean isGroupAdminMembersAccessible(int groupId) {
		try {
			HtmlPage group = Link.mobileManager
					.getPage("http://www.roblox.com/My/GroupAdmin.aspx?gid="
							+ String.valueOf(groupId));
			if (group.getUrl().toString() != "http://www.roblox.com/My/Groups.aspx"
					&& !group.getUrl().toString()
							.equals("http://www.roblox.com/My/Groups.aspx")) {

				if (group.getHtmlElementById("Members") != null) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public void importMembersOfRankThroughPublicList(int groupId, int rankNum) {
		try {
			HtmlPage groupOrig = Link.mobileManager
					.getPage("http://www.roblox.com/Groups/Group.aspx?gid="
							+ String.valueOf(groupId));
			HtmlPage group = groupOrig;
			HtmlForm rForm = group.getElementByName("aspnetForm");
			HtmlSelect rSelect;

			ArrayList<HtmlOption> list = new ArrayList<HtmlOption>();
			if (!LoginManager.currentUser.isEmpty()) {
				try {
					rSelect = rForm
							.getSelectByName("ctl00$ctl00$cphRoblox$cphMyRobloxContent$rbxGroupRoleSetMembersPane$dlRolesetList");
				} catch (ElementNotFoundException e) {
					rSelect = rForm
							.getSelectByName("ctl00$cphRoblox$rbxGroupRoleSetMembersPane$dlRolesetList");
				}
			} else {
				rSelect = rForm
						.getSelectByName("ctl00$cphRoblox$rbxGroupRoleSetMembersPane$dlRolesetList");
			}

			rForm = null;
			for (HtmlOption opt : rSelect.getOptions()) {
				list.add(opt);
			}

			for (int index = 0; index < list.size(); index++) {
				HtmlOption opt = list.get(index);

				System.out.println(index + "\n" + rankNum + "\n");
				if (index == rankNum) {
					group = groupOrig;

					HtmlPage tcl = group;
					try {
						HtmlElement rContain = tcl
								.getHtmlElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");
						HtmlElement rCurrentRoleSetID = tcl
								.getHtmlElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_currentRoleSetID");

						rCurrentRoleSetID.setAttribute("value",
								opt.getAttribute("value"));

						rSelect.setSelectedAttribute(opt, true, true);
						rSelect.focus();

						rCurrentRoleSetID = null;
						group = null;

						HtmlElement afterContainer = null;
						HtmlElement nextContainer = null;
						int pages = 1;
						try {
							HtmlElement rInputButton = rContain
									.getElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_HiddenInputButton");
							HtmlPage afterClick = rInputButton.click();

							Link.mobileManager
									.waitForBackgroundJavaScriptStartingBefore(3000);

							rForm = afterClick.getElementByName("aspnetForm");
							rSelect = rForm
									.getSelectByName("ctl00$ctl00$cphRoblox$cphMyRobloxContent$rbxGroupRoleSetMembersPane$dlRolesetList");
							opt = rSelect.getOption(index);

							afterContainer = afterClick
									.getHtmlElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");

							HtmlElement rCurrentPage = rContain
									.getElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_PageTextBox");
							rCurrentPage.setAttribute("value", "");

							DomNode rTotalPages = afterContainer
									.getElementById(
											"ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_Div1")
									.getChildNodes().get(3);

							System.out.println("Total Pages in rank "
									+ opt.asText() + ": "
									+ rTotalPages.getTextContent());
							pages = Integer.parseInt(rTotalPages
									.getTextContent());
							rTotalPages = null;
							rCurrentPage = null;
							afterClick = null;
							rInputButton = null;
							rContain = null;
						} catch (ElementNotFoundException e) {
							System.out.println("Total Pages in rank "
									+ opt.asText() + ": 1");
							pages = 1;
						} catch (IOException e) {
							e.printStackTrace();
						}

						nextContainer = afterContainer;
						for (int p = 1; p <= pages; p++) {
							System.out.println("Current Page: " + (p) + " of "
									+ pages + " | Rank: " + opt.asText());

							Link.g.changeState("pg: " + (p) + " of " + pages
									+ " | " + opt.asText());
							for (int m = 0; m < 8; m++) {
								try {
									HtmlElement member = nextContainer
											.getElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_ctrl"
													+ m + "_hlMember");

									Link.addItem(member.getAttribute("title"));

									System.out.println("Imported "
											+ member.getAttribute("title"));

									member = null;
								} catch (Exception e) {
								}
							}

							if (pages > 1) {
								HtmlElement input = afterContainer
										.getElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_PageTextBox");

								input.setAttribute("value",
										String.valueOf(p + 1));

								HtmlElement submitPage = afterContainer
										.getElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_HiddenInputButton");

								HtmlPage nextPage = null;
								try {
									nextPage = submitPage.click();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Link.mobileManager
										.waitForBackgroundJavaScriptStartingBefore(3000);
								nextContainer = (HtmlElement) nextPage
										.getHtmlElementById("ctl00_ctl00_cphRoblox_cphMyRobloxContent_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");

								rForm = nextPage.getElementByName("aspnetForm");
								rSelect = rForm
										.getSelectByName("ctl00$ctl00$cphRoblox$cphMyRobloxContent$rbxGroupRoleSetMembersPane$dlRolesetList");
								opt = rSelect.getOption(index);

								nextPage = null;
								submitPage = null;
								input = null;
							}
						}
					} catch (ElementNotFoundException e) {
						System.out.println(e.getMessage());

						HtmlElement rContain = tcl
								.getHtmlElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");
						HtmlElement rCurrentRoleSetID = tcl
								.getHtmlElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_currentRoleSetID");

						rCurrentRoleSetID.setAttribute("value",
								opt.getAttribute("value"));

						rSelect.setSelectedAttribute(opt, true, true);
						rSelect.focus();

						Link.mobileManager
								.waitForBackgroundJavaScriptStartingBefore(2000);

						rCurrentRoleSetID = null;
						group = null;

						HtmlElement afterContainer = null;
						HtmlElement nextContainer = null;
						int pages = 1;
						try {
							HtmlElement rInputButton = rContain
									.getElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_HiddenInputButton");

							HtmlPage afterClick = rInputButton.click();

							afterContainer = afterClick
									.getHtmlElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");

							HtmlElement rCurrentPage = rContain
									.getElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_PageTextBox");
							rCurrentPage.setAttribute("value", "");

							DomNode rTotalPages = afterContainer
									.getElementById(
											"ctl00_cphRoblox_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_Div1")
									.getChildNodes().get(3);

							System.out.println("Total Pages in rank "
									+ opt.asText() + ": "
									+ rTotalPages.getTextContent());
							pages = Integer.parseInt(rTotalPages
									.getTextContent());
							rTotalPages = null;
							rCurrentPage = null;
							afterClick = null;
							rInputButton = null;
							rContain = null;
						} catch (ElementNotFoundException e1) {
							System.out.println("Total Pages in rank "
									+ opt.asText() + ": 1");
							pages = 1;
						} catch (IOException e2) {
							e2.printStackTrace();
						}

						nextContainer = afterContainer;
						Link.mobileManager.waitForBackgroundJavaScript(2000);

						for (int p = 1; p <= pages; p++) {
							System.out.println("Current Page: " + (p) + " of "
									+ pages + " | Rank: " + opt.asText());
							Link.g.changeState("pg: " + (p) + " of " + pages
									+ " | " + opt.asText());

							for (int m = 0; m < 8; m++) {
								try {
									HtmlElement member = nextContainer
											.getElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_dlUsers_ctrl"
													+ m + "_hlMember");

									Link.addItem(member.getAttribute("title"));

									System.out.println("Imported "
											+ member.getAttribute("title"));

									member = null;
								} catch (Exception e3) {
								}
							}

							if (pages > 1) {
								HtmlElement input = afterContainer
										.getElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_dlUsers_Footer_ctl01_PageTextBox");

								input.setAttribute("value",
										String.valueOf(p + 1));

								HtmlElement submitPage = afterContainer
										.getElementById("ctl00_cphRoblox_GroupWallPane_GroupWallPager_ctl01_HiddenInputButton");

								HtmlPage nextPage = null;
								try {
									nextPage = submitPage.click();
								} catch (IOException e4) {
									e4.printStackTrace();
								}
								Link.mobileManager
										.waitForBackgroundJavaScript(2000);
								nextContainer = (HtmlElement) nextPage
										.getHtmlElementById("ctl00_cphRoblox_rbxGroupRoleSetMembersPane_GroupMembersUpdatePanel");
								nextPage = null;
								submitPage = null;
								input = null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to import rank " + rankNum);
			e.printStackTrace();
		}
		Link.g.changeState("Finished importing rank " + rankNum);
	}
}