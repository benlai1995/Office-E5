package com.merrycodes;

import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.auth.publicClient.UsernamePasswordProvider;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Github Action 版本 <br>
 * Office E5 调用 graph API <br>
 * <a href="https://docs.microsoft.com/zh-cn/graph/api/resources/message?view=graph-rest-1.0">关于邮件的操作</a>
 *
 * @author MerryCodes
 * @date 2020/12/19 16:49
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final static String CLIENT_ID;
    private final static String CLIENT_SECRET;
    private final static String TENANT_GUID;
    private final static String USERNAME;
    private final static String PASSWORD;
    private final static List<String> SCOPES = Collections.singletonList("https://graph.microsoft.com/.default");

    static {
        Properties officeE5Properties = new Properties();
        try {
            officeE5Properties.load(Main.class.getResourceAsStream("/officeE5.properties"));
        } catch (IOException e) {
            LOGGER.error("获取文件officeE5.properties失败，错误信息：{}", e.toString());
        }
        CLIENT_ID = officeE5Properties.getProperty("CLIENT_ID");
        CLIENT_SECRET = officeE5Properties.getProperty("CLIENT_SECRET");
        TENANT_GUID = officeE5Properties.getProperty("TENANT_GUID");
        USERNAME = officeE5Properties.getProperty("USERNAME");
        PASSWORD = officeE5Properties.getProperty("PASSWORD");
    }

    public static void main(String[] args) {
        UsernamePasswordProvider authProvider = new UsernamePasswordProvider(CLIENT_ID, SCOPES, USERNAME, PASSWORD, NationalCloud.Global, TENANT_GUID, CLIENT_SECRET);
        IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
        User user = graphClient.me().buildRequest().get();
        IMessageCollectionPage iMessageCollectionPage = graphClient.users(user.userPrincipalName).messages().buildRequest().select("sender,subject").get();
        List<Message> messageList = iMessageCollectionPage.getCurrentPage();
        LOGGER.info("运行时间：{} —— 共有{}封件邮件", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), messageList.size());
    }

}
