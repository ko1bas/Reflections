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

		boolean seeHints = false; //��� ��������, ��� ��� �����������.

		JarLoader jarClassLoader = new JarLoader(args[1], "", seeHints);
		// ������ "" ��� �� ���� �����-������ ����� "com.google.gson.Gson"
		// ����� ����������� �� ������ ������ �� ����.

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
			p("� ����� "
					+ args[1]
					+ " �� ������� �� ������ ������, ������������ ��������� CommonInterfaces.StringConvertable.");
			return;
		}

		Object loadedModule = currClass.newInstance();
		StringConvertable loadedModuleInterface = (StringConvertable) loadedModule;

		FileLoader loader = new FileLoader(args[0]);
		p("����� ����� " + args[0]);
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
		p("�������������: ");
		p("ReflectionEngine <����_�_�������> <jar-����> ");
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
				p("������������ ������ �������.");
				printHelpStr();
			}
			break;
		case 2:
			File f1 = new File(args[0]);
			File f2 = new File(args[1]);
			if (!f1.exists())
				p("���� " + args[0] + " �� ����������.");
			else if (!f1.canRead())
				p("���� " + args[0] + " �� ����� ���� ��������.");
			else if (f1.length() == 0)
				p("���� " + args[0] + " ������.");

			if (!f2.exists())
				p("���� " + args[1] + " �� ����������.");

			else if (!f2.canRead())
				p("���� " + args[1] + " �� ����� ���� ��������.");
			else if (f2.length() == 0)
				p("���� " + args[1] + " ������.");

			res = f1.exists() && f1.canRead() && f1.length() > 0 && f2.exists()
					&& f2.canWrite() && f2.length() > 0;
			break;
		default:
			p("������������ ������ �������.");
			printHelpStr();
			break;
		}
		return res;
	}

}
