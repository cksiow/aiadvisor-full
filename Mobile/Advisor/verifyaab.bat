java -jar bundletool-all-1.15.6.jar build-apks --bundle=.\android\app\build\outputs\bundle\release\app-release.aab --output=com.cksiow.aiadvisor.apks --ks="release-key.keystore" --ks-pass=pass:{password} --ks-key-alias="aiadvisor" --key-pass=pass:19841129

java -jar bundletool-all-1.15.6.jar install-apks --apks=com.cksiow.aiadvisor.apks --adb "C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe"

del com.cksiow.aiadvisor.apks