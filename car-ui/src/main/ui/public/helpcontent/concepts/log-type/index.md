# Log type

Log types define how a log file of this type has to be processed and what rules should be applied on it.

They basically define two things

1. __Detectors__ : A list of classification strategies which decide whether any given file is of this type or not. There are two different types of `detectors` available in the system now. 

    - [File named based detector](/ui/help/file-name-based-detector)
    - [Message finding detector](/ui/help/message-finding-detector)

2. __Record readers__: A file reading strategy which helps in reading different type of files, eg. Log4j log file, CSV file, Single line log file, Multi lined log file. There are two different record readers available in the system now. Read about them here

    - [Single lined record reader](/ui/help/single-lined-record-reader)
    - [Multi lined record reader](/ui/help/multi-lined-record-reader)



## How to define a log type

If you want a quick start guide, it's available [here](/ui/help/quick-start)

1. Go to rules tab.

2. Click on the `+` button on right side of the [`Log type group`](/ui/help/log-type-group) where you want to define this log type.

![new log type btn](/helpcontent/assets/log-type/new-log-type-btn.png)

3. A new `Log type` will be added under that `Log type group`.

4. Use the editor to configure it.