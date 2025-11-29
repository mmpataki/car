package car.engine.processor;

public enum Status {

    NEW,
    NOTDONE,

    UNZIPPING,

    QUEUED,

    DETECTING,
    NOT_DETECTED,
    DETECTION_FAILED,
    DETECTED,

    EXTRACTING,
    EXTRACTED,
    FAILED;

    boolean isDetected() {
        return this.ordinal() >= DETECTED.ordinal();
    }

}