package com.nibss.cmms.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.castor.util.Base64Decoder;
import org.springframework.web.multipart.MultipartFile;

import com.nibss.cmms.web.domain.FileAttachment;

public class BASE64DecodedMultipartFile implements MultipartFile {


	private final FileAttachment fileAttachment;

	private final String fileName="tmp_"+System.currentTimeMillis();

	private Map<String, String> mimeTypes() {
		Map<String, String> mimeTypes;

		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("swf", "application/x-shockwave-flash");
		return mimeTypes;
	} 

	public  BASE64DecodedMultipartFile(FileAttachment fileAttachment) {
		this.fileAttachment= fileAttachment;
	}

	@Override
	public byte[] getBytes() throws IOException {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return fileAttachment.getFileBase64EncodedString().getBytes();
		return null;
	}

	@Override
	public String getContentType() {
		return mimeTypes().get(fileAttachment.getFileExtension());
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return new ByteArrayInputStream(fileAttachment.getFileBase64EncodedString().getBytes());
		return null;
	}

	@Override
	public String getName() {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return fileName+"."+fileAttachment.getFileExtension();
		return null;
	}

	@Override
	public String getOriginalFilename() {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return fileName+"."+fileAttachment.getFileExtension();
		return null;
	}

	@Override
	public long getSize() {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return fileAttachment.getFileBase64EncodedString().getBytes().length;
		return 0;
	}

	@Override
	public boolean isEmpty() {
		if (fileAttachment!=null && fileAttachment.getFileBase64EncodedString()!=null)
			return false;
		return true;
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {

		try(FileOutputStream fileOutputStream=new FileOutputStream(dest)){
			fileOutputStream.write(Base64Decoder.decode(fileAttachment.getFileBase64EncodedString()));
			fileOutputStream.close();
		}

	}

}
