package consulting.gazman.security.idp.oauth.service;


import consulting.gazman.security.client.user.entity.GroupMembership;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.client.user.entity.User;
import io.jsonwebtoken.Claims;


import java.util.List;
import java.util.Map;

public interface JwtService {



    String generateAccessToken(User user, OAuthClient oAuthClient, List<GroupMembership> groups, Map<Long, List<String>> permissions);



    String generateIdToken(User user, OAuthClient oAuthClient);



    Claims validateToken(String token);

}
