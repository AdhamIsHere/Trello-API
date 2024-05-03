package DataModels;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Board implements Serializable{


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long boardId;

	@NotNull
	private String name;

	// owner of the board (user)
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	@JsonBackReference
	private User owner;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "UserXBoard", 
	joinColumns = @JoinColumn(name = "boardId"),
	inverseJoinColumns = @JoinColumn(name = "userId"))
	private Set<User> collaborators;

	@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
	private Set<CardList> cardLists;

	public Board() {

	}

	public Long getBoardId() {
		return boardId;
	}
	
	public void setBoardId(Long id) {
		boardId = id;
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Set<User> collaborators) {
		this.collaborators = collaborators;
	}

	public Set<CardList> getCardLists() {
		return cardLists;
	}

	public void setCardLists(Set<CardList> cardLists) {
		this.cardLists = cardLists;
	}

	@Override
	public String toString() {
		return "Board [ Id= "+ boardId+",name= " + name + "]";
	}

	public void addCardList(String name) {
		CardList cardList = new CardList();
		cardList.setName(name);
		cardLists.add(cardList);

	}

}
