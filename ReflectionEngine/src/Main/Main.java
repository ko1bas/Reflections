package Main;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import CommonInterfaces.StringConvertable;
import FileLoader.FileLoader;
import JarLoader.JarLoader;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException,
			FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException {

		if (!isArgsValid(args))
			return;

		boolean seeHints = false; //для проверки, что все загружается.

		JarLoader jarClassLoader = new JarLoader(args[1], "", seeHints);
		// вместо "" мог бы быть какой-нибудь пакет "com.google.gson.Gson"
		// тогда загружались бы классы только из него.

		Set<String> setClassNames = jarClassLoader.getCachedClasses();
		Class<?> currClass = null;
		Class<?> neededInterface = null;
		boolean isNeededInterface = false;
		for (String className : setClassNames) {
			currClass = jarClassLoader.loadClass(className);
			Class[] interfaces = currClass.getInterfaces();
			for (Class cInterface : interfaces) {
				if ("CommonInterfaces.StringConvertable".equals(cInterface.getName())) {
					isNeededInterface = true;
					neededInterface = cInterface;
					break;
				}
			} // for
			if (isNeededInterface)
				break;
		} // for

		if (!isNeededInterface) {
			p("В файле "
					+ args[1]
					+ " не найдено ни одного класса, реализующего интерфейс CommonInterfaces.StringConvertable.");
			return;
		}

		Object loadedModule = currClass.newInstance();
		StringConvertable loadedModuleInterface = (StringConvertable) loadedModule;

		FileLoader loader = new FileLoader(args[0]);
		p("Вывод файла " + args[0]);
		while (loader.hasNext()) {
			String str = loader.next();
			p("'" + str + "' convert to '" + loadedModuleInterface.convert(str)
					+ "'");
		}
		loader.close();
	} // main
	

	private static final void p(String s) {
		System.out.println(s);
	}


	private static void printHelpStr() {
		p("Использование: ");
		p("ReflectionEngine <Файл_с_текстом> <jar-файл> ");
	}

	private static final boolean isArgsValid(String[] args) {

		boolean res = false;
		switch (args.length) {
		case 0:
			printHelpStr();
			break;
		case 1:
			if (args[0].equalsIgnoreCase("-help")
					|| args[0].equalsIgnoreCase("/?"))
				printHelpStr();
			else {
				p("Неправильный формат команды.");
				printHelpStr();
			}
			break;
		case 2:
			File f1 = new File(args[0]);
			File f2 = new File(args[1]);
			if (!f1.exists())
				p("Файл " + args[0] + " не существует.");
			else if (!f1.canRead())
				p("Файл " + args[0] + " не может быть прочитан.");
			else if (f1.length() == 0)
				p("Файл " + args[0] + " пустой.");

			if (!f2.exists())
				p("Файл " + args[1] + " не существует.");

			else if (!f2.canRead())
				p("Файл " + args[1] + " не может быть прочитан.");
			else if (f2.length() == 0)
				p("Файл " + args[1] + " пустой.");

			res = f1.exists() && f1.canRead() && f1.length() > 0 && f2.exists()
					&& f2.canWrite() && f2.length() > 0;
			break;
		default:
			p("Неправильный формат команды.");
			printHelpStr();
			break;
		}
		return res;
	}

}
