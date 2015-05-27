package edu.dirla.common.security;/* Licensed under the Apache License, Version 2.0 (the "License");
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

public enum Role {
  GUEST("guest"),
  EDITOR("editor"),
  AUTHOR("author"),
  ADMIN("admin");

  private String roleName;
  Role(String roleName) {

    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }
}
