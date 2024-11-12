package digital.moveto.botinok.client.playwright;

import com.microsoft.playwright.*;
import digital.moveto.botinok.client.config.ClientConst;
import digital.moveto.botinok.client.config.GlobalConfig;
import digital.moveto.botinok.client.exeptions.StopBotWorkException;
import digital.moveto.botinok.client.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@NoArgsConstructor
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PlaywrightService implements AutoCloseable {

    final int DEFAULT_TIMEOUT_FOR_BROWSER = 60_000;
    private Playwright playwright;
    @Getter
    @Setter
    private Page page;
    private Path userDataDir;
    private BrowserContext context;
    private int width = 1366;
    private int height = 768;

    private int countOpenPage = 0;

    @Autowired
    private GlobalConfig config;

    private String playwrightLocalDir;
    private double speed;
    private boolean defaultHeadless;
    private boolean metamask = true;

    @PostConstruct
    public void init(){
        this.playwrightLocalDir = config.pathToStateFolder;
        this.speed = config.speedOfBot;
        this.defaultHeadless = config.headlessBrowser;
    }

    public void start(Path userDateDir, boolean headless) {
        playwright = Playwright.create();
        BrowserType chromium = playwright.chromium();
        context = chromium
                .launchPersistentContext(userDateDir, new BrowserType.LaunchPersistentContextOptions().setHeadless(headless).setViewportSize(1366, 768));
        this.userDataDir = userDateDir;

        this.page = context.pages().get(0);
        this.page.setDefaultTimeout(ClientConst.DEFAULT_TIMEOUT_FOR_BROWSER);
        this.page.setDefaultNavigationTimeout(ClientConst.DEFAULT_TIMEOUT_FOR_BROWSER);
    }
    public Page newPage() {
        return context.newPage();
    }

    public void start(boolean headless) {
        double uniqueId = Math.random();
        this.userDataDir = Paths.get(playwrightLocalDir + "tmp-" + uniqueId);
        start(this.userDataDir, headless);
    }

    public void start(String userDataDir, boolean headless) {
        this.userDataDir = Paths.get(userDataDir);
        start(this.userDataDir, headless);
    }

    public void start() {
        start(defaultHeadless);
    }
    public void startWithoutMetaMash(){
        metamask = false;
        start();
    }

    public void open(String url) {
        page.navigate(url);
        countOpenPage++;
    }

    public void openInNewPage(String url) {
        if (page.url() == null) {
            page.navigate(url);
        } else {
            Page newPage = context.newPage();
            newPage.navigate(url);
        }
    }

    public void openAndLoad(String url) {
        open(url);
        waitForLoadState();
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public Response reload() {
        return page.reload();
    }

    public void close() {
        if (this.playwright != null) {
            this.playwright.close();
        }
        this.playwright = null;
        this.page = null;
        this.countOpenPage = 0;
    }

    public void click(String selector) {
        page.click(selector);
    }

    public void click(int x, int y) {
        page.mouse().click(x, y);
    }

    public void type(String selector, String text) {
        page.fill(selector, text);
    }

    public void type(String text) {
        page.keyboard().type(text);
    }

    public void screenshot(String fileName) {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(fileName)));
    }

    public void screenshot(String url, String path) {
        page.navigate(url);
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
    }

    public void waitForSelector(String selector) {
        page.waitForSelector(selector);
    }

    public void waitForSelector(String selector, int timeout) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeout));
    }

    public boolean isSelectorVisible(String selector) {
        return page.isVisible(selector);
    }

    public boolean isSelectorFind(String selector) {
        return page.locator(selector).count() > 0;
    }

    public boolean isTextFind(String selector) {
        return getLocator("text=" + selector).count() > 0;
    }

    public Locator getLocator(String selector) {
        return page.locator(selector);
    }

    public Optional<ElementHandle> getElementWithCurrentText(String text) {
        Optional<ElementHandle> first = getLocator("text=" + text).elementHandles().stream()
                .filter(elementHandle -> elementHandle.innerText().trim().equalsIgnoreCase(text))
                .findFirst();
        return first;
    }

    public Optional<ElementHandle> getElementByLocator(String locator) {
        Optional<ElementHandle> first = getLocator(locator).elementHandles().stream()
                .findFirst();
        return first;
    }

    public List<ElementHandle> getElementsByLocator(String locator) {
        return new ArrayList<>(getLocator(locator).elementHandles());
    }

    public Optional<ElementHandle> getByText(String text) {
        Optional<ElementHandle> first = page.getByText(text).elementHandles().stream()
                .findFirst();
        return first;
    }

    public ElementHandle getByText(ElementHandle elementHandle, String text) {
        return elementHandle.querySelector("text=" + text);
    }

    public List<ElementHandle> getElementsWithCurrentText(String text) {
        return getLocator("text=" + text).elementHandles().stream()
                .filter(elementHandle -> elementHandle.innerText().trim().equalsIgnoreCase(text))
                .collect(Collectors.toList());
    }

    public Collection<ElementHandle> getElementsWithText(String text) {
        return getLocator("text=" + text).elementHandles().stream()
                .filter(elementHandle -> elementHandle.innerText().trim().contains(text))
                .collect(Collectors.toList());
    }


    public void waitForNavigation(Runnable code) {
        page.waitForNavigation(code);
    }

    public void waitForNavigation(Runnable code, int timeout) {
        page.waitForNavigation(new Page.WaitForNavigationOptions().setTimeout(timeout), code);
    }

    public void waitForLoadState() {
        page.waitForLoadState();
    }

    public void sleep(int timeout) {
        sleep(timeout, true);
    }

    public void sleep(int timeout, boolean changeSpeedOfBot) {
        if (changeSpeedOfBot) {
            timeout = (int) (timeout / speed);
        }
        if (timeout > 60_000) {
            log.debug("Sleep for {}:{} min.", timeout / 60_000, (timeout / 1000) % 60);
        } else {
            if (timeout > 1000) {
                log.trace("Sleep for {} sec.", timeout / 1000);
            }
        }
        try {

            sleepWhenPause();
            Thread.sleep(timeout);
            sleepWhenPause();
        } catch (InterruptedException e) {
            throw new StopBotWorkException(e);
        }
    }

    private void sleepWhenPause() throws InterruptedException {
        // pause bot if needed
        while (! config.workOrPauseBoolean){
            Thread.sleep(50);
        }
    }


    public void sleepRandom(int min, int max) {
        sleep((int) (Math.random() * (max - min) + min));
    }

    public void sleepRandom(int timeout) {
        sleep((int) (timeout / 2 + Math.random() * timeout));
    }

    public void sleep(int timeout, String message) {
        log.info(message);
        sleep(timeout);
    }

    public String innerHTML(String selector) {
        return page.querySelector(selector).innerHTML();
    }

    public void scrollToSelector(String selector) {
        Locator locator = getLocator(selector);
        locator.scrollIntoViewIfNeeded();
    }

    public void scrollToSelector(Locator locator) {
        locator.scrollIntoViewIfNeeded();
    }

    public void scrollMouseDown(int x, int y, int deltaY) {
        scrollMouse(x, y, 0, deltaY);
    }

    public void scrollMouse(int x, int y, int deltaX, int deltaY) {
        page.mouse().move(x, y);
        page.mouse().wheel(deltaX, deltaY);
    }

    public ElementHandle getParent(ElementHandle elementHandle) {
        JSHandle parentElementHandle = elementHandle.evaluateHandle("element => element.parentElement");
        return (ElementHandle) parentElementHandle;
    }

    public ElementHandle getNextElement(ElementHandle elementHandle) {
        JSHandle nextElementHandle = elementHandle.evaluateHandle("element => element.nextElementSibling");
        return (ElementHandle) nextElementHandle;
    }

    public ElementHandle getPreviousElement(ElementHandle elementHandle) {
        JSHandle previousElementHandle = elementHandle.evaluateHandle("element => element.previousElementSibling");
        return (ElementHandle) previousElementHandle;
    }


    public void setText(ElementHandle elementHandle, String text) {
        elementHandle.evaluate("el => el.innerHTML = '" + text + "'");
    }

    public void closeAndClear() {
        close();
        FileUtils.deleteFolder(userDataDir.toFile());
    }
}
