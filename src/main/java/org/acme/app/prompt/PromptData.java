package org.acme.app.prompt;

public class PromptData {
    public String name;
    public String value;
    public String imageUrl;

    public PromptData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public PromptData(String name, String value, String imageUrl) {
        this.name = name;
        this.value = value;
        this.imageUrl = imageUrl;
    }
}
