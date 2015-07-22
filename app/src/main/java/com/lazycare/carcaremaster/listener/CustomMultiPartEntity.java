package com.lazycare.carcaremaster.listener;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

/**
 * MultipartEntity   重写MultipartEntity类，发送完一定数量的字节以后通知ProgressListener
 * 
 * @author GMY 
 */
public class CustomMultiPartEntity extends MultipartEntity {
	public static final String tag = "CustomMultiPartEntity&quot";

	private final ProgressListener listener;

	public CustomMultiPartEntity(final ProgressListener listener) {
		super();
		this.listener = listener;
	}

	public CustomMultiPartEntity(final HttpMultipartMode mode,
			final ProgressListener listener) {
		super(mode);
		this.listener = listener;
	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary,
			final Charset charset, final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public static interface ProgressListener {
		/**
		 * 通知已经上传的字节数
		 * 
		 * @param num
		 */
		void transferred(long num);

		/**
		 * 通知计算出来的总的需要上传的字节数(由于Http头没有统计,有一些误差)
		 * 
		 * @param num
		 */
		void total(long num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
}