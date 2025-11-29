
## Terminology
Before using the `CAR` we should be familiar with few terms

### 1. Dataset
`Dataset` represents a set of files which are related and are processed as a group by CAR. This can be files of a support case from a Support department standpoint.

### 2. Rule
`Rule` is a definition of __processing work__ that has to be done on a log message to extract the information out of it. Here is an example of a rule

>
>Suppose there is a log message like this
>
>```log
>2019-12-02 23:21:11.453 Database [db1] is unavailable
>```
>A rule named `db_disconnected_event` can be defined to extract the timestamp, db name fields from this message. The definition can be based on a regular expression, for eg.
>
>__Regex__ : `(.*) Database \[(.*?)\] is unavailable`
>
>The rules which use regex to extract information are called `regex rules`. You can read more about this here in [Regex rules](/ui/help/regex-rule) section.
>
> 

### 3. Log type
When there are lot of rules, running all the rules on all the log files doesn't make sense and would take a lot of time. So rules are written per log file type. Refer [this page](/ui/help/log-type) to see more about the `log type` and how to define them


### 4. Log type group
Log types are further grouped as `Log type group`s. Refer [this document](/ui/help/log-type-group) on how to define a log type group.

