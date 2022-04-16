package com.krio.kintone.jdbc.partner;

import java.util.ArrayList;


/**
 * グローバル結果取得
 */
public class DescribeGlobalResult {

    ArrayList<DescribeGlobalSObjectResult> describeGlobalSObjectResult = new ArrayList<>();

    /**
     * @return
     */
    public ArrayList<DescribeGlobalSObjectResult> getSobjects() {
        return describeGlobalSObjectResult;
    }

    /**
     * @param dr
     */
    public void setSobjects(DescribeGlobalSObjectResult dr) {
        describeGlobalSObjectResult.add(dr);

    }
}
