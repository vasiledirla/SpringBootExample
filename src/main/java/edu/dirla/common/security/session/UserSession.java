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

import edu.dirla.common.UserRep;

public class UserSession {

  public UserSession() {
  }
  private String token;
  public UserSession(UserRep user) {
    this.user = user;
  }
  private UserRep user;
  public void setUser(UserRep user) {
    this.user = user;
  }
  public UserRep getUser() {
    return user;
  }
  public void setToken(String token) {
    this.token = token;
  }
  public String getToken() {
    return token;
  }
}
