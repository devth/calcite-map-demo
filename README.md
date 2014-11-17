# calcite-map-demo

## Run the Test

```shell
mvn -Dtest=TestMapCalcite -Dsurefire.useFile=false test
```

## Sample test output

```
Discovery starting.
Discovery completed in 314 milliseconds.
Run starting. Expected test count is: 3
TestMapCalcite:
{name=foo, address={city=seattle, state=wa, coords={lat=47.609722, long=-122.333056}}}
{name=bar, address={city=denver, state=co}}
{name=baz, address={city=chicago, state=il}}
{name=qux, address={city=san francisco, state=ca}}
{name=norf, address={city=new york city, state=ny}}
- Select everything
foo,seattle
bar,denver
baz,chicago
qux,san francisco
norf,new york city
- Select a few things
5
- Aggregate
Run completed in 1 second, 248 milliseconds.
Total number of tests run: 3
Suites: completed 2, aborted 0
Tests: succeeded 3, failed 0, canceled 0, ignored 0, pending 0
All tests passed.
```
