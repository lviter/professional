## 复杂统计示例

POST /vts_finance_insurance_policy/_search

```json
{
  "query": {
    "bool": {
      "filter": [
        {
          "bool": {
            "must": [
              {
                "bool": {
                  "must": [
                    {
                      "term": {
                        "enabledFlag": {
                          "value": "1",
                          "boost": 1.0
                        }
                      }
                    },
                    {
                      "term": {
                        "tmsBatchNumber": {
                          "value": "PC24041600006",
                          "boost": 1.0
                        }
                      }
                    },
                    {
                      "range": {
                        "bidTime": {
                          "from": null,
                          "to": "1715737414000",
                          "include_lower": true,
                          "include_upper": false,
                          "boost": 1.0
                        }
                      }
                    },
                    {
                      "bool": {
                        "must_not": [
                          {
                            "term": {
                              "bidTime": {
                                "value": "-62167420800000",
                                "boost": 1.0
                              }
                            }
                          }
                        ],
                        "adjust_pure_negative": true,
                        "boost": 1.0
                      }
                    }
                  ],
                  "adjust_pure_negative": true,
                  "boost": 1.0
                }
              }
            ],
            "adjust_pure_negative": true,
            "boost": 1.0
          }
        }
      ],
      "adjust_pure_negative": true,
      "boost": 1.0
    }
  },
  "_source": {
    "includes": [],
    "excludes": []
  },
  "aggregations": {
    "totalInsureAmount": {
      "sum": {
        "script": {
          "source": "Math.abs(doc['premiumAmount'].value)",
          "lang": "painless"
        }
      }
    },
    "totalDeductionAmount": {
      "sum": {
        "script": {
          "source": "Math.abs(doc['deductionAmount'].value)",
          "lang": "painless"
        }
      }
    },
    "unInsuredNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "policyStatus": [
                  "10"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "insuredNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "policyStatus": [
                  "20"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "insureCancelNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "policyStatus": [
                  "30"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "insureFailNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "policyStatus": [
                  "40"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "insureCancelFailNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "policyStatus": [
                  "50"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "pendingPusNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "tmsBillStatus": [
                  "100"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "tmsApproveNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "tmsBillStatus": [
                  "200"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "tmsConfirmNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "tmsBillStatus": [
                  "300"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    },
    "tmsPaiNum": {
      "filter": {
        "bool": {
          "must": [
            {
              "terms": {
                "tmsBillStatus": [
                  "400"
                ],
                "boost": 1.0
              }
            },
            {
              "terms": {
                "enabledFlag": [
                  "1"
                ],
                "boost": 1.0
              }
            }
          ],
          "adjust_pure_negative": true,
          "boost": 1.0
        }
      },
      "aggregations": {
        "insuranceOrderNumber": {
          "value_count": {
            "field": "id"
          }
        }
      }
    }
  }
}
```

## java代码示例

