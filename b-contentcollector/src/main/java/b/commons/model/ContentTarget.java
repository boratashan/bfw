package b.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.processing.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ContentTarget{

	@JsonProperty("pcode")
	private String pcode;

	@JsonProperty("targeturi")
	private String targeturi;

	@JsonProperty("project")
	private String project;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("enabled")
	private boolean enabled;

	@JsonProperty("target")
	private String target;

	public void setPcode(String pcode){
		this.pcode = pcode;
	}

	public String getPcode(){
		return pcode;
	}

	public void setTargeturi(String targeturi){
		this.targeturi = targeturi;
	}

	public String getTargeturi(){
		return targeturi;
	}

	public void setProject(String project){
		this.project = project;
	}

	public String getProject(){
		return project;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public boolean isEnabled(){
		return enabled;
	}

	public void setTarget(String target){
		this.target = target;
	}

	public String getTarget(){
		return target;
	}

	@Override
 	public String toString(){
		return 
			"ContentTarget{" + 
			"pcode = '" + pcode + '\'' + 
			",targeturi = '" + targeturi + '\'' + 
			",project = '" + project + '\'' + 
			",_id = '" + id + '\'' + 
			",enabled = '" + enabled + '\'' + 
			",target = '" + target + '\'' + 
			"}";
		}
}