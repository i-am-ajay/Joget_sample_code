package org.joget.geowatch.test.einvoice;

import org.joget.commons.util.LogUtil;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;

public class SSLTest {
    public static void main() {
        String url = "https://gw-apic-gov.gazt.gov.sa/e-invoicing/developer-portal/invoices/reporting/single";
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket("gw-apic-gov.gazt.gov.sa", 443);

            InputStream in = sslsocket.getInputStream();
            OutputStream out = sslsocket.getOutputStream();

            // Write a test byte to get a reaction :)
            out.write(1);

            while (in.available() > 0) {
                LogUtil.info("Stream",in.read()+"");
            }
            LogUtil.info("Successfully connected","");

        } catch (Exception exception) {
            LogUtil.error("SSLTest",exception,exception.getMessage());
            exception.printStackTrace();
        }
    }
}
return SSLTest.main();
