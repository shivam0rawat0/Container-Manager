package lib;

import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import functions.*;

public class Container {
	private String appName;
	private String apikey;
	private int port;
	private HttpServer server;
	private OutputStream ostream;
	private _generic_ consumer;

	public Container(String appName, String apikey, int port, _generic_ temp) {
		this.appName = appName;
		this.apikey = apikey;
		this.port = port;
		ostream = null;
		consumer = temp;
	}

	public void setOStream(String path) {
		try {
			this.ostream = new FileOutputStream(path, true);
		} catch (Exception e) {
		}
	}

	public void writeToOStream(String data) {
		if (ostream != null) {
			try {
				ostream.write(data.getBytes());
			} catch (Exception e) {
			}
		}
	}

	public String getAPIKey() {
		return apikey;
	}

	public int getPort() {
		return port;
	}

	public void setOutEndPoint(int oport, String okey, String oname) {
		consumer.setOutEndPoint(oport, okey, oname);
	}

	public void decouple() {
		consumer.decouple();
	}

	public void stop(){
		server.stop(1);
	}

	private void register() {
		HttpClient httpClient = HttpClient.newHttpClient();

		String apiUrl = "http://localhost:1000/registry/v1/" + appName + ":" + port;

		String jsonPayload = "{}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiUrl))
				.header("Content-Type", "application/json")
				.header("API-Key", "your-registry-key-here")
				.POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
				.build();
		try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			// Create a context with the function name
			server.createContext("/" + appName, (exchange -> {
				Headers RSheaders = exchange.getResponseHeaders();
				RSheaders.add("Access-Control-Allow-Origin", "*");

				Headers RQheaders = exchange.getRequestHeaders();
				String apiKeyHeader = RQheaders.getFirst("API-Key");

				String ckey = null;
				List<String> RQCookies = RQheaders.get("Cookie");
				if (RQCookies != null) {
					String cookies[] = RQCookies.get(0).split(";");
					for (String cookie : cookies) {
						String pair[] = cookie.split("=");
						if (pair[0].trim().equals("apikey")) {
							ckey = pair[1];
							break;
						}
					}
				}
				if ((apiKeyHeader != null && apiKeyHeader.equals(apikey)) || (ckey != null && ckey.equals(apikey))) {
					if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
						// Read the POST data
						InputStream inputStream = exchange.getRequestBody();
						byte[] requestBody = inputStream.readAllBytes();
						String postData = new String(requestBody, StandardCharsets.UTF_8);
						Map<String, String> map = new HashMap<>();
						String[] data = postData.split(",");
						for (String temp : data) {
							String[] pair = temp.split("=");
							map.put(pair[0], pair[1]);
						}
						String response = consumer.apply(map);
						// Process the POST data
						exchange.sendResponseHeaders(200, response.length());
						OutputStream os = exchange.getResponseBody();
						os.write(response.getBytes());
						os.close();
						writeToOStream("[POST : ok]\n");
					} else {
						String response = "<html>Container [ok]</html>";
						exchange.sendResponseHeaders(200, response.length());
						OutputStream os = exchange.getResponseBody();
						os.write(response.getBytes());
						os.close();
						writeToOStream("[GET : ok]\n");
					}
				} else {
					String response = "<html>Container [auth-error]</html>";
					exchange.sendResponseHeaders(401, response.length());
					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
					String rType = "GET";
					if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
						rType = "POST";
					writeToOStream("[" + rType + " : auth-error]\n");
				}
			}));
			server.start();
			register();
		} catch (Exception e) {
		}
	}
}
