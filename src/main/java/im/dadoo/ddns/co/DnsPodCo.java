package im.dadoo.ddns.co;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * Created by codekitten on 2017/2/28.
 */
@Component
public class DnsPodCo {

  @Autowired
  private Environment env;

  @Autowired
  private OkHttpClient client;

  public JSONArray getDomains() throws Exception {
    JSONArray r = null;
    RequestBody body = new FormBody.Builder()
        .add("login_token", String.format("%s,%s", this.env.getProperty("id"), this.env.getProperty("token")))
        .add("format", "json")
        .build();
    Request request = new Request.Builder()
        .addHeader("User-Agent", this.env.getProperty("user-agent"))
        .post(body)
        .url(this.env.getProperty("domain.list.url")).build();
    Response response = this.client.newCall(request).execute();
    JSONObject json = JSON.parseObject(response.body().string());
    if (json != null && json.containsKey("domains")) {
      r = json.getJSONArray("domains");
    }
    response.close();
    return r;
  }

  public JSONArray getRecords(String domainId) throws Exception {
    JSONArray r = null;
    RequestBody body = new FormBody.Builder()
        .add("login_token", String.format("%s,%s", this.env.getProperty("id"), this.env.getProperty("token")))
        .add("format", "json")
        .add("domain_id", domainId)
        .build();
    Request request = new Request.Builder()
        .addHeader("User-Agent", this.env.getProperty("user-agent"))
        .post(body)
        .url(this.env.getProperty("record.list.url")).build();
    Response response = this.client.newCall(request).execute();
    JSONObject json = JSON.parseObject(response.body().string());
    if (json != null && json.containsKey("records")) {
      r = json.getJSONArray("records");
    }
    response.close();
    return r;
  }

  public void updateIp(JSONObject record, String ip) throws Exception {
    RequestBody body = new FormBody.Builder()
        .add("login_token", String.format("%s,%s", this.env.getProperty("id"), this.env.getProperty("token")))
        .add("format", "json")
        .add("domain_id", record.getString("domain_id"))
        .add("record_id", record.getString("id"))
        .add("sub_domain", record.getString("name"))
        .add("record_line_id", record.getString("line_id"))
        .add("value", ip)
        .add("ttl", "10")
        .build();
    Request request = new Request.Builder()
        .addHeader("User-Agent", this.env.getProperty("user-agent"))
        .post(body)
        .url(this.env.getProperty("ddns.update.url")).build();
    Response response = this.client.newCall(request).execute();
    System.out.println(response.body().string());
  }
}
