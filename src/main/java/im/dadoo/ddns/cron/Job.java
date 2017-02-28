package im.dadoo.ddns.cron;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import im.dadoo.ddns.co.DnsPodCo;
import im.dadoo.ddns.co.IpCo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by codekitten on 2017/2/28.
 */
@Component
public class Job {

  private static final Logger MLOGGER = LoggerFactory.getLogger(Job.class);
  private static final Logger ELOGGER = LoggerFactory.getLogger(Exception.class);

  @Autowired
  private Environment env;

  @Autowired
  private IpCo ipCo;

  @Autowired
  private DnsPodCo dnsPodCo;

  @Scheduled(cron = "0 */5 * * * ?")
  public void execute() {
    try {
      MLOGGER.info("动态域名解析开始");
      String ip = this.ipCo.getIp();
      if (StringUtils.isBlank(ip)) {
        throw new Exception("ip地址获取失败");
      }
      MLOGGER.info(String.format("当前ip地址为%s", ip));
      JSONArray domains = this.dnsPodCo.getDomains();
      if (domains != null) {
        for (Object temp1 : domains) {
          JSONObject domain = (JSONObject)temp1;
          if (StringUtils.equals(this.env.getProperty("domain"), domain.getString("name"))) {
            String domainId = domain.getString("id");
            JSONArray records = this.dnsPodCo.getRecords(domainId);
            if (records != null) {
              for (Object temp2 : records) {
                JSONObject record = (JSONObject)temp2;
                List<String> set = Splitter.on(",").splitToList(this.env.getProperty("records"));
                if (set.contains(record.getString("name")) && !StringUtils.equals(ip, record.getString("value"))) {
                  record.put("domain_id", domainId);
                  this.dnsPodCo.updateIp(record, ip);
                  MLOGGER.info(String.format("%s的ip地址替换为%s", record.getString("name"), ip));
                }
              }
            }
            break;
          }
        }
      }
    } catch (Exception e) {
      ELOGGER.error("其他异常", e);
    }
    MLOGGER.info("动态域名解析完成");
  }
}
