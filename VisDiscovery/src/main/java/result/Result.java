
package result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nada
 */
@XmlRootElement
public class Result implements Serializable {
    Data data;
	Config config;
    Transform transform;
    String mark;
    Encoding encoding;
    Integer width;
    
    public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public Transform getTransform() {
		return transform;
	}
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public Encoding getEncoding() {
		return encoding;
	}
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getWidth() {
		return this.width;
	}
}