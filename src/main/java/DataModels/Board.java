package DataModels;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Board", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@NotNull
	private String name;

	// owner of the board (user)
	@NotNull
	@ManyToOne
	@JoinColumn(name = "owner_id")
	
	private User owner;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name ="UserXBoard", 
	joinColumns = @JoinColumn(name = "board_id"), 
	inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> collaborators;
	
	public Board() {

	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "Board [id=" + id + ", name=" + name + "]";
	}

}
