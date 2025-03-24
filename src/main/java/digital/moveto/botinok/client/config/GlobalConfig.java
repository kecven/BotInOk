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

    @Value("${botinok.connect.probabilityOfConnectWithHiringUser:0.98}")
    public double probabilityOfConnectWithHiringUser;

    @Value("${botinok.apply.thresholdPositionSuitableScore:0.7}")
    public double thresholdPositionSuitableScore;

}
