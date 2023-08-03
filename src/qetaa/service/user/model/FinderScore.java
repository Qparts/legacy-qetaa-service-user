package qetaa.service.user.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "usr_finder_score")
public class FinderScore implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "usr_finder_score_id_seq_gen", sequenceName = "usr_finder_score_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_finder_score_id_seq_gen")
	@Column(name = "id", updatable = false)
	private int id;
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="user_id")
	private int userId;
	@Column(name="stage")
	private String stage;
	@Column(name="description")
	private String desc;
	@Column(name="score")
	private int score;
	@Column(name="quotation_response_id")
	private Long quotationResponseId;
	@Column(name="cart_id")
	private Long cartId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Long getQuotationResponseId() {
		return quotationResponseId;
	}
	public void setQuotationResponseId(Long quotationResponseId) {
		this.quotationResponseId = quotationResponseId;
	}
	public Long getCartId() {
		return cartId;
	}
	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}
	
}
