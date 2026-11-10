
package com.example.core.model;

public class Book {

    private Long id;
    private String title;
    private String author;
    private String description;
    private Integer year;

    public Book() {
    }

    // Старий конструктор для сумісності з попередніми лабораторними
    public Book(Long id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
    }

    // Новий конструктор із роком видання
    public Book(Long id, String title, String author,
                String description, Integer year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public Integer getYear() {
        return year;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
