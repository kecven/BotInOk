package main

import (
	"archive/zip"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"runtime"
)

func main() {
	// Determine the operating system
	osType := runtime.GOOS
	fmt.Printf("Operating system: %s\n", osType)

	// Path to the application directory in the user's home directory
	homeDir, err := os.UserHomeDir()
	if err != nil {
		fmt.Println("Failed to get home directory:", err)
		return
	}

	appDir := filepath.Join(homeDir, ".botinok")
	err = os.MkdirAll(appDir, os.ModePerm)
	if err != nil {
		fmt.Println("Failed to create application directory:", err)
		return
	}

	// Check if OpenJDK is installed
	jdkDir := filepath.Join(appDir, "openjdk-21")
	if _, err := os.Stat(jdkDir); os.IsNotExist(err) {
		fmt.Println("OpenJDK 21 not found. Downloading...")
		err = downloadOpenJDK(osType, jdkDir)
		if err != nil {
			fmt.Println("Error downloading OpenJDK:", err)
			return
		}
	} else {
		fmt.Println("OpenJDK 21 is already installed.")
	}

	// Check for application updates
	appFile := filepath.Join(appDir, "BotInOk.jar")
	localVersionFile := filepath.Join(appDir, "version.txt")

	var localVersion string
	if _, err := os.Stat(appFile); os.IsNotExist(err) {
		fmt.Println("Application not found. Downloading the latest version...")
		err = downloadApp(appFile)
		if err != nil {
			fmt.Println("Error downloading application:", err)
			return
		}
		localVersion = getAppVersion(appFile)
		ioutil.WriteFile(localVersionFile, []byte(localVersion), 0644)
	} else {
		fmt.Println("Checking for application updates...")
		localVersionData, err := ioutil.ReadFile(localVersionFile)
		if err != nil {
			fmt.Println("Error reading local application version:", err)
			return
		}
		localVersion = string(localVersionData)
		latestVersion, err := getLatestVersion()
		if err != nil {
			fmt.Println("Error getting the latest application version:", err)
			return
		}
		if localVersion != latestVersion {
			fmt.Println("A new version is available. Updating application...")
			err = downloadApp(appFile)
			if err != nil {
				fmt.Println("Error downloading application:", err)
				return
			}
			ioutil.WriteFile(localVersionFile, []byte(latestVersion), 0644)
		} else {
			fmt.Println("The latest version of the application is already installed.")
		}
	}

	// Run the application

	err = runApp(jdkDir, appFile)
	if err != nil {
		fmt.Println("Error running application:", err)
		return
	}
}

func downloadOpenJDK(osType, jdkDir string) error {
	var url string
	var archiveFile string

	switch osType {
	case "windows":
		url = "https://botinok.work/downloads/windows-jdk-21.0.5.zip"
		archiveFile = filepath.Join(jdkDir, "windows-jdk-21.0.5.zip")
	case "darwin":
		url = "https://botinok.work/downloads/macos-jdk-21.0.5.zip"
		archiveFile = filepath.Join(jdkDir, "macos-jdk-21.0.5.zip")
	case "linux":
		url = "https://botinok.work/downloads/linux-jdk-21.0.5.zip"
		archiveFile = filepath.Join(jdkDir, "linux-jdk-21.0.5.zip")
	default:
		return fmt.Errorf("Unsupported operating system: %s", osType)
	}

	// Create the JDK directory
	err := os.MkdirAll(jdkDir, os.ModePerm)
	if err != nil {
		return err
	}

	// Download the OpenJDK archive
	fmt.Println("Downloading OpenJDK 21 from", url)
	err = downloadFile(archiveFile, url)
	if err != nil {
		return err
	}

	// Extract the archive
	fmt.Println("Extracting OpenJDK 21...")
	err = extractZip(archiveFile, jdkDir)
	if err != nil {
		return err
	}

	// Remove the archive
	os.Remove(archiveFile)

	return nil
}

func downloadApp(appFile string) error {
	url := "https://botinok.work/downloads/BotInOk-latest.jar"
	fmt.Println("Downloading application from", url)
	return downloadFile(appFile, url)
}

func getLatestVersion() (string, error) {
	url := "https://botinok.work/downloads/latest_version.txt"
	resp, err := http.Get(url)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()
	versionData, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}
	return string(versionData), nil
}

func getAppVersion(appFile string) string {
	// Implement a way to get the application version from the file
	// For example, read version from manifest or predefined value
	return "1.0.0"
}

func downloadFile(filepath string, url string) error {
	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	out, err := os.Create(filepath)
	if err != nil {
		return err
	}
	defer out.Close()

	_, err = io.Copy(out, resp.Body)
	return err
}

func extractZip(src string, dest string) error {
	r, err := zip.OpenReader(src)
	if err != nil {
		return err
	}
	defer r.Close()

	for _, f := range r.File {
		fpath := filepath.Join(dest, f.Name)

		if f.FileInfo().IsDir() {
			os.MkdirAll(fpath, os.ModePerm)
			continue
		}

		if err = os.MkdirAll(filepath.Dir(fpath), os.ModePerm); err != nil {
			return err
		}

		outFile, err := os.OpenFile(fpath, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, f.Mode())
		if err != nil {
			return err
		}

		rc, err := f.Open()
		if err != nil {
			return err
		}

		_, err = io.Copy(outFile, rc)

		outFile.Close()
		rc.Close()

		if err != nil {
			return err
		}
	}
	return nil
}

func runApp(jdkDir, appFile string) error {
	javaExec := filepath.Join(jdkDir, "openjdk-21.0.5", "bin", "java.exe")
	cmd := exec.Command(javaExec, "-jar", appFile)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}
