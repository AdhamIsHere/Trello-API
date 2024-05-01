package DataModels;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class CardList implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;
	
	@OneToMany(mappedBy = "cardList" ,fetch = FetchType.LAZY)
    private Set<Card> card;
    
	public CardList() {

	}
	
	public CardList(String name, Set<Card> card) {
		this.name = name;
		this.card = card;
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
	
	public Set<Card> getCard() {
		return card;
	}
	
	public void setCard(Set<Card> card) {
		this.card = card;
	}
	
	@Override
	public String toString() {
		return "CardList [id=" + id + ", name=" + name + ", card=" + card + "]";
	}
	

}
