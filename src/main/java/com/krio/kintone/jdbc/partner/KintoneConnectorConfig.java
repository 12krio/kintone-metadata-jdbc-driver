package com.krio.kintone.jdbc.partner;


/**
 * kintone接続設定情報
 */
public class KintoneConnectorConfig {

    String ntlmDomain;
    String proxyHost;
    int proxyPort;
    String proxyUsername;
    String proxyPassword;
    String username;
    String password;
    String authEndpoint;
    int connectionTimeout;

    public String getNtlmDomain() {
        return ntlmDomain;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthEndpoint() {
        return authEndpoint;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setNtlmDomain(String property) {
        ntlmDomain = property;
    }

    public void setProxy(String host, int port) {
        proxyHost = host;
        proxyPort = port;
    }

    public void setProxyUsername(String property) {
        proxyUsername = property;
    }

    public void setProxyPassword(String property) {
        proxyPassword = property;
    }

    public void setUsername(String un) {
        username = un;
    }

    public void setPassword(String pw) {
        password = pw;
    }

    public void setAuthEndpoint(String url) {
        authEndpoint = url;
    }

    public void setConnectionTimeout(int i) {
        connectionTimeout = i;
    }
}
