# lambda表达式汇总

## 去重

```Java
List<AffiliationLocationDTO> lists = list.stream().collect(
                            Collectors.collectingAndThen(
                                    Collectors.toCollection(
                                            () -> new TreeSet<>(Comparator.comparing(AffiliationLocationDTO::getCreationDate))
                                    ), ArrayList::new
                            )
                    );
```
