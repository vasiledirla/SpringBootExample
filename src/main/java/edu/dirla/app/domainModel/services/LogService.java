package edu.dirla.app.domainModel.services;
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

import edu.dirla.app.ws.rest.representations.log.LogData;
import edu.dirla.app.ws.rest.representations.log.UserDataTrafficResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LogService {

  @Autowired
  private StringRedisTemplate template;

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

  public void deleteLogs() {
    ValueOperations<String, String> ops = this.template.opsForValue();
    for (String accessKey : template.keys("accessLogs.*")) {
      ops.getOperations().delete(accessKey);
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
