/**
 * 
 */
package JarLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ��������� ����� �� ��������� jar-������. ������ ������ ��������� � ���������
 * ������.
 */

public class JarLoader extends ClassLoader {

	private HashMap<String, Class<?>> cache = new HashMap<String, Class<?>>();
	private String jarFileName;
	private String packageName;
	private boolean seeHints;
	private static String WARNING = "Warning : No jar file found. Packet unmarshalling won't be possible. Please verify your classpath";

	/**
	 * ��������� ��� ������ �� jar � �������� � map.
	 *
	 */
	public JarLoader(String jarFileName, String packageName, boolean seeHints) {

		this.jarFileName = jarFileName;
		this.packageName = packageName;
		this.seeHints = seeHints;
		cacheClasses();
	}

	/**
	 * ��������� ��� ������ �� jar � �������� � map
	 *
	 */
	private void cacheClasses() {
		try {

			JarFile jarFile = new JarFile(jarFileName);
			Enumeration entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) entries.nextElement();

				if (match(normalize(jarEntry.getName()), packageName)) {
					byte[] classData = loadClassData(jarFile, jarEntry);
					if (classData != null) {
						Class<?> clazz = defineClass(
								stripClassName(normalize(jarEntry.getName())),
								classData, 0, classData.length);
						cache.put(clazz.getName(), clazz);
						if (seeHints)
							System.out.println("== class " + clazz.getName()
									+ " loaded in cache");
					}
				}
			}
		}

		catch (IOException IOE) {
			// ������ ������� ��������� �� ������
			System.out.println(WARNING);
		}
	}

	public Set<String> getCachedClasses() {
		return cache.keySet();
	}

	/**
	 * 
	 * ��������� �������� ������
	 * 
	 *
	 */
	public synchronized Class<?> loadClass(String name)
			throws ClassNotFoundException {
		Class<?> result = cache.get(name);
		// �������� ����� ���������� �� �� ������� ����� - ������� ��� ������
		if (result == null)
			result = cache.get(packageName + "." + name);

		// ���� ������ ��� � ���� �� �������� �� ���������
		if (result == null)
			result = super.findSystemClass(name);
		if (seeHints)
			System.out.println("== loadClass(" + name + ")");
		return result;
	}

	/**
	 * 
	 * �������� ������������ ��� ������
	 * 
	 * @param className
	 * 
	 * @return
	 */
	private String stripClassName(String className) {
		return className.substring(0, className.length() - 6);

	}

	/**
	 * 
	 * ����������� ��� � �������� ������� � ��� ������ (�������� ����� �� �����)
	 * 
	 * @param className
	 * 
	 * @return
	 */

	private String normalize(String className) {
		return className.replace('/', '.');
	}

	/**
	 * 
	 * ��������� ������ - �������� ����������� �� ����� ��������� ������ � �����
	 * �� �� ���������� .class
	 * 
	 * @param className
	 * 
	 * @param packageName
	 * 
	 * @return
	 */

	private boolean match(String className, String packageName) {
		return className.startsWith(packageName)
				&& className.endsWith(".class");
	}

	/**
	 * 
	 * ��������� ���� �� ��������� JarEntry
	 * 
	 *
	 * 
	 * @param jarFile
	 *            - ���� jar-������ �� �������� ����������� ������ ����
	 * 
	 * @param jarEntry
	 *            - jar-�������� ������� �����������
	 * 
	 * @return null ���� ���������� �������� ����
	 */

	private byte[] loadClassData(JarFile jarFile, JarEntry jarEntry)
			throws IOException {
		long size = jarEntry.getSize();
		if (size == -1 || size == 0)
			return null;

		byte[] data = new byte[(int) size];
		InputStream in = jarFile.getInputStream(jarEntry);
		in.read(data);
		return data;
	}

}