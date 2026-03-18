package digital.moveto.botinok.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {

    @Value("${botinok.speedOfBot:0.3}")
    public double speedOfBot;

    @Value("${botinok.countParseForOneTime:50}")
    public int countParseForOneTime;

    @Value("${botinok.reverseAccounts:true}")
    public boolean reverseAccounts;

    @Value("${botinok.pathToStateFolder}")
    public String pathToStateFolder;

    @Value("${botinok.headlessBrowser:true}")
    public boolean headlessBrowser;

    @Value("${botinok.automaticStart:false}")
    public boolean automaticStart;

    @Value("true")
    public boolean workOrPauseBoolean;

    @Value("${botinok.connect.countConnectInYourLocation:16}")
    public int countConnectInYourLocation;

    @Value("${botinok.connect.probabilityOfConnectWithNotHiringUser:0.5}")
    public double probabilityOfConnectWithNotHiringUser;

    @Value("${botinok.apply.thresholdPositionSuitableScore:0.7}")
    public double thresholdPositionSuitableScore;

    @Value("${botinok.apply.ollama.enabled:false}")
    public boolean ollamaEnabled;

    @Value("${botinok.apply.ollama.host:127.0.0.1}")
    public String ollamaHost;

    @Value("${botinok.apply.ollama.port:11434}")
    public int ollamaPort;

    @Value("${botinok.apply.ollama.model:}")
    public String ollamaModel;

    @Value("${botinok.apply.ollama.healthcheckEnabled:true}")
    public boolean ollamaHealthcheckEnabled;

    @Value("${botinok.apply.ollama.checkModelAvailability:true}")
    public boolean ollamaCheckModelAvailability;

    @Value("${botinok.apply.ollama.healthcheckCacheMs:60000}")
    public long ollamaHealthcheckCacheMs;

    @Value("${botinok.apply.ollama.connectTimeoutMs:1500}")
    public int ollamaConnectTimeoutMs;

    @Value("${botinok.apply.ollama.requestTimeoutMs:5000}")
    public int ollamaRequestTimeoutMs;

    @Value("${botinok.apply.ollama.coldStartTimeoutMs:120000}")
    public int ollamaColdStartTimeoutMs;

    @Value("${botinok.apply.ollama.keepAlive:15m}")
    public String ollamaKeepAlive;

}
