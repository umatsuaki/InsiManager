package jp.kobe_u.cs27.insiManager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVCの構成をカスタマイズするConfigurationクラス
 */
@Configuration
public class InsiMvcConfigurer implements WebMvcConfigurer {

  /**
   * 静的リソースを提供するハンドラーを追加する
   *
   * @param registry
   */
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    /*
     * 画像を利用できるようにする
     * URIで/images/**を指定すると、classpath:/static/images/の中身が参照される
     */
    registry
        .addResourceHandler("/images/**")
        .addResourceLocations("classpath:/static/images/");
  }

}
