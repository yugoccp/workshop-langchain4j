package org.acme.app;

public class PromptItem {
    public String name;
    public String value;
    public String imageUrl;

    public PromptItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public PromptItem(String name, String value, String imageUrl) {
        this.name = name;
        this.value = value;
        this.imageUrl = imageUrl;
    }
}
