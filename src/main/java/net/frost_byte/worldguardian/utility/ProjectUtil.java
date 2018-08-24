package net.frost_byte.worldguardian.utility;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ProjectUtil
{
	private ProjectUtil() {}

	public static String getBranchAndID() {
		JSONObject jsonObject = (JSONObject) JSONValue.parse(readGitProperties());
		String result = "";

		if (jsonObject != null)
		{
			if (jsonObject.containsKey("git.branch"))
				result += "branch: " + jsonObject.get("git.branch");

			if (jsonObject.containsKey("git.commit.id.abbrev"))
				result += " commit id: " + jsonObject.get("git.commit.id.abbrev");
		}

		return result;
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
