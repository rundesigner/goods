/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcHttpTransport;
import org.apache.xmlrpc.client.XmlRpcHttpTransportException;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.parser.XmlRpcResponseParser;
import org.apache.xmlrpc.util.HttpUtil;
import org.apache.xmlrpc.util.LimitedInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author grigory
 */
public class Transport extends XmlRpcHttpTransport {

    private static final String userAgent = USER_AGENT + " (MY HTTP Transport)";
    private boolean ssl;
    private String hostname;
    private String host;
    private int port;
    private String uri;
    private Socket socket;
    private OutputStream output;
    public InputStream input;
    private final Map headers = new HashMap();
    private boolean responseGzipCompressed = false;
    private XmlRpcHttpClientConfig config;

    public Transport(XmlRpcClient pClient) {
        super(pClient, userAgent);
    }

    protected void setRequestHeader(String pHeader, String pValue) {
        Object value = headers.get(pHeader);
        if (value == null) {
            headers.put(pHeader, pValue);
        } else {
            List list;
            if (value instanceof String) {
                list = new ArrayList();
                list.add(value);
                headers.put(pHeader, list);
            } else {
                list = (List) value;
            }
            list.add(pValue);
        }
    }

    public Object sendRequest(XmlRpcRequest pRequest) throws XmlRpcException {

        config = (XmlRpcHttpClientConfig) pRequest.getConfig();
        URL url = config.getServerURL();
        ssl = "https".equals(url.getProtocol());
        hostname = url.getHost();
        int p = url.getPort();
        port = p < 1 ? 80 : p;
        String u = url.getFile();
        uri = (u == null || "".equals(u)) ? "/" : u;
        host = port == 80 ? hostname : hostname + ":" + port;
        headers.put("Host", host);
        System.out.println("Посылаем запрос!!!!!!!!!!!!!!!!!1");
        return sendRequestX(pRequest);
    }

    protected InputStream getInputStream() throws XmlRpcException {
        final byte[] buffer = new byte[2048];

        System.out.println("Входной поток!!!!!!!!!!!!!!!!!1");

        try {
            // If reply timeout specified, set the socket timeout accordingly
            if (config.getReplyTimeout() != 0) {
                socket.setSoTimeout(config.getReplyTimeout());
            }
            input = new BufferedInputStream(socket.getInputStream());
            // start reading  server response headers
            String line = HttpUtil.readLine(input, buffer);

            StringTokenizer tokens = new StringTokenizer(line);
            tokens.nextToken(); // Skip HTTP version
            String statusCode = tokens.nextToken();
            String statusMsg = tokens.nextToken("\n\r");
            final int code;
            try {
                code = Integer.parseInt(statusCode);
            } catch (NumberFormatException e) {
                throw new XmlRpcClientException("Server returned invalid status code: "
                        + statusCode + " " + statusMsg, null);
            }
            if (code < 200 || code > 299) {
                throw new XmlRpcHttpTransportException(code, statusMsg);
            }
            int contentLength = -1;
            for (;;) {
                line = HttpUtil.readLine(input, buffer);
                if (line == null || "".equals(line)) {
                    break;
                }
                line = line.toLowerCase();
                if (line.startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
                } else if (line.startsWith("content-encoding:")) {
                    responseGzipCompressed = HttpUtil.isUsingGzipEncoding(line.substring("content-encoding:".length()));
                }
            }
            InputStream result;
            if (contentLength == -1) {
                result = input;
            } else {
                result = new LimitedInputStream(input, contentLength);
            }
            return result;
        } catch (IOException e) {
            throw new XmlRpcClientException("Failed to read server response: " + e.getMessage(), e);
        }
    }

