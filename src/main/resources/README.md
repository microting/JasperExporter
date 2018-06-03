#####Adding certificate to jre

1. Export certificate with help of browser
2. Import (add) certificate to keystore via keytool 
>keytool -import -trustcacerts -keystore ..\jre\lib\security\cacerts -storepass changeit -noprompt -alias microting1eform.com -file microting.cer