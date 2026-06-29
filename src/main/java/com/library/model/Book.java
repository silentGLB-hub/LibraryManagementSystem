package com.library.model;

public class Book {

    private int id;
    private String code;
    private String title;
    private String category;
    private String author;
    private String publisher;
    private String categoryUrl;
    private String authorUrl;
    private String publisherUrl;
    private int quantity;
    private int available;
    private String coverImage;
    private String previewText;
    private String contentText;
    private String chapters;
    private String pdfFile;

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getCategoryUrl() { return categoryUrl; }
    public void setCategoryUrl(String categoryUrl) { this.categoryUrl = categoryUrl; }
    public String getAuthorUrl() { return authorUrl; }
    public void setAuthorUrl(String authorUrl) { this.authorUrl = authorUrl; }
    public String getPublisherUrl() { return publisherUrl; }
    public void setPublisherUrl(String publisherUrl) { this.publisherUrl = publisherUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getPreviewText() { return previewText; }
    public void setPreviewText(String previewText) { this.previewText = previewText; }
    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public String getChapters() { return chapters; }
    public void setChapters(String chapters) { this.chapters = chapters; }
    public String getPdfFile() { return pdfFile; }
    public void setPdfFile(String pdfFile) { this.pdfFile = pdfFile; }

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