    private OutputStream getOutputStream() throws XmlRpcException {
        try {
            final int retries = 3;
            final int delayMillis = 100;

            for (int tries = 0;; tries++) {
                try {
                    socket = newSocket(ssl, hostname, port);



                    output = new BufferedOutputStream(socket.getOutputStream()) {

                        /** Closing the output stream would close the whole socket, which we don't want,
                         * because the don't want until the request is processed completely.
                         * A close will later occur within
                         * {@link XmlRpcLiteHttpTransport#close()}.
                         */
                        public void close() throws IOException {
                            flush();
                            socket.shutdownOutput();
                        }
                    };
                    break;
                } catch (ConnectException e) {
                    if (tries >= retries) {
                        throw new XmlRpcException("Failed to connect to "
                                + hostname + ":" + port + ": " + e.getMessage(), e);
                    } else {
                        try {
                            Thread.sleep(delayMillis);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            }
            sendRequestHeaders(output);
            return output;
        } catch (IOException e) {
            throw new XmlRpcException("Failed to open connection to "
                    + hostname + ":" + port + ": " + e.getMessage(), e);
        }
    }

    private void sendRequestHeaders(OutputStream pOut) throws IOException {
        pOut.write(("POST " + uri + " HTTP/1.0\r\n").getBytes("US-ASCII"));
        for (Iterator iter = headers.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                sendHeader(pOut, key, (String) value);
            } else {
                List list = (List) value;
                for (int i = 0; i < list.size(); i++) {
                    sendHeader(pOut, key, (String) list.get(i));
                }
            }
        }
        pOut.write(toHTTPBytes("\r\n"));
    }

    private byte[] toHTTPBytes(String pValue) throws UnsupportedEncodingException {
        return pValue.getBytes("US-ASCII");
    }

    private void sendHeader(OutputStream pOut, String pKey, String pValue) throws IOException {
        pOut.write(toHTTPBytes(pKey + ": " + pValue + "\r\n"));
    }

    protected Socket newSocket(boolean pSSL, String pHostName, int pPort) throws UnknownHostException, IOException {
        if (pSSL) {
            throw new IOException("Unable to create SSL connections, use the XmlRpcLite14HttpTransportFactory.");
        }
        return new Socket(pHostName, pPort);
    }

    protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig pConfig) {
        return responseGzipCompressed;
    }

    protected void writeRequest(ReqWriter pWriter) throws XmlRpcException, IOException, SAXException {
        pWriter.write(getOutputStream());
    }

    protected void close() throws XmlRpcClientException {
        IOException e = null;
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                e = ex;
            }
        }
        if (output != null) {
            try {
                output.close();
            } catch (IOException ex) {
                if (e != null) {
                    e = ex;
                }
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                if (e != null) {
                    e = ex;
                }
            }
        }
        if (e != null) {
            throw new XmlRpcClientException("Failed to close connection: " + e.getMessage(), e);
        }
    }

    public Object sendRequestX(XmlRpcRequest pRequest) throws XmlRpcException {
        
        XmlRpcStreamRequestConfig config = (XmlRpcStreamRequestConfig) pRequest.getConfig();
        boolean closed = false;
        try {
            ReqWriter reqWriter = newReqWriter(pRequest);
            writeRequest(reqWriter);
            InputStream istream = getInputStream();
            if (isResponseGzipCompressed(config)) {
                istream = new GZIPInputStream(istream);
            }
            Object result = readResponse(config, istream);
            closed = true;
            close();
            return result;
        } catch (IOException e) {
            throw new XmlRpcException("Failed to read server's response: "
                    + e.getMessage(), e);
        } catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof XmlRpcException) {
                throw (XmlRpcException) ex;
            }
            throw new XmlRpcException("Failed to generate request: "
                    + e.getMessage(), e);
        } finally {
            if (!closed) {
                try {
                    close();
                } catch (Throwable ignore) {
                }
            }
        }
    }

    protected Object readResponse(XmlRpcStreamRequestConfig pConfig, InputStream pStream) throws XmlRpcException {
        if(0==0){
        System.out.println("===================Читаем ответ");
         printStream(pStream);
        System.out.println("===================Конец ответа");
        }
        InputSource isource = new InputSource(pStream);
        XMLReader xr = newXMLReader();
        XmlRpcResponseParser xp;
        try {
            xp = new XmlRpcResponseParser(pConfig, getClient().getTypeFactory());
            xr.setContentHandler(xp);
            xr.parse(isource);
        } catch (SAXException e) {
            throw new XmlRpcClientException("Failed to parse server's response1: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new XmlRpcClientException("Failed to read server's response2: " + e.getMessage(), e);
        }
        if (xp.isSuccess()) {
            return xp.getResult();
        }
        Throwable t = xp.getErrorCause();
        if (t == null) {
            throw new XmlRpcException(xp.getErrorCode(), xp.getErrorMessage());
        }
        if (t instanceof XmlRpcException) {
            throw (XmlRpcException) t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        throw new XmlRpcException(xp.getErrorCode(), xp.getErrorMessage(), t);
    }

    public void printStream(InputStream pStream){
         BufferedReader in = new BufferedReader(new InputStreamReader(pStream));
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
        }
    }
    
}
