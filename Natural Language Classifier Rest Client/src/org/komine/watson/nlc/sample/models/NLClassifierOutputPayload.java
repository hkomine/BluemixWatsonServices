package org.komine.watson.nlc.sample.models;

public class NLClassifierOutputPayload {
    public String classifier_id;
    public String url;
    public String text;
    public String top_class;
    public NLClassifiedClass[] classes;

    // field for error case
    public String description;
    public String error;
    public int code;

    public String toString() {
        StringBuffer buf = new StringBuffer("NLClassifierOutputPayload { \n");
        buf.append("classifier_id : ").append(classifier_id).append(", \n");
        buf.append("url : ").append(url).append(", \n").append("text : ").append(text).append(", \n");
        buf.append("top_class : ").append(top_class).append(", \n");
        buf.append("classes : [ \n");
        for (int i = 0; i < classes.length; i++) {
            buf.append("\t").append(classes[i].toString()).append("\n");
        }
        buf.append("]").append("}\n");
        return buf.toString();
    }

}
