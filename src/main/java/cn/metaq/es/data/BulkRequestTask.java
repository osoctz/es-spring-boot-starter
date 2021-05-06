package cn.metaq.es.data;

import cn.metaq.es.util.IdUtils;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static cn.metaq.es.constants.IndexType.DOC;


@Log4j2
public class BulkRequestTask<T> implements Runnable {

    private Client client;

    private String index;

    private List<T> data;

    private RowMapper<T> rowMapper;

    private int start;
    private int end;

    public BulkRequestTask(Client client, String index, List<T> data, RowMapper<T> rowMapper, int start, int end) {
        this.client = client;
        this.index = index;
        this.data = data;
        this.rowMapper = rowMapper;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {

        BulkRequestBuilder builder = client.prepareBulk();

        if (!CollectionUtils.isEmpty(data)) {

            for (int i = start; i < end; i++) {

                Map<String, Object> row = null;
                try {
                    row = rowMapper.mapRow(data.get(i));
                    builder.add(client.prepareIndex(index, DOC.getValue(), IdUtils.objectId()).setSource(row));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            BulkResponse bulkResponse = builder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                log.error("bulk failure,message={}", bulkResponse.buildFailureMessage());
            }

            client.admin().indices().prepareRefresh(index).get();
        }
    }
}
