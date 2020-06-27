package kakao.recruit.jisoo.mq.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kakao.recruit.jisoo.mq.ReceiveConsumer;
import kakao.recruit.jisoo.mq.SprayProducer;
import kakao.recruit.jisoo.mq.dto.Spray;
import kakao.recruit.jisoo.mq.dto.SprayHistory;
import kakao.recruit.jisoo.mq.error.ReceiveException;
import kakao.recruit.jisoo.mq.error.SearchEmptyException;
import kakao.recruit.jisoo.mq.error.SprayException;
import kakao.recruit.jisoo.mq.repo.SprayHistoryRepository;
import kakao.recruit.jisoo.mq.svc.JwtService;

@RestController
public class SprayController {
	
	@Autowired
    private JwtService jwtService;

	@Autowired 
	private SprayHistoryRepository shRepository;
	
	
	// 뿌리기 API 
	// ** Param price : 뿌리기 금액
	// ** Param cnt : 뿌리기 인원
	
	@GetMapping("/spray")
	public Greeting spray(
			@RequestHeader HttpHeaders headers,
			@RequestParam(value="price", required=true) String price,
			@RequestParam(value="cnt", required=true) int cnt) {
		
		
		int sPrice = Integer.parseInt(price);
		int chk = 0;
		String sRoomId = headers.get("X-ROOM-ID").get(0);
		String sUserId = headers.get("X-USER-ID").get(0);
		
		long startTime = System.currentTimeMillis();
		
		// 뿌리기 인원 수 만큼 토큰을 생성 
		for(int i = 0; i < cnt; i++) {
			
			// 분배 금액을 설정
			int sNprice = (sPrice/2);
			
			// 뿌리기 정보를 DB에 저장
			SprayHistory sh = new SprayHistory(sPrice, sNprice, sUserId, "", "N", startTime);
			sh = shRepository.save(sh);
			
			Spray sSpray = new Spray(sh.getId(),sNprice,sUserId,startTime);

			// 뿌리기 토큰 생성
			String dToken = jwtService.create(sRoomId,sSpray,sUserId);
			sPrice = sNprice/2;

			// 생성된 토큰을 mq에 전달 
			// roomid 가 키값
			if(new SprayProducer().run(sRoomId,dToken));
				chk++;
		}
		if(chk == cnt)
			return new Greeting(sRoomId, "뿌리기 성공");
		else
			throw new SprayException();
		
	}
	
	// 받기 API 
	
	@GetMapping("/receiver")
	public Map<String, Object> receiver(
			@RequestHeader HttpHeaders headers) {
		String sRoomId = headers.get("X-ROOM-ID").get(0);
		String sUserId = headers.get("X-USER-ID").get(0);
		
		Map<String, Object> rData = null;
		
		// 뿌리기를 받았는지 여부 확인
		List<SprayHistory> sSpHistory = shRepository.chkReceiver(sUserId,sRoomId);
		
		// roomid 가  같은 메시지를 랜덤으로 큐에서 가져옴
		if (sSpHistory.size() == 0) {
			String rToken = new ReceiveConsumer().run(sRoomId);
			
			if (rToken.isEmpty())
				throw new ReceiveException();
			
			
			// 토큰을 디코딩해 받은 금액 및 정보를 받음
			rData = jwtService.get(sRoomId,rToken);
			
			// 토큰 정보 저장
			shRepository.update(Integer.parseInt(rData.get("id").toString()),sUserId);
		}
		else {
			throw new ReceiveException();
		}
		
		return rData;
	}
	
	// 조회  API 
	  @GetMapping(path="/all")
	  public @ResponseBody Iterable<SprayHistory> getSprayData(@RequestHeader HttpHeaders headers) {

		  String sUserId = headers.get("X-USER-ID").get(0);
			/* System.out.println(shRepository.select(sUserId)); */
		  
		  
		  // 조회 시 뿌리기 했던 유저 id로 조회 
		  // 조회 조건은 오늘 기준 7일 전까지 
		  List<SprayHistory> sSpHistory = shRepository.select(sUserId);
		  
		  // 조회 데이터가 없을 경우 실패 응답 리턴
		  if(sSpHistory.size() == 0)
			  throw new SearchEmptyException();
		  
	    return shRepository.select(sUserId);
	  }
	
}