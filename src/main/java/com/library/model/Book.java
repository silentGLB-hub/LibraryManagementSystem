package com.library.model;

public class Book {

    private int id;
    private String code;
    private String title;
    private String category;
    private String author;
    private String publisher;
    private int quantity;
    private int available;
    private String coverImage;

    public Book() {
    }

    public Book(int id, String code, String title, String category, String author, String publisher,
                int quantity, int available, String coverImage) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.category = category;
        this.author = author;
        this.publisher = publisher;
        this.quantity = quantity;
        this.available = available;
        this.coverImage = coverImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public boolean isCoverImageExternal() {
        if (coverImage == null) {
            return false;
        }
        String value = coverImage.trim().toLowerCase();
        return value.startsWith("http://")
                || value.startsWith("https://")
                || value.startsWith("data:");
    }
}
