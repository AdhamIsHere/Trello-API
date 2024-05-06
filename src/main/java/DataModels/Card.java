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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import Serializers.CustomUserSerializer;


@Entity
public class Card implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String title;
	String description;
	String status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cardList_id")
	@JsonBackReference
	CardList cardList;

	@OneToMany(mappedBy = "card", fetch = FetchType.EAGER)
	@JsonSerialize(contentUsing = Serializers.CommentsSerializer.class)
	Set<Comment> comments = new HashSet<Comment>();

	@ManyToMany(mappedBy = "assignedCards", fetch = FetchType.EAGER)
	@JsonSerialize(contentUsing = CustomUserSerializer.class)
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
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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

	public CardList getCardList() {
		return cardList;
	}

	public void setCardList(CardList cardList) {
		this.cardList = cardList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
