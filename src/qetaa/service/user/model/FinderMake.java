package qetaa.service.user.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="usr_finder_make")
@IdClass(FinderMake.FinderMakePK.class)
public class FinderMake implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@JoinColumn(name="user_id", updatable=false, insertable=false)
	@ManyToOne(cascade= {CascadeType.REMOVE})
	private User user;
	@Id
	@Column(name="make_id")
	private int makeId;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + makeId;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinderMake other = (FinderMake) obj;
		if (makeId != other.makeId)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}




	public static class FinderMakePK implements Serializable{
		private static final long serialVersionUID = 1L;
		protected int user;
		protected int makeId;
		public FinderMakePK() {}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + user;
			result = prime * result + makeId;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FinderMakePK other = (FinderMakePK) obj;
			if (user != other.user)
				return false;
			if (makeId != other.makeId)
				return false;
			return true;
		}
		
		
		
	}
	
}
