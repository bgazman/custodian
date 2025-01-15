//package consulting.gazman.security.config;
//
//import lombok.Data;
//
//@Configuration
//public class ThemeConfig {
//    @Bean
//    public ThemeResolver themeResolver() {
//        SessionThemeResolver resolver = new SessionThemeResolver();
//        resolver.setDefaultThemeName("default");
//        return resolver;
//    }
//
//    @Bean
//    public ResourceBundleThemeSource themeSource() {
//        ResourceBundleThemeSource source = new ResourceBundleThemeSource();
//        source.setBasenamePrefix("themes/");
//        return source;
//    }
//}
//
//@Data
//@ConfigurationProperties(prefix = "oauth.themes")
//public class ThemeProperties {
//    private Map<String, ClientTheme> clients = new HashMap<>();
//}
//
//@Data
//public class ClientTheme {
//    private String primaryColor;
//    private String secondaryColor;
//    private String logo;
//    private String backgroundImage;
//}