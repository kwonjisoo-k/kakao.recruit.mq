package kakao.recruit.jisoo.mq.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import kakao.recruit.jisoo.mq.dto.User;
import kakao.recruit.jisoo.mq.error.UnauthorizedException;
import kakao.recruit.jisoo.mq.svc.AuthenticationService;
import kakao.recruit.jisoo.mq.svc.JwtService;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AuthenticationService authenticationService;

	private static final String HEADER_AUTH = "Authorization";
	private static final String HEADER_USER = "X-USER-ID";
	private static final String HEADER_ROOM = "X-ROOM-ID";
	@Autowired
	private JwtService jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		
		// 사용자 인증 토큰 체크
		// 인증 토큰에서 X-USER-ID 값과 X-ROOM-ID을 request쪽으로 전달
		final String token = request.getHeader(HEADER_AUTH);
		
		if(token != null && jwtService.isUsable(token))
		{ 
			
			return super.preHandle(request, response, handler);
			
		}
		else
		{ 
			throw new UnauthorizedException();
		}
		
		

		
	}
}
