package ar.com.juliospa.edu.textmining.ner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import opennlp.tools.util.InputStreamFactory;

public class StringInputStreamFactory implements InputStreamFactory {
	private String string;
	private Charset charset;



	public StringInputStreamFactory(String string,Charset charset) {
		this.string = string;
		this.charset = charset;
	}

	public InputStream createInputStream() throws IOException {
		return new ByteArrayInputStream(string.getBytes(charset));
	}
}
