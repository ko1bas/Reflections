package Main;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import CommonInterfaces.StringConvertable;
import FileLoader.FileLoader;
import JarLoader.JarLoader;
import java.util.ResourceBundle;

public class Main {
	
	private static ResourceBundle resource; 
	
	public static void main(String[] args) throws ClassNotFoundException,
			FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException {

		
		resource = ResourceBundle.getBundle("data_en_EN");
			
		if (!isArgsValid(args))
			return;

		boolean seeHints = false; 

		JarLoader jarClassLoader = new JarLoader(args[1], "", seeHints);

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
			p(resource.getString("FileNotContainInterface").replace("<file>", args[0]).replace("<interface>", "CommonInterfaces.StringConvertable"));
			return;
		}

		Object loadedModule = currClass.newInstance();
		StringConvertable loadedModuleInterface = (StringConvertable) loadedModule;

		FileLoader loader = new FileLoader(args[0]);
		p(resource.getString("PrintFile").replace("<file", args[0]));
		while (loader.hasNext()) {
			String str = loader.next();
			p(resource.getString("ConvertString").replace("<Str1>", str).replace("<Str2>", loadedModuleInterface.convert(str)));
		}
		loader.close();
	} // main
	

	
	private static final void p(String s) {
		System.out.println(s);
	}
	

	private static void printHelpStr() {
		
		p(resource.getString("HelpStr"));
		p(resource.getString("HelpStrDescription"));
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
				p(resource.getString("ErrCommandFailure"));
				printHelpStr();
			}
			break;
		case 2:
			File f1 = new File(args[0]);
			File f2 = new File(args[1]);
			if (!f1.exists()){
				p(resource.getString("FileIsNotExsist").replace("<file>", args[0]));
			}
			else if (!f1.canRead())
				p(resource.getString("FileIsNotCanRead").replace("<file>", args[0]));
			else if (f1.length() == 0)
				p(resource.getString("FileIsEmpty").replace("<file>", args[0]));

			if (!f2.exists())
				p(resource.getString("FileIsNotExsist").replace("<file>", args[1]));

			else if (!f2.canRead())
				p(resource.getString("FileIsNotCanRead").replace("<file>", args[1]));
			else if (f2.length() == 0)
				p(resource.getString("FileIsNotExsist").replace("<file>", args[1]));

			res = f1.exists() && f1.canRead() && f1.length() > 0 && f2.exists()
					&& f2.canWrite() && f2.length() > 0;
			break;
		default:
			p(resource.getString("ErrCommandFailure"));
			printHelpStr();
			break;
		}
		return res;
	}

}
