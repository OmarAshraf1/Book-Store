package HelperClasses;

public class ViewCart {
	
    
    private String ISBN ;
	private String title;
	private String category ;
	private String publisher;
	private String year;
	private String copies ;
	private String price ;
	private String totalPrice ;
	
	public ViewCart(String iSBN, String title, String category, String publisher, String year, String copies, String price,
			String totalPrice) {
		
		this.ISBN = iSBN;
		this.title = title;
		this.category = category;
		this.publisher = publisher;
		this.year = year;
		this.copies = copies;
		this.price = price;
		this.totalPrice = totalPrice;
	}
	public ViewCart() {
		// TODO Auto-generated constructor stub
	}
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
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
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getCopies() {
		return copies;
	}
	public void setCopies(String copies) {
		this.copies = copies;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
}
