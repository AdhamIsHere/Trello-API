package DataModels;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	@NotNull
	private String name;
	@NotNull
	private String email;
	@NotNull
	private String password;
	

	@OneToMany(mappedBy = "owner",fetch=FetchType.LAZY)
	private Set<Board> OwnedBoards;
	
	@ManyToMany(mappedBy = "collaborators" ,fetch=FetchType.LAZY)
	private Set<Board> CollaboratedBoards;
	
	public User() {
		
	}

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
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
		return OwnedBoards;
	}
	
	public void setOwnedBoards(Set<Board> ownedBoards) {
		OwnedBoards = ownedBoards;
	}
	
	public Set<Board> getCollaboratedBoards() {
		return CollaboratedBoards;
	}
	
	public void setCollaboratedBoards(Set<Board> collaboratedBoards) {
		CollaboratedBoards = collaboratedBoards;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + "]";
	}
}
