import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import functions.*;
import lib.*;

public class App {
	private static String PREFIX = "\n		";
	private static ContainerFactory cf;

	public static void main(String args[]) {
		cf = new ContainerFactory();
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.print("\n#cm> ");
		} while (runCommand(scanner.nextLine()));
		scanner.close();
	}

	public static Boolean runCommand(String cmd) {
		String[] verb = cmd.split(" ");
		switch (verb[0]) {
			case "init":
				if (verb.length != 3) {
					print("init error" + PREFIX + "usage init container_name port");
				} else {
					initContainer(verb[1], Integer.parseInt(verb[2]));
				}
				break;
			case "map":
				if (verb.length != 3) {
					print("map error" + PREFIX + "usage map container_A container_B");
				} else {
					joinAPI(verb[1], verb[2]);
				}
				break;
			case "key":
				getAPIKey(verb[1]);
				break;
			case "-map":
				detachAPI(verb[1]);
				break;
			case "load":
				readScript(verb[1]);
				break;
			case "exit":
			case "quit":
				return false;
			default:
				print("[ERROR] [" + verb[0] + "] no such command");
		}
		return true;
	}

	public static void readScript(String sname) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("script/" + sname + ".script"));
			String line = null;
			do {
				line = reader.readLine();
				if (line != null)
					runCommand(line);
			} while (line != null);
			reader.close();
		} catch (Exception e) {
			print("[ERROR] [" + sname + "] script not found");
		}
	}

	public static void initContainer(String functionName, int port) {
		try {
			String path = "logs/" + port + ".log";
			File file = new File(path);
			file.createNewFile();
			String className = "functions." + functionName;
			Class<?> dynamicClass = Class.forName(className);
			Constructor<?> constructor = dynamicClass.getDeclaredConstructor();
			Object instance = constructor.newInstance();
			cf.createContainer(functionName, port, path, (_generic_) instance);
		} catch (Exception e) {
			print("[ERROR] [" + functionName + "] function not found");
		}
	}

	public static void getAPIKey(String functionName) {
		cf.getAPIKey(functionName);
	}

	public static void joinAPI(String fn1, String fn2) {
		cf.joinAPI(fn1, fn2);
	}

	public static void detachAPI(String fn1) {
		cf.detachAPI(fn1);
	}

	public static void print(String message) {
		System.out.println(PREFIX + message);
	}
}