package kakao.recruit.jisoo.mq.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.data.annotation.Id;

public class User {
	
    private String userId;
    private String roooId;

    protected User() {}

    public User(String userId, String roooId) {
        this.userId = userId;
        this.roooId = roooId;
    }
    
    

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoooId() {
		return roooId;
	}

	public void setRoooId(String roooId) {
		this.roooId = roooId;
	}

    @Override
    public String toString() {
        return String.format("User[userId='%s', roooId='%s']", userId, roooId);
    }
}
