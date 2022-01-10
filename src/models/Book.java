package models;

public class Book {
    private int ISBN;
    private String title;
    private String[] authors;
    private String publisher;
    private int publicationYear;
    private double sellingPrice;
    private String category;
    private int threshold;
    private int noOfCopies;

    

    public Book(int iSBN, String title, String[] authors, String publisher, int publicationYear, double sellingPrice,
            String category, int threshold) {
        ISBN = iSBN;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.sellingPrice = sellingPrice;
        this.category = category;
        this.threshold = threshold;
    }

    public Book(int iSBN, String title, String publisher, int publicationYear, double sellingPrice,
        String category, int noOfCopies) {
        ISBN = iSBN;
        this.title = title;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.sellingPrice = sellingPrice;
        this.category = category;
        this.noOfCopies = noOfCopies;
    }

    public int getISBN() {
        return ISBN;
    }
    public void setISBN(int iSBN) {
        ISBN = iSBN;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String[] getAuthors() {
        return authors;
    }
    public void setAuthors(String[] authors) {
        this.authors = authors;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public int getPublicationYear() {
        return publicationYear;
    }
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getThreshold() {
        return threshold;
    }
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

}