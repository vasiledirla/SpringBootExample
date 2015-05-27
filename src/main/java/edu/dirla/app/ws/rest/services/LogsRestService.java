package edu.dirla.app.ws.rest.services;
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

import com.google.common.util.concurrent.*;
import com.wordnik.swagger.annotations.Api;
import edu.dirla.app.domainModel.services.LogService;
import edu.dirla.app.ws.rest.common.AbstractRestService;
import edu.dirla.app.ws.rest.representations.log.CheckTrafficRep;
import edu.dirla.app.ws.rest.representations.log.DataTrafficResult;
import edu.dirla.app.ws.rest.representations.log.LogData;
import edu.dirla.app.ws.rest.representations.log.UserDataTrafficResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Api(value = "Logs", description = "Logs management") // Swagger annotation
@RestController
@RequestMapping(value = "/rest/logs",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE })
public class LogsRestService extends AbstractRestService {

  private static final int READ_IP = 1;
  private static final int READ_STATUS = 2;
  private static final int READ_SIZE = 3;
  private static final int SKIP = 4;
  @Autowired
  private LogService logsService;

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public DataTrafficResult addLogs(@RequestBody CheckTrafficRep checkTrafficRep) {

    List<LogData> results = null;
    long t1 = Calendar.getInstance().getTimeInMillis();

    final List<String> filesToUpload = checkTrafficRep.getFilesToUpload();

    ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(3));
    for (final String fileName : filesToUpload) {

      Callable<Integer> job = new Callable<Integer>() {

        @Override
        public Integer call() throws Exception {
          List<LogData> lines = new ArrayList<LogData>();
          try {
            lines.addAll(readFile(fileName));
          } catch (IOException e) {
            e.printStackTrace();
          }

          Map<String, Long> data = new HashMap<String, Long>();

          for (LogData res : lines) {
            String key = res.getDomain();
            long value = res.getSize();
            Long oldValue = data.get(key);
            data.put(key, value + (oldValue != null ? oldValue : 0));
          }

          logsService.pushLogs("accessLogs." + fileName, data);

          return 0;
        }
      }; // create the job here
      ListenableFuture<Integer> completion = executor.submit(job);
      Futures.addCallback(completion, new FutureCallback<Integer>() {

        @Override
        public void onFailure(Throwable t) {
          // log error
        }

        @Override
        public void onSuccess(Integer result) {
          // do something with the result
        }

      });
    }
    executor.shutdown();
    while (!executor.isTerminated()) {
      try {
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    UserDataTrafficResult userTrafficData = logsService.checkDataTraffic(checkTrafficRep.getClientAddress());

    long t2 = Calendar.getInstance().getTimeInMillis();

    DataTrafficResult dtr = new DataTrafficResult();
    dtr.setCheckTrafficRequest(checkTrafficRep);
    dtr.setTrafficValue(userTrafficData);
    dtr.setTime(t2 - t1);
    return dtr;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, value = "/client/{clientAddress}/",
          consumes = { MediaType.ALL_VALUE })
  public UserDataTrafficResult getUserLogs(@PathVariable String clientAddress) {
    return logsService.checkDataTraffic(clientAddress);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.DELETE,
          consumes = { MediaType.ALL_VALUE })
  public boolean deleteLogs() {
    logsService.deleteLogs();
    return false;
  }

  private List<LogData> readFile(String fileName) throws IOException {
    List<LogData> results = new ArrayList<LogData>();
    File file = new File(fileName);

    final InputStream gzipInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));

    StringBuffer domainBuffer = new StringBuffer();
    int status = 0;
    int size = 0;

    int parserState = READ_IP; //IP or domain

    boolean done = false;
    int ch = 0;

    while (!done) {
      try {
        switch (parserState) {
        case SKIP:
          ch = gzipInputStream.read();

          if (ch == -1 || ch == '\n' || ch == '\r') {
            results.add(new LogData(domainBuffer.toString(), status, size));
            if (ch == -1) {
              done = true;
            } else {
              status = 0;
              size = 0;
              domainBuffer.delete(0, domainBuffer.length());
              parserState = READ_IP;
            }
          }
          break;

        case READ_IP:
          ch = gzipInputStream.read();
          if (ch == 32) {
            parserState = READ_STATUS; //after ip
          } else {
            if (ch == -1) {
              done = true;
            } else {
              domainBuffer.append((char) ch);
            }
          }
          break;

        case READ_SIZE:
          ch = gzipInputStream.read();
          boolean foundSize = false;
          do {
            foundSize = ch == 32;
            if (!foundSize) {
              ch = gzipInputStream.read();
            }
          } while (!foundSize);

          ch = gzipInputStream.read();
          while (ch <= '9' && ch >= '0') {
            size *= 10;
            size += (ch - '0');
            ch = gzipInputStream.read();
          }

          if (ch == 32) {
            parserState = SKIP; //after ip
          }
          break;

        case READ_STATUS:
          status = 0;
          boolean foundStatus = true;
          while (status == 0 && ch != -1 && ch != '\n' && ch != '\r') {
            do {
              foundStatus = ch == 32;
              if (!foundStatus) {
                ch = gzipInputStream.read();
              }
            } while (!foundStatus);
            ch = gzipInputStream.read();
            if (ch <= '9' && ch >= '0') {
              status = (ch - '0');
              ch = gzipInputStream.read();
              if (ch <= '9' && ch >= '0') {
                status *= 10;
                status += (ch - '0');
                ch = gzipInputStream.read();
                if (ch <= '9' && ch >= '0') {
                  status *= 10;
                  status += (ch - '0');
                } else {
                  status = 0;
                  ch = gzipInputStream.read();
                }
              } else {
                status = 0;
                ch = gzipInputStream.read();

              }
            } else {
              status = 0;
              ch = gzipInputStream.read();
            }
          }
          if (status > 0) {
            parserState = READ_SIZE; //after ip
          }
          break;

        default:
          //          System.out.println("ch=" + (char) ch);
          break;
        }

      } catch (BufferUnderflowException ex) {
        System.out.println("Horror !");
        break;
      }
    }
    gzipInputStream.close();
    return results;
  }

  //  /Users/p3700487/Work/access_logs/Site0.0-access.log
}