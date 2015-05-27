package edu.dirla.app.config;
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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan("edu.dirla")
@ConditionalOnClass({ JedisConnection.class, RedisOperations.class, Jedis.class })
@EnableAutoConfiguration
public class RedisConfig {

  @Bean
  public RedisConnectionFactory jedisConnectionFactory() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(5);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    JedisConnectionFactory ob = new JedisConnectionFactory(poolConfig);
    ob.setUsePool(true);
    ob.setHostName("localhost");
    ob.setPort(6379);
    return ob;
  }
  @Bean
  public StringRedisTemplate stringRedisTemplate() {
    return new StringRedisTemplate(jedisConnectionFactory());
  }
}
