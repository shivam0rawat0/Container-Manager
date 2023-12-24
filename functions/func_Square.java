package functions;

import java.util.*;

public class func_Square extends _generic_ {
	@Override
	public String apply(Map<String, String> inputMap) {
		int k = Integer.parseInt(inputMap.get("a"));
		return send("a=" + Integer.toString(k * k));
	}
}