package edu.dirla.app.ws.rest.representations.log;/* Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.List;

public class UserDataTrafficResult {

  private long overallSize;
  private Long totalTrafficDataForUser;
  private long overallTraffic;
  private List<LogData> collections;
  public void setOverallSize(long overallSize) {
    this.overallSize = overallSize;
  }
  public long getOverallSize() {
    return overallSize;
  }
  public void setTotalTrafficDataForUser(Long totalTrafficDataForUser) {
    this.totalTrafficDataForUser = totalTrafficDataForUser;
  }
  public Long getTotalTrafficDataForUser() {
    return totalTrafficDataForUser;
  }
  public void setOverallTraffic(long overallTraffic) {
    this.overallTraffic = overallTraffic;
  }
  public long getOverallTraffic() {
    return overallTraffic;
  }
  public void setCollections(List<LogData> collections) {
    this.collections = collections;
  }
  public List<LogData> getCollections() {
    return collections;
  }
}
