package SecondModule;

import CommonClasses.StringSplitter;
import CommonInterfaces.StringConvertable;

public class SecondModule implements StringConvertable {

	@Override
	public String convert(String input) {
		StringSplitter splitter = new StringSplitter(input);
		StringBuffer buf = new StringBuffer(input.length());
		boolean flag = false;
		while (splitter.hasMoreStrings()) {
			String str = splitter.getNextString();
			if (splitter.isDelemiters(str)) {
				if (" ".equals(str)) {
					if (!flag)
						buf.append(str);
					flag = true;
				} else {
					buf.append(str);
					flag = false;
				}
			} else {
				buf.append(str);
				flag = false;
			}
		}
		return buf.toString();
	}

}
