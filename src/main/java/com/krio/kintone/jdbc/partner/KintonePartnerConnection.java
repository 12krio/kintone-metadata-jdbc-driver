package com.krio.kintone.jdbc.partner;

import com.kintone.client.AppClient;
import com.kintone.client.KintoneClient;
import com.kintone.client.api.app.GetAppsRequest;
import com.kintone.client.api.app.GetAppsResponseBody;
import com.kintone.client.api.app.GetFormFieldsRequest;
import com.kintone.client.api.app.GetFormFieldsResponseBody;
import com.kintone.client.model.app.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * kintoneClientLib Wrapper
 * https://github.com/kintone/kintone-java-client
 */
public class KintonePartnerConnection {
    KintoneClient client = null;
    AppClient appClient = null;

    public KintonePartnerConnection(KintoneClient kintoneClient) {
        client = kintoneClient;
        appClient = client.app();
    }

    public void closeKintoneClient() throws IOException {
        client.close();
    }

    public List<App> getApps() {
        //TODO Loop
        Long limmit = 100L;
        Long offset = 0L;
//        GetAppsResponseBody res = appClient.getApps(new GetAppsRequest());
        List<App> resultAppsList = new ArrayList<>();
//        //loop STAET
        for(int i = 0; i< 100;i++){
            GetAppsResponseBody res = appClient.getApps(new GetAppsRequest().setOffset(offset));
            int applist_size = res.getApps().size();
            if(applist_size == 0){
                break;
            }else{
                resultAppsList.addAll(res.getApps());
                //limmit = limmit + 100L;
                offset = offset + limmit;
            }
        }
        //loop END
        //List<App> resultAppsList = res.getApps();
        return resultAppsList;
    }

    public GetFormFieldsResponseBody getAppColumns(Long appId) {
        //HashMap<Long, GetFormFieldsResponseBody> resultFieldsMap = new HashMap<Long, GetFormFieldsResponseBody>();
        GetFormFieldsResponseBody res = appClient.getFormFields(new GetFormFieldsRequest().setApp(appId));
        return res;
    }

    public DescribeSObjectResult[] describeSObjects(String[] batch) {

        DescribeSObjectResult describeSObjectResult = new DescribeSObjectResult();
        ArrayList<DescribeSObjectResult> list = new ArrayList<>();
        list.add(describeSObjectResult);
        return (DescribeSObjectResult[]) list.toArray();
    }

    public KintoneClient debug() {
        return client;
    }

    public DescribeGlobalResult describeGlobal() throws KintoneConnectionException {
        client.app();
        DescribeGlobalResult dgr = new DescribeGlobalResult();

        DescribeGlobalSObjectResult dgsor = new DescribeGlobalSObjectResult();
        dgr.setSobjects(dgsor);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new KintoneConnectionException(e);
        }
        return dgr;
    }

}
