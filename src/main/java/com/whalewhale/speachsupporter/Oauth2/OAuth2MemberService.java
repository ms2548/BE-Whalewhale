package com.whalewhale.speachsupporter.Oauth2;

import com.whalewhale.speachsupporter.Users.Users;
import com.whalewhale.speachsupporter.Users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 provider did not return email address");
        }

        Users user = usersRepository.findByUsername(email).orElse(null);
        if (user == null) {
            user = new Users();
            user.setUsername(email);
            user.setNickname(name != null ? name : "Unnamed User");
            user.setIsAdmin(false);
            user.setPassword(null);
            user.setUser_job("INCOMPLETE_PROFILE");
            usersRepository.save(user);
        }

        // Returning DefaultOAuth2User with username as principal name
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "email"  // This should match the attribute key you want to use as the principal name
        );
    }
}
