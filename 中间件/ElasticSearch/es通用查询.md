## 精确查找数据

GET ecs_vts_finance_payable_bill_new/_search

```json
{
  "query": {
    "term": {
      "tradeNumber": "JY2204-30032801"
    }
  }
}

```

## 判断某个字段是否为空

GET /vts_finance_fee_confirmed_order/_search

```json

{
  "query": {
    "bool": {
      "must_not": {
        "exists": {
          "field": "transportOrder"
        }
      }
    }
  }
}
```

## 复杂查找

POST ecs_vts_finance_fee_confirmed_order/_search

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
                      "bool": {
                        "should": [
                          {
                            "bool": {
                              "must_not": [
                                {
                                  "terms": {
                                    "transportOrder.transportCapacityType": [
                                      "4"
                                    ],
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
                    {
                      "bool": {
                        "should": [
                          {
                            "bool": {
                              "must_not": [
                                {
                                  "terms": {
                                    "payableBillDetail.orderSource": [
                                      "SHEIN"
                                    ],
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
                    {
                      "term": {
                        "approveStatus": {
                          "value": "100",
                          "boost": 1.0
                        }
                      }
                    },
                    {
                      "terms": {
                        "businessType": [
                          "1"
                        ],
                        "boost": 1.0
                      }
                    },
                    {
                      "bool": {
                        "should": [
                          {
                            "terms": {
                              "transportOrder.orderStatus": [
                                "50"
                              ],
                              "boost": 1.0
                            }
                          },
                          {
                            "bool": {
                              "must": [
                                {
                                  "terms": {
                                    "transportOrder.orderStatus": [
                                      "40"
                                    ],
                                    "boost": 1.0
                                  }
                                },
                                {
                                  "term": {
                                    "transportOrder.receiptFlag": {
                                      "value": "10",
                                      "boost": 1.0
                                    }
                                  }
                                },
                                {
                                  "range": {
                                    "transportOrder.signReportDate": {
                                      "to": "1705852799000",
                                      "include_lower": true,
                                      "include_upper": true,
                                      "boost": 1.0
                                    }
                                  }
                                },
                                {
                                  "bool": {
                                    "must_not": [
                                      {
                                        "term": {
                                          "transportOrder.signReportDate": {
                                            "value": "-62167420800000",
                                            "boost": 1.0
                                          }
                                        }
                                      }
                                    ],
                                    "adjust_pure_negative": true,
                                    "boost": 1.0
                                  }
                                },
                                {
                                  "term": {
                                    "transportOrder.expressStatus": {
                                      "value": "40",
                                      "boost": 1.0
                                    }
                                  }
                                }
                              ],
                              "adjust_pure_negative": true,
                              "boost": 1.0
                            }
                          },
                          {
                            "bool": {
                              "must": [
                                {
                                  "terms": {
                                    "transportOrder.orderStatus": [
                                      "40"
                                    ],
                                    "boost": 1.0
                                  }
                                },
                                {
                                  "term": {
                                    "transportOrder.receiptFlag": {
                                      "value": "10",
                                      "boost": 1.0
                                    }
                                  }
                                },
                                {
                                  "term": {
                                    "transportOrder.expressStatus": {
                                      "value": "60",
                                      "boost": 1.0
                                    }
                                  }
                                }
                              ],
                              "adjust_pure_negative": true,
                              "boost": 1.0
                            }
                          },
                          {
                            "bool": {
                              "must": [
                                {
                                  "terms": {
                                    "transportOrder.orderStatus": [
                                      "40"
                                    ],
                                    "boost": 1.0
                                  }
                                },
                                {
                                  "range": {
                                    "transportOrder.signReportDate": {
                                      "to": "1705852799000",
                                      "include_lower": true,
                                      "include_upper": true,
                                      "boost": 1.0
                                    }
                                  }
                                },
                                {
                                  "bool": {
                                    "must_not": [
                                      {
                                        "term": {
                                          "transportOrder.signReportDate": {
                                            "value": "-62167420800000",
                                            "boost": 1.0
                                          }
                                        }
                                      }
                                    ],
                                    "adjust_pure_negative": true,
                                    "boost": 1.0
                                  }
                                },
                                {
                                  "terms": {
                                    "transportOrder.receiptFlag": [
                                      "20"
                                    ],
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
}
```

## 新增字段

PUT vts_finance_payable_bill/_mapping/ecs_vts_finance_payable_bill

```json
{
  "properties": {
    "placeOrderBy": {
      "fields": {
        "raw": {
          "null_value": "",
          "type": "keyword"
        }
      },
      "type": "text"
    }
  }
}

```

## 新建索引

PUT vts_finance_fee_confirmed_order

```json
{
  "aliases": {
    "ecs_vts_finance_fee_confirmed_order": {}
  },
  "settings": {
    "index": {
      "refresh_interval": "1s",
      "max_inner_result_window": "100000",
      "max_result_window": "100000",
      "number_of_replicas": "2",
      "number_of_shards": "5"
    }
  },
  "mappings": {
    "ecs_vts_finance_fee_confirmed_order": {
      "dynamic": "false",
      "properties": {
        "id": {
          "type": "long"
        },
        "tradeNumber": {
          "type": "keyword"
        },
        "demandOrderCode": {
          "type": "keyword"
        },
        "approveStatus": {
          "type": "keyword"
        },
        "frozenStatus": {
          "type": "keyword"
        },
        "payChannel": {
          "type": "keyword"
        },
        "confirmTime": {
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
          "type": "date"
        },
        "confirmUserName": {
          "type": "keyword"
        },
        "userName": {
          "type": "keyword"
        },
        "carModel": {
          "type": "keyword"
        },
        "userMobile": {
          "type": "keyword"
        },
        "userType": {
          "type": "keyword"
        },
        "bidTime": {
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
          "type": "date"
        },
        "bidAmount": {
          "type": "double"
        },
        "carNumber": {
          "type": "keyword"
        },
        "carLength": {
          "type": "keyword"
        },
        "platformCode": {
          "type": "keyword"
        },
        "businessType": {
          "type": "keyword"
        },
        "waybillNumbers": {
          "type": "keyword"
        },
        "enabledFlag": {
          "type": "keyword"
        },
        "payableBillDetail": {
          "properties": {
            "tradeNumber": {
              "type": "keyword"
            },
            "confirmedTimeoutStatus": {
              "type": "integer"
            },
            "confirmedUseTime": {
              "type": "integer"
            },
            "hllAppealStatus": {
              "type": "integer"
            },
            "goodsWeight": {
              "type": "double"
            },
            "goodsVolume": {
              "type": "double"
            },
            "customerRemark": {
              "type": "keyword"
            }
          }
        },
        "orderTagList": {
          "type": "nested",
          "properties": {
            "demandOrderId": {
              "type": "long"
            },
            "demandOrderCode": {
              "type": "keyword"
            },
            "tagCode": {
              "type": "keyword"
            },
            "tagName": {
              "type": "keyword"
            },
            "remark": {
              "type": "keyword"
            }
          }
        },
        "transportOrder": {
          "properties": {
            "id": {
              "type": "long"
            },
            "expressStatus": {
              "type": "keyword"
            },
            "receiptFlag": {
              "type": "keyword"
            },
            "confirmCostFlag": {
              "type": "keyword"
            },
            "demandOrderCode": {
              "type": "keyword"
            },
            "tradeCode": {
              "type": "keyword"
            },
            "costWorkOrderAuditFlag": {
              "type": "keyword"
            },
            "frozenName": {
              "type": "keyword"
            },
            "thirdOrderCode": {
              "type": "keyword"
            },
            "transportCapacityCode": {
              "type": "keyword"
            },
            "transportCapacityType": {
              "type": "keyword"
            },
            "driverName": {
              "type": "keyword"
            },
            "userName": {
              "type": "keyword"
            },
            "winType": {
              "type": "keyword"
            },
            "driverPhone": {
              "type": "keyword"
            },
            "plateNo": {
              "type": "keyword"
            },
            "carLength": {
              "type": "keyword"
            },
            "carType": {
              "type": "keyword"
            },
            "dispatchDriverDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "businessType": {
              "type": "keyword"
            },
            "serviceType": {
              "type": "keyword"
            },
            "startThreeAddress": {
              "type": "keyword"
            },
            "endThreeAddress": {
              "type": "keyword"
            },
            "placeOrderPerson": {
              "type": "keyword"
            },
            "placeOrderTime": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "companyNo": {
              "type": "keyword"
            },
            "loadingTime": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "finishDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "destinationRequireDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "expectedArriveDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "returnDistance": {
              "type": "double"
            },
            "referenceDistance": {
              "type": "double"
            },
            "needCarType": {
              "type": "keyword"
            },
            "waybillCode": {
              "type": "keyword"
            },
            "requireArrivalTime": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "loadService": {
              "type": "keyword"
            },
            "unloadService": {
              "type": "keyword"
            },
            "startDepartmentName": {
              "type": "keyword"
            },
            "endDepartmentName": {
              "type": "keyword"
            },
            "locationDepartmentName": {
              "type": "keyword"
            },
            "needCarLength": {
              "type": "double"
            },
            "insuredMoney": {
              "type": "double"
            },
            "platformType": {
              "type": "keyword"
            },
            "ycContractType": {
              "type": "keyword"
            },
            "ycContractName": {
              "type": "keyword"
            },
            "ycContractId": {
              "type": "keyword"
            },
            "ycDepartmentName": {
              "type": "keyword"
            },
            "ycTaiwanNum": {
              "type": "keyword"
            },
            "signReportDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "orderStatus": {
              "type": "keyword"
            },
            "shipperReceiptStatus": {
              "type": "integer"
            },
            "signDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "exceptionType": {
              "type": "integer"
            },
            "orderRiskFlag": {
              "type": "keyword"
            },
            "orderRiskStatus": {
              "type": "keyword"
            },
            "cancelCause": {
              "type": "keyword"
            },
            "arrivePlateNo": {
              "type": "keyword"
            },
            "orderStatusSub": {
              "type": "keyword"
            },
            "arrivePlateFlag": {
              "type": "keyword"
            },
            "vehicleInspectionStatus": {
              "type": "keyword"
            },
            "pickGoodsFlag": {
              "type": "keyword"
            },
            "takeTrackRate": {
              "type": "double"
            },
            "deliveryTrackRate": {
              "type": "double"
            },
            "score": {
              "type": "double"
            },
            "executeDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "deliveryDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "pickupSignDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "trackRate": {
              "type": "double"
            },
            "cancelDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "cancelOperatorName": {
              "type": "keyword"
            },
            "pickupReportDate": {
              "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis",
              "type": "date"
            },
            "orderRemark": {
              "type": "keyword"
            },
            "warningFollowup": {
              "type": "nested",
              "properties": {
                "id": {
                  "type": "long"
                },
                "demandOrderCode": {
                  "type": "keyword"
                },
                "warningContent": {
                  "type": "keyword"
                },
                "warningContentCode": {
                  "type": "keyword"
                }
              }
            },
            "orderVehicle": {
              "properties": {
                "tradeCode": {
                  "type": "keyword"
                },
                "leakingUsableFlag": {
                  "type": "keyword"
                },
                "pdaLeakingStatus": {
                  "type": "keyword"
                },
                "ponchosAuditStatus": {
                  "type": "keyword"
                },
                "ponchosStatus": {
                  "type": "keyword"
                }
              }
            },
            "extra": {
              "properties": {
                "tradeCode": {
                  "type": "keyword"
                },
                "demandOrderCode": {
                  "type": "keyword"
                },
                "loadServiceType": {
                  "type": "keyword"
                },
                "unloadServiceType": {
                  "type": "keyword"
                },
                "unFrozenByName": {
                  "type": "keyword"
                },
                "projectOrderFlag": {
                  "type": "keyword"
                },
                "projectCode": {
                  "type": "keyword"
                },
                "projectFollower": {
                  "type": "keyword"
                },
                "projectTenderType": {
                  "type": "integer"
                },
                "receiptType": {
                  "type": "keyword"
                },
                "projectLineCode": {
                  "type": "keyword"
                },
                "contractBorrowLineName": {
                  "type": "keyword"
                },
                "pdaOutRangeFlag": {
                  "type": "keyword"
                },
                "appOutRangeFlag": {
                  "type": "keyword"
                },
                "lastRemarkBy": {
                  "type": "keyword"
                },
                "businessAreaName": {
                  "type": "text",
                  "fields": {
                    "raw": {
                      "type": "keyword",
                      "null_value": ""
                    }
                  }
                },
                "expensiveFlag": {
                  "type": "keyword"
                },
                "vehicleInspectionStatus": {
                  "type": "keyword"
                },
                "pdaPlateFakedType": {
                  "type": "keyword"
                },
                "driverPlateFakedType": {
                  "type": "keyword"
                },
                "driverBlacklistFlag": {
                  "type": "keyword"
                },
                "plateBlacklistFlag": {
                  "type": "keyword"
                },
                "carTypeLabel": {
                  "type": "keyword"
                },
                "deliveryTimeoutFlag": {
                  "type": "keyword"
                },
                "arriveDriversNumber": {
                  "type": "integer"
                },
                "requireDriversNumber": {
                  "type": "integer"
                },
                "beforeAssignFlag": {
                  "type": "keyword"
                },
                "contractBorrowFlag": {
                  "type": "keyword"
                }
              }
            }
          }
        }
      }
    }
  }
}

```