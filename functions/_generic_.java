package functions;

import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class _generic_ implements Function<Map<String, String>, String> {
	private int oport;
	private String okey;
	private String oname;
	private boolean channel;

	public _generic_() {
		channel = true;
	}

	@Override
	public String apply(Map<String, String> _var_) {
		return "";
	}

	public void setOutEndPoint(int op, String ok, String on) {
		oport = op;
		okey = ok;
		oname = on;
		channel = false;
	}

	public void decouple() {
		oport = -1;
		okey = "";
		oname = "";
		channel = true;
	}

	public String send(String data) {
		if (channel)
			return data;
		try {
			URL apiUrl = new URL("http://localhost:" + oport + "/" + oname);
			HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Cookie", "apikey=" + okey);
			connection.setDoOutput(true);

			String formDataString = data;
			connection.getOutputStream().write(formDataString.getBytes(StandardCharsets.UTF_8));

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}