# cert-trust-demo

## Why

This app is a simple Java Spring app to demonstrate that there is
something intermittently preventing it from connecting to 
UAA and CAPI on cf-for-k8s v0.1.0.

This is a simplification of a mature Java app that works great on cf-deployment.
That app uses
[cloudfoundry-certificate-truster](https://github.com/pivotal-cf/cloudfoundry-certificate-truster)
to implement an important "skip SSL" feature for non-production use cases.
In this app, rather than declaring cloudfoundry-certificate-truster as
a dependency in our `pom.xml` file as we normally would, we have copied
the source code from the library's master branch into this project.
This allowed us to enhance the debugging output from the library code,
but we have left it otherwise unchanged.

## Steps to Reproduce

1. Clone this repo.
   We'll assume that you cloned it to `/Users/pivotal/workspace/cert-trust-demo` for the rest of this doc.

1. Edit `manifest.yml` and change the system domain to match the system domain of your cf-for-k8s.

1. Compile the app. Don't forget to repeat this step each time you change the code while debugging.

   ```
   cd /Users/pivotal/workspace/cert-trust-demo
   ./mvnw install
   ```

1. Do the following a few times to push/re-push the app and observe the app's log.

   `cf delete -f cert-trust-demo && cf push cert-trust-demo -p /Users/pivotal/workspace/cert-trust-demo/target/demo-0.0.1-SNAPSHOT.jar -f ./manifest.yml && sleep 3 && kubectl logs $(kubectl get pods -n cf-workloads | grep cert-trust-demo | cut -d' ' -f1) -n cf-workloads -c opi`

Sometimes the certificate truster will work, and sometimes it will fail because it
is unable to connect to port 443 of the UAA and CAPI platform components.
The desired behavior is that it should always work, as it would when used in
Java apps pushed to cf-deployment.

The log of a successful run will start with happy messages like this:
```
Calculated JVM Memory Configuration: -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=82977K -XX:ReservedCodeCacheSize=240M -Xss1M -Xmx405022K (Head Room: 0%, Loaded Class Count: 12236, Thread Count: 250, Total Memory: 1024000000)
starting trusting certificate ***********************************
trusting certificate at succeeded for: api.tacos.sso.identity.team:443
trusting certificate at succeeded for: login.tacos.sso.identity.team:443
trusting certificate at succeeded for: uaa.tacos.sso.identity.team:443
```

The log of a failed run will start with sad messages like this:
```
Calculated JVM Memory Configuration: -XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=82977K -XX:ReservedCodeCacheSize=240M -Xss1M -Xmx405022K (Head Room: 0%, Loaded Class Count: 12236, Thread Count: 250, Total Memory: 1024000000)
starting trusting certificate ***********************************
Error downloading certificate for api.tacos.sso.identity.team:443
java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
	at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
	at java.base/java.net.Socket.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.<init>(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketFactoryImpl.createSocket(Unknown Source)
	at com.example.demo.SslCertificateTruster$2.run(SslCertificateTruster.java:103)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
	at java.base/java.util.concurrent.FutureTask.run(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.base/java.lang.Thread.run(Unknown Source)
trusting certificate failed for api.tacos.sso.identity.team:443
java.security.cert.CertificateException: Could not obtain server certificate chain
	at com.example.demo.SslCertificateTruster.getUntrustedCertificateInternal(SslCertificateTruster.java:123)
	at com.example.demo.SslCertificateTruster.getUntrustedCertificate(SslCertificateTruster.java:89)
	at com.example.demo.SslCertificateTruster.trustCertificateInternal(SslCertificateTruster.java:146)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificatesInternal(CloudFoundryCertificateTruster.java:91)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificates(CloudFoundryCertificateTruster.java:54)
	at com.example.demo.CloudFoundryCertificateTruster.<clinit>(CloudFoundryCertificateTruster.java:103)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Unknown Source)
	at com.example.demo.DemoApplication.forceClassInitializationToCallStaticBlock(DemoApplication.java:11)
	at com.example.demo.DemoApplication.main(DemoApplication.java:22)
Error downloading certificate for login.tacos.sso.identity.team:443
java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
	at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
	at java.base/java.net.Socket.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.<init>(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketFactoryImpl.createSocket(Unknown Source)
	at com.example.demo.SslCertificateTruster$2.run(SslCertificateTruster.java:103)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
	at java.base/java.util.concurrent.FutureTask.run(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.base/java.lang.Thread.run(Unknown Source)
trusting certificate failed for login.tacos.sso.identity.team:443
java.security.cert.CertificateException: Could not obtain server certificate chain
	at com.example.demo.SslCertificateTruster.getUntrustedCertificateInternal(SslCertificateTruster.java:123)
	at com.example.demo.SslCertificateTruster.getUntrustedCertificate(SslCertificateTruster.java:89)
	at com.example.demo.SslCertificateTruster.trustCertificateInternal(SslCertificateTruster.java:146)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificatesInternal(CloudFoundryCertificateTruster.java:91)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificates(CloudFoundryCertificateTruster.java:54)
	at com.example.demo.CloudFoundryCertificateTruster.<clinit>(CloudFoundryCertificateTruster.java:103)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Unknown Source)
	at com.example.demo.DemoApplication.forceClassInitializationToCallStaticBlock(DemoApplication.java:11)
	at com.example.demo.DemoApplication.main(DemoApplication.java:22)
Error downloading certificate for uaa.tacos.sso.identity.team:443
java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
	at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
	at java.base/java.net.Socket.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.connect(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketImpl.<init>(Unknown Source)
	at java.base/sun.security.ssl.SSLSocketFactoryImpl.createSocket(Unknown Source)
	at com.example.demo.SslCertificateTruster$2.run(SslCertificateTruster.java:103)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
	at java.base/java.util.concurrent.FutureTask.run(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.base/java.lang.Thread.run(Unknown Source)
trusting certificate failed for uaa.tacos.sso.identity.team:443
java.security.cert.CertificateException: Could not obtain server certificate chain
	at com.example.demo.SslCertificateTruster.getUntrustedCertificateInternal(SslCertificateTruster.java:123)
	at com.example.demo.SslCertificateTruster.getUntrustedCertificate(SslCertificateTruster.java:89)
	at com.example.demo.SslCertificateTruster.trustCertificateInternal(SslCertificateTruster.java:146)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificatesInternal(CloudFoundryCertificateTruster.java:91)
	at com.example.demo.CloudFoundryCertificateTruster.trustCertificates(CloudFoundryCertificateTruster.java:54)
	at com.example.demo.CloudFoundryCertificateTruster.<clinit>(CloudFoundryCertificateTruster.java:103)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Unknown Source)
	at com.example.demo.DemoApplication.forceClassInitializationToCallStaticBlock(DemoApplication.java:11)
	at com.example.demo.DemoApplication.main(DemoApplication.java:22)
```
