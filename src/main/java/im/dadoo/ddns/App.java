package im.dadoo.ddns;

import im.dadoo.ddns.context.Context;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by codekitten on 2017/2/24.
 */
public class App {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Context.class);
  }
}
