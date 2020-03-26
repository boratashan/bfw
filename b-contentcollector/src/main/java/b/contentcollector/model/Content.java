package b.contentcollector.model;

import b.contentcollector.adapters.EncodeDecodeType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;


public class Content{
	private boolean processed;
	private int size;
	private String pcode;
	private String targeturi;
	private String project;
	private String id;
	private String target;
	private EncodeDecodeType encoding;
	private LocalDateTime processtime;
	private LocalDateTime downloadtime;
	private String content;

	public boolean isProcessed() {
		return processed;
	}

	public Content setProcessed(boolean processed) {
		this.processed = processed;
		return this;
	}

	public int getSize() {
		return size;
	}

	public Content setSize(int size) {
		this.size = size;
		return this;
	}

	public String getPcode() {
		return pcode;
	}

	public Content setPcode(String pcode) {
		this.pcode = pcode;
		return this;
	}

	public String getTargeturi() {
		return targeturi;
	}

	public Content setTargeturi(String targeturi) {
		this.targeturi = targeturi;
		return this;
	}

	public String getProject() {
		return project;
	}

	public Content setProject(String project) {
		this.project = project;
		return this;
	}

	public String getId() {
		return id;
	}

	public Content setId(String id) {
		this.id = id;
		return this;
	}

	public String getTarget() {
		return target;
	}

	public Content setTarget(String target) {
		this.target = target;
		return this;
	}

	public EncodeDecodeType getEncoding() {
		return encoding;
	}

	public Content setEncoding(EncodeDecodeType encoding) {
		this.encoding = encoding;
		return this;
	}

	public LocalDateTime getProcesstime() {
		return processtime;
	}

	public Content setProcesstime(LocalDateTime processtime) {
		this.processtime = processtime;
		return this;
	}

	public LocalDateTime getDownloadtime() {
		return downloadtime;
	}

	public Content setDownloadtime(LocalDateTime downloadtime) {
		this.downloadtime = downloadtime;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Content setContent(String content) {
		this.content = content;
		return this;
	}

	@Override
 	public String toString(){
		return 
			"Content{" + 
			"processed = '" + processed + '\'' + 
			",size = '" + size + '\'' + 
			",pcode = '" + pcode + '\'' + 
			",targeturi = '" + targeturi + '\'' + 
			",project = '" + project + '\'' + 
			",_id = '" + id + '\'' + 
			",encoding = '" + encoding + '\'' + 
			",processtime = '" + processtime + '\'' + 
			",downloadtime = '" + downloadtime + '\'' + 
			",content = '" + content + '\'' + 
			",target = '" + target + '\'' +
			"}";
		}



}