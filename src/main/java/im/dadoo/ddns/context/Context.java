package im.dadoo.ddns.context;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codekitten on 2017/2/24.
 */
@Configuration
@EnableScheduling
@ComponentScan({"im.dadoo.ddns"})
@PropertySource({"file:application.properties"})
public class Context {

  @Bean
  public OkHttpClient client() {
    return new OkHttpClient();
  }

  @Bean(destroyMethod = "shutdown")
  public ExecutorService scheduler() {
    return Executors.newScheduledThreadPool(3);
  }

}
