package hr.fer.susjedi.security;

import hr.fer.susjedi.service.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2Service oAuth2Service;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String provider = extractProvider(request);

        log.info("OAuth2 authentication successful - Provider: {}", provider);

        try {
            String jwtToken = oAuth2Service.handleOAuth2Login(oauth2User, provider);

            String redirectUrl = frontendUrl + "/#/auth/callback?token=" + jwtToken;
            log.info("Redirecting to: {}", redirectUrl);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("Error during OAuth2 success handling", e);
            response.sendRedirect(frontendUrl + "/#/?error=oauth_failed");
        }
    }

    private String extractProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("/google")) return "google";
        else if (uri.contains("/microsoft")) return "microsoft";
        else throw new IllegalArgumentException("Unknown OAuth2 provider in URI: " + uri);
    }
}
