# #2. Browsing subtasks of a task
Let's assume you have a log file which has entries containing a subtask id and parent task id.

Log messages

```
2012-01-20 00:00:00 Starting task A, parent task 123
2012-01-20 00:00:05 Starting task B, parent task 123
2012-01-20 00:00:10 Starting task C, parent task 456
2012-01-20 00:01:10 Task A finished
2012-01-20 00:01:15 Starting task D, parent task 123
2012-01-20 00:01:15 This is some log message
2012-01-20 00:03:00 Task C failed
2012-01-20 00:05:00 Starting task C again as it failed earlier
2012-01-20 00:05:30 Task D finished
2012-01-20 00:05:40 Starting task E, parent task 456
2012-01-20 00:06:00 Task C failed
2012-01-20 00:06:00 Task C failed twice, giving up on it.
2012-01-20 00:07:00 Task E finished
2012-01-20 00:08:00 Task B finished
```

If you are interested in debugging a particular task, and want to list of all subtasks of it, CAR can do this easily.

![Dropdown demo with parent task and subtask timeline](/helpcontent/assets/introduction/intro_example_dropdown.png)

Using the dropdown, you can control what is shown in the timeline and work without clutter.