# #1. Task Timelines
Let's assume a process which executes mutiple tasks using a thread pool of size 3. The process log looks something like this.

```
2012-01-20 00:00:00 Starting task A
2012-01-20 00:01:00 Starting task B
2012-01-20 00:02:00 Task A finished
2012-01-20 00:03:00 This is some log message
2012-01-20 00:04:00 Starting task C
2012-01-20 00:06:00 Task C failed
2012-01-20 00:07:00 Starting task D
2012-01-20 00:08:00 Starting task C again as it failed earlier
2012-01-20 00:09:00 Starting task E
2012-01-20 00:10:00 Task C failed
2012-01-20 00:11:00 Task C failed twice, giving up on it.
2012-01-20 00:12:00 Task D finished
2012-01-20 00:12:00 Task B finished
```

Looking at these logs and figuring out the task which ran for long is hard. But if you plot a timeline using CAR, it is a easy task. Take a look at the below visual.

![Timeline chart visual](/helpcontent/assets/introduction/intro_example_timeline.png)

Looking at these visual many things can be inferred easily.
- Task start and end time
- Parallelism of the executor
- If you look carefully, you can even point out a huge scheduler delay in here which may be missed out if you are staring at logs.
