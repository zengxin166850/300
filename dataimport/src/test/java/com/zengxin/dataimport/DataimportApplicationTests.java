package com.zengxin.dataimport;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferFastJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DataimportApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    void dataImport() {
        BulkRequest bulkRequest = new BulkRequest();
        List<Object> jsonObjectList = new ArrayList<>();
        try (InputStream stream = DataimportApplicationTests.class
                .getClassLoader().getResourceAsStream("300.json")) {
            JsonSurfer surfer = JsonSurferFastJson.INSTANCE;
            surfer.configBuilder().bind("$[*]", (value, context) -> {
                jsonObjectList.add(value);
            }).buildAndSurf(stream);
            jsonObjectList.forEach(json -> {
                Map<String, ?> jsonObject = (Map<String, Object>) json;
                IndexRequest indexRequest = new IndexRequest("tangshi300");
                indexRequest.id(String.valueOf(jsonObject.get("id")));
                indexRequest.source(jsonObject);
                bulkRequest.add(indexRequest);
            });
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            String failureMessage = bulkResponse.buildFailureMessage();
            System.out.println(failureMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    @Test
//    void dataImport1() {
//
//        try (InputStream stream = DataimportApplicationTests.class
//                .getClassLoader().getResourceAsStream("300.json")) {
//            JsonSurfer surfer = JsonSurferJackson.INSTANCE;
//            Collector collector = surfer.collector(stream);
//            ValueBox<String> box1 = collector.collectOne("$..author", String.class);
//            ValueBox<String> box2 = collector.collectOne("$..type", String.class);
//            ValueBox<Collection<Object>> box3 = collector.collectAll("$..type");
//            collector.exec(); // make sure exec() invoked before getting value from boxes
//            String s1 = box1.get();
//            System.out.println(s1);
//            String s = box2.get();
//            System.out.println(s);
//            Collection<Object> objects = box3.get();
//            System.out.println(objects.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void dataImport2() {
//
//        try (InputStream stream = DataimportApplicationTests.class
//                .getClassLoader().getResourceAsStream("300.json")) {
//            JsonSurfer surfer = JsonSurferJackson.INSTANCE;
//            SurfingConfiguration config = surfer.configBuilder().bind("$[*]",
//                    (value, context) -> System.out.println(value)).build();
//            surfer.surf(stream, config);
//            surfer.surf(stream, config);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
