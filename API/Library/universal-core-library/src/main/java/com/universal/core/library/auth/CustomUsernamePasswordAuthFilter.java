package com.universal.core.library.auth;

import com.universal.core.library.auth.dto.ClaimUser;
import com.universal.core.library.auth.dto.Token;
import com.universal.core.library.exception.BadRequestException;
import com.universal.core.library.utils.AuthUtil;
import com.universal.core.library.utils.TokenUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final CustomUsernamePasswordAuthFilterSetting setting;

    public CustomUsernamePasswordAuthFilter(CustomUsernamePasswordAuthFilterSetting setting) {
        this.setting = setting;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        //start auth
        String username = request.getParameter(setting.getUsernameKey() == null ? "username" : setting.getUsernameKey());
        String password = request.getParameter(setting.getPasswordKey() == null ? "password" : setting.getPasswordKey());
        log.info("start to auth user: {}", username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return setting.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
            , FilterChain chain, Authentication authResult) {
        //when success login
        var user = (ClaimUser) authResult.getPrincipal();
        log.info("successful auth: {}", user.getUsername());

        String accessToken = TokenUtil.generateToken(
                user.getUsername(),
                request.getRequestURL().toString(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                setting.getSignKey(), setting.getTokenExpiryMinute()
                , user.getClaimList()
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream().write(
                setting.getMapper().writeValueAsBytes(Token.builder()
                        .accessToken(accessToken)
                        .build())
        );
        //todo: enhance include refresh token in future
    }

    @Override
    @SneakyThrows
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException failed) {
        //when fail login
        log.info("fail auth: {}", failed.getMessage());
        AuthUtil.setAuthExceptionResponse(response, setting.getMapper(), failed.getMessage()
                , failed.getCause() instanceof BadRequestException ? ((BadRequestException) failed.getCause()).getData() : null);
    }
}
