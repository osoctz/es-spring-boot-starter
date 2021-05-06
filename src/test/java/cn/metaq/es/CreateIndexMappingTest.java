package cn.metaq.es;

import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import javax.annotation.Resource;

import java.io.IOException;

import static cn.metaq.es.constants.IndexType.DOC;

/**
 * @author zantang
 * @version 1.0
 * @description TODO
 * @date 2021/5/6 5:48 下午
 */
@Log4j2
public class CreateIndexMappingTest {

    @Resource
    private Client client;

    public void testCreateMapping() throws IOException {

        String indexName = "testIndexName";
        IndicesExistsResponse response = client.admin().indices().exists(
                new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();

        if (response.isExists()) {
            log.info("索引" + indexName + "已经存在");
            return;
        }
        //创建索引
        client.admin().indices().prepareCreate(indexName)
                .setSettings(Settings.builder().put("refresh_interval","-1"))//关闭索引自动刷新
                .execute().actionGet();
        //配置mapping
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(DOC.getValue())
                .startObject("properties")
                //标题
                .startObject("title").field("type", "text")
                .field("productName", "ik_smart")
                .field("search_analyzer", "ik_max_word").endObject()
                .endObject()
                .endObject()
                .endObject();

        PutMappingRequest mappingRequest = Requests.putMappingRequest(indexName).type(DOC.getValue()).source(mapping);
        client.admin().indices().putMapping(mappingRequest).actionGet();

    }
}
