package edu.dirla.app.ws.rest.common;/* Licensed under the Apache License, Version 2.0 (the "License");
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

import edu.dirla.common.exceptions.NotAuthorizedException;
import edu.dirla.common.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractRestService {

  protected Logger log = LoggerFactory.getLogger(this.getClass());

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResourceNotFoundException.class)
  public
  @ResponseBody
  RestErrorInfo handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request, HttpServletResponse response) {
    log.info("Converting Resource Not Found exception to RestResponse : " + ex.getMessage());
    return new RestErrorInfo(ex, "resource not found");
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public
  @ResponseBody
  RestErrorInfo handleGeneralException(Exception ex, WebRequest request, HttpServletResponse response) {
    log.info("Converting Internal exception to RestResponse : " + ex.getMessage());
    return new RestErrorInfo(ex, "There is a error in the server side.");
  }


  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(NotAuthorizedException.class)
  public
  @ResponseBody
  RestErrorInfo handleAuthorizationException(Exception ex, WebRequest request, HttpServletResponse response) {
    log.info("Converting Internal exception to RestResponse : " + ex.getMessage());
    return new RestErrorInfo(ex, "not authorized to do the action: "+ex.getMessage());
  }
}
