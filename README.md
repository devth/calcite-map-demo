# calcite-map-demo

Demonstrates querying nested data with Calcite.

## Run the Test

```shell
mvn -Dtest=TestMapCalcite -Dsurefire.useFile=false test
```

## Open a sqlline shell

```
./sqlline
sqlline> !connect jdbc:calcite:model=src/test/resources/model.json admin admin
0: jdbc:calcite:model=src/test/resources/mode> !tables
+-----------+-------------+------------+------------+---------+----------+------------+-----------+---------------------------+----------------+
| TABLE_CAT | TABLE_SCHEM | TABLE_NAME | TABLE_TYPE | REMARKS | TYPE_CAT | TYPE_SCHEM | TYPE_NAME | SELF_REFERENCING_COL_NAME | REF_GENERATION |
+-----------+-------------+------------+------------+---------+----------+------------+-----------+---------------------------+----------------+
| null      | map_raw     | foo        | TABLE      | null    | null     | null       | null      | null                      | null           |
| null      | metadata    | COLUMNS    | SYSTEM_TABLE | null    | null     | null       | null      | null                      | null           |
| null      | metadata    | TABLES     | SYSTEM_TABLE | null    | null     | null       | null      | null                      | null           |
+-----------+-------------+------------+------------+---------+----------+------------+-----------+---------------------------+----------------+
```

