package css.servlet.processor;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Processor {

	HttpServletRequest req = null;
	HttpServletResponse resp = null;
	OutputStream out = null;

	public void process(HttpServletRequest req, HttpServletResponse resp,
			OutputStream out) throws Exception {
		this.req = req;
		this.resp = resp;
		this.out = out;
		process();
	}

	public abstract void process() throws Exception;
}