```Java
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collections;

/**
 * @Description:
 * @author: 582401
 * @date: 2022/4/1
 */
public class InsurancePolicyPreSearch extends BaseGSPreSearch {

    /**
     * 待投保数量
     */
    public static final String UN_INSURED_NUM = "unInsuredNum";


    /**
     * 已投保数量
     */
    public static final String INSURED_NUM = "insuredNum";

    /**
     * 取消投保数量
     */
    public static final String INSURED_CANCEL_NUM = "insureCancelNum";

    /**
     * 投保失败数量
     */
    public static final String INSURE_FAIL_NUM = "insureFailNum";

    /**
     * 退保失败数量
     */
    public static final String INSURE_CANCEL_FAIL_NUM = "insureCancelFailNum";

    /**
     * 待推送TMS数量
     */
    public static final String PENDING_PUS_NUM = "pendingPusNum";

    /**
     * 待对账数量
     */
    public static final String TMS_APPROVE_NUM = "tmsApproveNum";

    /**
     * 已对账数量
     */
    public static final String TMS_CONFIRM_NUM = "tmsConfirmNum";

    /**
     * 已支付数量
     */
    public static final String TMS_PAI_NUM = "tmsPaiNum";

    /**
     * 订单数量
     */
    public static final String ORDER_NUMBER = "insuranceOrderNumber";


    /**
     * 保单状态分组
     */
    public static final String POLICY_STATUS_GROUP = "policyStatusGroup";

    /**
     * 投保总金额
     */
    public static final String TOTAL_INSURE_AMOUNT = "totalInsureAmount";

    /**
     * 扣款总金额
     */
    public static final String TOTAL_DEDUCTION_AMOUNT = "totalDeductionAmount";

    @Override
    public void buildAggregation(SearchSourceBuilder sourceBuilder) {

        //待投保订单数
        QueryBuilder unInsuredNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("policyStatus", "10"))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder unInsuredNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.UN_INSURED_NUM, unInsuredNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //已投保订单数
        QueryBuilder insuredNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("policyStatus", "20"))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder insuredNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.INSURED_NUM, insuredNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //取消投保数量
        QueryBuilder insureCancelNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("policyStatus", "30"))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder insureCancelNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.INSURED_CANCEL_NUM, insureCancelNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //投保失败数量insureFailNum
        QueryBuilder insureFailNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("policyStatus", "40"))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder insureFailNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.INSURE_FAIL_NUM, insureFailNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //退保失败数量insureCancelFailNum
        QueryBuilder insureCancelFailNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("policyStatus", "50"))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder insureCancelFailNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.INSURE_CANCEL_FAIL_NUM, insureCancelFailNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //待推送TMS数量pendingPusNum
        QueryBuilder pendingPusNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("tmsBillStatus", TmsBillStatusEnum.PENDING_PUSH.getCode()))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder pendingPusNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.PENDING_PUS_NUM, pendingPusNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //待对账数量tmsApproveNum
        QueryBuilder tmsApproveNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("tmsBillStatus", TmsBillStatusEnum.TMS_APPROVE.getCode()))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder tmsApproveNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.TMS_APPROVE_NUM, tmsApproveNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //已对账数量tmsConfirmNum
        QueryBuilder tmsConfirmNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("tmsBillStatus", TmsBillStatusEnum.TMS_CONFIRM.getCode()))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder tmsConfirmNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.TMS_CONFIRM_NUM, tmsConfirmNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));

        //已支付数量tmsPaiNum
        QueryBuilder tmsPaiNumQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("tmsBillStatus", TmsBillStatusEnum.TMS_PAID.getCode()))
                .must(QueryBuilders.termsQuery("enabledFlag", EnabledFlagEnum.YES.getCode()));
        AggregationBuilder tmsPaiNumBuilder = AggregationBuilders.filter(InsurancePolicyPreSearch.TMS_PAI_NUM, tmsPaiNumQuery)
                .subAggregation(AggregationBuilders.count(InsurancePolicyPreSearch.ORDER_NUMBER).field("id"));
        sourceBuilder
                .aggregation(AggregationBuilders.sum(InsurancePolicyPreSearch.TOTAL_INSURE_AMOUNT)
                        .script(new Script(ScriptType.INLINE, "painless", "Math.abs(doc['premiumAmount'].value)", Collections.emptyMap()))
                        )
                .aggregation(AggregationBuilders.sum(InsurancePolicyPreSearch.TOTAL_DEDUCTION_AMOUNT)
                        .script(new Script(ScriptType.INLINE, "painless", "Math.abs(doc['deductionAmount'].value)", Collections.emptyMap()))
                        )
                .aggregation(unInsuredNumBuilder)
                .aggregation(insuredNumBuilder)
                .aggregation(insureCancelNumBuilder)
                .aggregation(insureFailNumBuilder)
                .aggregation(insureCancelFailNumBuilder)
                .aggregation(pendingPusNumBuilder)
                .aggregation(tmsApproveNumBuilder)
                .aggregation(tmsConfirmNumBuilder)
                .aggregation(tmsPaiNumBuilder);
    }
}
```

查询取值代码

