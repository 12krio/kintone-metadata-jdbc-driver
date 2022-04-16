package com.krio.kintone.jdbc.partner;

import com.kintone.client.KintoneClient;
import com.kintone.client.KintoneClientBuilder;

/**
 *  kintoneConnect作成
 */
public class Connector {
    /**
     * @param partnerConfig
     * @return
     * @throws KintoneConnectionException
     */
    public static KintonePartnerConnection newConnection(KintoneConnectorConfig partnerConfig) throws KintoneConnectionException {
        KintoneClient client = null;
        try {
            //ToDo Proxy
            client = KintoneClientBuilder
                    // アクセス対象の kintone URL を設定
                    .create(partnerConfig.getAuthEndpoint())
                    // 認証用ユーザー名とパスワードを設定
                    .authByPassword(partnerConfig.getUsername(), partnerConfig.getPassword()).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new KintoneConnectionException(e);
        }
        return new KintonePartnerConnection(client);
    }
}