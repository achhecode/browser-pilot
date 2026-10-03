mkdir -p runtime/{browsers,profiles,downloads,screenshots,videos,traces}


echo $PLAYWRIGHT_BROWSERS_PATH
output > /Users/ardanish/Documents/Projects/browser-pilot/runtime/browsers


mvn exec:java \
-Dexec.mainClass=com.microsoft.playwright.CLI \
-Dexec.args="install chromium"


---



Start Chromium separately


```sh
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome \
  --remote-debugging-port=9222 \
  --user-data-dir="$PWD/runtime/profiles/debug"
```

If  using Brave:

```sh
/Applications/Brave\ Browser.app/Contents/MacOS/Brave\ Browser \
  --remote-debugging-port=9222 \
  --user-data-dir="$PWD/runtime/profiles/debug"
```

---


```sh

java -jar browser-pilot.jar

# with a custom browser:

java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"



java -jar browser-pilot.jar \
  --browserpilot.browser.executable-path="/usr/bin/google-chrome"
```



---


Making chrome for testing executable:

If
```bash
"/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" --version

# shows error: “Google Chrome for Testing” is damaged and can’t be opened. You should move it to the Trash.
```

Because Apple notes that macOS Gatekeeper checks downloaded applications and can block software it considers untrusted, modified, or otherwise unable to verify.


```bash
xattr -l "/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app"

com.apple.quarantine: 0281;6abf8d12;Brave;4B6F408B-FB83-4137-9CB6-8C10FCA99404


# Remove the quarantine attribute

xattr -dr com.apple.quarantine "/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app"

# now

"/Users/ardanish/Downloads/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing" --version

Google Chrome for Testing 154.0.8037.92

```


<!-- Fix git issue: File releases/v-1.0.2/browser-pilot-1.0.2.jar is 229.08 MB; this exceeds GitHub's file size limit of 100.00 MB remote: error: GH001: Large files detected. You may want to try Git Large File Storage - 
https://git-lfs.github.com. To github-ac:achhecode/browser-pilot.git ! [remote rejected] main -> main (pre-receive hook declined) error: failed to push some refs to 'github-ac:achhecode/browser-pilot.git' -->

```bash
git log --all --oneline -- releases/v-1.0.2/browser-pilot-1.0.2.jar
git rm --cached releases/v-1.0.2/browser-pilot-1.0.2.jar
brew install git-filter-repo
git filter-repo --path releases/v-1.0.2/browser-pilot-1.0.2.jar --invert-paths --force
echo "releases/v-1.0.2/*.jar" >> .gitignore                    
git add .gitignore    
git commit -m "Ignore release JAR files"                       
git log --all -- releases/v-1.0.2/browser-pilot-1.0.2.jar  
git remote -v    
git remote add origin git@github-ac:achhecode/browser-pilot.git
git push --force-with-lease origin main  
git fetch origin        
git push --force-with-lease origin main      
git push --force origin main  
git ls-files | grep 'browser-pilot-1.0.2.jar'
```


---

Remove browser binaries from your project

```bash

find . -type d \( \
  -name "chromium-*" -o \
  -name "chrome-*" -o \
  -name "ms-playwright" \
\)

find . -type f | grep -Ei 'chromium|chrome|webkit|firefox'

```

Now 

```bash
mvn clean package

mvn clean package -DskipTests

# still jar 240 mb

unzip -l target/*.jar | sort -k1,1nr | head -30


unzip -l target/*.jar \
  | awk '{print $1, $4}' \
  | sort -nr \
  | head -30



unzip -l target/*.jar | grep -Ei \
'chromium|chrome|firefox|webkit|ms-playwright|browser'


du -sh target/*

mvn dependency:tree

mvn dependency:tree -Dverbose

find . -type f -size +50M -print


grep -R "runtime" . --exclude-dir=target --exclude-dir=.git

grep -R "PLAYWRIGHT_BROWSERS_PATH" . --exclude-dir=target --exclude-dir=.git


rm -rf runtime

ls -lh target/browser-pilot-1.0.2.jar

jar tf target/browser-pilot-1.0.2.jar | grep -Ei 'chromium|chrome|firefox|webkit|runtime'

unzip -l target/browser-pilot-1.0.2.jar | sort -k1,1nr | head -30


du -sh ~/.m2/repository/com/microsoft/playwright/*


du -sh ~/.m2/repository/com/github/kwhat/*

rm -rf target

ls -lh target/browser-pilot-1.0.2.jar

unzip -l target/browser-pilot-1.0.2.jar | grep 'driver'


unzip -l target/browser-pilot-1.0.2.jar \
  | sort -k1,1nr \
  | head -40



https://googlechromelabs.github.io/chrome-for-testing/
```