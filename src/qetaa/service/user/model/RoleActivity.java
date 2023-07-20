package qetaa.service.user.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="usr_role_activity")
@IdClass(RoleActivity.RoleActivityPK.class)
public class RoleActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JoinColumn(name="role_id", referencedColumnName="id")
	@ManyToOne
	private Role role;
	
	@Id
	@JoinColumn(name="activity_id", referencedColumnName="id")
	@ManyToOne
	private Activity activity;
	
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		RoleActivity other = (RoleActivity) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}




	public static class RoleActivityPK implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int role;
		protected int activity;
		public RoleActivityPK() {}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + role;
			result = prime * result + activity;
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
			RoleActivityPK other = (RoleActivityPK) obj;
			if (role != other.role)
				return false;
			if (activity != other.activity)
				return false;
			return true;
		}
		
		
		
	}
	
}
