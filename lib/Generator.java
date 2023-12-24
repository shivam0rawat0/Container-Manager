package lib;
public class Generator{
	public static String getApiKey(){
		int min = 0;
      		int max = 25;
      		String res = "";
      		for(int i=0;i<128;++i) {
          		char rd = (char)('a' + (int)(Math.random() * (max - min + 1) + min));
          		res += String.valueOf(rd);
      		}
      		return res;
	}
}