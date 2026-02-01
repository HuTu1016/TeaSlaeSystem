package org.example.teasystem.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.example.teasystem.repository")
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.uris}")
    private String uris;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        try {
            // 1. 使用 java.net.URI 正确解析地址（支持 http:// 和 https://）
            java.net.URI uri = new java.net.URI(uris);
            String scheme = uri.getScheme(); // http 或 https
            String host = uri.getHost();
            int port = uri.getPort();

            // 2. 配置账号密码
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));

            RestClientBuilder builder;

            if ("https".equalsIgnoreCase(scheme)) {
                // 3. HTTPS协议：配置 SSLContext 忽略证书校验
                SSLContext sslContext = new SSLContextBuilder()
                        .loadTrustMaterial(null, (x509Certificates, s) -> true)
                        .build();

                builder = RestClient.builder(new HttpHost(host, port, "https"))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setSSLContext(sslContext)
                                .setSSLHostnameVerifier((hostname, session) -> true) // 忽略主机名校验
                                .setDefaultCredentialsProvider(credentialsProvider));
            } else {
                // 4. HTTP协议：无需SSL配置
                builder = RestClient.builder(new HttpHost(host, port, "http"))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider));
            }

            return new RestHighLevelClient(builder);
        } catch (Exception e) {
            throw new RuntimeException("ES客户端初始化失败", e);
        }
    }
}