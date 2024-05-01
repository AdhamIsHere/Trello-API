package DTOs;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import DataModels.Board;
import DataModels.Card;
import DataModels.Comment;
import DataModels.User;

public class UserDTO implements Serializable{
	
	private Long userId;
	
	private String name;

	private String email;

	private String password;
	
	private Set<Board> ownedBoards;
	
	private Set<Board> collaboratedBoards;
	
	private Set<Comment> comments;
	
	private Set<Card> assignedCards;

	public UserDTO(User user) {
		this.userId = user.getUserId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.ownedBoards = user.getOwnedBoards();
		this.collaboratedBoards = user.getCollaboratedBoards();
		this.comments = user.getComments();
		this.assignedCards = user.getAssignedCards();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
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

	@Override
	public String toString() {
		return "UserDTO [userId=" + userId + ", name=" + name + ", email=" + email + ", password=" + password
				+ ", ownedBoards=" + ownedBoards + ", collaboratedBoards=" + collaboratedBoards + ", comments="
				+ comments + ", assignedCards=" + assignedCards + "]";
	}
	

}
