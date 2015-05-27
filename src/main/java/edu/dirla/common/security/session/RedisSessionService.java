package edu.dirla.common.security.session;
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import edu.dirla.app.ws.rest.representations.log.UserDataTrafficResult;
import edu.dirla.app.ws.rest.representations.log.LogData;
import edu.dirla.common.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSessionService implements SessionService {

  public static String SESSIONS_KEY_BY_TOKEN_TEMPLATE = "dirla.edu.app.sessions.%s";
  public static String FIND_SESSION_KEYS_BY_USER_PATTERN = "dirla.edu.app.sessions.%s*";

  @Autowired
  private StringRedisTemplate template;

  @Autowired
  private ObjectMapper objectMapper;

  public long pushLogs(String key, Map<String, Long> values) {
    HashOperations<String, String, String> ops = template.opsForHash();
    long time1 = Calendar.getInstance().getTimeInMillis();
    HashMap<String, String> data = new HashMap<String, String>();
    for (String next : values.keySet()) {
      data.put(next, String.valueOf(values.get(next)));
    }
    ops.putAll(key, data);
    long time2 = Calendar.getInstance().getTimeInMillis();
    return (time2 - time1);
  }

  public long countLogs() {
    HashOperations<String, String, String> ops = template.opsForHash();
    return ops.size("keys");
  }

  public void deleteLogs() {
    ValueOperations<String, String> ops = this.template.opsForValue();
    ops.getOperations().delete("keys");
  }

  public Optional<UserSession> findSessionByToken(String sessionToken) {
    ValueOperations<String, String> ops = this.template.opsForValue();
    String serializedSession = ops.get(String.format(SESSIONS_KEY_BY_TOKEN_TEMPLATE, sessionToken));
    return Optional.fromNullable(deserializeSession(serializedSession));
  }

  public Optional<UserSession> findSessionByUserName(String userName) {
    ValueOperations<String, String> ops = this.template.opsForValue();
    Set<String> sessionKeys = ops.getOperations().keys(String.format(FIND_SESSION_KEYS_BY_USER_PATTERN, userName));
    if (sessionKeys.size() == 0)
      return Optional.absent();

    String serializedSession = ops.get(sessionKeys.iterator().next());
    return Optional.fromNullable(deserializeSession(serializedSession));
  }

  public Optional<UserSession> createOrExtendSession(UserRep userRep, Boolean keepSession) {
    UserSession session = null;

    Optional<UserSession> userSessionOptional = findSessionByUserName(userRep.getUserName());
    if (userSessionOptional.isPresent()) {
      session = userSessionOptional.get();
    } else {
      session = UserSessionBuilder.buildSession(userRep);
    }

    String serializedSession = null;
    try {
      String sessionKey = String.format(SESSIONS_KEY_BY_TOKEN_TEMPLATE, session.getToken());
      serializedSession = objectMapper.writeValueAsString(session);
      ValueOperations<String, String> ops = this.template.opsForValue();
      if (!keepSession) {
        ops.set(sessionKey, serializedSession, 1, TimeUnit.MINUTES);
      } else {
        ops.set(sessionKey, serializedSession, 30, TimeUnit.DAYS);
      }
      return Optional.fromNullable(session);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return Optional.absent();
  }

  private UserSession deserializeSession(String s) {
    if (s == null)
      return null;
    try {
      return objectMapper.readValue(s, UserSession.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  ;

  @Override
  public void deleteSession(String sessionId) {
    ValueOperations<String, String> ops = this.template.opsForValue();
    String sessionKey = String.format(SESSIONS_KEY_BY_TOKEN_TEMPLATE, sessionId);
    ops.getOperations().delete(sessionKey);
  }

  public void addLogs(List<LogData> results) {
    ListOperations<String, String> ops = this.template.opsForList();
    int index = 0;
    Map<String, List<String>> data = new HashMap<String, List<String>>();
    for (LogData logData : results) {
      List<String> data1 = data.get(logData.getDomain());
      if (data1 == null) {
        data1 = new ArrayList<String>();
        data.put(logData.getDomain(), data1);
      }
      data1.add("" + logData.getSize());
    }

    Set<String> keys = data.keySet();
    for (String next : keys) {
      List<String> userData = data.get(next);
      if (userData != null && userData.size() > 0) {
        String[] dataS = new String[userData.size()];
        userData.toArray(dataS);

        ops.rightPushAll(String.format("access.%s", next), dataS);
      }
    }
  }
  public UserDataTrafficResult checkDataTraffic(String clientAddress) {
    long overallSize = 0;
    long overallTraffic = 0;
    Long totalTrafficDataForUser = 0L;
    Long storedTrafficValue = 0L;
    List<LogData> collections = new ArrayList<LogData>();

    HashOperations<String, String, String> ops = template.opsForHash();

    String stringValue = "";
    for (String accessKey : template.keys("accessLogs.*")) {
      //overall calculations
      overallSize += ops.size(accessKey);

      //user specific calculations
      stringValue = ops.get(accessKey, clientAddress);
      if (stringValue != null) {
        try {
          storedTrafficValue = Long.parseLong(ops.get(accessKey, clientAddress));
        } catch (Exception ex) {
        }
        collections.add(new LogData(accessKey, 0, storedTrafficValue));
        totalTrafficDataForUser += storedTrafficValue;
      }
    }

    UserDataTrafficResult udtr = new UserDataTrafficResult();
    udtr.setOverallSize(overallSize);
    udtr.setOverallTraffic(overallTraffic);
    udtr.setTotalTrafficDataForUser(totalTrafficDataForUser);
    udtr.setCollections(collections);
    return udtr;
  }
}
