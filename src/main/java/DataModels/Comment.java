package DataModels;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Comment implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String content;
	
	@ManyToOne
	@JoinColumn(name = "author_id")
	User author;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "card_id")
	Card card;
	public Comment() {

	}
	
	public Comment(String content, User author) {
		this.content = content;
		this.author = author;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", content=" + content + ", author=" + author + "]";
	}
	
	
}
