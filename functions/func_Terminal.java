package functions;

import java.util.*;

public class func_Terminal extends _generic_ {
	@Override
	public String apply(Map<String, String> inputMap) {
		String result;
		int k = Integer.parseInt(inputMap.get("a"));
		result = "" + k * 2;
		return result;
	}
}