# Installation

Installation is needed because the processing happens in your machine. It's a one time activity.

### Steps

1. Download the zipped binary from here

    <a href='/downloads/car.zip' download="true">/downloads/car.zip</a>

2. Unzip it on your machine

3. Run it using below command

    ```
    $ java -jar car.jar
    ```

<br/>

### Upgrade

There is no need for a manual upgrade, the tool automatically upgrades when a new version is available

<br/>

### Syncing rules

You don't need to build rules from scratch once you install CAR.

- When CAR is installed on your system, it comes with a default configuration file (`$INSTALL_LOCATION/config.properties`) which contains the master server address where all the rules, dashboards are stored. 
- Once you start the CAR on your system, it tries to sync with the master server to get the rules and dashboards.
