package DataModels;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class User implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	@NotNull
	private String name;
	@NotNull
	private String email;
	@NotNull
	private String password;
	
	@OneToMany(mappedBy = "owner",fetch=FetchType.EAGER)
	@JsonManagedReference
	private Set<Board> ownedBoards;
	
	@ManyToMany(mappedBy = "collaborators" ,fetch=FetchType.EAGER)
	private Set<Board> collaboratedBoards;
	
	
	@OneToMany(mappedBy = "author",fetch=FetchType.EAGER)
	private Set<Comment> comments;
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name ="UserXCard", 
	joinColumns = @JoinColumn(name = "card_id"), 
	inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<Card> assignedCards;
	

	
	public User() {
		
	}

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long id) {
		this.userId = id;
	}
	
	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Board> getOwnedBoards() {
		return ownedBoards;
	}
	
	public void setOwnedBoards(Set<Board> ownedBoards) {
		this.ownedBoards = ownedBoards;
	}

	public Set<Board> getCollaboratedBoards() {
		return collaboratedBoards;
	}
	
	public void setCollaboratedBoards(Set<Board> collaboratedBoards) {
		this.collaboratedBoards = collaboratedBoards;
	}
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
	
	public Set<Card> getAssignedCards() {
		return assignedCards;
	}
	
	public void setAssignedCards(Set<Card> assignedCards) {
		this.assignedCards = assignedCards;
	}
	
	public Board getBoard(String boardName) {
		for (Board board : ownedBoards) {
			if (board.getName().equals(boardName)) {
				return board;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "User [id=" + userId + ", name=" + name + ", email=" + email + ", password=" + password + "]";
	}
}
