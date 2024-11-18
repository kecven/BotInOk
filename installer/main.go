package main

import (
    "archive/zip"
    "fmt"
    "io"
    "io/ioutil"
    "net/http"
    "os"
//     "os/exec"
    "os/user"
    "path/filepath"
    "runtime"
    "strings"
)

func main() {
    osType := runtime.GOOS
    fmt.Println("Operating System:", osType)

    // Determine URLs for downloading OpenJDK and JavaFX based on OS
    var openJDKURL, javaFXURL string

    switch osType {
    case "windows":
        openJDKURL = "https://botinok.work/downloads/windows-jdk-21.0.5.zip"
        javaFXURL = "https://botinok.work/downloads/windows-javafx-sdk-21.0.5.zip"
    case "darwin":
        openJDKURL = "https://botinok.work/downloads/macos-jdk-21.0.5.jdk.zip"
        javaFXURL = "https://botinok.work/downloads/macos-javafx-sdk-21.0.5.zip"
    case "linux":
        openJDKURL = "https://botinok.work/downloads/linux-jdk-21.0.5.zip"
        javaFXURL = "https://botinok.work/downloads/linux-javafx-sdk-21.0.5.zip"
    default:
        fmt.Println("Unsupported operating system")
        return
    }

    // Installation directory: ~/.botinok
    usr, err := user.Current()
    if err != nil {
        fmt.Println("Error getting current user:", err)
        return
    }
    installDir := filepath.Join(usr.HomeDir, ".botinok")
    os.MkdirAll(installDir, os.ModePerm)

    // Check and install OpenJDK
    if !isInstalled(installDir, "openjdk") {
        fmt.Println("Downloading OpenJDK...")
        downloadAndExtract(openJDKURL, installDir)
    } else {
        fmt.Println("OpenJDK is already installed")
    }

    // Check and install JavaFX
    if !isInstalled(installDir, "javafx") {
        fmt.Println("Downloading JavaFX...")
        downloadAndExtract(javaFXURL, installDir)
    } else {
        fmt.Println("JavaFX is already installed")
    }

    // Get the latest version number
    latestVersion := getLatestVersion("https://botinok.work/downloads/latest_version.txt")
    if latestVersion == "" {
        fmt.Println("Failed to get the latest version")
        return
    }

    // Application download URL
    appURL := fmt.Sprintf("https://botinok.work/downloads/BotInOk-%s.jar", latestVersion)

    // Application local path
    localAppDir := filepath.Join(installDir, "build", "libs")
    os.MkdirAll(localAppDir, os.ModePerm)
    localAppPath := filepath.Join(localAppDir, fmt.Sprintf("BotInOk-%s.jar", latestVersion))

    // Check local version
    localVersion := getLocalVersion(installDir)

    if localVersion != latestVersion {
        fmt.Println("Updating BotInOk application...")
        downloadFile(appURL, localAppPath)
        saveLocalVersion(installDir, latestVersion)
    } else {
        fmt.Println("You have the latest version of BotInOk")
    }

    // Launch the application
    launchApplication(installDir, localAppPath)
}

func isInstalled(installDir, component string) bool {
    componentPath := filepath.Join(installDir, component)
    _, err := os.Stat(componentPath)
    return !os.IsNotExist(err)
}

func downloadAndExtract(url, installDir string) {
    tmpZipPath := filepath.Join(os.TempDir(), "temp_download.zip")
    downloadFile(url, tmpZipPath)
    extractZip(tmpZipPath, installDir)
    os.Remove(tmpZipPath)
}

func downloadFile(url, dest string) {
    resp, err := http.Get(url)
    if err != nil {
        fmt.Println("Error downloading file:", err)
        return
    }
    defer resp.Body.Close()

    out, err := os.Create(dest)
    if err != nil {
        fmt.Println("Error creating file:", err)
        return
    }
    defer out.Close()

    _, err = io.Copy(out, resp.Body)
    if err != nil {
        fmt.Println("Error saving file:", err)
        return
    }
}

func extractZip(zipPath, destDir string) {
    r, err := zip.OpenReader(zipPath)
    if err != nil {
        fmt.Println("Error opening zip file:", err)
        return
    }
    defer r.Close()

    for _, f := range r.File {
        fpath := filepath.Join(destDir, f.Name)
        if f.FileInfo().IsDir() {
            os.MkdirAll(fpath, os.ModePerm)
            continue
        }
        if err = os.MkdirAll(filepath.Dir(fpath), os.ModePerm); err != nil {
            fmt.Println("Error creating directory:", err)
            return
        }
        outFile, err := os.OpenFile(fpath, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, f.Mode())
        if err != nil {
            fmt.Println("Error opening file:", err)
            return
        }
        rc, err := f.Open()
        if err != nil {
            fmt.Println("Error reading zip file:", err)
            return
        }
        _, err = io.Copy(outFile, rc)
        if err != nil {
            fmt.Println("Error extracting file:", err)
            return
        }
        outFile.Close()
        rc.Close()
    }
}

func getLatestVersion(url string) string {
    resp, err := http.Get(url)
    if err != nil {
        fmt.Println("Error getting latest version:", err)
        return ""
    }
    defer resp.Body.Close()
    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println("Error reading latest version:", err)
        return ""
    }
    return strings.TrimSpace(string(body))
}

func getLocalVersion(installDir string) string {
    versionFile := filepath.Join(installDir, "version.txt")
    data, err := ioutil.ReadFile(versionFile)
    if err != nil {
        return ""
    }
    return strings.TrimSpace(string(data))
}

func saveLocalVersion(installDir, version string) {
    versionFile := filepath.Join(installDir, "version.txt")
    ioutil.WriteFile(versionFile, []byte(version), 0644)
}

func launchApplication(installDir, appPath string) {
    // Find the JavaFX lib directory
    javafxBase := filepath.Join(installDir, "javafx")
    javafxLib := ""

    // Assuming that JavaFX is extracted to a directory like javafx-sdk-21.0.5
    files, err := ioutil.ReadDir(javafxBase)
    if err != nil {
        fmt.Println("Error reading JavaFX directory:", err)
        return
    }
    for _, file := range files {
        if file.IsDir() && strings.HasPrefix(file.Name(), "javafx-sdk") {
            javafxLib = filepath.Join(javafxBase, file.Name(), "lib")
            break
        }
    }
    if javafxLib == "" {
        fmt.Println("JavaFX lib directory not found")
        return
    }

//     // Java binary path
//     javaBin := filepath.Join(installDir, "openjdk", "bin", "java")
//
//     // Command to launch the application
//     cmd := exec.Command(
//         javaBin,
//         "--module-path", javafxLib,
//         "--add-modules", "javafx.controls",
//         "-jar", appPath,
//     )
//
//     cmd.Stdout = os.Stdout
//     cmd.Stderr = os.Stderr
//     err = cmd.Run()
//     if err != nil {
//         fmt.Println("Error launching application:", err)
//     }
}