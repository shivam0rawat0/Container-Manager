package lib;

import java.util.*;
import functions.*;

public class ContainerFactory {
	private static String PREFIX = "\n		";
	private Map<String, Container> cmap;

	public ContainerFactory() {
		cmap = new HashMap<String, Container>();
	}

	public void createContainer(String containerName, int port, String path, _generic_ requestHandler) {
		if(!cmap.containsKey(containerName)) {
			String cid = Generator.getApiKey();
			Container tcontainer = new Container(containerName, cid, port, requestHandler);
			cmap.put(containerName, tcontainer);
			tcontainer.setOStream(path);
			tcontainer.start();
			print("[INFO] Container started @[http://localhost:" + port + "/" + containerName + "]");
		} else {
			print("[ERROR] Container already runnig  @[http://localhost:" + port + "/" + containerName + "]");
		}
	}

	public void stopContainer(String containerName) {
		Container temp = cmap.get(containerName);
		String result = "";
		if (temp != null) {
			temp.stop();
			cmap.remove(containerName);
			result = "[INFO] " + containerName + " : stopped";
		}
		else {
			result = "[ERROR] [" + containerName + "] Container is not initialized";
		}
		print(result);
	}
	
	public void getAPIKey(String containerName) {
		Container temp = cmap.get(containerName);
		String result = "";
		if (temp != null)
			result = "[INFO] " + containerName + " : " + temp.getAPIKey();
		else {
			result = "[ERROR] [" + containerName + "] Container is not initialized";
		}
		print(result);
	}

	public void joinAPI(String fn1, String fn2) {
		Container c1 = cmap.get(fn1);
		Container c2 = cmap.get(fn2);
		String result = "";
		if (c1 != null) {
			if (c2 != null) {
				c1.setOutEndPoint(c2.getPort(), c2.getAPIKey(), fn2);
				result = "[INFO] " + fn1 + "(output) -> " + fn2 + "(input) mapped";
			} else {
				result = "[ERROR] [" + fn2 + "] Container is not initialized";
			}
		} else {
			result = "[ERROR] [" + fn1 + "] Container is not initialized";
		}
		print(result);
	}

	public void detachAPI(String fn1) {
		Container c1 = cmap.get(fn1);
		String result = "";
		if (c1 != null) {
			c1.decouple();
			result = "[INFO] " + fn1 + "(output -> std response) decoupled";
		} else {
			result = "[ERROR] [" + fn1 + "] Container is not initialized";
		}
		print(result);
	}

	public static void print(String message) {
		System.out.println(PREFIX + message);
	}
}