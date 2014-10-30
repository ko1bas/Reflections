package FirstModule;

import CommonClasses.StringSplitter;
import CommonInterfaces.StringConvertable;

public class FirstModule implements StringConvertable {

	@Override
	public String convert(String input) {

		StringSplitter splitter = new StringSplitter(input);
		StringBuffer buf = new StringBuffer(input.length());
		boolean flag = true;
		while (splitter.hasMoreStrings()) {
			String str = splitter.getNextString();
			if (splitter.isDelemiters(str)) {
				buf.append(str);
			} else {
				if (flag) {
					buf.append(str);
				}
				flag = !flag;
			}
		}
		return buf.toString();
	}
}
