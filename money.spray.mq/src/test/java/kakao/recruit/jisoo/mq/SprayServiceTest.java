package kakao.recruit.jisoo.mq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import kakao.recruit.jisoo.mq.controller.Greeting;
import kakao.recruit.jisoo.mq.controller.SprayController;
import kakao.recruit.jisoo.mq.dto.SprayHistory;

@RestClientTest(SprayController.class)
public class SprayServiceTest {
	
	public final int CNT = 3;
	
	@Autowired private MockRestServiceServer server;
	@Autowired private SprayController spService;
	
	//뿌리기 테스트
	@Test void getSpray() 
	{ 
		server.expect(requestTo("/spray?price=1000&cnt=3"))
		.andRespond(withSuccess(new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON)); 
		 
		
		HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", "user1");
        headers.set("X-ROOM-ID", "1000");
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJyZWdEYXRlIjoxNTkzMjMyNzg3Mjk2LCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsIjEwMDAiOnsidXNlcklkIjoidXNlcjEiLCJyb29vSWQiOiIxMDAwIn19.2g3AV5kUXUDWNLufowRkSxzPddg3iq772Xny0HNEmrI");
        Greeting testG = spService.spray(headers,"10000",3);
		
		// Junit5 BDD 사용시
		assertThat("1000").isEqualTo(testG.getRoomId()); 
		assertThat("뿌리기성공").isEqualTo(testG.getContent()); 
	}
	
	//받기 테스트
	@Test void getRecive() 
	{ 
		server.expect(requestTo("/receiver"))
		.andRespond(withSuccess(new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON)); 
		 
		
		HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", "user2");
        headers.set("X-ROOM-ID", "1000");
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJyZWdEYXRlIjoxNTkzMjI1OTM0MDU2LCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMiIsIjEwMDEiOnsiaWQiOm51bGwsInVzZXJuYW1lIjoidXNlcjIiLCJwYXNzd29yZCI6InVzZXIyIn19.xK8z7HV64bgSnV1-3BDKgXZ5cLxZAce66ed3vQoObe0");
        Map<String, Object> testG = spService.receiver(headers);
		
		// Junit5 BDD 사용시
		assertThat("user2").isEqualTo(testG.get("id")); 
		
		//똑같은 유저가 한번 더 받기 눌렀을 경우
		headers = new HttpHeaders();
        headers.set("X-USER-ID", "user2");
        headers.set("X-ROOM-ID", "1000");
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJyZWdEYXRlIjoxNTkzMjI1OTM0MDU2LCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMiIsIjEwMDEiOnsiaWQiOm51bGwsInVzZXJuYW1lIjoidXNlcjIiLCJwYXNzd29yZCI6InVzZXIyIn19.xK8z7HV64bgSnV1-3BDKgXZ5cLxZAce66ed3vQoObe0");
        testG = spService.receiver(headers);
	}
	
	//조회 테스트
	@Test void getSearch() 
	{ 
		server.expect(requestTo("/all"))
		.andRespond(withSuccess(new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON)); 
		 
		
		//뿌리기 한 유저
		HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", "user1");
        headers.set("X-ROOM-ID", "1000");
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJyZWdEYXRlIjoxNTkzMjMyNzg3Mjk2LCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsIjEwMDAiOnsidXNlcklkIjoidXNlcjEiLCJyb29vSWQiOiIxMDAwIn19.2g3AV5kUXUDWNLufowRkSxzPddg3iq772Xny0HNEmrI");
        Iterable<SprayHistory> Result = spService.getSprayData(headers);
		
        for (SprayHistory sprayHistory : Result) {
			System.out.println(sprayHistory);
		}
        
        //뿌리기 안한 유저
        headers = new HttpHeaders();
        headers.set("X-USER-ID", "user2");
        headers.set("X-ROOM-ID", "1000");
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJyZWdEYXRlIjoxNTkzMjMyNzg3Mjk2LCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsIjEwMDAiOnsidXNlcklkIjoidXNlcjEiLCJyb29vSWQiOiIxMDAwIn19.2g3AV5kUXUDWNLufowRkSxzPddg3iq772Xny0HNEmrI");
        Iterable<SprayHistory> Result1 = spService.getSprayData(headers);
		
        for (SprayHistory sprayHistory : Result1) {
			System.out.println(sprayHistory);
		}
        
		// Junit5 BDD 사용시
		/*
		 * assertThat("1000").isEqualTo(Result.get);
		 * assertThat("뿌리기성공").isEqualTo(testG.getContent());
		 */
	}

}
