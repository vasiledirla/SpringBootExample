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

public class DataTrafficResult {

  private CheckTrafficRep checkTrafficRequest;
  private UserDataTrafficResult trafficValue;
  private long time;
  public void setCheckTrafficRequest(CheckTrafficRep checkTrafficRequest) {
    this.checkTrafficRequest = checkTrafficRequest;
  }
  public CheckTrafficRep getCheckTrafficRequest() {
    return checkTrafficRequest;
  }
  public void setTime(long time) {
    this.time = time;
  }
  public long getTime() {
    return time;
  }

  public UserDataTrafficResult getTrafficValue() {
    return trafficValue;
  }
  public void setTrafficValue(UserDataTrafficResult trafficValue) {
    this.trafficValue = trafficValue;
  }
}