```Java
 public InsurancePolicyStatisticsDTO statisticsInsurancePolicy(InsurancePolicyBO insurancePolicyBO) {
        Pagination<InsurancePolicy> pagination = (Pagination<InsurancePolicy>) esPreHandleUtils.newPagination(insurancePolicyBO);
        //组装es通用查询条件
        String authCode = KeyValueConfigClient.getValue(VtsBaseConfigConstant.INSURANCE_STATISTICS_AUTH_CODE, EsConstant.INSURANCE_POLICY_AUTH_CODE);
        SimpleQuery simpleQuery = getSimpleQuery(pagination, authCode);
        SearchResponse searchResponse = esPreHandleUtils.getEsGroupStatisticsClient().searchResult(simpleQuery, new InsurancePolicyPreSearch());
        InsurancePolicyStatisticsDTO insurancePolicyStatisticsDTO = new InsurancePolicyStatisticsDTO();

        //待投保数量
        ParsedFilter unInsuredNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.UN_INSURED_NUM);
        insurancePolicyStatisticsDTO.setUnInsuredNum(Objects.nonNull(unInsuredNum) ? Integer.parseInt(String.valueOf(unInsuredNum.getDocCount())) : 0);

        //已投保数量
        ParsedFilter insuredNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.INSURED_NUM);
        insurancePolicyStatisticsDTO.setInsuredNum(Objects.nonNull(insuredNum) ? Integer.parseInt(String.valueOf(insuredNum.getDocCount())) : 0);

        //取消投保数量
        ParsedFilter insureCancelNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.INSURED_CANCEL_NUM);
        insurancePolicyStatisticsDTO.setInsureCancelNum(Objects.nonNull(insureCancelNum) ? Integer.parseInt(String.valueOf(insureCancelNum.getDocCount())) : 0);

        //投保失败数量
        ParsedFilter insureFailNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.INSURE_FAIL_NUM);
        insurancePolicyStatisticsDTO.setInsureFailNum(Objects.nonNull(insureFailNum) ? Integer.parseInt(String.valueOf(insureFailNum.getDocCount())) : 0);

        //退保失败数量
        ParsedFilter insureCancelFailNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.INSURE_CANCEL_FAIL_NUM);
        insurancePolicyStatisticsDTO.setInsureCancelFailNum(Objects.nonNull(insureCancelFailNum) ? Integer.parseInt(String.valueOf(insureCancelFailNum.getDocCount())) : 0);

        //待推送TMS数量
        ParsedFilter pendingPusNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.PENDING_PUS_NUM);
        insurancePolicyStatisticsDTO.setPendingPusNum(Objects.nonNull(pendingPusNum) ? Integer.parseInt(String.valueOf(pendingPusNum.getDocCount())) : 0);

        //待对账数量
        ParsedFilter tmsApproveNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.TMS_APPROVE_NUM);
        insurancePolicyStatisticsDTO.setTmsApproveNum(Objects.nonNull(tmsApproveNum) ? Integer.parseInt(String.valueOf(tmsApproveNum.getDocCount())) : 0);

        //已对账数量
        ParsedFilter tmsConfirmNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.TMS_CONFIRM_NUM);
        insurancePolicyStatisticsDTO.setTmsConfirmNum(Objects.nonNull(tmsConfirmNum) ? Integer.parseInt(String.valueOf(tmsConfirmNum.getDocCount())) : 0);

        //已支付数量
        ParsedFilter tmsPaiNum = searchResponse.getAggregations().get(InsurancePolicyPreSearch.TMS_PAI_NUM);
        insurancePolicyStatisticsDTO.setTmsPaiNum(Objects.nonNull(tmsPaiNum) ? Integer.parseInt(String.valueOf(tmsPaiNum.getDocCount())) : 0);

        //投保总金额
        ParsedSum totalInsureAmount = searchResponse.getAggregations().get(InsurancePolicyPreSearch.TOTAL_INSURE_AMOUNT);
        BigDecimal policyTotalAmount = (Objects.nonNull(totalInsureAmount)) ?
                BigDecimal.valueOf(Math.abs(totalInsureAmount.getValue())).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        insurancePolicyStatisticsDTO.setPolicyTotalAmount(policyTotalAmount);

        //扣款总金额
        ParsedSum totalDeductionAmount = searchResponse.getAggregations().get(InsurancePolicyPreSearch.TOTAL_DEDUCTION_AMOUNT);
        BigDecimal deductionTotalAmount = (Objects.nonNull(totalDeductionAmount)) ?
                BigDecimal.valueOf(Math.abs(totalDeductionAmount.getValue())).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        insurancePolicyStatisticsDTO.setDeductionTotalAmount(deductionTotalAmount);
        return insurancePolicyStatisticsDTO;
    }
```

## 核心点

```Java
sourceBuilder
    .aggregation(AggregationBuilders.sum(InsurancePolicyPreSearch.TOTAL_INSURE_AMOUNT)
        .script(new Script(ScriptType.INLINE, "painless", "Math.abs(doc['premiumAmount'].value)", Collections.emptyMap()))
    )
    .aggregation(AggregationBuilders.sum(InsurancePolicyPreSearch.TOTAL_DEDUCTION_AMOUNT)
        .script(new Script(ScriptType.INLINE, "painless", "Math.abs(doc['deductionAmount'].value)", Collections.emptyMap()))
    );
```

字段未double类型，用脚本统计绝对值的聚合值