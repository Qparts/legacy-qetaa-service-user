package qetaa.service.user.restful;

import java.util.*;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.user.dao.DAO;
import qetaa.service.user.filters.Secured;
import qetaa.service.user.filters.SecuredUser;
import qetaa.service.user.filters.ValidApp;
import qetaa.service.user.helpers.Helper;
import qetaa.service.user.model.Activity;
import qetaa.service.user.model.FinderMake;
import qetaa.service.user.model.Role;
import qetaa.service.user.model.RoleActivity;
import qetaa.service.user.model.User;
import qetaa.service.user.model.UserHolder;
import qetaa.service.user.model.UserRole;
import qetaa.service.user.model.UserWithPassword;
import qetaa.service.user.model.security.AccessMap;
import qetaa.service.user.model.security.AccessToken;
import qetaa.service.user.model.security.WebApp;

@Path("/")
public class UserService {
	@EJB
	private DAO dao;

	@ValidApp
	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test(){
		return Response.status(200).entity("Received!").build();
	}

	@SecuredUser
	@GET
	@Path("finder-ids/make/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFinderIds(@PathParam(value = "param") int makeId) {
		try {
			String jpql = "select b.id from User b where b.id in ("
					+ "select c.user.id from FinderMake c where c.makeId = :value0)";
			List<Integer> ids = dao.getJPQLParams(Integer.class, jpql, makeId);
			return Response.status(200).entity(ids).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("make-ids/finder/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFinderMakes(@PathParam(value = "param") int finderId) {
		try {
			String jpql = "select b.makeId from FinderMake b where b.user.id = :value0";
			List<Integer> makeIds = dao.getJPQLParams(Integer.class, jpql, finderId);
			return Response.status(200).entity(makeIds).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("finder-make")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addFinderMake(Map<String, Integer> map) {
		try {
			Integer makeId = map.get("makeId");
			Integer userId = map.get("userId");
			List<FinderMake> vendorMakes = dao.getTwoConditions(FinderMake.class, "user.id", "makeId", userId, makeId);
			if (!vendorMakes.isEmpty()) {
				return Response.status(409).build();
			}
			String sql = "insert into usr_finder_make (user_id, make_id)" + "values(" + userId + "," + makeId + ")";
			dao.insertNative(sql);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@DELETE
	@Path("finder-make/user/{param}/make/{param2}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeVendorMake(@PathParam(value = "param") int userId, @PathParam(value = "param2") int makeId) {
		try {
			String sql = "delete from user_finder_make where user_id = " + userId + " and make_id = " + makeId;
			dao.updateNative(sql);
			// log the delete operation
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/user/has-access-to/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserWhoHasAccessTo(@PathParam(value = "param") int activityId) {
		try {
			String sql = "select * from usr_user where id in ("
					+ "  select user_id from usr_user_role where role_id in ("
					+ "    select role_id from usr_role_activity where activity_id = " + activityId + "))";
			List<User> users = dao.getNative(User.class, sql);
			return Response.status(200).entity(users).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("active-advisor-ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveAdvisors() {
		try {
			int activityId = 8;
			String sql = "select * from usr_user where id in ("
					+ "  select user_id from usr_user_role where role_id in ("
					+ "    select role_id from usr_role_activity where activity_id = " + activityId + "))";
			List<User> users = dao.getNative(User.class, sql);
			List<Integer> list = new ArrayList<>();
			for (User u : users) {
				list.add(u.getId());
			}
			return Response.status(200).entity(list).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("all-roles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRoles() {
		try {
			List<Role> roles = dao.get(Role.class);
			for (Role role : roles) {
				role.setActivityList(this.getRoleActivites(role));
			}
			return Response.status(200).entity(roles).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-roles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveRoles() {
		try {
			List<Role> roles = dao.getCondition(Role.class, "status", 'A');
			for (Role role : roles) {
				role.setActivityList(this.getRoleActivites(role));
			}
			return Response.status(200).entity(roles).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/all-users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		try {
			List<User> users = dao.get(User.class);
			for (User user : users) {
				user.setRolesList(getUserRoles(user));
			}
			return Response.status(200).entity(users).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/all-activities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllActivities() {
		try {
			List<Activity> act = dao.get(Activity.class);
			return Response.status(200).entity(act).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("/user/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam(value = "param") int userId) {
		try {
			User user = dao.find(User.class, userId);
			return Response.status(200).entity(user).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent
	@SecuredUser
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewUser(UserWithPassword uwp) {
		try {
			List<User> users = dao.getCondition(User.class, "username", uwp.getUser().getUsername());
			if (!users.isEmpty()) {
				return Response.status(409).build();
			}
			User u = new User();
			u.setFirstName(uwp.getUser().getFirstName());
			u.setLastName(uwp.getUser().getLastName());
			u.setPassword(Helper.cypher(uwp.getPassword()));
			u.setStatus(uwp.getUser().getStatus());
			u.setUsername(uwp.getUser().getUsername());
			u = dao.persistAndReturn(u);

			for (Role r : uwp.getUser().getRolesList()) {
				String sql = "insert into usr_user_role (user_id, role_id) values(" + u.getId() + "," + r.getId() + ")";
				dao.insertNative(sql);
			}
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent
	@SecuredUser
	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRole(Role role) {
		try {
			List<Role> roles = dao.getCondition(Role.class, "name", role.getName());
			if (!roles.isEmpty()) {
				return Response.status(409).build();
			}

			Role r = new Role();
			r.setName(role.getName());
			r.setNameAr(role.getNameAr());
			r.setStatus(role.getStatus());
			r = dao.persistAndReturn(r);
			for (Activity act : role.getActivityList()) {
				if (act.isAccess()) {
					String sql = "insert into usr_role_activity (role_id, activity_id) values" + "(" + r.getId() + ","
							+ act.getId() + ") on conflict do nothing";
					dao.insertNative(sql);
				}
			}
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(User user) {
		try {
			String sql = "update usr_user set " + " first_name = '" + user.getFirstName() + "'" + " , last_name = '"
					+ user.getLastName() + "'" + " , username = '" + user.getUsername() + "'" + " , status = '"
					+ user.getStatus() + "'" + " WHERE id = " + user.getId();
			dao.updateNative(sql);

			String sql2 = "delete from usr_user_role where user_id = " + user.getId();
			dao.updateNative(sql2);
			for (Role role : user.getRolesList()) {
				String sql3 = "insert into usr_user_role (role_id, user_id) values (" + role.getId() + ", "
						+ user.getId() + ")";
				dao.updateNative(sql3);
			}
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@PUT
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRole(Role role) {
		try {
			List<Activity> all = role.getActivityList();
			for (Activity activity : all) {
				if (activity.isAccess()) {
					String sql = "insert into usr_role_activity(role_id, activity_id) values " + "(" + role.getId()
							+ "," + activity.getId() + ") on conflict do nothing";
					dao.insertNative(sql);
				} else {
					String sql = "delete from usr_role_activity where role_id = " + role.getId() + " and activity_id = "
							+ activity.getId();
					dao.updateNative(sql);
				}
			}
			Properties  prop =  new Properties();
			System.out.println("Number of props loaded: " +prop.entrySet().size());

			dao.update(role);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}


	@ValidApp
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(AccessMap map) {
		try {
			// verify web app and get it
			WebApp webApp = getWebAppFromSecret(map.getAppSecret());
			// get password
			String hashed = Helper.cypher(map.getCode());
			User user = dao.findThreeConditions(User.class, "status", "username", "password", 'A', map.getUsername(),
					hashed);
			if (user != null) {
				String token = issueToken(user, webApp, 500);
				List<Role> roles = dao.getConditionClassColumn(UserRole.class, Role.class, "role", "user", user);
				//
				// get list of activities for this user and send it along
				UserHolder holder = new UserHolder();
				holder.setUser(user);
				holder.setRoles(roles);
				holder.setActivities(getUserActivities(user));
				holder.setToken(token);
				return Response.status(200).entity(holder).build();
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			return Response.status(404).build();
		}
	}

	private List<Activity> getUserActivities(User user) {
		String sql = "select * from usr_activity a where a.id in ("
				+ "select ra.activity_id from usr_role_activity ra where ra.role_id in ("
				+ "select ur.role_id from usr_user_role ur where ur.user_id = " + user.getId() + ") ) order by a.id";
		return dao.getNative(Activity.class, sql);
	}

	private List<Role> getUserRoles(User user) {
		String jpql = "select b.role from UserRole b where b.user = :value0";
		List<Role> roles = dao.getJPQLParams(Role.class, jpql, user);
		return roles;
	}

	private List<Activity> getRoleActivites(Role role) {
		List<Activity> allActs = dao.getOrderBy(Activity.class, "name");
		for (Activity a : allActs) {
			RoleActivity roleAct = dao.findTwoConditions(RoleActivity.class, "role", "activity", role, a);
			if (roleAct != null) {
				a.setAccess(true);
			}
		}
		return allActs;
	}

	@SecuredUser
	@POST
	@Path("/match-token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response matchToken(AccessMap usermap) {
		try {
			WebApp webApp = getWebAppFromSecret(usermap.getAppSecret());
			List<AccessToken> l = dao.getFourConditionsAndDateBefore(AccessToken.class, "userId", "webApp.appCode",
					"status", "token", "expire", Integer.parseInt(usermap.getUsername()), webApp.getAppCode(), 'A',
					usermap.getCode(), new Date());
			if (!l.isEmpty()) {
				return Response.status(200).build();
			} else {
				return Response.status(403).build();// forbidden response
			}
		} catch (Exception e) {
			return Response.status(403).build();// unauthorized
		}
	}

	private void deactivateOldTokens(User user) {
		List<AccessToken> tokens = dao.getTwoConditions(AccessToken.class, "userId", "status", user.getId(), 'A');
		for (AccessToken t : tokens) {
			t.setStatus('K');// kill old token
			dao.update(t);
		}
	}

	private String issueToken(User user, WebApp webapp, int expireMinutes) {
		deactivateOldTokens(user);
		Date tokenTime = new Date();
		AccessToken accessToken = new AccessToken(user.getId(), tokenTime);
		accessToken.setWebApp(webapp);
		accessToken.setExpire(Helper.addMinutes(tokenTime, expireMinutes));
		accessToken.setStatus('A');
		accessToken.setToken(Helper.getSecuredRandom());
		dao.persist(accessToken);
		return accessToken.getToken();
	}

	// retrieves app object from app secret
	private WebApp getWebAppFromSecret(String secret) throws Exception {
		// verify web app secret
		WebApp webApp = dao.findTwoConditions(WebApp.class, "appSecret", "active", secret, true);
		if (webApp == null) {
			throw new Exception();
		}
		return webApp;
	}

	@ValidApp
	@POST
	@Path("validate-app")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateApp(String secret) {
		try {
			this.getWebAppFromSecret(secret);
			return Response.status(200).build();
		} catch (Exception e) {
			return Response.status(404).build();
		}
	}

	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

}
