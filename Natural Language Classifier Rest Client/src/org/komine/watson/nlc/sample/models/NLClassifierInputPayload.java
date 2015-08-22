package org.komine.watson.nlc.sample.models;

public class NLClassifierInputPayload {
    public String text;

    public NLClassifierInputPayload(String text) {
        this.text = text;
    }

    public String toString() {
        return "NLClassifierInputPayload { text = " + text + "}";
    }
}
