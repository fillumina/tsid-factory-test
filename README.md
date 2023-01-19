# README

This application performs a test about various [TSID](https://github.com/f4b6a3/tsid-creator) factory strategies.

The tests consist in the generation of TSIDs by the given factory concurrently and analyzing how many duplicated values were generated and the generation rate.

See the blog article about ["How to not use TSID factories"](https://fillumina.wordpress.com/2023/01/19/how-to-not-use-tsid-factories/).

This is the result from one test execution on a host with a 4-core, 8-threads CPU:

```
Test 1: Creates a new default TSID factory for each thread
 i -> TsidFactory.newInstance1024()
 duplicates: 21333, op/ms: 27444.0, sequential: true
 duplication present because of not enough random bits available

Test 2: Shares the same default TSID factory for each thread
 i -> sharedInstance1024
 duplicates: 0, op/ms: 5300.0, sequential: true
 slow because of contention accessing the TSID generator

Test 3: Creates a new node TSID factory for each thread with the same node id
 i -> TsidFactory.newInstance1024(0)
 duplicates: 14734723, op/ms: 37915.0, sequential: true
 duplications because generators use the same node id

Test 4: Shares the same node TSID factory for each thread
 i -> sharedInsstance1024Node0
 duplicates: 0, op/ms: 5919.0, sequential: true
 slow because of contention accessing the TSID generator

Test 5: Use a different TSID factory for each thread
 i -> TsidFactory.newInstance1024(i)
 duplicates: 0, op/ms: 33403.0, sequential: true
 fast because node ids are different so no overlapping

Test 6: Use a new thread local random TSID factory for each thread
 i -> factoryCreator()
 duplicates: 50338, op/ms: 57554.0, sequential: true
 duplication present because of not enough random bits available

Test 7: Shares the same thread local random TSID factory for each thread
 i -> sharedFactory
 duplicates: 0, op/ms: 5652.0, sequential: true
 slow because of contention accessing the TSID generator
```
