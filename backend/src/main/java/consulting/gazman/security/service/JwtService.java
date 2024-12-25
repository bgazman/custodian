package consulting.gazman.security.service;


import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;


import java.util.List;
import java.util.Map;

public interface JwtService {



    String generateAccessToken(User user, String appName, List<GroupMembership> groups, Map<Long, List<String>> permissions);

    String generateRefreshToken(User user, String appName);




    String validateToken(String token);

    Map<String, Object> parseHeader(String token);
}
