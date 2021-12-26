package com.mindspore.ide.toolkit.search;

import com.mindspore.ide.toolkit.common.config.DocSearchConfig;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import com.mindspore.ide.toolkit.search.constant.Constants;
import com.mindspore.ide.toolkit.search.entity.DocumentResultModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public enum SearchService {
    INSTANCE;

    public DocumentResultModel requestSearchResult(String searchText){
        DocumentResultModel doc = null;
        Map<String,String> contentMap = new HashMap<>();
        contentMap.put(Constants.HTTP_REQUEST_SEARCHTEXT,searchText);
        try {
            HttpResponse response = HttpUtils.doPost(DocSearchConfig.get().getSearchApi(),getHeader(),contentMap);
            if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
                String bodyStr = EntityUtils.toString(response.getEntity(),Constants.CHARSET_UTF_8);
                if(!bodyStr.isEmpty()){
                    doc = GsonUtils.INSTANCE.getGson().fromJson(bodyStr,DocumentResultModel.class);
                    log.info("request success");
                }else{
                    log.info("request success , but no data");
                }
            }
        }catch (IOException e){
            log.error("request error3", e);
        }
        return doc;
    }
    private HashMap<String,String> getHeader(){
        HashMap<String,String> header = new HashMap<>();
        header.put("Accept","application/json");
        header.put("Connection","keep-alive");
        return header;
    }
}
