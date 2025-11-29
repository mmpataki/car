# Introduction

__C-A-R__ is an `collaborative` `exploration` and `reporting` application which works on log files and helps people to understand the log files better by

- Allowing users to extract the required information from logs.
- Allowing user to visualize the extracted information in the way they want.

Here is an example (Don't forget to look other cool examples at the end)

### Performance metrics
Let's say there is a file which records free memory available and cache memory in the system (in bytes) for every minute starting from 00:00. It might look something like this

```
2012-01-20 00:00:00 15000000000	2000000000
2012-01-20 00:01:00 15003420000 3000000000
2012-01-20 00:02:00 20000000000 1000000000
2012-01-20 00:03:00 12000000000 4000000000
2012-01-20 00:04:00 12500000000 2000000000
2012-01-20 00:05:00 14000000000 3000000000
2012-01-20 00:06:00 13000343000 1800000000
2012-01-20 00:07:00 19000000000 1900000000
2012-01-20 00:08:00 18000000000 2000000000
2012-01-20 00:09:00 15000000000 2000000000
2012-01-20 00:10:00 14000000000 1200000000
2012-01-20 00:11:00 10000000000 500000000
2012-01-20 00:12:00 8000000000  1000000000
...
```

Looking at such kind of huge files as charts make much sense than staring at text like this. `CAR` allows you to extract this info and create a visualization to view this as you want. Once you create these visualizations, others need not worry about creating them again and reuse these creations.

Take a look at below visual.

![Visual of free mem plotted against time](/helpcontent/assets/introduction/intro_example_mem_usage.png)


Here are few other examples...

1. [Task timelines](/ui/help/intro-example-task-timelines)
2. [Browsing subtasks of a task](/ui/help/intro-example-browse-task-timeline-by-subtask)
