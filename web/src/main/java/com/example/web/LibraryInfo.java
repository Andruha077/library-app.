package com.example.web;

public class LibraryInfo {

    private final String name;
    private final String version;

    public LibraryInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}