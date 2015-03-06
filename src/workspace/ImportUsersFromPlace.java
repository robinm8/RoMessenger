package workspace;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ImportUsersFromPlace {
	int usersImported = 0;
	int guests = 0;
	String placeName = "";

	public void importUsersFromPlaceId(int placeId) {
		usersImported = 0;
		guests = 0;

		Link.g.changeState("Preparing to import users from placeId " + placeId
				+ ".");
		try {
			final HtmlPage placeNameAPI = (HtmlPage) Link.mobileManager
					.getPage("http://api.robloxapi.com/Assets/AssetInfo?AssetId="
							+ placeId);

			System.out.println(placeNameAPI.asText());

			JSONObject json = JSONObject.fromObject(placeNameAPI.asText());

			placeName = json.getString("Name");

			final UnexpectedPage placeAPI = (UnexpectedPage) Link.mobileManager
					.getPage("http://www.roblox.com/games/getgameinstancesjson?placeId="
							+ placeId + "&startindex=0");

			System.out.println(placeAPI.getWebResponse().getContentAsString());

			Link.g.changeState("Preparing to import users from " + placeName
					+ ".");

			JSONObject jsonObject = JSONObject.fromObject(placeAPI
					.getWebResponse().getContentAsString());
			int pages = Integer.parseInt(jsonObject.get("TotalCollectionSize")
					.toString());

			for (int pageNum = 0; pageNum < pages; pageNum++) {
				final UnexpectedPage placeAPIPage = (UnexpectedPage) Link.mobileManager
						.getPage("http://www.roblox.com/games/getgameinstancesjson?placeId="
								+ placeId + "&startindex=" + pageNum);

				JSONObject pageJSON = JSONObject.fromObject(placeAPIPage
						.getWebResponse().getContentAsString());
				JSONArray servers = pageJSON.getJSONArray("Collection");

				for (int serverNum = 0; serverNum < servers.size(); serverNum++) {
					JSONObject server = servers.getJSONObject(serverNum);
					JSONArray players = server.getJSONArray("CurrentPlayers");

					for (int playerNum = 0; playerNum < players.size(); playerNum++) {
						JSONObject player = players.getJSONObject(playerNum);
						String id = player.getString("Id");
						String username = player.getString("Username");

						if (Integer.parseInt(id) > 0) {
							if (Link.addItem(username)) {
								usersImported++;
							}
						} else {
							guests++;
						}

						Link.g.changeState("Imported "
								+ usersImported
								+ " users"
								+ (guests > 0 ? " and " + guests
										+ (guests == 1 ? " guest" : " guests")
										: "") + " from " + placeName);
					}
				}

				System.out.println(placeAPIPage.getWebResponse()
						.getContentAsString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("All done.");

		Link.g.changeState("Finished importing "
				+ usersImported
				+ " users"
				+ (guests > 0 ? " and " + guests
						+ (guests == 1 ? " guest" : " guests") : "") + " from "
				+ placeName);

		Link.mobileManager.closeAllWindows();
	}
}