package org.komine.watson.nlc.sample.models;

public class NLClassifiedClass {
    public float confidence;
	public String class_name;
	
	public String toString() {
		return "NLClassifiedClass : { class_name : " + class_name + ", confidence : " + confidence + "}";
	}
}
