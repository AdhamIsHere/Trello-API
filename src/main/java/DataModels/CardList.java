package DataModels;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class CardList implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "board_id")
	@JsonBackReference
	private Board board = new Board();
	

	@OneToMany(mappedBy = "cardList" ,fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
	@JsonManagedReference
    private Set<Card> cards = new HashSet<Card>();
    
	public CardList() {

	}
	
	public CardList(String name, Set<Card> card) {
		this.name = name;
		this.cards = card;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Card> getCards() {
		return cards;
	}
	
	public void setCards(Set<Card> card) {
		this.cards = card;
	}
	
	@Override
	public String toString() {
		return "CardList [id=" + id + ", name=" + name + ", card=" + cards + "]";
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}

}
