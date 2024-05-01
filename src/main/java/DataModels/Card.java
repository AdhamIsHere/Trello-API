package DataModels;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Card implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cardList_id")
	CardList cardList;
	
	@OneToMany(mappedBy = "card",fetch = FetchType.LAZY)
	Set<Comment> comments;
	
	@ManyToMany(mappedBy = "assignedCards" ,fetch = FetchType.LAZY)
	Set<User> assignedUsers;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public void setAssignedUsers(Set<User> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	public Set<User> getAssignedUsers() {
		return assignedUsers;
	}

}
