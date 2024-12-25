package consulting.gazman.security.service;


import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.User;


import java.util.List;
import java.util.Map;

public interface JwtService {



    String generateAccessToken(User user, String clientId, List<GroupMembership> groups, Map<Long, List<String>> permissions);

    String generateRefreshToken(User user, String clientId);




    String validateToken(String token);

    Map<String, Object> parseHeader(String token);
}
