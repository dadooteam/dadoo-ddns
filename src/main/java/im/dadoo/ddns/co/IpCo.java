package im.dadoo.ddns.co;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by codekitten on 2017/2/28.
 */
@Component
public class IpCo {

  @Autowired
  private Environment env;

  @Autowired
  private OkHttpClient client;

  public String getIp() throws Exception {
    String r = null;
    Request request = new Request.Builder().url(this.env.getProperty("ip.url")).build();
    Response response = this.client.newCall(request).execute();
    JSONObject json = JSON.parseObject(response.body().string());
    if (json != null && json.containsKey("ip")) {
      r = json.getString("ip");
    }
    return r;
  }
}
