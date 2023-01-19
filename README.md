# README

This application performs a test about various [TSID](https://github.com/f4b6a3/tsid-creator) factory strategies.

The tests consist in the generation of TSIDs by the given factory concurrently and analyzing how many duplicated values were generated and the generation rate.

See the blog article about ["How to not use TSID factories"](https://fillumina.wordpress.com/2023/01/19/how-to-not-use-tsid-factories/).

This is the result from one test execution on a host with a 4-core, 8-threads CPU:

```
Test 1: Creates a new default TSID factory on each thread
 i -> TsidFactory.newInstance1024()
 duplicates: 46526, op/ms: 25641.0, sequential: true
 duplication present because of not enough random bits available

Test 2: Shares the same default TSID factory on each thread
 i -> sharedInstance1024
 duplicates: 0, op/ms: 5416.0, sequential: true
 slow because of contention accessing the TSID generator

Test 3: Creates a new node TSID factory on each thread with the same node-id
 i -> TsidFactory.newInstance1024(0)
 duplicates: 14433272, op/ms: 28571.0, sequential: true
 duplications because generators use the same node-id

Test 4: Shares the same node TSID factory on each thread
 i -> sharedInsstance1024Node0
 duplicates: 0, op/ms: 7601.0, sequential: true
 slow because of contention accessing the TSID generator

Test 5: Use a different TSID factory on each thread with a different node-id
 i -> TsidFactory.newInstance1024(i)
 duplicates: 0, op/ms: 29907.0, sequential: true
 fast because node-ids are different so no overlapping

Test 6: Use a new thread local random TSID factory on each thread
 i -> factoryCreator()
 duplicates: 92635, op/ms: 20619.0, sequential: true
 duplication present because of not enough random bits available

Test 7: Shares the same thread local random TSID factory on each thread
 i -> sharedFactory
 duplicates: 0, op/ms: 5885.0, sequential: true
 slow because of contention accessing the TSID generator
 ```
