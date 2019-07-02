package net.frost_byte.worldguardian.utility;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ProjectUtil
{
	private ProjectUtil() {}

	public static String getBranchAndID() {
		JsonObject jsonObject = new JsonParser().parse(readGitProperties()).getAsJsonObject();
		String branch = "";
		String commitId = "";
		String result = "";

		if (jsonObject != null)
		{
			if (jsonObject.has("git")) {
				JsonObject gitObject = (JsonObject) jsonObject.get("git");

				try {
					branch = gitObject.get("branch").getAsString();
					commitId = gitObject.get("commit").getAsJsonObject().get("id").getAsJsonObject().get("abbrev").getAsString();
//					if (gitObject.has("commit")) {
//						JsonObject commitObject = (JsonObject) gitObject.get("commit");
//						if (commitObject.has("id")) {
//							JsonObject idObject = (JsonObject) commitObject.get("id");
//							if(idObject.has("abbrev")) {
//								result += " commit id: " + idObject.get("abbrev");
//							}
//						}
//					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return "branch: " + branch + "; commit id: " + commitId;
	}

	public static String readGitProperties() {
		InputStream inputStream = ProjectUtil.class.getClassLoader()
				.getResourceAsStream("git.properties");
		try {
			return readFromInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return "Version information could not be retrieved";
		}
	}

	private static String readFromInputStream(InputStream inputStream)
			throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}
}
