
package FileLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;


public class FileLoader implements Iterator<String> {

	BufferedReader reader;

	public FileLoader(String fname) throws IOException, FileNotFoundException {
		reader = new BufferedReader(new FileReader(new File(fname)));
	}

	@Override
	public boolean hasNext() {
		try {
			return reader.ready();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String next() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	public void close() throws IOException {
		reader.close();
	}

}
