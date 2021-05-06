package cn.metaq.es.config;

import cn.metaq.es.data.ElasticsearchTemplate;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchConfiguration {

    @Value("${es.cluster-name}")
    private String cluster_name;

    @Value("${es.hosts}")
    private String hosts;

    @Value("${es.port}")
    private Integer port;

    @Value("${es.batch-num}")
    private Integer batchNum;

    @Bean
    public Client client() throws UnknownHostException {

        Settings settings = Settings.builder()
                //指定集群名称
                .put("cluster.name", cluster_name)
                //探测集群中机器状态-关闭
                .put("client.transport.sniff", false).build();

        String[] host_array = hosts.split(",");

        TransportAddress[] transportAddresses = new TransportAddress[host_array.length];

        for (int i = 0; i < host_array.length; i++) {
            transportAddresses[i] = new TransportAddress(InetAddress.getByName(host_array[i]), port);
        }

        return new PreBuiltTransportClient(settings).addTransportAddresses(transportAddresses);
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(ThreadPoolTaskExecutor executor) throws UnknownHostException {

        return new ElasticsearchTemplate(client(),batchNum,executor);
    }
}
